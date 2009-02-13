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

import com.google.common.collect.Maps;
import com.google.devtools.depan.eclipse.trees.NodeTreeView.NodeWrapper;
import com.google.devtools.depan.eclipse.trees.NodeTreeView.NodeWrapperRoot;
import com.google.devtools.depan.graph.api.DirectedRelationFinder;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view.TreeModel;

import org.eclipse.jface.viewers.TreePath;

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

  public static final TreePath[] EMPTY_PATHS =
    new TreePath[0];

  @SuppressWarnings("unchecked")
  private static final NodeTreeView.NodeWrapper[] LEAF_KIDS =
      new NodeTreeView.NodeWrapper[0];

  private final NodeTreeProvider<F> provider;

  /** Generated hierarchical view */
  private final TreeModel treeData;

  private NodeWrapperRoot<F> hierarchyRoots;

  private Map<GraphNode, NodeTreeView.NodeWrapper<F>>
      reverseMap = Maps.newHashMap();

  private TreePath[] expandState = EMPTY_PATHS;

  /**
   * Comprehensive constructor for GraphData.
   * 
   * @param graph table of relationship data
   * @param relationFinder relations to include in children
   * @param provider source for rendering information
   */
  public GraphData(
      NodeTreeProvider<F> provider, TreeModel treeData) {
    super();
    this.provider = provider;
    this.treeData = treeData;
    this.hierarchyRoots = null;
  }

  public static <F> GraphData<F> createGraphData(
      NodeTreeProvider<F> provider,
      GraphModel graph, DirectedRelationFinder relFinder) {
    TreeModel hierarchy = new TreeModel(graph.computeSpanningHierarchy(relFinder));
    return new GraphData<F>(provider, hierarchy);
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
   * @return the hierarchyRoots
   */
  public NodeWrapperRoot<F> getHierarchyRoots() {
    if (null == hierarchyRoots) {
      hierarchyRoots = computeRoots();
    }
    return hierarchyRoots;
  }

  /**
   * Compute the roots for the relationship for this graph.
   * This should only be called once - lazily, or by the constructor.
   * 
   * @return node wrapper with all roots
   */
  private NodeWrapperRoot<F> computeRoots() {
    Collection<GraphNode> roots = treeData.computeRoots();

    NodeWrapperRoot<F> wrapper = new NodeWrapperRoot<F>();
    wrapper.roots = buildNodeWrapperArray(roots);
    return wrapper;
  }

  /**
   * @param nodes
   * @return
   */
  private void buildChildWrapperArray(
      NodeWrapper<F> parent,
      Collection<GraphNode> nodes) {

    parent.childs = buildNodeWrapperArray(nodes);
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

  public TreePath[] getExpandState() {
    return expandState;
  }

  public void saveExpandState(TreePath[] expandState) {
    this.expandState = expandState;
  }
}
