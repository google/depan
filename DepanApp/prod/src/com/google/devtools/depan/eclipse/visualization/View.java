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

package com.google.devtools.depan.eclipse.visualization;

import com.google.devtools.depan.collect.Lists;
import com.google.devtools.depan.collect.Maps;
import com.google.devtools.depan.collect.Sets;
import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds;
import com.google.devtools.depan.eclipse.utils.DefaultRelationshipSet;
import com.google.devtools.depan.eclipse.visualization.layout.Layouts;
import com.google.devtools.depan.eclipse.visualization.ogl.ArrowHead;
import com.google.devtools.depan.eclipse.visualization.ogl.GLPanel;
import com.google.devtools.depan.eclipse.visualization.ogl.RenderingPipe;
import com.google.devtools.depan.graph.api.DirectedRelationFinder;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.interfaces.GraphListener;
import com.google.devtools.depan.view.CollapseData;
import com.google.devtools.depan.view.EdgeDisplayProperty;
import com.google.devtools.depan.view.NodeDisplayProperty;
import com.google.devtools.depan.view.ViewModel;
import com.google.devtools.depan.view.ViewModelListener;
import com.google.devtools.depan.view.EdgeDisplayProperty.LineStyle;
import com.google.devtools.depan.view.NodeDisplayProperty.Size;

import edu.uci.ics.jung.algorithms.importance.PageRank;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class View implements SelectionChangeListener {

  /** The underlying source of rendering data */
  private final ViewModel viewModel;

  /** Detect changes to the underlying ViewModel. */
  private ViewModelListener viewModelListener;

  /**
   * The JUNG graph that is use for rendering and presentation.
   * It is derived and rebuild from the viewModel on demand,
   * based on the exposed nodes and edges in that viewModel.
   */
  private DirectedGraph<GraphNode, GraphEdge> jungGraph;

  /**
   * The ranking values for each nodes (using a page rank algorithm).
   */
  private Map<GraphNode, Double> ranking;

  /**
   * Collection of listeners listening for a change in the node selection.
   */
  private Collection<SelectionChangeListener> changeListeners =
      Lists.newArrayList();

  /**
   * Relation finder used by layout algorithms by default if it wasn't
   * specified.
   */
  private final DirectedRelationFinder finder;

  /**
   * Rendering object.
   */
  private GLPanel glPanel;

  /**
   * Listener for changes in the graph.
   */
  private GraphListener graphListener;

  /**
   * Create a new View, with the given model and initialize the layout.
   *
   * @param model underlying model.
   * @param initLayout first layout to apply to the view.
   * @param parent parent SWT control.
   * @param style style to apply to the SWT object containing the view.
   */
  public View(ViewModel model, Layouts initLayout, Composite parent,
      int style) {
    viewModel = model;

    // select a default finder for this view.
    finder = DefaultRelationshipSet.SET;

    createJungGraph();
    rankGraph();
    glPanel = new GLPanel(parent, this, this);
    applyLayout(initLayout);

    // Get informed about ViewModel changes
    viewModelListener = new ViewModelIntegration();
    getViewModel().registerListener(viewModelListener);

    // register this object with the GraphModel that is currently shown.
    // This will be useful when nodes are selected through the Node Editor.
    graphListener = new GraphListener() {
      /**
       * Does not do anything for now.
       */
      @Override
      public void newView(ViewModel view) {
      }

      /**
       * Callback when display property of a node is modified.
       *
       * @param node The node whose display property is modified.
       * @param property The display property that holds the modifications.
       */
      @Override
      public void nodePropertyChanged(
          GraphNode node, NodeDisplayProperty property) {
        updateNodeProperty(node, property);
      }

      @Override
      public void edgePropertyChanged(GraphEdge edge,
          EdgeDisplayProperty property) {
        updateEdgeProperty(edge, property);
      }
    };
    getViewModel().getExposedGraph().registerListener(graphListener);
  }

  /**
   * Updates various properties of the given <code>GraphEdge</code> such as
   * edge color, line style and arrow head using the given
   * {@link EdgeDisplayProperty} object.
   *
   * @param edge {@link GraphEdge} whose properties are updated.
   * @param property {@link EdgeDisplayProperty} object that stores various
   * changes related to the given edge.
   */
  private void updateEdgeProperty(
      GraphEdge edge, EdgeDisplayProperty property) {
    glPanel.setEdgeColor(edge, property.getColor());

    boolean stippled = property.getLineStyle() == LineStyle.DASHED;
    glPanel.setEdgeLineStyle(edge, stippled);

    glPanel.setArrowhead(edge,
        ArrowHead.createNewArrowhead(property.getArrowhead()));
  }

  /**
   * Updates various properties of the given <code>GraphNode</code> such as
   * color and size model.
   *
   * @param node Node whose properties are modified.
   * @param property Holds various display properties such as color and size
   * model.
   */
  private void updateNodeProperty(
      GraphNode node, NodeDisplayProperty property) {
    // set node color first.
    // if it is set in property object, it will be colored using this color
    // otherwise, default color will be used.
    glPanel.setNodeColor(node, property.getColor());

    // set if this node is visible
    glPanel.setVisible(node, property.isVisible());

    // set if this node is selected
    glPanel.setSelected(node, property.isSelected());

    // Set the size of this node
    Size nodeSize = property.getSize();
    if (nodeSize != null) {
      NodePreferencesIds.NodeSize sizeRepresentation =
          NodePreferencesIds.NodeSize.convertSizeRepresentation(nodeSize);
      glPanel.setNodeSize(node, sizeRepresentation);
    }
  }

  public void dispose() {
    if (null != viewModelListener) {
      getViewModel().unRegisterListener(viewModelListener);
      viewModelListener = null;
    }
    // unregister graph listener object if there is one.
    if (null != graphListener) {
      getViewModel().getExposedGraph().unRegisterListener(graphListener);
      graphListener = null;
    }
  }

  /////////////////////////////////////
  // Basic Getters and Setters

  public Graph<GraphNode, GraphEdge> getJungGraph() {
    return jungGraph;
  }

  public Control getControl() {
    return glPanel.getContext();
  }

  /**
   * @return a list of currently selected nodes.
   */
  public GraphNode[] getPickedNodes() {
    return glPanel.getSelectedNodes();
  }

  /**
   * Unselect all currently selected nodes, and select only the nodes in the
   * given list.
   *
   * @param pickedNodes set of nodes to select.
   */
  public void setPickedNodes(Collection<GraphNode> pickedNodes) {
    glPanel.setSelection(pickedNodes);
  }

  /**
   * @return a map of importance, i.e. a map associating a value to each node,
   *         determined after applying a ranking algorithm to the underlying
   *         graph.
   */
  public Map<GraphNode, Double> getNodeImportanceMap() {
    return ranking;
  }

  /**
   * @return the rendering pipe controlling rendering.
   */
  public RenderingPipe getRenderingPipe() {
    return glPanel.getRenderingPipe();
  }

  /**
   * @return the object responsible for rendering the graph.
   */
  public GLPanel getGLPanel() {
    return glPanel;
  }

  /**
   * @return the wrapped ViewModel.
   */
  public ViewModel getViewModel() {
    return viewModel;
  }

  /////////////////////////////////////
  // Rendering support

  /**
   * Create a JUNG Graph for the underlying ViewModel.
   */
  private void createJungGraph() {
    jungGraph =
        new DirectedSparseMultigraph<GraphNode, GraphEdge>();

    GraphModel exposedGraph = viewModel.getExposedGraph();
    for (GraphNode node : exposedGraph.getNodes()) {
      jungGraph.addVertex(node);
    }

    for (GraphEdge edge : exposedGraph.getEdges()) {
      addEdge(jungGraph, edge);
    }
  }

  /**
   * Associate to each node a value based on it's "importance" in the graph.
   * The selected algorithm is a Page Rank algorithm.
   *
   * The result is stored in the map {@link #ranking}.
   */
  private void rankGraph() {
    if (null == ranking) {
      ranking = Maps.newHashMap();
    }
    ranking.clear();

    PageRank<GraphNode, GraphEdge> pageRank =
        new PageRank<GraphNode, GraphEdge>(jungGraph, 0.15);
    pageRank.setRemoveRankScoresOnFinalize(false);
    pageRank.evaluate();

    GraphModel exposedGraph = viewModel.getExposedGraph();
    for (GraphNode node : exposedGraph.getNodes()) {
      ranking.put(node, pageRank.getVertexRankScore(node));
    }
  }

  /////////////////////////////////////
  // Update Graph Layouts

  /**
   * Apply the given layout with a default size to all the nodes of the graph.
   *
   * @param newLayout the new Layout to apply
   */
  public void applyLayout(Layouts newLayout) {
    applyLayout(newLayout, finder);
  }

  /**
   * Apply the given layout with the given {@link DirectedRelationFinder} to
   * build a tree if the layout need one, to the graph.
   *
   * @param newLayout the new Layout to apply
   * @param relationfinder {@link DirectedRelationFinder} helping to build a
   *        tree if necessary
   */
  public void applyLayout(Layouts newLayout,
      DirectedRelationFinder relationfinder) {
    if (null == newLayout) {
      glPanel.getRenderingPipe().getLayout().setLayout(
          viewModel.getLayoutMap());
    } else {
      Map<GraphNode, Point2D> layout = glPanel.getRenderingPipe().getLayout()
          .setLayout(newLayout.getLayout(this, relationfinder));
      viewModel.setLocations(layout);
    }
    glPanel.getRenderingPipe().getFactor().computeBestScalingFactor();
  }

  /**
   * Add an edge to a graph.
   * <p>
   * This method exist primarily to limit the scope of the SuppressWarnings.
   *
   * @param graph
   * @param edge
   */
  private static void addEdge(
      Graph<GraphNode, GraphEdge> graph,
      GraphEdge edge) {
    graph.addEdge(edge, edge.getHead(), edge.getTail());
  }

  /**
   * Call {@link #cluster(Layouts, DirectedRelationFinder)} with a
   * default {@link DirectedRelationFinder}.
   *
   * @param layout
   */
  public void cluster(Layouts layout) {
    cluster(layout, finder);
  }

  /**
   * Try to clusterize the selected nodes (if more than two nodes are selected),
   * or, if no nodes are selected, apply the layout to the entire graph.
   *
   * @param layout
   * @param relationFinder a relation finder if needed by the layout.
   */
  public void cluster(Layouts layout, DirectedRelationFinder relationFinder) {
    GraphNode[] picked = getPickedNodes();
    if (0 == picked.length) {
      applyLayout(layout, relationFinder);
      return;
    }
    if (2 > picked.length) {
      return;
    }

    // find the center of all picked points to place the new node
    // representing collapsed nodes
    Point2D center = new Point2D.Double();
    double x = 0, y = 0;
    double minx = Double.MAX_VALUE, maxx = Double.MIN_VALUE;
    double miny = Double.MAX_VALUE, maxy = Double.MIN_VALUE;
    for (GraphNode node : picked) {
      Point2D p = viewModel.getNodeLocation(node);
      x += p.getX();
      y += p.getY();
      minx = Math.min(minx, p.getX());
      maxx = Math.max(maxx, p.getX());
      miny = Math.min(miny, p.getY());
      maxy = Math.max(maxy, p.getY());
    }
    center.setLocation(x / picked.length, y / picked.length);

    Graph<GraphNode, GraphEdge> subGraph =
        new DirectedSparseMultigraph<GraphNode, GraphEdge>();

    Set<GraphEdge> edges = Sets.newHashSet();
    for (GraphNode node : picked) {
      subGraph.addVertex(node);
      edges.addAll(getJungGraph().getIncidentEdges(node));
    }
    for (GraphEdge edge : edges) {
      GraphNode head = edge.getHead();
      GraphNode tail = edge.getTail();
      boolean containsHead = false;
      boolean containsTail = false;
      for (int i = 0; i < picked.length && !(containsHead && containsTail);
          ++i) {
        if (picked[i] == head) {
          containsHead = true;
        } else if (picked[i] == tail) {
          containsTail = true;
        }
      }
      subGraph.addEdge(edge, head, tail);
    }

    // FIXME: reimplement
/*
    // try to not move the not selected nodes
    for (GraphNode node : viewModel.getGraph().getNodes()) {
      if (picked.contains(node)) {
        staticLayout.lock(node, false);
      } else {
        staticLayout.lock(node, true);
      }
    }

    // add +0.5 to round to the upper bound.
    Dimension size = new Dimension(
          (int) (maxx - minx + 0.5), (int) (maxy - miny + 0.5));

    AggregateLayout<GraphNode, GraphEdge> aggregate =
       new AggregateLayout<GraphNode, GraphEdge>(staticLayout);

    Layout<GraphNode, GraphEdge> subLayout;
    subLayout = layout.getLayout(this, relationFinder);
    subLayout.setInitializer(vv.getGraphLayout());
    subLayout.setSize(size);
    aggregate.put(subLayout, center);
    vv.setGraphLayout(aggregate);
    */
  }

  /**
   * Sets the selection state of all nodes in this view to on.
   */
  public void selectAll() {
    setPickedNodes(getViewModel().getNodes());
  }

  /////////////////////////////////////
  // Event callback support

  public void registerListener(SelectionChangeListener listener) {
    if (!changeListeners.contains(listener)) {
      changeListeners.add(listener);
    }
  }

  public void unRegisterListener(SelectionChangeListener listener) {
    if (changeListeners.contains(listener)) {
      changeListeners.remove(listener);
    }
  }

  /**
   * Handle integration with the underlying ViewModel.
   */
  private class ViewModelIntegration implements ViewModelListener {

    public void collapseChanged(
        Collection<CollapseData> created,
        Collection<CollapseData> removed,
        Object author) {
      for (CollapseData data : created) {
        // collapse each child under the parent
        for (GraphNode child : data.getChildrenNodes()) {
          glPanel.getRenderingPipe().getCollapsePlugin().collapseUnder(
              glPanel.node2property(child),
              glPanel.node2property(data.getMasterNode()));
        }
      }

      for (CollapseData data : removed) {
        // uncollapse every chlidren
        for (GraphNode child : data.getChildrenNodes()) {
          glPanel.getRenderingPipe().getCollapsePlugin().unCollapse(
              glPanel.node2property(child),
              glPanel.node2property(data.getMasterNode()));
        }
      }
    }

    public void locationsChanged(
        Collection<GraphNode> movedNodes,
        Object author) {
      // handled separately - nothing to do
    }
  }

  /**
   * Notify listeners that there were nodes added to the current selection.
   */
  @Override
  public void notifyAddedToSelection(GraphNode[] selected) {
    for (SelectionChangeListener listener : changeListeners) {
      listener.notifyAddedToSelection(selected);
    }
  }

  /**
   * Notify listeners that there were nodes removed from the current selection.
   */
  @Override
  public void notifyRemovedFromSelection(GraphNode[] unselected) {
    for (SelectionChangeListener listener : changeListeners) {
      listener.notifyRemovedFromSelection(unselected);
    }
  }

}
