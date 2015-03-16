/*
 * Copyright 2008 The Depan Project Authors
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

/**
 * A simple concrete node that just saves the id from its constructor.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 *
 */
public class SimpleNode<T> extends BasicNode<T> {

  /** Internal storage for the id. */
  private final T id;

  /**
   * Create a SimpleNode for the provided id.
   * 
   * @param id id to associate with SimpleNode
   */
  public SimpleNode(T id) {
    super();
    this.id = id;
  }

  @Override
  public T getId() {
    return id;
  }
}
