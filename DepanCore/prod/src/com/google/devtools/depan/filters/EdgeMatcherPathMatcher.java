/*
 * Copyright 2015 The Depan Project Authors
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

import com.google.devtools.depan.model.GraphEdgeMatcher;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;

import java.util.Collection;

/**
 * Provide a path matcher based on an edge matcher.
 * 
 * @since 2015 Provides the same ability previous accessed via the legacy
 * types {@code RelationshipSetAdapter} and
 * {@code MultipleDirectedRelationFinder}.  These wound up delegating to
 * {@link GraphModel#getRelated(Collection, GraphEdgeMatcher)} as well.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class EdgeMatcherPathMatcher implements PathMatcher {
  private final String displayName;
  private final GraphEdgeMatcher edgeMatcher;

  public EdgeMatcherPathMatcher(
      String displayName, GraphEdgeMatcher edgeMatcher) {
    this.displayName = displayName;
    this.edgeMatcher = edgeMatcher;
  }

  @Override
  public Collection<GraphNode> nextMatch(
      GraphModel graph, Collection<GraphNode> input) {
    return graph.getRelated(input, edgeMatcher);
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }
}
