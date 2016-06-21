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

package com.google.devtools.depan.eclipse.ui.nodes.viewers;

import com.google.devtools.depan.model.GraphNode;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class NodeTreeProviders {

  private NodeTreeProviders() {
    // Prevent instantiation.
  }

  public static class GraphNodeProvider
      implements NodeTreeProvider<GraphNode> {

    @Override
    public GraphNode getObject(GraphNode node) {
      return node;
    }
  }

  // Only need one.
  public static final NodeTreeProvider<GraphNode> GRAPH_NODE_PROVIDER =
      new GraphNodeProvider();
}
