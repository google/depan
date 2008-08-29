/*
 * Copyright 2006, 2008 Google Inc.
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
package com.google.devtools.depan.graph.api;

/**
 * @author <a href="leeca@google.com">Lee Carver</a>
 * @param <T> Node content type.
 */
public interface Graph<T> {
  
  /**
   * Add a new node.  The id must be unique within this graph.
   * 
   * @param id identifier for new Node
   * @return newly created Node
   * @throws IllegalArgumentException if id exists
   */
   void addNode(Node<? extends T> node);

  /**
   * Find an existing Node.
   * 
   * @param id identifier for existing Node
   * @return an existing Node, or <code>null</code> if not in Graph
   */
  Node<? extends T> findNode(T id);

  /**
   * @param relation
   * @param head
   * @param tail
   * @return
   */
  void addEdge(Edge<? extends T> edge);

  /**
   * Find an edge given the relation, head, and tail.
   * 
   * @param relation connection between head and tail
   * @param head starting node
   * @param tail final node
   * @return Edge for relationship, or <code>null</code> if not in Graph
   */
  Edge<? extends T> findEdge(Relation relation,
      Node<? extends T> head, Node<? extends T> tail);
}
