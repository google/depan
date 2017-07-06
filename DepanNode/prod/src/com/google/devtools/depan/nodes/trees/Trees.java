/*
 * Copyright 2017 The Depan Project Authors
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

import com.google.devtools.depan.graph.api.EdgeMatcher;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;

import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class Trees {

  private Trees() {
    // Prevent instantiation.
  }

  /**
   * Compute a map of nodes to their successor edges
   * for the given relationship finder.
   * All matched edges are retained as forward edges.
   * <p>
   * If a node does not participate in the relationships, it is not
   * included in the map.
   *
   * @param relations relations to include in result map
   * @return map of nodes to successors
   */
  static public Map<GraphNode, ? extends SuccessorEdges> computeSuccessorHierarchy(
          GraphModel model, EdgeMatcher<String> relations) {
  
    SuccessorsMap builder = new SuccessorsMap();
  
    // Only include nodes that participate in the relations.
    for (GraphEdge edge : model.getEdges()) {
      if (relations.edgeForward(edge)) {
        builder.addForwardEdge(edge);
      }
      else if (relations.edgeReverse(edge)) {
        builder.addReverseEdge(edge);
      }
    }
    return builder.getSuccessorMap();
  }

  /**
   * Compute a map of nodes to their successor edges
   * for the given relationship finder.
   * This successor map is strictly hierarchical, defining a spanning tree
   * over the graph.
   * No node is in the successor list of multiple parent nodes, so this is
   * safe for algorithms and tree renderings that don't handle loops or
   * dags well.
   * <p>
   * If a node does not participate in the relationships, it is not
   * included in the map.
   * 
   * Limitations on the successor map
   * - Only one edge for any successor is included.
   * - Only nodes with successor nodes are included.
   * - Leaf nodes are not included since they have no successors.
   *   However, each leaf node should occur as the far target for some
   *   included node.
   * - Nodes outside the relationship are not included.
   *
   * @param relations relations to include in result map
   * @return map of nodes to successors
   */
  static public Map<GraphNode, ? extends SuccessorEdges> computeSpanningHierarchy(
          GraphModel model, EdgeMatcher<String> edgeMatcher) {
  
    SuccessorsMap builder = new SuccessorsMap();
  
    Set<GraphNode> visited = Sets.newHashSet();
  
    // Only include nodes that participate in the relations.
    for (GraphEdge edge : model.getEdges()) {
      // No self-loops in a spanning tree.
      if (edge.getHead() == edge.getTail()) {
        continue;
      }
  
      // On forward matches, include the link only
      // if the tail has not yet been visited.
      if (edgeMatcher.edgeForward(edge)) {
        if (false == visited.contains((edge.getTail()))) {
          builder.addForwardEdge(edge);
          visited.add((edge.getTail()));
        }
        continue;
      }
  
      // For spanning hierarchies, each edge gets added only once.
      // And the forward direction is preferred if both are allowed.
      // On reverse matches, include the link only
      // if the head has not yet been visited.
      if (edgeMatcher.edgeReverse(edge)) {
        if (false == visited.contains((edge.getHead()))) {
          builder.addReverseEdge(edge);
          visited.add((edge.getHead()));
        }
        continue;
      }
    }
    return builder.getSuccessorMap();
  }
}
