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
import com.google.devtools.depan.model.builder.api.GraphBuilder;

/**
 * Implements a DependenciesListener, which means that this is the first class
 * that receive all the new dependencies after their creation.
 *
 * This class will first check if the two elements involved in the new
 * dependency pass the filter constructor-provided filter.
 *
 * If yes, the next step is to check if we already seen this element before
 * (this operation is longer than the filter check, so we do it after).
 * For this, it uses an ElementToNodeMapper, which return the correct
 * Node to insert in the graph, and says if we have to insert it into
 * the graph, or if it should already be present in the graph.
 *
 * Finally, insert in the graph the edge, representing the dependency.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class DependenciesDispatcher extends FilteringDependencyListener {

  /**
   * The filter for incoming elements.
   */
  private final ElementFilter filter;

  /**
   * Construct a DependenciesDispatcher using the given
   * ElementFilter, and GraphBuilder.
   *
   * @param filter ElementFilter to check which Element to insert into the
   *        graph.
   * @param gb Graph builder which create the graph.
   */
  public DependenciesDispatcher(ElementFilter filter, GraphBuilder builder) {
    super(builder);
    this.filter = filter;
  }

  /**
   * Construct a DependenciesDispatcher without filtering for
   * the given GraphBuilder.
   *
   * @param gb Graph builder which create the graph.
   */
  public DependenciesDispatcher(GraphBuilder builder) {
    this(ElementFilter.ALL_NODES, builder);
  }

  /**
   * check if the given element pass the filter.
   *
   * @param e Element to check
   * @return true if the element passes the filter, or if no filter were
   *         specified (filter is null). false otherwise.
   */
  @Override
  protected boolean passFilter(GraphNode e) {
    return filter.passFilter(e);
  }
}
