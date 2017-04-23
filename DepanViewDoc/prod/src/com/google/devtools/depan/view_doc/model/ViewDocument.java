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

import com.google.devtools.depan.collapse.model.Collapser;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeTreeProvider;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph_doc.eclipse.ui.resources.GraphResourceBuilder;
import com.google.devtools.depan.graph_doc.eclipse.ui.resources.GraphResources;
import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.graph_doc.model.GraphModelReference;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.api.GraphBuilders;
import com.google.devtools.depan.nodes.trees.TreeModel;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.resources.PropertyDocumentReference;
import com.google.devtools.depan.view_doc.eclipse.ui.plugins.ViewExtension;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlan;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlanDocument;

import com.google.common.collect.ImmutableList;

import org.eclipse.core.resources.IResource;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A document that provides all properties about a view.  The ViewEditor
 * references an instance of this, and these can be saved and restored from
 * the file system.
 * <p>
 * Information derived from this (actual hierarchies, sub graphs, etc.
 * should be build, maintained, and used internally to the various editors
 * and views.
 *
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class ViewDocument {
  /**
   * Standard extension to use when loading or saving {@code ViewDocument}s.
   * The characters represent "DepAn View Info".
   */
  public static final String EXTENSION = "dvi";

  private final GraphModelReference parentGraph;

  private final Collection<GraphNode> viewNodes;

  private final ViewPreferences userPrefs;

  public ViewDocument(
      GraphModelReference parentGraph,
      Collection<GraphNode> viewNodes,
      ViewPreferences userPrefs) {
    this.parentGraph = parentGraph;
    this.viewNodes = viewNodes;
    this.userPrefs = userPrefs;
  }

  /////////////////////////////////////
  // Internal components available for serialization

  public class Components {
    public GraphModelReference getParentGraph() {
      return parentGraph;
    }

    public Collection<GraphNode> getViewNodes() {
      return viewNodes;
    }

    public ViewPreferences getUserPrefs() {
      return userPrefs;
    }
  }

  public Components getComponents() {
    return new Components();
  }

  /////////////////////////////////////
  // Simple accessors (direct, delegating, and wrappers)

  public GraphModel getParentGraph() {
    return parentGraph.getGraph().getGraph();
  }

  public IResource getGraphModelLocation() {
    return parentGraph.getLocation();
  }

  public DependencyModel getDependencyModel() {
    return parentGraph.getGraph().getDependencyModel();
  }
  
  /**
   * The result should be retained by the controlling editor, since the
   * value is not retained or saved.
   */
  public GraphResources buildGraphResources() {
    return GraphResourceBuilder.forModel(getDependencyModel());
  }

  public Collection<GraphNode> getViewNodes() {
    return viewNodes;
  }

  public Map<GraphNode, Point2D> getNodeLocations() {
    return userPrefs.getNodeLocations();
  }

  public NodeDisplayProperty getNodeProperty(GraphNode node) {
    return userPrefs.getNodeProperty(node);
  }

  public void editNodeLocations(
      Map<GraphNode, Point2D> changes, Object author) {
    userPrefs.editNodeLocations(changes, author);
  }

  public void setNodeLocations(Map<GraphNode, Point2D> nodeLocations) {
    userPrefs.setNodeLocations(nodeLocations);
  }

  public void setNodeProperty(
      GraphNode node, NodeDisplayProperty newProperty) {
    userPrefs.setNodeProperty(node, newProperty);
  }

  public PropertyDocumentReference<RelationSetDescriptor>
      getVisibleRelationSet() {
    return userPrefs.getVisibleRelationSet();
  }

  public void setVisibleRelationSet(
      PropertyDocumentReference<RelationSetDescriptor> visRelSet) {
    userPrefs.setVisibleRelationSet(visRelSet);
  }

  public boolean isVisibleRelation(Relation relation) {
    return userPrefs.isVisibleRelation(relation);
  }

  public EdgeDisplayProperty getEdgeProperty(GraphEdge edge) {
    return userPrefs.getEdgeProperty(edge);
  }

  public void setEdgeProperty(
      GraphEdge edge, EdgeDisplayProperty newProperty) {
    userPrefs.setEdgeProperty(edge, newProperty);
  }

  public EdgeDisplayProperty getRelationProperty(Relation relation) {
    return userPrefs.getRelationProperty(relation);
  }

  public void setRelationProperty(
      Relation relation, EdgeDisplayProperty edgeProp) {
    userPrefs.setRelationProperty(relation, edgeProp);
  }

  public void setSelectedLayout(
      PropertyDocumentReference<LayoutPlanDocument<? extends LayoutPlan>>
          layoutName) {
    userPrefs.setSelectedLayout(layoutName);
  }

  public PropertyDocumentReference<LayoutPlanDocument<? extends LayoutPlan>>
      getSelectedLayout() {
    return userPrefs.getSelectedLayout();
  }

  public Collection<GraphNode> getSelectedNodes() {
    return userPrefs.getSelectedNodes();
  }

  public void setSelectedNodes(
      Collection<GraphNode> newSelection, Object author) {
    userPrefs.setSelectedNodes(newSelection, author);
  }

  public void editSelectedNodes(
      Collection<GraphNode> removeNodes, Collection<GraphNode> addNodes,
      Object author) {
    userPrefs.editSelectedNodes(removeNodes, addNodes, author);
  }

  public PropertyDocumentReference<GraphEdgeMatcherDescriptor>
      getLayoutMatcherRef() {
    return userPrefs.getLayoutMatcherRef();
  }

  public void setLayoutEdgeMatcher(
      PropertyDocumentReference<GraphEdgeMatcherDescriptor> matcherRef) {
    userPrefs.setLayoutMatcherRef(matcherRef);
  }

  public NodeTreeProvider<NodeDisplayProperty>
      getNodeDisplayPropertyProvider() {
    return userPrefs.getNodeDisplayPropertyProvider();
  }

  public String getDescription() {
    return userPrefs.getDescription();
  }

  public void setDescription(String newDescription) {
    userPrefs.setDescription(newDescription);
  }

  public String getOption(String optionId) {
    return userPrefs.getOption(optionId);
  }

  public void setOption(String optionId, String value) {
    userPrefs.setOption(optionId, value);
  }

  /////////////////////////////////////
  // Extension Data API

  public ExtensionData getExtensionData(ViewExtension extension) {
    return userPrefs.getExtensionData(extension);
  }

  public ExtensionData getExtensionData(
      ViewExtension extension, Object instance) {
    return userPrefs.getExtensionData(extension, instance);
  }

  public void setExtensionData(
      ViewExtension ext, Object instance, ExtensionData data) {
    userPrefs.setExtensionData(ext, instance, data);
  }

  public void setExtensionData(
      ViewExtension ext, Object instance, ExtensionData data,
      Object propId, Object updates) {
    userPrefs.setExtensionData(ext, instance, data, propId, updates);
  }

  public void addExtensionDataListener(ExtensionDataListener listener) {
    userPrefs.addExtensionDataListener(listener);
  }

  public void removeExtensionDataListener(ExtensionDataListener listener) {
    userPrefs.removeExtensionDataListener(listener);
  }

  /////////////////////////////////////
  // Manage scene preference

  public ScenePreferences getScenePrefs() {
    return userPrefs.getScenePrefs();
  }

  public void setScenePrefs(ScenePreferences prefs) {
    userPrefs.setScenePrefs(prefs);
  }

  /////////////////////////////////////
  // Node view compression

  public List<GraphEdgeMatcherDescriptor> getTreeDescriptors() {
    return userPrefs.getTreeDescriptors();
  }

  public void addNodeTreeHierarchy(GraphEdgeMatcherDescriptor matcher) {
    userPrefs.addNodeTreeHierarchy(matcher);
  }

  public void removeNodeTreeHierarchy(GraphEdgeMatcherDescriptor matcher) {
    userPrefs.removeNodeTreeHierarchy(matcher);
  }

  public Collapser getCollapser() {
    return userPrefs.getCollapser();
  }

  public void collapseTree(GraphModel viewGraph, TreeModel treeModel) {
    userPrefs.collapseTree(viewGraph, treeModel);
  }

  public void collapseNodeList(
      GraphNode master, Collection<GraphNode> children) {
    userPrefs.collapseNodeList(master, children);
  }

  public void uncollapseMasterNode(GraphNode master) {
    userPrefs.uncollapseMasterNode(master);
  }

  /////////////////////////////////////
  // Factories for derived instances

  /**
   * Note that the returned GraphModel shares edges and nodes with the parent
   * graph.  These are immutable, so it shouldn't be too dangerous.
   * Normally, derived graphs have their own copies of the nodes and edges.
   */
  public GraphModel buildGraphView() {

    GraphModel master = parentGraph.getGraph().getGraph();
    return GraphBuilders.buildFromNodes(master, viewNodes);
  }

  public ViewDocument newViewDocument(Collection<GraphNode> nodes) {
    ImmutableList<GraphNode> newView = ImmutableList.copyOf(nodes);
    ViewPreferences newPrefs =
        ViewPreferences.buildFilteredNodes(userPrefs, newView);
    return new ViewDocument(parentGraph, newView, newPrefs);
  }

  /////////////////////////////////////
  // Manage change listeners

  public void addPrefsListener(ViewPrefsListener listener) {
    userPrefs.addListener(listener);
  }

  public void removePrefsListener(ViewPrefsListener listener) {
    userPrefs.removeListener(listener);
  }
}
