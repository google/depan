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

package com.google.devtools.depan.graph.basic;

import com.google.devtools.depan.graph.api.Node;
import com.google.devtools.depan.graph.api.NodeFinder;

import java.util.Set;

/**
 * This {@link NodeFinder} matches any {@link Node} contained in given set.
 * The set is initialized with the constructor.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 *
 * @param <T> Type contained in Nodes
 */
public class MultipleNodeFinder<T> implements NodeFinder<T> {

  /**
   * A set of targets. 
   */
  private final Set<Node<T>> targets;

  /**
   * Construct a {@link MultipleNodeFinder} given a set of targets.
   * 
   * @param targets set of nodes matching this {@link NodeFinder}.
   */
  public MultipleNodeFinder(Set<Node<T>> targets) {
    this.targets = targets;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.google.devtools.depan.graph.api.NodeFinder
   *      #match(com.google.devtools.depan.graph.api.Node)
   */
  public boolean match(Node<? extends T> test) {
    return targets.contains(test);
  }

}
