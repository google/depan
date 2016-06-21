/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.nodes.filters.model;

import com.google.devtools.depan.model.GraphNode;

import java.util.Collection;

/**
 * Define the expected behaviors of a node filter.
 * 
 * Since {@link ContextualFilter}s are often composed, that need to manage
 * their own identity.  The {@link #getName()} and {@link #getSummary()}
 * methods provide basic support.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public interface ContextualFilter {

  /**
   * Announce which context keys, if any, are interesting to this filter.
   */
  Collection<? extends ContextKey> getContextKeys();

  /**
   * Receive the context to be used with future invocations of
   * {@link #computeNodes(Collection)}.
   */
  void receiveContext(FilterContext context);

  /**
   * Compute the set of nodes provided by this {@link ContextualFilter}.
   */
  Collection<GraphNode> computeNodes(Collection<GraphNode>nodes);

  /**
   * Provide a displayable name for this {@link ContextualFilter}.
   * This may be null, especially of the filter has not been saved.
   */
  String getName();

  /**
   * Provide a displayable summary for this {@link ContextualFilter}.
   * This may be null, especially of the filter has not been saved.
   */
  String getSummary();
}
