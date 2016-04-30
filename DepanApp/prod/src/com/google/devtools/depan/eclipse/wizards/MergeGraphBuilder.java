/*
 * Copyright 2014 The Depan Project Authors
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

/**
 * Handle composition of Graphs.
 * 
 * Multiple combination operators are feasible.
 * The current set of composition operations is smale:
 * - merge();
 */
package com.google.devtools.depan.eclipse.wizards;

import com.google.devtools.depan.eclipse.plugins.SourcePlugin;
import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.api.GraphBuilder;
import com.google.devtools.depan.model.builder.api.GraphBuilders;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Lee Carver <leeca@pnambic.com>
 */
public class MergeGraphBuilder {
  private GraphBuilder builder = GraphBuilders.createGraphModelBuilder();

  private List<SourcePlugin> analyzers = Lists.newArrayList();
  private  Map<GraphNode, Set<GraphEdge>> headToEdges = Maps.newHashMap();

  public GraphDocument getGraphDocument() {
    // Release all internal state
    analyzers = null;
    headToEdges = null;

    GraphModel graph = builder.createGraphModel();
    return new GraphDocument(graph, analyzers);
  }

  public void merge(GraphDocument nextGraph) {
    mergeGraph(nextGraph.getGraph());
    analyzers.addAll(nextGraph.getAnalyzers());
  }

  private void mergeGraph(GraphModel merge) {

    // Add any new nodes
    for (GraphNode node : merge.getNodes()) {
      if (null == builder.findNode(node.getId())) {
        // TODO: Should be cloning the node.
        builder.newNode(node);

        // Speed up edge lookups
        Set<GraphEdge> empty = Sets.newHashSet();
        headToEdges.put(node, empty);
      }
    }

    // Add any new edges
    // [Apr-2016]: This is probably flawed.
    // Shouldn't all edges be in merge result?
    for (GraphEdge edge : merge.getEdges()) {
      Set<GraphEdge> forHead = headToEdges.get(edge.getHead());
      if (null == forHead) {
        continue;
      }
      if (!forHead.contains(edge)) {
        GraphEdge newEdge = new GraphEdge(edge.getHead(), edge.getTail(), edge.getRelation());
        builder.addEdge(newEdge);
        forHead.add(edge);
      }
    }
  }
}
