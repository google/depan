/*
 * Copyright 2014 Pnambic Computing
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

import com.google.devtools.depan.model.GraphNode;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Set;

/**
 * Use collapser start to represent a hierarchical tree of GraphNodes.
 * 
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
public class CollapseTreeModel implements TreeModel {

  /**
   * Index of interior nodes to their successors.
   */
  private final Collapser collapser;

  public CollapseTreeModel(Collapser collapser) {
    this.collapser = collapser;
  }

  @Override
  public SuccessorEdges getSuccessors(GraphNode node) {
    // Provide suitable factory when necessary.
    return SuccessorEdges.EMPTY;
  }

  @Override
  public Collection<GraphNode> getSuccessorNodes(GraphNode node) {

    CollapseData data = collapser.getCollapseData(node);
    if (null == data) {
      return GraphNode.EMPTY_NODE_LIST;
    }

    int size = data.getChildrenNodes().size();
    Collection<GraphNode> result =
        Lists.newArrayListWithExpectedSize(size);
    result.addAll(getDirectChildren(node, data));

    return result;
  }

  private Collection<GraphNode> getDirectChildren(
      GraphNode node, CollapseData data) {
    Collection<GraphNode> childrenNodes = data.getChildrenNodes();
    Collection<GraphNode> result =
        Lists.newArrayListWithCapacity(childrenNodes.size());
    for (GraphNode child : childrenNodes) {
      // Oct-14: Child list currently includes master,
      // same as source of collapse data (node),
      // but that leads to recursive trees
      if (node != child) {
        result.add(child);
      }
    }
    return result;
  }

  @Override
  public Collection<GraphNode> computeRoots() {
    return collapser.getMasterNodeSet();
  }

  /**
   * Provide the number of interior nodes in this tree.
   *
   * @return number of nodes in this tree
   */
  @Override
  public int countInteriorNodes() {
    int result = 0;

    for (GraphNode root: computeRoots()) {
      result += countTree(root);
    }

    return result;
  }

  private int countTree(GraphNode tree) {
    CollapseData data = collapser.getCollapseData(tree);
    int result = 0;
    for (CollapseData desc : data.getChildrenCollapse()) {
      result += countTree(desc.getMasterNode());
    }
    return result; 
  }


  @Override
  public Set<GraphNode> computeTreeNodes() {
    Set<GraphNode> result = Sets.newHashSet();

    for (GraphNode root: computeRoots()) {
      CollapseData data = collapser.getCollapseData(root);
      data.addMemberNodes(result);
    }

    return result;
  }

  /**
   * Provide the number of nodes in this tree.
   *
   * @return number of nodes in this tree
   */
  @Override
  public int countTreeNodes() {
    return computeTreeNodes().size();
  }
}
