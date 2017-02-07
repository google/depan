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
package com.google.devtools.depan.stats.jung;

import com.google.devtools.depan.graph.api.EdgeMatcher;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;

import java.util.Set;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

/**
 * Construct a JUNG graph from the supplied subset on nodes and edges.
 * 
 * Extracted from previous versions of ViewEditor.
 * 
 * @author <a href='mailto:leeca@pnambic.com'>Lee Carver</a>
 */
public class JungBuilder {

  public JungBuilder() {
    // Prevent instantiation.
  }

  public static DirectedGraph<GraphNode, GraphEdge> build(
      GraphModel graphModel, EdgeMatcher<String> matcher) {

    DirectedGraph<GraphNode, GraphEdge> result =
            new DirectedSparseMultigraph<GraphNode, GraphEdge>();

    Set<GraphNode> includedNodes = graphModel.getNodesSet();

    for (GraphEdge edge : graphModel.getEdges()) {
      // Filter on nodes first
      if (!includedNodes.contains(edge.getHead()))
        continue;

      if (!includedNodes.contains(edge.getTail()))
        continue;

      if (matcher.edgeForward(edge)) {
        result.addEdge(edge, edge.getHead(), edge.getTail());
      }
      else if (matcher.edgeReverse(edge)) {
        result.addEdge(edge, edge.getTail(), edge.getHead());
      }
    }
    return result;
  }
}
