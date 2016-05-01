/*
 * Copyright 2009 The Depan Project Authors
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

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.api.GraphBuilder;

/**
 * Extends a SimpleDependenciesListener to filter entities that are outside
 * the scope of the current analysis tasks.
 *
 * This class will first check if the two elements involved in the new
 * dependency pass the filter constructor-provided filter.  This is a "cheap"
 * operation, so we do it before checking for duplicate nodes.  If it passes,
 * delegate to SimpleDependencyListener to de-dup nodes as the relation
 * between them is inserted.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 *
 */
public abstract class FilteringDependencyListener
    extends SimpleDependencyListener {

  /**
   * Construct a FilteringDependencyListener for the given GraphBuilder.
   *
   * @param gb Graph builder which create the graph.
   */
  public FilteringDependencyListener(GraphBuilder builder) {
    super(builder);
  }

  @Override
  public void newDep(GraphNode parent, GraphNode child, Relation t) {
    if (passFilter(parent, child)) {
      super.newDep(parent, child, t);
    }
  }

  /**
   * Check if the given element pass the filter.
   *
   * @param e Element to check
   * @return true if the element passes the filter, or if no filter were
   *         specified (filter is null). false otherwise.
   */
  protected abstract boolean passFilter(GraphNode e);

  /**
   * Check if both elements passes the filter.
   *
   * @param e1 first Element
   * @param e2 second Element
   * @return true if both elements pass the filter, false if at least one
   *         of them failed.
   */
  private boolean passFilter(GraphNode e1, GraphNode e2) {
    return passFilter(e1) && passFilter(e2);
  }
}
