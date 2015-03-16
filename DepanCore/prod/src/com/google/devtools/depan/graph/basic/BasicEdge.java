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

import com.google.devtools.depan.graph.api.Edge;
import com.google.devtools.depan.graph.api.Node;
import com.google.devtools.depan.graph.api.Relation;

/**
 * @author <a href="leeca@google.com">Lee Carver</a>
 * @param <T> Node content type.
 */
public class BasicEdge<T> implements Edge<T> {
  
  private final Node<? extends T> head;
  private final Node<? extends T> tail;
  private final Relation relation;

  public BasicEdge(final Relation relation,
      final Node<? extends T> head, final Node<? extends T> tail) {
    this.relation = relation;
    this.head = head;
    this.tail = tail;
  }

  @Override
  public Relation getRelation() {
    return relation;
  }

  @Override
  public Node<? extends T> getHead() {
    return head;
  }

  @Override
  public Node<? extends T> getTail() {
    return tail;
  }
}
