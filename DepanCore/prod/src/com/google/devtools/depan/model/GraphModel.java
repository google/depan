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

package com.google.devtools.depan.model;

import com.google.devtools.depan.graph.basic.BasicEdge;
import com.google.devtools.depan.graph.basic.BasicGraph;
import com.google.devtools.depan.graph.basic.BasicNode;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Main Graph implementation.
 *
 * Node that any inserted node in the graph should be involved in at least one
 * edge. Otherwise, it will not be saved by PersistentGraph.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class GraphModel extends BasicGraph<String> {

  /**
   * Basic constructor for a view model.
   */
  public GraphModel(
      Map<String, BasicNode<? extends String>> nodes,
      Set<BasicEdge<? extends String>> edges) {
    super(nodes, edges);
  }

  /**
   * Returns the collection of Nodes in this graph.
   *
   * @return the collection of Nodes in this graph.
   */
  @Override
  @SuppressWarnings("unchecked")
  public Collection<GraphNode> getNodes() {
    return (Collection<GraphNode>) super.getNodes();
  }

  /**
   * Returns a copy of the collection of Nodes in this graph as a Set.
   *
   * @return the collection of Nodes in this graph.
   */
  public Set<GraphNode> getNodesSet() {
    return Sets.newHashSet(getNodes());
  }

  /**
   * Returns the collection of Nodes in this graph as a Map from their
   * String name to the underlying node.
   *
   * @return the collection of Nodes in this graph.
   */
  public Map<String, GraphNode> getNodesMap() {
    Collection<GraphNode> nodes = getNodes();
    Map<String, GraphNode> result =
        Maps.newHashMapWithExpectedSize(nodes.size());
    for (GraphNode node : nodes) {
      result.put(node.getId().toString(), node);
    }
    return result;
  }

  /**
   * Returns the collection of edges in this graph.
   *
   * @return the collection of edges in this graph.
   */
  @Override
  @SuppressWarnings("unchecked")
  public Collection<GraphEdge> getEdges() {
    return (Collection<GraphEdge>) super.getEdges();
  }

  /**
   * Returns a copy of the list of edges in this graph as a Set.
   *
   * @return the Set of edges in this graph.
   */
  public Set<GraphEdge> getEdgesSet() {
    return Sets.newHashSet(getEdges());
  }

  /////////////////////////////////////
  // Expanded Graph methods.
  // These should probably be pushed up into the Graph interface.

  public Collection<GraphNode> and(GraphModel that) {
    Collection<GraphNode> result = Sets.newHashSet(getNodesSet());
    result.retainAll(that.getNodes());

    return result;
  }

  public Collection<GraphNode> not(GraphModel that) {
    Collection<GraphNode> result = Sets.newHashSet(getNodesSet());
    result.removeAll(that.getNodes());

    return result;
  }

  public Collection<GraphNode> or(GraphModel that) {
    Collection<GraphNode> result = Sets.newHashSet(getNodesSet());
    result.addAll(that.getNodes());

    return result;
  }

  public Collection<GraphNode> xor(GraphModel that) {
    Collection<GraphNode> result = Sets.newHashSet(getNodesSet());
    result.addAll(that.getNodes());

    return result;
  }
}
