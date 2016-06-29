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

package com.google.devtools.depan.collapse.model;

import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphNode;

import java.util.Collection;
import java.util.Map;

/**
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
public class HiddenNodesGizmo {

  /** Map hidden nodes to their top-most exposed node */
  private final Map<GraphNode, GraphNode> hiddenNodes;

  /**
   * Create a gizmo for computing exposed nodes and edges.
   * 
   * @param hiddenNodes map of hidden nodes to their top-most exposed node
   */
  public HiddenNodesGizmo(Map<GraphNode, GraphNode> hiddenNodes) {
    this.hiddenNodes = hiddenNodes;
  }

  /**
   * Add any exposed nodes in the available set to the result.
   * A node is exposed if it is not in the set of hidden nodes.
   * 
   * @param result destination for exposed nodes
   * @param availNodes collection of possible nodes
   */
  public void addExposedNodes(
      Collection<GraphNode> result,
      Collection<GraphNode> availNodes) {

    for (GraphNode candidate : availNodes) {
      if (false == hiddenNodes.containsKey(candidate)) {
        result.add(candidate);
      }
    }
  }

  /**
   * Synthesize a new edge, and add it to the result.
   * The new edge goes from head to tail, and it's relationship type is
   * obtained from the original edge.
   * @param result destination for new edges
   * @param original source of edge relation
   * @param head head node for new edge
   * @param tail tail node for new edge
   */
  private void addSyntheticEdge(
      Collection<GraphEdge> result,
      GraphEdge original,
      GraphNode head,
      GraphNode tail) {
    result.add(new GraphEdge(head, tail, original.getRelation()));
  }

  public void addExposedEdges(
      Collection<GraphEdge> result,
      Collection<GraphEdge> availEdges) {

    for (GraphEdge candidate : availEdges) {
      GraphNode edgeHead = hiddenNodes.get(candidate.getHead());
      GraphNode edgeTail = hiddenNodes.get(candidate.getTail());

      // If neither head or tail is collapsed, just use the original edge
      if ((null == edgeHead) && (null == edgeTail)) {
        result.add(candidate);
      }

      // If only one of the head or the tail is collapsed,
      // add the edge from the uncollapsed node to the master node.
      // Carefully reuse the edge if we can.
      else if (null == edgeHead) {
        if (edgeTail == candidate.getTail()) {
          result.add(candidate);
        }
        else {
          addSyntheticEdge(result, candidate, candidate.getHead(), edgeTail);
        }
      }
      else if (null == edgeTail) {
        if (edgeHead == candidate.getHead()) {
          result.add(candidate);
        }
        else {
          addSyntheticEdge(result, candidate, edgeHead, candidate.getTail());
        }
      }

      // If both head and tail are collapsed, but these are the same as
      // from the candidate edge, then the edge is between two exposed
      // master nodes.  Just reuse the edge.
      else if ((edgeHead == candidate.getHead())
          && (edgeTail == candidate.getTail())) {
        result.add(candidate);
      }

      // If both head and tail are collapsed, and they are not the same
      // then we have edges from inside one collapse group to another
      else if (edgeHead != edgeTail) {
        addSyntheticEdge(result, candidate, edgeHead, edgeTail);
      }

      // Otherwise, the candidate edge is entirely between nodes of
      // the same collapse group, and it is omitted from the graph.
    }
  }

}
