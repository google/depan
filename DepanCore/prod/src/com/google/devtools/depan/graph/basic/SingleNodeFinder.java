/*
 * Copyright 2006 Google Inc.
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

import com.google.devtools.depan.graph.api.Node;
import com.google.devtools.depan.graph.api.NodeFinder;

/**
 * @author <a href="leeca@google.com">Lee Carver</a>

 * @param <T> Node content type.
 */
public class SingleNodeFinder<T> implements NodeFinder<T> {
  
  private final Node<T> target;

  /**
   * Construct a NodeFinder that matches exactly one Node.
   */
  public SingleNodeFinder(final Node<T> target) {
    this.target = target;
  }

  /**
   * @inheritDoc
   * 
   * This version matches if the test Node is equal target node.
   */
  public boolean match(final Node<? extends T> test) {
    return (target == test);
  }

}
