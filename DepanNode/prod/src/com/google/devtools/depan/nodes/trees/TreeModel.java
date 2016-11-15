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
package com.google.devtools.depan.nodes.trees;

import com.google.devtools.depan.model.GraphNode;

import java.util.Collection;
import java.util.Collections;

/**
 * Define the interface for accessing a tree of GraphNodes.
 * Based on SuccessorEdges for the nodes.
 * 
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
public interface TreeModel {

  /**
   * Indicate whether the supplied node has successors.
   * May be less expensive than computing full set of successors.
   */
  boolean hasSuccessorNodes(GraphNode node);

  /**
   * Provide the Collection of successors for the given node.
   * 
   * @param node node
   * @return Collection of successor nodes
   */
  Collection<GraphNode> getSuccessorNodes(GraphNode node);

  /**
   * Provide the roots of the graph base on the TreeModel's successor map.
   * Only key nodes from the successor map are included.
   * 
   * @return Dependency ordered List of Nodes
   */
  Collection<GraphNode> computeRoots();

  /**
   * Provide the set of nodes in this tree.
   *
   * @return nodes in this tree
   */
  Collection<GraphNode> computeTreeNodes();

  /**
   * Provide the total number of nodes in this tree.
   *
   * @return number of nodes in this tree
   */
  int countTreeNodes();

  /**
   * Provide the number of interior nodes in this tree.
   *
   * @return number of nodes in this tree
   */
  public int countInteriorNodes();

  public class Empty implements TreeModel {

    @Override
    public boolean hasSuccessorNodes(GraphNode node) {
      return false;
    }

    @Override
    public Collection<GraphNode> getSuccessorNodes(GraphNode node) {
      return Collections.emptyList();
    }

    @Override
    public Collection<GraphNode> computeRoots() {
      return  Collections.emptyList();
    }

    @Override
    public Collection<GraphNode> computeTreeNodes() {
      return Collections.emptyList();
    }

    @Override
    public int countTreeNodes() {
      return 0;
    }

    @Override
    public int countInteriorNodes() {
      return 0;
    }
  }

  // Only need one empty instance
  public Empty EMPTY = new Empty();

  public class Flat implements TreeModel {
    private final Collection<GraphNode> roots;

    public Flat(Collection<GraphNode> roots) {
      this.roots = roots;
    }

    @Override
    public boolean hasSuccessorNodes(GraphNode node) {
      return false;
    }

    @Override
    public Collection<GraphNode> getSuccessorNodes(GraphNode node) {
      return Collections.emptyList();
    }

    @Override
    public Collection<GraphNode> computeRoots() {
      return roots;
    }

    @Override
    public Collection<GraphNode> computeTreeNodes() {
      return roots;
    }

    @Override
    public int countTreeNodes() {
      return roots.size();
    }

    @Override
    public int countInteriorNodes() {
      // could be root.size();
      return 0;
    }
  }
}
