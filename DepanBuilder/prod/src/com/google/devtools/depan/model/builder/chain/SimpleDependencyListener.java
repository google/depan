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

package com.google.devtools.depan.model.builder.chain;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.api.GraphBuilder;
import com.google.devtools.depan.model.builder.chain.DependenciesListener;

/**
 * Implements a simple form of DependenciesListener, primarily detection
 * of duplicate nodes.  If either a parent or child node already exists
 * in the Graph, the existing node is used instead of adding a duplicate.
 * 
 * Once the nodes have be de-duped, the indicated edge between the nodes
 * is added to the graph.
 *
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class SimpleDependencyListener
    implements DependenciesListener {

  /**
   * GraphBuilder used to create/extend the graph.
   */
  private final GraphBuilder graphBuilder;

  /**
   * Construct the listener for the given GraphBuilder.
   *
   * @param builder Graph builder which creates the graph.
   */
  public SimpleDependencyListener(GraphBuilder builder) {
    this.graphBuilder = builder;
  }


  @Override
  public GraphNode newNode(GraphNode orphan) {
    return graphBuilder.mapNode(orphan);
  }

  @Override
  public void newDep(GraphNode parent, GraphNode child, Relation t) {
    GraphNode p = graphBuilder.mapNode(parent);
    GraphNode c = graphBuilder.mapNode(child);

    graphBuilder.addEdge(new GraphEdge(p, c, t));
  }

  @Override
  public void newDeps(
      GraphNode parent, GraphNode[] childs, Relation t) {
    for (GraphNode c : childs) {
      newDep(parent, c, t);
    }
  }


  @Override
  public GraphNode lookup(GraphNode target) {
    return graphBuilder.findNode(target.getId());
  }
}
