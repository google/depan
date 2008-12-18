/*
 * Copyright 2008 Google Inc.
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

import com.google.devtools.depan.collect.Lists;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;

import java.util.Collection;
import java.util.List;

/**
 * A PathMatcher that only matches nodes for a set of node types.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class NodeKindMatcher implements PathMatcher {
  private final Collection<Class<? extends Element>> nodeClasses;

  public NodeKindMatcher(Collection<Class<? extends Element>> nodeClasses) {
    this.nodeClasses = nodeClasses;
  }

  @Override
  public String getDisplayName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Collection<GraphNode> nextMatch(
      GraphModel graph, Collection<GraphNode> input) {
    List<GraphNode> result = Lists.newArrayList();
    for (GraphNode node : input) {
      if (matchesKind(node)) {
        result.add(node);
      }
    }

    return result;
  }

  /**
   * Determine if node is an instance of one of the acceptable classes.
   * 
   * @param node item to test
   * @return {@code true} iff node is an instance of an accepting class
   */
  private boolean matchesKind(GraphNode node) {
    for (Class<? extends Element> nodeClass : nodeClasses) {
      if (nodeClass.isInstance(node)) {
        return true;
      }
    }

    return false;
  }
}