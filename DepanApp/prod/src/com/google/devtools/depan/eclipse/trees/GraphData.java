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

package com.google.devtools.depan.eclipse.trees;

import com.google.devtools.depan.collect.Maps;
import com.google.devtools.depan.eclipse.trees.NodeTreeView.NodeWrapper;
import com.google.devtools.depan.eclipse.trees.NodeTreeView.NodeWrapperRoot;
import com.google.devtools.depan.graph.api.DirectedRelationFinder;
import com.google.devtools.depan.graph.api.RelationFinder;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view.TreeModel;

import java.util.Collection;
import java.util.Map;

/**
 * Provide child and root information for a hierarchical set of relations.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 *
 * @param <F> Type for node wrapper objects
 */
public class GraphData<F> {

  private final NodeTreeProvider<F> provider;

  /** Source of dependency data */
  private GraphModel graph;

  /** Generated hierarchical view */
  private TreeModel treeData;

  /**
   * Stores all <code>reverseMap</code>s so that they can be reloaded whenever
   * necessary.
   */
  @SuppressWarnings("unchecked")
  private static Map<GraphModel, Map<GraphNode, NodeTreeView.NodeWrapper>>
      allReverseMaps = Maps.newHashMap();
  
  private Map<GraphNode, NodeTreeView.NodeWrapper<F>>
      reverseMap = Maps.newHashMap();

  @SuppressWarnings("unchecked")
  private static NodeTreeView.NodeWrapper[] LEAF_KIDS =
      new NodeTreeView.NodeWrapper[0];

  /**
   * Comprehensive constructor for GraphData.
   * 
   * @param graph table of relationship data
   * @param relationFinder relations to include in children
   * @param provider source for rendering information
   */
  public GraphData(
      GraphModel graph, RelationFinder relationFinder,
      NodeTreeProvider<F> provider) {
    super();
    this.provider = provider;

    initTreeData(graph, relationFinder);
  }

  /**
   * Provider only constructor for GraphData.
   * You will need to call
   * {@link #initTreeData(GraphModel, DirectedRelationFinder)} before requesting
   * children for any node.
   * 
   * @param provider source for rendering information
   */
  public GraphData(NodeTreeProvider<F> provider) {
    this.provider = provider;
    // Don't initialize the tree data
  }

  /**
   * Establish the relationship data and interesting relationships
   * for this hierarchical view of the graph.
   * 
   * @param parentGraph table of relationship data
   * @param initRelationFinder relations to include in children
   */
  @SuppressWarnings("unchecked")
  public void initTreeData(
      GraphModel parentGraph, DirectedRelationFinder initRelationFinder) {

    graph = parentGraph;
    // restore the reverseMap object associated with this GraphModel.
    Object obj = allReverseMaps.get(graph);
    if (obj != null) {
      reverseMap = (Map<GraphNode, NodeTreeView.NodeWrapper<F>>) obj;
    } else {
      // We must create it now, it will be filled later.
      reverseMap = Maps.newHashMap();
      allReverseMaps.put(graph, (Map<GraphNode, NodeTreeView.NodeWrapper>) reverseMap);
    }
    updateTreeData(initRelationFinder);
  }

  /**
   * Update the hierarchical view of the graph.
   * 
   * @param initRelationFinder relations to include in children
   */
  public void updateTreeData(DirectedRelationFinder initRelationFinder) {
    treeData = new TreeModel(
        graph.computeSpanningHierarchy(initRelationFinder));
  }

  /**
   * Provide the children of a parent node.
   * 
   * @param parent Parent node for children
   * @return Array of children NodeWrappers.
   */
  public NodeWrapper<F>[] getChildren(NodeTreeView.NodeWrapper<F> parent) {
    Collection<GraphNode> childList = treeData.getSuccessors(parent.getNode());

    buildChildWrapperArray(parent, childList);
    return parent.childs;
  }

  /**
   * Compute the roots for the relationship for this graph.
   * 
   * @return node wrapper with all roots
   */
  public NodeWrapperRoot<F> computeRoots() {
    Collection<GraphNode> roots = treeData.computeRoots();

    NodeWrapperRoot<F> wrapper = new NodeWrapperRoot<F>();
    wrapper.roots = buildNodeWrapperArray(roots);
    return wrapper;
  }

  /**
   * @param nodes
   * @return
   */
  @SuppressWarnings("unchecked")
  private void buildChildWrapperArray(
      NodeWrapper<F> parent,
      Collection<GraphNode> nodes) {

    // All empty children lists look the same,
    // so early exit with the singleton
    if (0 == nodes.size()) {
      parent.childs = LEAF_KIDS;
      return;
    }

    NodeWrapper<F>[] children = new NodeWrapper[nodes.size()];
    int index = 0;
    for (GraphNode node : nodes) {
      NodeWrapper<F> nodeWrapper = createNodeWrapper(node, parent);
      children[index] = nodeWrapper;
      reverseMap.put(node, children[index]);
      index++;
    }
    parent.childs = children;
  }

  /**
   * @param nodes
   * @return
   */
  @SuppressWarnings("unchecked")
  private NodeWrapper<F>[] buildNodeWrapperArray(Collection<GraphNode> nodes) {

    // All empty children lists look the same,
    // so early exit with the singleton
    if (0 == nodes.size()) {
      return LEAF_KIDS;
    }

    NodeWrapper<F>[] children = new NodeWrapper[nodes.size()];
    int index = 0;
    for (GraphNode node : nodes) {
      NodeWrapper<F> nodeWrapper = createNodeWrapper(node, null);
      children[index] = nodeWrapper;
      reverseMap.put(node, children[index]);
      index++;
    }
    return children;
  }

  /**
   * @param node
   * @return
   */
  private NodeWrapper<F> createNodeWrapper(
      GraphNode node, NodeWrapper<F> parent) {
    return new NodeWrapper<F>(
        node, provider.getObject(node), parent, this);
  }

  /**
   * Gives the NodeWrapper containing the given node. Useful for update methods
   * when we just have a node, but need the object actually contained in a
   * tree for example.
   * 
   * @param node the node
   * @return the NodeWrapper<F> containing the given node.
   */
  public NodeWrapper<F> getNodeWrapper(GraphNode node) {
    return reverseMap.get(node);
  }
}
