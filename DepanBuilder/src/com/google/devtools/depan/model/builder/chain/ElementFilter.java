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

import com.google.devtools.depan.model.GraphNode;

/**
 * A simple interface for checking if an element pass a filter.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public interface ElementFilter {
  /**
   * Say if the given element passes the filter.
   *
   * @param node element to filter.
   * @return true if the element passes the test.
   */
  boolean passFilter(GraphNode node);

  /**
   * Common filter to accept all nodes
   */
  static final ElementFilter ALL_NODES = new ElementFilter() {

    @Override
    public boolean passFilter(GraphNode node) {
      return true;
    }
  };
}
