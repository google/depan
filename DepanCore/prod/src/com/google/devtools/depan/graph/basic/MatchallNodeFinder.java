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

/**
 * A simple {@link NodeFinder} that matches all nodes (match just always return
 * true). usefull when looking for incomming/outgoing edges.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 * 
 * @param <T> Node content type.
 */
public class MatchallNodeFinder<T> implements NodeFinder<T> {

  public MatchallNodeFinder() {}
  
  /*
   * (non-Javadoc)
   * 
   * @see com.google.devtools.depan.graph.api.NodeFinder
   *      #match(com.google.devtools.depan.graph.api.Node)
   */
  public boolean match(final Node<? extends T> test) {
    return true;
  }

}
