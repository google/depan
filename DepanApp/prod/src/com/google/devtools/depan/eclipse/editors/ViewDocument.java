/*
 * Copyright 2009 Google Inc.
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

package com.google.devtools.depan.eclipse.editors;

import com.google.devtools.depan.eclipse.plugins.SourcePlugin;
import com.google.devtools.depan.eclipse.trees.NodeTreeProvider;
import com.google.devtools.depan.graph.api.DirectedRelationFinder;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.RelationshipSet;
import com.google.devtools.depan.model.interfaces.GraphBuilder;
import com.google.devtools.depan.view.EdgeDisplayProperty;
import com.google.devtools.depan.view.NodeDisplayProperty;

import org.eclipse.core.resources.IResource;

import com.google.common.collect.ImmutableList;

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

  private GraphModelReference parentGraph;

  private Collection<GraphNode> viewNodes;

  private ViewPreferences userPrefs;

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

  public RelationshipSet getDefaultContainerRelSet() {
    return parentGraph.getGraph().getDefaultAnalysis().getDefaultRelationshipSet();
  }

  public List<SourcePlugin> getBuiltinAnalysisPlugins() {
    return parentGraph.getGraph().getAnalyzers();
  }

  public Collection<RelationshipSet> getBuiltinAnalysisRelSets() {
    return parentGraph.getGraph().getBuiltinAnalysisRelSets();
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

  public EdgeDisplayProperty getEdgeProperty(GraphEdge edge) {
    return userPrefs.getEdgeProperty(edge);
  }

  public void setEdgeProperty(
      GraphEdge edge, EdgeDisplayProperty newProperty) {
    userPrefs.setEdgeProperty(edge, newProperty);
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

  public DirectedRelationFinder getLayoutFinder() {
    return userPrefs.getLayoutFinder();
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

  /////////////////////////////////////
  // Factories for derived instances

  public GraphModel buildGraphView() {
    GraphModel parent = parentGraph.getGraph().getGraph();
    GraphModel result = parent.newView();

    // add the nodes
    GraphBuilder builder = result.getBuilder();
    for (GraphNode node : viewNodes) {
      builder.newNode(node);
    }

    parent.populateRelations(result);
    return result;
  }

  public ViewDocument newViewDocument(Collection<GraphNode> nodes) {
    ImmutableList<GraphNode> newView = ImmutableList.copyOf(nodes);
    ViewPreferences newPrefs =
        ViewPreferences.buildFilteredNodes(userPrefs, newView);
    return new ViewDocument(parentGraph, newView, newPrefs);
  }

  /////////////////////////////////////
  // Manage collapse state

  public GraphModel buildExposedGraph(GraphModel graph) {
    return userPrefs.getExposedGraph(graph);
  }

  public void autoCollapse(
      GraphModel graph, DirectedRelationFinder finder, Object author) {
    userPrefs.autoCollapse(graph, finder, author);
  }

  public void collapse(
      GraphNode master, Collection<GraphNode> picked,
      boolean erase, Object author) {
    userPrefs.collapse(master, picked, erase, author);
  }

  public void uncollapse(
      GraphNode master, boolean deleteGroup, Object author) {
    userPrefs.uncollapse(master, deleteGroup, author);
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
