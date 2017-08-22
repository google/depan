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
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.nodes.trees.TreeModel;
import com.google.devtools.depan.platform.ListenerManager;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.persistence.RelationSetResources;
import com.google.devtools.depan.resources.PropertyDocumentReference;
import com.google.devtools.depan.view_doc.eclipse.ViewDocLogger;
import com.google.devtools.depan.view_doc.eclipse.ui.plugins.ViewExtension;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlan;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlanDocument;

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

/**
 * Persistent user preferences for presentation of a graph view.  This is the
 * sole means to save and load the graph's presentation, and all preference
 * setting must be member fields of this class.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class ViewPreferences {

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
  private PropertyDocumentReference<RelationSetDescriptor> visibleRelationSet;

  /**
   * Hash map that contains a list of edge display property objects
   * for known relations.
   */
  private Map<Relation, EdgeDisplayProperty> relationProperties;

  private Collection<GraphNode> selectedNodes = ImmutableList.of();

  private PropertyDocumentReference<LayoutPlanDocument<? extends LayoutPlan>>
      selectedLayout;

  // private String selectedLayout;

  private ScenePreferences scenePrefs;

  private OptionPreference options;

  /**
   * Edge matcher used by layout algorithms by default if it wasn't
   * specified.
   */
  private PropertyDocumentReference<GraphEdgeMatcherDescriptor>
      layoutEdgeMatcher;

  private List<GraphEdgeMatcherDescriptor> treeDescriptors;

  /**
   * Manager object for handling all collapsed nodes.
   */
  private Collapser collapser;

  /**
   * Extension data is stored as a list, but retrieved via hash
   * tables when the document is opened.
   */
  private List<ExtensionData> extensionData;

  /**
   * Provide efficient lookup for {@link ExtensionData}.
   * Initialization moved to an explicit public method since XStream
   * unmarshalling does not set this.
   */
  private transient Map<ViewExtension, Map<Object, ExtensionData>>
      extDataByView;

  /**
   * Manage objects that are interested in preference changes.
   * Initialization moved to an explicit public method since XStream
   * unmarshalling does not set this.
   */
  private transient ListenerManager<ExtensionDataListener> extListeners;

  private abstract static class SimpleDataDispatcher
      implements ListenerManager.Dispatcher<ExtensionDataListener> {

    @Override
    public void captureException(RuntimeException errAny) {
      ViewDocLogger.LOG.warn(errAny.toString());
    }
  }

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
      ViewDocLogger.LOG.warn(errAny.toString());
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
        RelationSetResources.ALL_REF,
        Maps.<GraphEdge, EdgeDisplayProperty>newHashMap(),
        Maps.<Relation, EdgeDisplayProperty>newHashMap(),
        ImmutableList.<GraphNode>of(),
        OptionPreferences.getDefaultOptions(),
        new Collapser(),
        Lists.<GraphEdgeMatcherDescriptor>newArrayList(),
        Lists.<ExtensionData>newArrayList());
  }

  public ViewPreferences(
      ScenePreferences gripPrefs,
      Map<GraphNode, Point2D> newNodeLocations,
      Map<GraphNode, NodeDisplayProperty> newNodeProperties,
      PropertyDocumentReference<RelationSetDescriptor> visibleRelationSet,
      Map<GraphEdge, EdgeDisplayProperty> newEdgeProperties,
      Map<Relation, EdgeDisplayProperty> newRelationProperties,
      Collection<GraphNode> newSelectedNodes,
      OptionPreference options,
      Collapser collapser,
      List<GraphEdgeMatcherDescriptor> treeDescriptors,
      List<ExtensionData> extensionData) {
    this.scenePrefs = gripPrefs;
    this.nodeLocations = newNodeLocations;
    this.nodeProperties = newNodeProperties;
    this.visibleRelationSet = visibleRelationSet;
    this.edgeProperties = newEdgeProperties;
    this.relationProperties = newRelationProperties;
    this.selectedNodes = newSelectedNodes;
    this.options = options;
    this.collapser = collapser;
    this.treeDescriptors = treeDescriptors;
    this.extensionData = extensionData;

    // Initialize transients after normal values are configured.
    initTransients();
  }

  /**
   * Populate any required fields after an unmarshall(), since that process
   * by-passes the constructors.  This method should execute before any other
   * operations (e.g. {@link #initTransients()} to ensure the object is
   * semantically complete.
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
      visibleRelationSet = RelationSetResources.ALL_REF;
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
    if (null == extensionData) {
      extensionData = Lists.newArrayList();
    }
  }

  /**
   * Initialize transient fields.  This is used directly in the XStream
   * unmarshalling converter, since none of the constructors are actually
   * invoked.  This method should run after {@link #afterUnmarshall()}, since
   * that methods ensures a complete object state.
   */
  public void initTransients() {
    listeners = new ListenerManager<ViewPrefsListener>();

    // Extension-data transient fields
    extDataByView = buildExtensionLookup(extensionData);
    extListeners = new ListenerManager<ExtensionDataListener>();
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

    List<GraphEdgeMatcherDescriptor> mutableTreeDescriptors =
        Lists.newArrayList(source.getTreeDescriptors());

    ViewPreferences result = new ViewPreferences(
        ScenePreferences.getDefaultScenePrefs(),
        newNodeLocations, newNodeProperties,
        source.visibleRelationSet, newEdgeProperties, newRelationProps,
        newSelectedNodes, newOptions, new Collapser(),
        mutableTreeDescriptors, Lists.<ExtensionData>newArrayList());

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

  public PropertyDocumentReference<GraphEdgeMatcherDescriptor>
      getLayoutMatcherRef() {
    return layoutEdgeMatcher;
  }

  public void setLayoutMatcherRef(
      PropertyDocumentReference<GraphEdgeMatcherDescriptor> matcherRef) {
    this.layoutEdgeMatcher = matcherRef;
  }

  public PropertyDocumentReference<LayoutPlanDocument<? extends LayoutPlan>>
      getSelectedLayout() {
    return selectedLayout;
  }

  public void setSelectedLayout(
      PropertyDocumentReference<LayoutPlanDocument<? extends LayoutPlan>>
          selectedLayout) {
    this.selectedLayout = selectedLayout;
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

  public PropertyDocumentReference<RelationSetDescriptor>
      getVisibleRelationSet() {
    return visibleRelationSet;
  }

  public void setVisibleRelationSet(
      final PropertyDocumentReference<RelationSetDescriptor> visibleRelationSet) {
    this.visibleRelationSet = visibleRelationSet;

    listeners.fireEvent(new SimpleDispatcher() {
      @Override
      public void dispatch(ViewPrefsListener listener) {
        listener.relationSetVisibleChanged(visibleRelationSet);
      }
    });
  }

  public boolean isVisibleRelation(Relation relation) {
    return visibleRelationSet.getDocument().getInfo().contains(relation);
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

  /////////////////////////////////////
  // Manipulate the extension data

  public ExtensionData getExtensionData(
      ViewExtension ext, Object instance) {
    Map<Object, ExtensionData> result = extDataByView.get(ext);
    if (null == result) {
      return null;
    }
    return result.get(instance);
  }

  public ExtensionData getExtensionData(ViewExtension ext) {
    Map<Object, ExtensionData> result = extDataByView.get(ext);
    if (null == result) {
      return null;
    }
    int size = result.size();
    if (0 == size) {
      return null;
    }
    if (1 == size) {
      return result.values().iterator().next();
    }
    throw new IllegalStateException(
        "At most one extension data element allowed.  Found " + size + ".");
  }

  public void setExtensionData(
      ViewExtension ext, Object instance, ExtensionData data) {
    Map<Object, ExtensionData> insts = extDataByView.get(ext);
    if (null == insts) {
      insts = Maps.newHashMap();
      extDataByView.put(ext, insts);
    }
    insts.put(instance, data);
    updateData(ext, instance, data);

    fireExtensionDataChange(ext, instance, data);
  }

  public void setExtensionData(
      final ViewExtension ext,
      final Object instance,
      ExtensionData data,
      final Object propId,
      final Object updates) {
    Map<Object, ExtensionData> insts = extDataByView.get(ext);
    if (null == insts) {
      insts = Maps.newHashMap();
      extDataByView.put(ext, insts);
    }
    insts.put(instance, data);
    updateData(ext, instance, data);

    // Fire specific change event
    extListeners.fireEvent(new SimpleDataDispatcher() {
      @Override
      public void dispatch(ExtensionDataListener listener) {
        listener.extensionDataChanged(ext, instance, propId, updates);
      }
    });

    // Also fire generic data change event
    fireExtensionDataChange(ext, instance, data);
  }

  public void addExtensionDataListener(ExtensionDataListener listener) {
    extListeners.addListener(listener);
  }

  public void removeExtensionDataListener(ExtensionDataListener listener) {
    extListeners.removeListener(listener);
  }

  private void fireExtensionDataChange(
      final ViewExtension ext,
      final Object instance,
      final ExtensionData data) {
    extListeners.fireEvent(new SimpleDataDispatcher() {
      @Override
      public void dispatch(ExtensionDataListener listener) {
        listener.extensionDataChanged(ext, instance, null, data);
      }
    });
  }

  /**
   * Build a new extension map from the supplied list of extensions.
   */
  private Map<ViewExtension, Map<Object, ExtensionData>>
      buildExtensionLookup(List<ExtensionData> buildData) {
    Map<ViewExtension, Map<Object, ExtensionData>> result = Maps.newHashMap();
    for (ExtensionData data : buildData) {
      ViewExtension extension = data.getExtension();

      Map<Object, ExtensionData> insts = result.get(extension);
      if (null == insts) {
        insts = Maps.newHashMap();
        result.put(extension, insts);
      }
      insts.put(data.getInstance(), data);
    }
    return result;
  }

  private void updateData(
      ViewExtension ext, Object instance, ExtensionData data) {
    if (null != data) {
      saveData(ext, instance, data);
      return;
    }
    removeData(ext, instance);
  }

  private void saveData(
      ViewExtension ext, Object instance, ExtensionData data) {
    int item = findData(ext, instance);
    if (item >= 0) {
      extensionData.set(item, data);
      return;
    }
    extensionData.add(data);
  }

  private void removeData(ViewExtension ext, Object instance) {
    int item = findData(ext, instance);
    if (item >=0) {
      extensionData.remove(item);
    }
  }

  private int findData(ViewExtension ext, Object instance) {
    for (int index = 0; index < extensionData.size(); index++) {
      ExtensionData test = extensionData.get(index);
      if (ext != test.getExtension()) {
        return -1;
      }
      if (null == instance) {
        return (null == test.getInstance() ? index : -1);
      }
      if (instance.equals(test.getInstance())) {
        return index;
      }
    }
    return -1;
  }
}
