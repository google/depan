/*
 * Copyright 2009 The Depan Project Authors
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

package com.google.devtools.depan.view_doc.model;

import com.google.devtools.depan.collapse.model.CollapseData;
import com.google.devtools.depan.collapse.model.Collapser;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeTreeProvider;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.graph.registry.RelationRegistry;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.RelationSets;
import com.google.devtools.depan.nodes.trees.TreeModel;
import com.google.devtools.depan.platform.ListenerManager;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Persistent user preferences for presentation of a graph view.  This is the
 * sole means to save and load the graph's presentation, and all preference
 * setting must be member fields of this class.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class ViewPreferences {

  private static final Logger logger =
      Logger.getLogger(ViewPreferences.class.getName());

  public static final String EMPTY_DESCRIPTION = "";

  /////////////////////////////////////
  // Persisted instance members

  /**
   * Rendering location for nodes.
   */
  private Map<GraphNode, Point2D> nodeLocations;

  /**
   * Rendering properties for nodes.
   */
  private Map<GraphNode, NodeDisplayProperty> nodeProperties;

  /**
   * Hash map that contains a list of edge display property objects for each
   * edge in graph.
   */
  private Map<GraphEdge, EdgeDisplayProperty> edgeProperties;

  /**
   * The set of edges that are currently visible in the diagram.
   */
  private RelationSet visibleRelationSet;

  /**
   * Hash map that contains a list of edge display property objects
   * for known relations.
   */
  private Map<Relation, EdgeDisplayProperty> relationProperties;

  /**
   * Preferred relation set of displaying edges.
   * A value of {@code null} indicates that the {@link #relationProperties}
   * should be used as an anonymous relation set.
   */
  private RelationSetDescriptor edgeDisplayRelationSet;

  private Collection<GraphNode> selectedNodes = ImmutableList.of();

  private String selectedLayout;

  private ScenePreferences scenePrefs;

  private OptionPreference options;

  /**
   * Edge matcher used by layout algorithms by default if it wasn't
   * specified.
   */
  private GraphEdgeMatcherDescriptor layoutEdgeMatcher;

  private List<GraphEdgeMatcherDescriptor> treeDescriptors;

  /**
   * Manager object for handling all collapsed nodes.
   */
  private Collapser collapser;

  /**
   * Defines the edge matcher used to define the view hierarchy
   */
  @SuppressWarnings("unused")  // Should be useful soon.
  private GraphEdgeMatcherDescriptor treeEdgeMatcher;

  /////////////////////////////////////
  // Transient instance members

  /**
   * Manage objects that are interested in preference changes.
   * Initialization moved to an explicit public method since XStream
   * unmarshalling does not set this.
   */
  private transient ListenerManager<ViewPrefsListener> listeners;

  /////////////////////////////////////
  // Listeners for structures changes

  private abstract static class SimpleDispatcher
      implements ListenerManager.Dispatcher<ViewPrefsListener> {

    @Override
    public void captureException(RuntimeException errAny) {
      logger.warning(errAny.toString());
    }
  }

  public void addListener(ViewPrefsListener listener) {
    listeners.addListener(listener);
  }

  public void removeListener(ViewPrefsListener listener) {
    listeners.removeListener(listener);
  }

  /////////////////////////////////////
  // Instance constructors and factories

  /**
   * Explicit simple constructor
   */
  public ViewPreferences() {
    this(
        ScenePreferences.getDefaultScenePrefs(),
        Maps.<GraphNode, Point2D>newHashMap(),
        Maps.<GraphNode, NodeDisplayProperty>newHashMap(),
        RelationSets.ALL,
        Maps.<GraphEdge, EdgeDisplayProperty>newHashMap(),
        Maps.<Relation, EdgeDisplayProperty>newHashMap(),
        ImmutableList.<GraphNode>of(),
        OptionPreferences.getDefaultOptions(),
        new Collapser(),
        Lists.<GraphEdgeMatcherDescriptor>newArrayList());
  }

  public ViewPreferences(
      ScenePreferences gripPrefs,
      Map<GraphNode, Point2D> newNodeLocations,
      Map<GraphNode, NodeDisplayProperty> newNodeProperties,
      RelationSet visibleRelationSet,
      Map<GraphEdge, EdgeDisplayProperty> newEdgeProperties,
      Map<Relation, EdgeDisplayProperty> newRelationProperties,
      Collection<GraphNode> newSelectedNodes,
      OptionPreference options,
      Collapser collapser,
      List<GraphEdgeMatcherDescriptor> treeDescriptors) {
    initTransients();
    this.collapser = collapser;
    this.treeDescriptors = treeDescriptors;

    this.scenePrefs = gripPrefs;
    this.nodeLocations = newNodeLocations;
    this.nodeProperties = newNodeProperties;
    this.visibleRelationSet = visibleRelationSet;
    this.edgeProperties = newEdgeProperties;
    this.relationProperties = newRelationProperties;
    this.selectedNodes = newSelectedNodes;
    this.options = options;
  }

  /**
   * Initialize transient fields.  This is used directly in the XStream
   * unmarshalling converter, since none of the constructors are actually
   * invoked.
   */
  public void initTransients() {
    listeners = new ListenerManager<ViewPrefsListener>();
  }

  /**
   * Populate any required fields after an unmarshall(), since that process
   * by-passes the constructors.
   */
  public void afterUnmarshall() {
    if (null == scenePrefs) {
      scenePrefs = ScenePreferences.getDefaultScenePrefs();
    }
    if (null == nodeLocations) {
      nodeLocations = Maps.newHashMap();
    }
    if (null == nodeProperties) {
      nodeProperties = Maps.newHashMap();
    }
    if (null == visibleRelationSet) {
      visibleRelationSet = RelationSets.ALL;
    }
    if (null == edgeProperties) {
      edgeProperties = Maps.newHashMap();
    }
    if (null == relationProperties) {
      relationProperties = Maps.newHashMap();
    }
    if (null == selectedNodes) {
      selectedNodes = ImmutableList.of();
    }
    if (null == options) {
      options = OptionPreferences.getDefaultOptions();
    }
    if (null == treeDescriptors) {
      treeDescriptors = Lists.newArrayList();
    }
    if (null == collapser) {
      collapser = new Collapser();
    }
  }

  /**
   * Construct a new set of ViewPreferences by copying any preferences
   * in the current view.  The only preferences (including location) that
   * are copied are those for the indicated nodes.  For example, only edge
   * properties that include both nodes are included in the result.
   * 
   * @param source source of node properties
   * @param nodes selector for properties to retain
   * @return new view preferences for indicated nodes
   */
  public static ViewPreferences buildFilteredNodes(
      ViewPreferences source, Collection<GraphNode> nodes) {
    Map<GraphNode, Point2D> newNodeLocations = 
        filterMap(nodes, source.nodeLocations);

    Map<GraphNode, NodeDisplayProperty> newNodeProperties =
        filterMap(nodes, source.nodeProperties);

    Map<GraphEdge, EdgeDisplayProperty> newEdgeProperties =
        Maps.newHashMap();
    Set<Relation> newRelations = Sets.newHashSet();
    for (Entry<GraphEdge, EdgeDisplayProperty> entry : 
        source.edgeProperties.entrySet()) {
      GraphEdge edge = entry.getKey();
      if (nodes.contains(edge.getHead()) && nodes.contains(edge.getTail())) {
        newEdgeProperties.put(edge, entry.getValue());
        newRelations.add(edge.getRelation());
      }
    }

    Map<Relation, EdgeDisplayProperty> newRelationProps = Maps.newHashMap();
    for (Relation relation : newRelations) {
      EdgeDisplayProperty edgeProp = source.getRelationProperty(relation);
      if (null != edgeProp) {
        newRelationProps.put(relation, new EdgeDisplayProperty(edgeProp));
      }
    }

    Collection<GraphNode> newSelectedNodes = Lists.newArrayList();
    for (GraphNode node : source.selectedNodes) {
      if (nodes.contains(node)) {
        newSelectedNodes.add(node);
      }
    }

    OptionPreference newOptions = OptionPreferences.getDefaultOptions();
    String descrp = source.options.getOption(
        OptionPreferences.OPTION_DESCRIPTION, "");
    String newDescription = (descrp.isEmpty()) ? "" : "Derived from " + descrp;
    newOptions.setOption(OptionPreferences.OPTION_DESCRIPTION, newDescription);

    ViewPreferences result = new ViewPreferences(
        ScenePreferences.getDefaultScenePrefs(),
        newNodeLocations, newNodeProperties,
        source.visibleRelationSet, newEdgeProperties, newRelationProps,
        newSelectedNodes, newOptions, new Collapser(),
        source.getTreeDescriptors());

    return result;
  }

  private static <K, V> Map<K, V> filterMap(
      Collection<K> filter, Map<K, V> source) {
    Map<K, V> result = Maps.newHashMap();
    for (Entry<K, V> entry : source.entrySet()) {
      if (filter.contains(entry.getKey())) {
        result.put(entry.getKey(), entry.getValue());
      }
    }
    return result;
  }

  /////////////////////////////////////
  // Simple accessors (event-less)

  public ScenePreferences getScenePrefs() {
    return scenePrefs;
  }

  public void setScenePrefs(ScenePreferences prefs) {
    scenePrefs = prefs;
  }

  public Map<GraphNode, Point2D> getNodeLocations() {
    return nodeLocations;
  }

  /**
   * Set new locations for all nodes.  Nodes not included in the map will
   * move to the origin (0.0, 0.0).
   *
   * @param newLocations
   */
  public void setNodeLocations(final Map<GraphNode, Point2D> newLocations) {
    nodeLocations = Maps.newHashMap(newLocations);

    listeners.fireEvent(new SimpleDispatcher() {
      @Override
      public void dispatch(ViewPrefsListener listener) {
        listener.nodeLocationsSet(newLocations);
      }
    });
  }

  /**
   * Edit the locations for nodes in the {@code newLocations} map.
   * Nodes not included in the map will be left at their current location.
   * 
   * @param newLocations 
   * @param author
   */
  public void editNodeLocations(
      final Map<GraphNode, Point2D> newLocations, final Object author) {
    nodeLocations.putAll(newLocations);

    listeners.fireEvent(new SimpleDispatcher() {
      @Override
      public void dispatch(ViewPrefsListener listener) {
        listener.nodeLocationsChanged(newLocations, author);
      }
    });
  }

  public GraphEdgeMatcherDescriptor getLayoutFinder() {
    return layoutEdgeMatcher;
  }

  public void setLayoutFinder(GraphEdgeMatcherDescriptor layoutEdgeMatcher) {
    this.layoutEdgeMatcher = layoutEdgeMatcher;
  }

  public String getSelectedLayout() {
    return selectedLayout;
  }

  public void setSelectedLayout(String layoutName) {
    selectedLayout = layoutName;
  }

  /////////////////////////////////////
  // Manipulate the nodes

  public NodeDisplayProperty getNodeProperty(GraphNode node) {
    return nodeProperties.get(node);
  }

  public void setNodeProperty(
      final GraphNode node, final NodeDisplayProperty newProperty) {
    nodeProperties.put(node, newProperty);

    listeners.fireEvent(new SimpleDispatcher() {
      @Override
      public void dispatch(ViewPrefsListener listener) {
        listener.nodePropertyChanged(node, newProperty);
      }
    });
  }

  public NodeTreeProvider<NodeDisplayProperty>
      getNodeDisplayPropertyProvider() {
    return new NodeDisplayPropertyProvider();
  }

  private class NodeDisplayPropertyProvider
      implements NodeTreeProvider<NodeDisplayProperty> {

    @Override
    // TODO(leeca): consolidate with ViewEditor.getNodeProperty()
    public NodeDisplayProperty getObject(GraphNode node) {
      NodeDisplayProperty result = getNodeProperty(node);
      if (null != result) {
        return result;
      }
      return new NodeDisplayProperty();
    }
  }

  /////////////////////////////////////
  // Manipulate the selected nodes

  public Collection<GraphNode> getSelectedNodes() {
    return selectedNodes;
  }

  public void setSelectedNodes(
      Collection<GraphNode> newSelection, final Object author) {
    final Collection<GraphNode> previous = selectedNodes;

    // Make a defensive copy
    selectedNodes = Lists.newArrayList(newSelection);

    listeners.fireEvent(new SimpleDispatcher() {
      @Override
      public void dispatch(ViewPrefsListener listener) {
        listener.selectionChanged(previous, selectedNodes, author);
      }
    });
  }

  public void editSelectedNodes(
      Collection<GraphNode> removeNodes, Collection<GraphNode> addNodes,
      final Object author) {
    final Collection<GraphNode> previous = selectedNodes;

    Collection<GraphNode> working = Lists.newArrayList(selectedNodes);
    working.removeAll(removeNodes);
    working.addAll(addNodes);

    selectedNodes = working;

    listeners.fireEvent(new SimpleDispatcher() {
      @Override
      public void dispatch(ViewPrefsListener listener) {
        listener.selectionChanged(previous, selectedNodes, author);
      }
    });
  }

  /////////////////////////////////////
  // Manipulate the edges

  public EdgeDisplayProperty getEdgeProperty(GraphEdge edge) {
    return edgeProperties.get(edge);
  }

  public void setEdgeProperty(
      final GraphEdge edge, final EdgeDisplayProperty newProperty) {
    edgeProperties.put(edge, newProperty);

    listeners.fireEvent(new SimpleDispatcher() {
      @Override
      public void dispatch(ViewPrefsListener listener) {
        listener.edgePropertyChanged(edge, newProperty);
      }
    });
  }

  public RelationSet getVisibleRelationSet() {
    return visibleRelationSet;
  }

  public void setVisibleRelationSet(RelationSet relationSet) {
    visibleRelationSet = relationSet;

    listeners.fireEvent(new SimpleDispatcher() {
      @Override
      public void dispatch(ViewPrefsListener listener) {
        listener.relationSetVisibleChanged(visibleRelationSet);
      }
    });
  }

  public boolean isVisibleRelation(Relation relation) {
    return visibleRelationSet.contains(relation);
  }

  public void setVisibleRelation(
      final Relation relation, final boolean isVisible) {
    boolean nowVisible = isVisibleRelation(relation);
    if (nowVisible == isVisible) {
      return;
    }

    Collection<Relation> visibleRelations = getVisibleRelations();
    if (isVisible) {
      visibleRelations.add(relation);
      visibleRelationSet = RelationSets.createSimple(visibleRelations);

      listeners.fireEvent(new SimpleDispatcher() {
        @Override
        public void dispatch(ViewPrefsListener listener) {
          listener.relationVisibleChanged(relation, isVisible);
        }
      });
    } else {
      visibleRelations.remove(relation);
      visibleRelationSet = RelationSets.createSimple(visibleRelations);

      listeners.fireEvent(new SimpleDispatcher() {
        @Override
        public void dispatch(ViewPrefsListener listener) {
          listener.relationVisibleChanged(relation, isVisible);
        }
      });
    }
  }

  private Collection<Relation> getVisibleRelations() {
    return RelationSets.filterRelations(
        visibleRelationSet, getDisplayRelations());
  }

  public Collection<Relation> getDisplayRelations() {
    // TODO: Should be based on included Relation plugins from
    // GraphDoc reference
    return RelationRegistry.getRegistryRelations();
  }

  public EdgeDisplayProperty getRelationProperty(Relation relation) {
    return relationProperties.get(relation);
  }

  public void setRelationProperty(
      final Relation relation, final EdgeDisplayProperty newProperty) {
    relationProperties.put(relation, newProperty);

    listeners.fireEvent(new SimpleDispatcher() {
      @Override
      public void dispatch(ViewPrefsListener listener) {
        listener.relationPropertyChanged(relation, newProperty);
      }
    });
  }

  public RelationSetDescriptor getDisplayRelationSet() {
    return edgeDisplayRelationSet;
  }

  public void setDisplayRelationSet(
      RelationSetDescriptor edgeDisplayRelationSetDescriptor) {
    this.edgeDisplayRelationSet = edgeDisplayRelationSetDescriptor;
  }

  /////////////////////////////////////
  // Manipulate the option

  public String getOption(String optionId) {
    return options.getOption(optionId);
  }

  public void setOption(final String optionId, final String value) {
    options.setOption(optionId, value);

    listeners.fireEvent(new SimpleDispatcher() {
      @Override
     public void dispatch(ViewPrefsListener listener) {
        listener.optionChanged(optionId, value);
      }
    });
  }

  /////////////////////////////////////
  // Manipulate the description

  public String getDescription() {
    return getOption(OptionPreferences.OPTION_DESCRIPTION);
  }

  public void setDescription(String newDescription) {
    setOption(OptionPreferences.OPTION_DESCRIPTION, newDescription);
  }

  /////////////////////////////////////
  // Manipulate the compacted node view state

  public List<GraphEdgeMatcherDescriptor> getTreeDescriptors() {
    return ImmutableList.copyOf(treeDescriptors);
  }

  public void addNodeTreeHierarchy(GraphEdgeMatcherDescriptor matcher) {
    treeDescriptors.add(matcher);

    listeners.fireEvent(new SimpleDispatcher() {
      @Override
     public void dispatch(ViewPrefsListener listener) {
        listener.nodeTreeChanged();
      }
    });
  }

  public void removeNodeTreeHierarchy(GraphEdgeMatcherDescriptor matcher) {
    treeDescriptors.remove(matcher);

    listeners.fireEvent(new SimpleDispatcher() {
      @Override
     public void dispatch(ViewPrefsListener listener) {
        listener.nodeTreeChanged();
      }
    });
  }

  public Collapser getCollapser() {
    return collapser;
  }

  public void collapseTree(GraphModel viewGraph, TreeModel treeModel) {
    final Collection<CollapseData> delta =
        collapser.collapseTree(viewGraph, treeModel);

    listeners.fireEvent(new SimpleDispatcher() {
      @Override
      public void dispatch(ViewPrefsListener listener) {
        listener.collapseChanged(
            delta, Collections.<CollapseData> emptyList(), null);
      }
    });
  }

  public void collapseNodeList(
      GraphNode master, Collection<GraphNode> children) {
    CollapseData data = collapser.collapse(master, children, false);

    final List<CollapseData> delta = Collections.singletonList(data);
    listeners.fireEvent(new SimpleDispatcher() {
      @Override
      public void dispatch(ViewPrefsListener listener) {
        listener.collapseChanged(
            delta, Collections.<CollapseData> emptyList(), null);
      }
    });
  }

  public void uncollapseMasterNode(GraphNode master) {
    CollapseData data = collapser.getCollapseData(master);
    collapser.uncollapse(master);

    final List<CollapseData> delta = Collections.singletonList(data);
    listeners.fireEvent(new SimpleDispatcher() {
      @Override
      public void dispatch(ViewPrefsListener listener) {
        listener.collapseChanged(
            Collections.<CollapseData> emptyList(), delta, null);
      }
    });
  }
}
