/*
 * Copyright 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.devtools.depan.view;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Transformer;

import com.google.devtools.depan.collect.Lists;
import com.google.devtools.depan.collect.Maps;
import com.google.devtools.depan.collect.Sets;
import com.google.devtools.depan.graph.api.DirectedRelationFinder;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.interfaces.GraphBuilder;
import com.google.devtools.depan.util.BinaryOperators;

/**
 * A ViewModel is the class handling the drawing of a subgraph.
 * It's purpose is only to be displayed on a canvas as a graph, and performing
 * some operations visually. Thus, it doesn't contains any graph logic.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class ViewModel
    implements BinaryOperators<ViewModel>, NodeDisplayProperties {

  /**
   * Name of the ViewModel.
   * <p>
   * This is persisted.
   */
  private String name;

  /**
   * parent Graph, i.e. the graph containing all the nodes and edges in the
   * graph analysis.
   * <p>
   * This is persisted as a reference.
   */
  private final GraphModel parentGraph;

  /**
   * GraphModel containing nodes and edges from this view only.
   * <p>
   * This is persisted.
   */
  private GraphModel viewGraph;

  /**
   * Rendering location for nodes.
   * <p>
   * This is persisted.
   */
  private Map<GraphNode, Point2D> nodeLayout = Maps.newHashMap();

  /**
   * Rendering properties for nodes.
   * <p>
   * This is persisted. [TODO]
   */
  public Map<GraphNode, NodeDisplayProperty> propertyMap = Maps.newHashMap();

  /**
   * Manager object for handling all collapsed nodes.
   * <p>
   * This is persisted. [TODO]
   */
  private final Collapser collapser = new Collapser();

  /**
   * Indicate if any information about the view has been changed since the
   * ViewModel was saved to the persistent store.  A newly created ViewModel
   * is assumed to be dirty (e.g. unsaved).
   */
  private boolean isDirty = true;

  /**
   * A collection of listeners for ViewModel events.
   */
  private Collection<ViewModelListener> listeners = Lists.newArrayList();

  /**
   * Constructor for a viewModel with a default name (View N, where N is
   * incremented at each new view).
   *
   * @param parentGraph this View's Graph
   */
  public ViewModel(GraphModel parentGraph) {
    this(ViewModelHelper.nextViewName(), parentGraph);
  }

  /**
   * Create a view with the given name.
   *
   * @param name name for this new ViewModel.
   * @param parentGraph this View's Graph
   */
  public ViewModel(String name, GraphModel parentGraph) {
    this.parentGraph = parentGraph;
    this.name = name;
    init();
  }

  public void init() {
    parentGraph.registerView(this);

    // React to self-inflicted changes.
    registerListener(new SimpleViewModelListener() {
      @Override
      public void simpleChange() {
        markDirty();
      }
    });

    ViewModelHelper.addViewName(getName());
    // Leave viewGraph null until the nodes are set.
  }

  /////////////////////////////////////
  // Simple Getters and Setters

  /**
   * Provide that dirty status for the viewModel.
   */
  public boolean getDirty() {
    return isDirty;
  }

  /**
   * Mark that persistent data in the viewModel has been altered.
   */
  public void markDirty() {
    isDirty = true;
  }

  /**
   * Mark that persistent data in the viewModel is unchanged.
   */
  public void clearDirty() {
    isDirty = false;
  }

  @Override
  public String toString() {
    return name;
  }

  /**
   * Returns GraphModel representing <code>this</code> view.
   *
   * WARNING: use <code>getParentGraph()</code> if you want a GraphModel
   * representing the entire graph.
   *
   * @return a GraphModel representing <code>this</code> view.
   */
  public GraphModel getGraph() {
    return viewGraph;
  }

  public Collection<GraphNode> getNodes() {
    return getGraph().getNodes();
  }

  public int getNodesCount() {
    return getGraph().getNodes().size();
  }

  public Collection<GraphEdge> getEdges() {
    return getGraph().getEdges();
  }

  /**
   * returns a GraphModel representing the entire graph, not just
   * <code>this</code> view, as opposed to getGraph().
   *
   * @return a GraphModel representing the entire graph.
   */
  public GraphModel getParentGraph() {
    return parentGraph;
  }

  /**
   * Provide the name of this ViewModel.
   * @return name of this ViewModel
   */
  public String getName() {
    return name;
  }

  /**
   * Set the name of this view to the given name.
   *
   * @param newName the new name.
   */
  public void setName(String newName) {
    this.name = newName;
  }

  public Map<GraphNode, Point2D> getLayoutMap() {
    return nodeLayout;
  }

  public Point2D getNodeLocation(GraphNode node) {
    return getLayoutMap().get(node);
  }

  public boolean isVisible(GraphNode node) {
    // Don't create a property object just to check for visibility
    NodeDisplayProperty nodeProps = propertyMap.get(node);
    if (null == nodeProps) {
      return true;
    }
    return nodeProps.isVisible();
  }

  public boolean isVisible(GraphEdge edge) {
    return getEdges().contains(edge);
  }

  public NodeDisplayProperty getNodeDisplayProperty(GraphNode node) {
    if (!propertyMap.containsKey(node)) {
      propertyMap.put(node, new NodeDisplayProperty());
    }
    return propertyMap.get(node);
  }

  /**
   * @param collection
   * @return a new ViewModel containing the nodes in the given collection and
   *         edges connecting those nodes; return null if the collection
   *         contains less than two elements.
   */
  private ViewModel newChildView(Collection<GraphNode> newNodes) {
    ViewModel newModel = new ViewModel(getParentGraph());
    newModel.setNodes(newNodes);
    return newModel;
  }

  /////////////////////////////////////
  // Methods for defining the active nodes in the viewModel

  /**
   * Create the viewGraph if it doesn't exist.
   * @throws IllegalStateException if viewGraph already exists.
   */
  private void initViewGraph() {
    if (null != viewGraph) {
      throw new IllegalStateException("Nodes in view already defined.");
    }

    viewGraph = parentGraph.newView();
    markDirty();
  }

  /**
   * Define the view of the nodes from an array of nodes.
   * <p>
   * Although this is nearly identical to setNodes(Collection),
   * both the method signature and the for/in loop generated different code.
   *
   * @param srcNodes the array of nodes that are in this view
   */
  public void setNodes(GraphNode[] srcNodes) {
    initViewGraph();

    // add the nodes
    GraphBuilder builder = viewGraph.getBuilder();
    for (GraphNode node : srcNodes) {
      builder.newNode(node);
    }

    parentGraph.populateRelations(viewGraph);
  }

  /**
   * Define the view of the nodes from Collection of nodes.
   *
   * Although this is nearly identical to setNodes(Node[]),
   * both the method signature and the for/in loop generated different code.
   *
   * @param srcNodes the array of nodes that are in this view
   */
  public void setNodes(Collection<GraphNode> srcNodes) {
    initViewGraph();

    // add the nodes
    GraphBuilder builder = viewGraph.getBuilder();
    for (GraphNode node : srcNodes) {
      builder.newNode(node);
    }

    parentGraph.populateRelations(viewGraph);
  }

  /**
   * Define the view of the nodes from a collection of node names
   * Node names are defined by getId().toString().
   * @param srcNodes the Collection of node names that are in this view
   */
  public void setStringNodes(Collection<String> nodeNames) {
    initViewGraph();

    // construct a map with toString as key, and corresponding node as value
    Map<String, GraphNode> parentNodes = parentGraph.getNodesMap();

    // add the nodes
    GraphBuilder builder = viewGraph.getBuilder();
    for (String nodeName : nodeNames) {
      if (parentNodes.containsKey(nodeName)) {
        GraphNode node = parentNodes.get(nodeName);
        builder.newNode(node);
      }
    }

    parentGraph.populateRelations(viewGraph);
  }

  /**
   * says if the given node is in this view.
   *
   * @param node
   * @return true if the given node is in this view.
   */
  public boolean containsNode(GraphNode node) {
    return viewGraph.getNodesSet().contains(node);
  }

  public void setLocations(
      final Map<GraphNode, Point2D> map) {
    boolean change = false;
    Set<GraphNode> viewNodes = viewGraph.getNodesSet();
    Collection<GraphNode> movedNodes = map.keySet();

    for (GraphNode node : movedNodes) {
      if (viewNodes.contains(node)) {
        Point2D newLocation = map.get(node);
        updateLocation(node, newLocation);
      }
    }

    if (change) {
      fireLocationsChanged(movedNodes, this);
    }
  }

  public void setLocations(
      Collection<GraphNode> movedNodes,
      Transformer<GraphNode, Point2D> mapper) {
    boolean change = false;
    Set<GraphNode> viewNodes = viewGraph.getNodesSet();

    for (GraphNode node : movedNodes) {
      if (viewNodes.contains(node)) {
        change |= updateLocation(node, mapper.transform(node));
      }
    }

    if (change) {
      fireLocationsChanged(movedNodes, this);
    }
  }

  /**
   * Tell the model that a node was moved outside of the normal
   * pathways.
   *
   * @param movedNode
   * @param author
   */
  public void movedNode(
      GraphNode movedNode, Point2D newLocation, Object author) {
    updateLocation(movedNode, newLocation);
    fireLocationsChanged(
        Collections.<GraphNode>singleton(movedNode), this);
  }

  /**
   * Update a nodes location.  This also marks the ViewModel as dirty.
   * <p>
   * The implementation tries to careful update the location.
   * First, it verifies that the new location is different from the old
   * location.  Secondly, it updates the old location object in place, rather
   * the attempting to update the nodeLayout.  Updating the nodeLayout map
   * can have some odd behaviors if it shared among multiple views.
   *
   * @param node
   * @param newLocation
   * @return true iff a location has changed
   */
  private boolean updateLocation(GraphNode node, Point2D newLocation) {

    Point2D currLocation = nodeLayout.get(node);
    if (null == currLocation) {
      currLocation = (Point2D) newLocation.clone();
      nodeLayout.put(node, currLocation);
      return true;
    }
    if (currLocation.getX() != newLocation.getX()
        || currLocation.getY() != newLocation.getY()) {
      currLocation.setLocation(newLocation.getX(), newLocation.getY());
      return true;
    }

    return false;
  }

  /////////////////////////////////////
  // ViewModel-combining operations

  /* (non-Javadoc)
   * @see com.google.devtools.depan.util.interfaces.BinaryOperators
   *      #and(java.lang.Object)
   */
  public ViewModel and(ViewModel that) {
    return newChildView(viewGraph.and(that.getGraph()));
  }

  /* (non-Javadoc)
   * @see com.google.devtools.depan.util.interfaces.BinaryOperators
   *      #not(java.lang.Object)
   */
  public ViewModel not(ViewModel that) {
    return newChildView(viewGraph.not(that.getGraph()));
    }

  /* (non-Javadoc)
   * @see com.google.devtools.depan.util.interfaces.BinaryOperators
   *      #or(java.lang.Object)
   */
  public ViewModel or(ViewModel that) {
    return newChildView(viewGraph.or(that.getGraph()));
  }

  /* (non-Javadoc)
   * @see com.google.devtools.depan.util.interfaces.BinaryOperators
   *      #xor(java.lang.Object)
   */
  public ViewModel xor(ViewModel that) {
    Collection<GraphNode> result = viewGraph.not(that.getGraph());
    result.addAll(that.getGraph().not(getGraph()));
    return newChildView(result);
  }

  /////////////////////////////////////
  // Collapsed Nodes support

  /** Virtualize all access to the collapser */
  private Collapser getCollapser() {
    return collapser;
  }

  /**
   * Collapse a set of nodes under a specific master node.
   *
   * @param master node to represent collapsed nodes.  Should be in picked.
   * @param picked collection of nodes to collapse
   * @param erase erase (and merge) any collapsed nodes in picked as part of
   * the new collapsed node
   * @param author the initiator for this process
   */
  public void collapse(
      GraphNode master,
      Collection<GraphNode> picked,
      boolean erase, Object author) {
    getCollapser().collapse(master, picked, erase);

    fireCollapseChanged(
        Sets.newSingleton(getCollapser().getCollapseData(master)),
        Collections.<CollapseData>emptyList(),
        author);
  }

  /**
   * Collapse a set of nodes under a specific master node.
   *
   * @param master node to represent collapsed nodes.  Should be in picked.
   * @param picked collection of nodes to collapse
   * @param erase erase (and merge) any collapsed nodes in picked as part of
   * the new collapsed node
   * @param author the initiator for this process
   */
  public void uncollapse(
      GraphNode master, boolean deleteGroup, Object author) {
    CollapseData removedGroup = getCollapser().getCollapseData(master);
    if (null == removedGroup) {
      return;
    }

    // Perform the collapsing
    getCollapser().uncollapse(master, deleteGroup);

    fireCollapseChanged(
        Collections.<CollapseData>emptyList(),
        Sets.newSingleton(removedGroup),
        author);
  }

  /**
   * Collapse all Nodes in the exposed graph using the hierarchy implied
   * by the given set of relations.
   * <p>
   * The algorithm works by computing a topological sort over the imputed
   * hierarchy, and then collapsing the nodes in order from bottom to top.
   * This allows a user to later uncollapse individual masters,
   * and to incrementally expose their internal details.
   *
   * @param finder set of relations that define the hierarchy
   * @param author interface component that initiated the action
   */
  public void autoCollapse(DirectedRelationFinder finder, Object author) {
    TreeModel treeData = new TreeModel(
        getExposedGraph().computeSuccessorHierarchy(finder));

    List<GraphNode> inOrder = treeData.topoSort();
    Collection<CollapseData> collapseChanges = Lists.newArrayList();

    for (GraphNode top : inOrder) {
      addCollapseData(collapseChanges, treeData, top);
    }

    fireCollapseChanged(
      collapseChanges,
      Collections.<CollapseData>emptyList(),
      author);
  }

  /**
   * Collapse a single node based on its ancestors in the tree model.
   * The given node becomes the master for the collapse group.
   * All exposed children (and their exposed ancestors) become members
   * of the collapse group, and the collapse group stops for (but includes)
   * top-level master nodes in the ancestor set.
   *
   * @param collapseChange destination of any added collapseData
   * @param parent master node for collapse group
   * @param treeModel source of successor/ancestor relations
   */
  private void addCollapseData(
      Collection<CollapseData> collapseChanges,
      TreeModel treeModel,
      GraphNode parent) {

    // Nothing to do if the node has no successors
    Collection<GraphNode> successors = treeModel.getSuccessors(parent);
    if (successors.isEmpty()) {
      return;
    }

    // Only include successor nodes that are exposed
    Map<GraphNode, GraphNode> hiddenNodeMap =
        getCollapser().buildHiddenNodeMap();
    HiddenNodesGizmo gizmo = new HiddenNodesGizmo(hiddenNodeMap);
    Set<GraphNode> exposedNodes = getExposedNodeSet(gizmo);

    // Don't include the ancestors of already collapsed nodes
    Set<GraphNode> masterNodes = collapser.getMasterNodeSet();

    Collection<GraphNode> result = Lists.newArrayList();
    result.add(parent);
    addExposedAncestors(result, treeModel, exposedNodes, masterNodes, parent);

    CollapseData collapseData = collapser.collapse(parent, result, false);
    collapseChanges.add(collapseData);
  }

  private void addExposedAncestors(
      Collection<GraphNode> result,
      TreeModel treeModel,
      Set<GraphNode> exposedNodes,
      Set<GraphNode> masterNodes,
      GraphNode parent) {
    for (GraphNode child : treeModel.getSuccessors(parent)) {

      // Only include exposed children
      if (exposedNodes.contains(child)) {
        result.add(child);

        // Recursively add any exposed ancestors
        addExposedAncestors(
            result, treeModel, exposedNodes, masterNodes, child);
      }
    }
  }

  public GraphModel getExposedGraph() {
    Map<GraphNode, GraphNode> hiddenNodeMap =
        getCollapser().buildHiddenNodeMap();

    // quick exit if nothing is collapsed
    if (hiddenNodeMap.isEmpty()) {
      return getGraph();
    }

    HiddenNodesGizmo gizmo = new HiddenNodesGizmo(hiddenNodeMap);

    // Determine the exposed nodes and edges
    Collection<GraphNode> nodes = getExposedNodeSet(gizmo);

    Collection<GraphEdge> edges = Lists.newArrayList();
    gizmo.addExposedEdges(edges, getEdges());

    // Add the exposed components to the generated result
    GraphModel result = new GraphModel();
    GraphBuilder builder = result.getBuilder();
    for (GraphNode node : nodes) {
      builder.newNode(node);
    }
    for (GraphEdge edge : edges) {
      builder.addEdge(edge);
    }
    return result;
  }

  /**
   * Provide the current Set of exposed Nodes.
   *
   * @param gizmo source of exposed Node information
   * @return Set of exposed Nodes, including exposed master Nodes.
   */
  private Set<GraphNode> getExposedNodeSet(HiddenNodesGizmo gizmo) {
    Set<GraphNode> nodeSet = Sets.newHashSet();
    getCollapser().addMasterNodes(nodeSet);
    gizmo.addExposedNodes(nodeSet, getNodes());
    return nodeSet;
  }

  /////////////////////////////////////
  // Change Notification SUpport

  public void registerListener(ViewModelListener listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  public void unRegisterListener(ViewModelListener listener) {
    if (listeners.contains(listener)) {
      listeners.remove(listener);
    }
  }

  public void fireLocationsChanged(
      Collection<GraphNode> movedNodes, Object author) {

    // Ignore empty move events - listeners never care
    if (movedNodes.isEmpty()) {
      return;
    }
    for (ViewModelListener listener : listeners) {
      listener.locationsChanged(movedNodes, author);
    }
  }

  public void fireCollapseChanged(
      Collection<CollapseData> created,
      Collection<CollapseData> removed,
      Object author) {

    // Ignore empty collapse events - listeners never care
    if (created.isEmpty() && removed.isEmpty()) {
      return;
    }
    for (ViewModelListener listener : listeners) {
      listener.collapseChanged(created, removed, author);
    }
  }

}
