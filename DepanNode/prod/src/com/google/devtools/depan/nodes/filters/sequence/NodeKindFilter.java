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

import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.GraphNode;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class NodeKindFilter extends BasicFilter {

  private Collection<Class<? extends Element>> nodeClasses;

  public NodeKindFilter(Collection<Class<? extends Element>> nodeClasses) {
    this.nodeClasses = nodeClasses;
  }

  @Override
  public Collection<GraphNode> computeNodes(Collection<GraphNode> nodes) {
    List<GraphNode> result = Lists.newArrayList();
    for (GraphNode node : nodes) {
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

  public Collection<Class<? extends Element>> getNodeKinds() {
    return nodeClasses;
  }
}
