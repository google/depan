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
package com.google.devtools.depan.view_doc.layout.jung;

import com.google.devtools.depan.graph.api.EdgeMatcher;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptors;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view_doc.layout.LayoutContext;

import com.google.common.collect.Sets;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;

import java.util.Collection;
import java.util.Set;

/**
 * Construct a JUNG graph from the supplied subset on nodes and edges.
 * 
 * Extracted from previous versions of ViewEditor.
 * 
 * @author <a href='mailto:lee.carver@servicenow.com'>Lee Carver</a>
 */
public class JungBuilder {
  private final GraphModel graphModel;
  private Collection<GraphNode> movableNodes = GraphNode.EMPTY_NODE_LIST;
  private Collection<GraphNode> fixedNodes = GraphNode.EMPTY_NODE_LIST;
  private GraphEdgeMatcherDescriptor edgeMatcher = 
      GraphEdgeMatcherDescriptors.FORWARD;

  public JungBuilder(GraphModel graphModel) {
    this.graphModel = graphModel;
  }

  public void setMovableNodes(Collection<GraphNode> movableNodes) {
    this.movableNodes = movableNodes;
  }

  public void setFixedNodes(Collection<GraphNode> fixedNodes) {
    this.fixedNodes = fixedNodes;
  }

  public void setEdgeMatcher(GraphEdgeMatcherDescriptor edgeMatcher) {
    this.edgeMatcher = edgeMatcher;
  }

  public DirectedGraph<GraphNode, GraphEdge> build() {
    DirectedGraph<GraphNode, GraphEdge> result =
            new DirectedSparseMultigraph<GraphNode, GraphEdge>();

    Set<GraphNode> includedNodes = Sets.newHashSet();

    for (GraphNode node : movableNodes) {
      result.addVertex(node);
      includedNodes.add(node);
    }

    for (GraphNode node : fixedNodes) {
      result.addVertex(node);
      includedNodes.add(node);
    }

    addEdges(result, includedNodes);
    return result;
  }

  // TODO: Reconcile with GraphModel.computeSuccessorHierarchy().
  private void addEdges(
          DirectedGraph<GraphNode, GraphEdge> result,
          Set<GraphNode> includedNodes) {

    EdgeMatcher<String> matcher = edgeMatcher.getInfo();
    for (GraphEdge edge : graphModel.getEdges()) {
      // Filter on nodes first
      if (!includedNodes.contains(edge.getHead()))
        continue;

      if (!includedNodes.contains(edge.getTail()))
        continue;

      if (matcher.edgeForward(edge)) {
        addForwardEdge(result, edge);
      }
      else if (matcher.edgeReverse(edge)) {
        addReverseEdge(result, edge);
      }
    }
  }

  private void addForwardEdge(
          Graph<GraphNode, GraphEdge> graph,
          GraphEdge edge) {
    graph.addEdge(edge, edge.getHead(), edge.getTail());
  }

  private void addReverseEdge(
          Graph<GraphNode, GraphEdge> graph,
          GraphEdge edge) {
    graph.addEdge(edge, edge.getTail(), edge.getHead());
  }

  public static DirectedGraph<GraphNode, GraphEdge> buildJungGraph(
      LayoutContext context) {
    JungBuilder builder = new JungBuilder(context.getGraphModel());

    builder.setMovableNodes(context.getMovableNodes());
    builder.setFixedNodes(context.getFixedNodes());
    builder.setEdgeMatcher(context.getEdgeMatcher());

    DirectedGraph<GraphNode, GraphEdge> result = builder.build();
    return result;
  }
}
