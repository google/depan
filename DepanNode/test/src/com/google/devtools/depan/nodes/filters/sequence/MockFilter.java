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

import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.nodes.filters.model.ContextKey;
import com.google.devtools.depan.nodes.filters.model.ContextualFilter;
import com.google.devtools.depan.nodes.filters.model.FilterContext;

import java.util.Collection;

/**
 * Test {@link ContextualFilter} for wrapper filter tests.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
class MockFilter implements ContextualFilter {

  public String name;
  public String summary;
  public Collection<? extends ContextKey> keys;
  public Collection<GraphNode> compute;
  public FilterContext context;

  @Override
  public Collection<? extends ContextKey> getContextKeys() {
    return keys;
  }

  @Override
  public void receiveContext(FilterContext context) {
    this.context = context;
  }

  @Override
  public Collection<GraphNode> computeNodes(Collection<GraphNode> nodes) {
    return compute;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getSummary() {
    return summary;
  }
}
