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

package com.google.devtools.depan.nodes.filters.sequence;

import com.google.devtools.depan.model.GraphEdgeMatcher;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.nodes.Graphs;
import com.google.devtools.depan.nodes.filters.model.ContextKey;

import java.util.Collection;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class EdgeMatcherFilter extends BasicFilter {

  private GraphEdgeMatcher matcher;

  public EdgeMatcherFilter(GraphEdgeMatcher matcher) {
    this.matcher = matcher;
  }

  @Override
  public Collection<GraphNode> computeNodes(Collection<GraphNode> nodes) {
    return Graphs.getRelated(getContextUniverse(), nodes, matcher);
  }

  @Override
  public Collection<? extends ContextKey> getContextKeys() {
    return KEYS_UNIVERSE;
  }

  public GraphEdgeMatcher getEdgeMatcher() {
    return matcher;
  }

  public void setEdgeMatcher(GraphEdgeMatcher matcher) {
    this.matcher = matcher;
  }
}
