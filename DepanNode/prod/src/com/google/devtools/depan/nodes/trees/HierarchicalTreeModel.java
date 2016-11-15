/*
 * Copyright 2013 The Depan Project Authors
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
package com.google.devtools.depan.nodes.trees;

import com.google.devtools.depan.model.GraphNode;

import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Use SuccessorEdges to represent a hierarchical tree of GraphNodes.
 * 
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
public class HierarchicalTreeModel implements TreeModel {

  /**
   * Index of interior nodes to their successors.
   */
  private final Map<GraphNode, ? extends SuccessorEdges> index;

  /**
   * Create a TreeModel from a Map of SuccessorEdges.
   * 
   * @param index data to answer hierarchical queries
   */
  public HierarchicalTreeModel(Map<GraphNode, ? extends SuccessorEdges> index) {
    this.index = index;
  }

  @Override
  public boolean hasSuccessorNodes(GraphNode node) {
    return getSuccessors(node).hasSuccessors();
  }

  @Override
  public Collection<GraphNode> getSuccessorNodes(GraphNode node) {
    return getSuccessors(node).computeSuccessorNodes();
  }

  @Override
  public Collection<GraphNode> computeRoots() {
    return computeRoots(index.keySet());
  }

  /**
   * Provide the number of interior nodes in this tree.
   *
   * @return number of nodes in this tree
   */
  @Override
  public int countInteriorNodes() {
    int result = 0;

    for (SuccessorEdges fanout: index.values()) {
      if (fanout.hasSuccessors()) {
        ++result;
      }
    }

    return result;
  }

   
  @Override
  public Set<GraphNode> computeTreeNodes() {
    Set<GraphNode> treeNodes = Sets.newHashSet();

    // Since the index entry only contains the interior nodes,
    // build up result that includes each interior nodes' successors.
    for (Map.Entry<GraphNode, ? extends SuccessorEdges> entry
        : index.entrySet()) {
      treeNodes.add(entry.getKey());
      treeNodes.addAll(entry.getValue().computeSuccessorNodes());
    }

    return treeNodes;
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

  private SuccessorEdges getSuccessors(GraphNode node) {
    if (false == index.containsKey(node)) {
      return SuccessorEdges.EMPTY;
    }
    return index.get(node);
  }

  /**
   * Provide a collection of nodes to serve as roots of a tree.
   * <p>
   * The returned set of nodes guarantees a successor path to
   * every node in the universe.
   * 
   * @param universe set of node for root discovery
   * @return collection of root nodes
   */
  private Collection<GraphNode> computeRoots(
      Collection<GraphNode> universe) {

    DfsState dfsState = new DfsState(this);

    for (GraphNode node : universe) {
      if (dfsState.isUnvisited(node)) {
        dfsState.visitNode(node);
      }
    }

    return dfsState.extractRoots();
  }
}
