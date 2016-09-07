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

import com.google.common.base.CaseFormat;
import com.google.common.collect.Sets;

import java.util.Collection;

/**
 * Define (and implement) the standard ways of combining groups of nodes.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public enum ComposeMode {

  INTERSECT("intersect") {
    @Override
    public Collection<GraphNode> compose(
        Collection<GraphNode> nodes, Collection<GraphNode> input) {
      Collection<GraphNode> result = Sets.newHashSet(nodes);
      result.retainAll(input);
      return result;
    }
  },

  SUBTRACT("subtract") {
    @Override
    public Collection<GraphNode> compose(
        Collection<GraphNode> nodes, Collection<GraphNode> input) {
      Collection<GraphNode> result = Sets.newHashSet(nodes);
      result.removeAll(input);
      return result;
    }
  },

  UNION("union") {
    @Override
    public Collection<GraphNode> compose(
        Collection<GraphNode> nodes, Collection<GraphNode> input) {
      int size = nodes.size() + input.size();
      Collection<GraphNode> result = Sets.newHashSetWithExpectedSize(size);
      result.addAll(nodes);
      result.addAll(input);
      return result;
    }
  };

  private final String name;

  private ComposeMode(String name) {
    this.name = name;
  }

  public abstract Collection<GraphNode> compose(
      Collection<GraphNode> nodes, Collection<GraphNode> input);

  public String getName() {
    return name;
  }

  public String getTitle() {
    return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name);
  }
}
