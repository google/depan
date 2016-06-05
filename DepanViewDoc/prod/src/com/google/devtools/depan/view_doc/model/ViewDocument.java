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

import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeTreeProvider;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.graph_doc.eclipse.ui.resources.GraphResources;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.api.GraphBuilders;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;

import com.google.common.collect.ImmutableList;

import org.eclipse.core.resources.IResource;

import java.awt.geom.Point2D;
import java.util.Collection;
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

  public GraphResources getGraphResources() {
    // TODO: Lookup known resources
    // and save reference as transient member.
    return new GraphResources(parentGraph.getGraph().getDependencyModel());
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

  public RelationSet getVisibleRelationSet() {
    return userPrefs.getVisibleRelationSet();
  }

  public void setVisibleRelationSet(RelationSet relationSet) {
    userPrefs.setVisibleRelationSet(relationSet);
  }

  public boolean isVisibleRelation(Relation relation) {
    return userPrefs.isVisibleRelation(relation);
  }

  public void setVisibleRelation(Relation relation, boolean isVisible) {
    userPrefs.setVisibleRelation(relation, isVisible);
  }

  public EdgeDisplayProperty getEdgeProperty(GraphEdge edge) {
    return userPrefs.getEdgeProperty(edge);
  }

  public void setEdgeProperty(
      GraphEdge edge, EdgeDisplayProperty newProperty) {
    userPrefs.setEdgeProperty(edge, newProperty);
  }

  public Collection<Relation> getDisplayRelations() {
    return userPrefs.getDisplayRelations();
  }

  public EdgeDisplayProperty getRelationProperty(Relation relation) {
    return userPrefs.getRelationProperty(relation);
  }

  public void setRelationProperty(
      Relation relation, EdgeDisplayProperty edgeProp) {
    userPrefs.setRelationProperty(relation, edgeProp);
  }

  public RelationSetDescriptor getDisplayRelationSetDescriptor() {
    return userPrefs.getDisplayRelationSet();
  }

  public void setDisplayRelationSetDescriptor(RelationSetDescriptor newDisplay) {
    userPrefs.setDisplayRelationSet(newDisplay);
  }

  public void setSelectedLayout(String layoutName) {
    userPrefs.setSelectedLayout(layoutName);
  }

  public String getSelectedLayout() {
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

  public GraphEdgeMatcherDescriptor getLayoutFinder() {
    return userPrefs.getLayoutFinder();
  }

  public void setLayoutEdgeMatcher(GraphEdgeMatcherDescriptor layoutEdgeMatcher) {
    userPrefs.setLayoutFinder(layoutEdgeMatcher);
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
    return userPrefs.getDescription();
  }

  public void setOption(String optionId, String value) {
    userPrefs.setOption(optionId, value);
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
  // Manage scene preference

  public ScenePreferences getScenePrefs() {
    return userPrefs.getScenePrefs();
  }

  public void setScenePrefs(ScenePreferences prefs) {
    userPrefs.setScenePrefs(prefs);
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
