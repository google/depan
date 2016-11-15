/*
 * Copyright 2013 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.devtools.depan.nodes.trees;

import com.google.devtools.depan.model.GraphNode;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

/**
 * Extend the basic DfsState to also construct a topological ordering for
 * the set of relations.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class TopoSortState extends DfsState {

  private List<GraphNode> topoOrder= Lists.newArrayList();

  public TopoSortState(TreeModel treeData) {
    super(treeData);
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation extends the finish-visit step to add the
   * fully explored node to the topological sequence of nodes.
   */
  @Override
  public void setExplored(GraphNode node) {
    super.setExplored(node);

    // Only keep the interior nodes of the tree.
    // Omit all the leaves.
    if (hasSuccessorNodes(node)) {
      topoOrder.add(node);
    }
  }

  /**
   * Compute a topological ordering for the Nodes in the universe.
   * 
   * @param universe Collection of Nodes to order
   * @return a topological permutation of the input Nodes
   */
  public List<GraphNode> topoSort(
      Collection<GraphNode> universe) {
    for (GraphNode node : universe) {
      if (isUnvisited(node)) {
        visitNode(node);
      }
    }
    return topoOrder;
  }
}
