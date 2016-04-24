/*
 * Copyright 2006 The Depan Project Authors
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

package com.google.devtools.depan.graph.basic;

import com.google.devtools.depan.graph.api.Graph;
import com.google.devtools.depan.graph.api.Node;
import com.google.devtools.depan.graph.api.Relation;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="leeca@google.com">Lee Carver</a>
 * 
 * @param <T> Node content type.
 */
public class BasicGraph<T> implements Graph<T> {
  
  private final Map<T, BasicNode<? extends T>> nodes;

  private final Set<BasicEdge<? extends T>> edges;

  /**
   * 
   */
  public BasicGraph(
      Map<T, BasicNode<? extends T>> nodes,
      Set<BasicEdge<? extends T>> edges) {
    this.nodes = nodes;
    this.edges = edges;
  }

  /////////////////////////////////////
  // Inherited Graph<> methods

  @Override
  public BasicNode<? extends T> findNode(T id) {
    final BasicNode<? extends T> result = nodes.get(id);
    return result;
  }

  @Override
  public BasicEdge<? extends T> findEdge(final Relation relation,
      final Node<? extends T> head, final Node<? extends T> tail) {
    for (BasicEdge<? extends T> edge : edges) {
      if ((relation == edge.getRelation()) &&
          (head == edge.getHead()) &&
          (tail == edge.getTail())) {
        return edge;
      }
    }

    // Not found
    return null;
  }

  /////////////////////////////////////
  // BasicGraph methods

  /**
   * Returns the collection of Nodes in this graph.
   * 
   * @return the collection of Nodes in this graph.
   */
  public Collection<? extends BasicNode<? extends T>> getNodes() {
    return Collections.unmodifiableCollection(nodes.values());
  }

  /**
   * Returns the collection of Nodes in this graph.
   * 
   * @return the collection of Nodes in this graph.
   */
  public Collection<? extends BasicEdge<? extends T>> getEdges() {
    return Collections.unmodifiableCollection(edges);
  }
}
