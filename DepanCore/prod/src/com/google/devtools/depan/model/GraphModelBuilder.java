/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.model;

import com.google.devtools.depan.model.interfaces.GraphBuilder;

/**
 * @author leeca
 *
 */
public abstract class GraphModelBuilder implements GraphBuilder {

  protected abstract GraphModel getGraphModel();

  protected abstract void addGraphEdge(GraphEdge edge);

  protected abstract void addGraphNode(GraphNode node);

  @Override
  public GraphModel getGraph() {
    return getGraphModel();
  }

  @Override
  public GraphNode newNode(GraphNode node) {
    addGraphNode(node);
    return node;
  }

  @Override
  public GraphEdge addEdge(GraphEdge edge) {
    addGraphEdge(edge);
    return edge;
  }

  @Override
  public GraphNode mapNode(GraphNode mapNode) {
    GraphNode graphNode = (GraphNode) getGraph().findNode(mapNode.getId());
    if (graphNode != null) {
      return graphNode;
    }

    newNode(mapNode);
    return mapNode;
  }
}
