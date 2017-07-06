/*
 * Copyright 2007 The Depan Project Authors
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

package com.google.devtools.depan.eclipse.ui.nodes.trees;

import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeTreeProvider;
import com.google.devtools.depan.graph.api.EdgeMatcher;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.nodes.trees.HierarchicalTreeModel;
import com.google.devtools.depan.nodes.trees.TreeModel;
import com.google.devtools.depan.nodes.trees.Trees;

import com.google.common.collect.Maps;

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

  /** Convert from {@link GraphNode} to supplied data type. */
  private final NodeTreeProvider<F> provider;

  /** Generated hierarchical view */
  private final TreeModel treeData;

  private Map<GraphNode, NodeWrapper<F>>
      reverseMap = Maps.newHashMap();

  private TreePath[] expandState = EMPTY_PATHS;

  // Cache the root objects, so the root objects are the same one.
  private Collection<GraphNode> rootNodes;
  private NodeWrapper<F>[] rootWrapper;
  private ViewerRoot hierarchyRoots;

  /**
   * Comprehensive constructor for GraphData.
   * 
   * @param graph table of relationship data
   * @param relationFinder relations to include in children
   * @param provider source for rendering information
   */
  public GraphData(
      NodeTreeProvider<F> provider, TreeModel treeData) {
    this.provider = provider;
    this.treeData = treeData;
    this.hierarchyRoots = null;
  }

  public static <F> GraphData<F> createGraphData(
      NodeTreeProvider<F> provider,
      GraphModel graph, EdgeMatcher<String> edgeMatcher) {
    TreeModel hierarchy = new HierarchicalTreeModel(
        Trees.computeSpanningHierarchy(graph, edgeMatcher));
    return new GraphData<F>(provider, hierarchy);
  }

  /**
   * Provide the children of a parent node.
   * 
   * @param parent Parent node for children
   * @return Array of children NodeWrappers.
   */
  public NodeWrapper<F>[] getChildren(NodeWrapper<F> parent) {
    Collection<GraphNode> childList =
        treeData.getSuccessorNodes(parent.getNode());

    return buildNodeWrapperArray(childList);
  }

  public TreeModel getTreeModel() {
    return treeData;
  }

  /**
   * @return the hierarchyRoots
   */
  public ViewerRoot getHierarchyRoots() {
    if (null == hierarchyRoots) {
      hierarchyRoots = new ViewerRoot(computeRootWrappers());
    }
    return hierarchyRoots;
  }

  public NodeWrapper<F>[] computeRootWrappers() {
    if (null == rootNodes) {
      rootNodes = treeData.computeRoots();
    }
    if (null == rootWrapper) {
      rootWrapper = buildNodeWrapperArray(rootNodes);
    }
    return rootWrapper;
  }

  private NodeWrapper<F>[] buildNodeWrapperArray(Collection<GraphNode> nodes) {
    NodeWrapper<F>[] result = NodeWrapper.buildNodeWrapperArray(
        nodes, null, this);
    for (NodeWrapper<F> item : result) {
      reverseMap.put(item.getNode(), item);
    }
    return result;
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

  public F getContent(GraphNode node) {
    return provider.getObject(node);
  }

  public TreePath[] getExpandState() {
    return expandState;
  }

  public void saveExpandState(TreePath[] expandState) {
    this.expandState = expandState;
  }

  /**
   * Provide the total number of nodes in this tree.
   *
   * @return number of nodes in this tree
   */
  public int countTreeNodes() {
    return treeData.countTreeNodes();
  }

  /**
   * Provide the number of interior nodes in this tree.
   *
   * @return number of nodes in this tree
   */
  public int countInteriorNodes() {
    return treeData.countInteriorNodes();
  }
}
