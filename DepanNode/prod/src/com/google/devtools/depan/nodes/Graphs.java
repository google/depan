/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.nodes;

import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphEdgeMatcher;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Map;

/**
 * Collected Edge Matcher algorithms (side-effect free) for DepAn Graphs.
 * 
 * Many of these methods originated from earlier version of GraphModel.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class Graphs {
  private Graphs() {
    // Prevent instantiation of this utility class.
  }

  /**
   * Computes a set of nodes in this graph that satisfy any of the filters in
   * <code>finder</code> using <code>nodeSet</code> as input.
   *
   * @param nodeSet A collection of nodes that are used as starting points.
   * @param finder Finder object that contains the filters.
   * @return A collection of nodes that satisfy filters with the given input.
   */
  static public Collection<GraphNode> getRelated(
      GraphModel model,
      Collection<GraphNode> nodeSet,
      GraphEdgeMatcher edgeMatcher) {
    Collection<GraphNode> result = Sets.newHashSet();

    for (GraphEdge edge : model.getEdges()) {
      if (nodeSet.contains(edge.getHead()) &&
          edgeMatcher.edgeForward(edge)) {
        result.add(edge.getTail());
      }
      if (nodeSet.contains(edge.getTail()) &&
          edgeMatcher.edgeReverse(edge)) {
        result.add(edge.getHead());
      }
    }
    return result;
  }

  /**
  * Create a map for each {@code headNode} to it's count of forward (departing)
  * edges in the {@code RelationSet}.  Even nodes with zero edge counts are
  * included.
  *
  * @param headNodes nodes to compute edge count
  * @param relations relations to use for edge count
  * @return {@code Map} of each node to its edge count
  */
  static public Map<GraphNode, Integer> getForwardRelationCount(
      GraphModel model,
      Collection<GraphNode> headNodes,
      RelationSet relationSet) {

    Map<GraphNode, Integer> result = populateRelationCount(headNodes);
    for (GraphEdge edge : model.getEdges()) {
      GraphNode head = edge.getHead();
      Integer update = result.get(head);
      if (null == update) {
        continue;
      }

      if (!relationSet.contains(edge.getRelation())) {
        continue;
      }
      result.put(head, update + 1);
    }
    return result;
  }

  /**
  * Create a map for each {@code headNode} to it's count of reverse (incoming)
  * edges in the {@code RelationSet}.  Even nodes with zero edge counts are
  * included.
  *
  * @param tailNodes nodes to compute edge count
  * @param relations relations to use for edge count
  * @return {@code Map} of each node to its edge count
  */
  static public Map<GraphNode, Integer> getReverseRelationCount(
      GraphModel model,
      Collection<GraphNode> tailNodes,
      RelationSet relationSet) {

    Map<GraphNode, Integer> result = populateRelationCount(tailNodes);
    for (GraphEdge edge : model.getEdges()) {
      GraphNode tail = edge.getTail();
      Integer update = result.get(tail);
      if (null == update) {
        continue;
      }

      if (!relationSet.contains(edge.getRelation())) {
        continue;
      }
      result.put(tail, update + 1);
    }
    return result;
  }

  /**
   * Populate a map of nodes to edge counts with zero as the count for every
   * node.  This ensures that nodes with no edges are included, and avoids a
   * test to check if the node is already in the result set.
   *
   * @param nodes collection of nodes to include in set
   * @return map of all input nodes to the count zero
   */
  static private Map<GraphNode, Integer> populateRelationCount(
      Collection<GraphNode> nodes) {
    Map<GraphNode, Integer> result = Maps.newHashMap();
    for (GraphNode node : nodes) {
      result.put(node, 0);
    }
    return result;
  }
}
