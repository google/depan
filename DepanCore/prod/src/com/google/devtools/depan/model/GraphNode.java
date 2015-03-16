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

package com.google.devtools.depan.model;

import com.google.devtools.depan.graph.basic.BasicNode;

import java.util.Collections;
import java.util.List;

/**
 * A wrapper for Element, implementing the interface Node.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public abstract class GraphNode extends BasicNode<String> implements Element {

  /**
   * Return a friendly string that may be used in a user interface for example.
   * @return a friendly name for this element.
   */
  public abstract String friendlyString();

  /** Empty collection */
  public static final List<GraphNode> EMPTY_NODE_LIST =
          Collections.<GraphNode>emptyList();
}
