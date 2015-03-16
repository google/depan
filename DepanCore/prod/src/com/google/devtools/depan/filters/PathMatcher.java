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

package com.google.devtools.depan.filters;

import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;

import java.util.Collection;

/**
 * Determines a set of nodes that satisfy given criteria for a collection of 
 * starting nodes.
 * 
 * @author tugrul@google.com (Tugrul Ince)
 */
public interface PathMatcher {
  /**
   * Computes a list of nodes that are the output of a filter, given a set of
   * nodes as input.
   * 
   * @param graph The model that contains the relations between nodes.
   * @param input A set of <code>GraphNode</code> objects given as input.
   * @return Result of applying this filter to the input.
   */
  Collection<GraphNode> nextMatch(
      GraphModel graph, Collection<GraphNode> input);

  /**
   * Returns the display name for this object.
   * 
   * @return Display Name.
   */
  String getDisplayName();
}
