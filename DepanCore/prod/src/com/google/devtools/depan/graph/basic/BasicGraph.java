/*
 * Copyright 2006 The Depan Project Authors
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

package com.google.devtools.depan.graph.basic;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.google.devtools.depan.graph.api.Edge;
import com.google.devtools.depan.graph.api.Graph;
import com.google.devtools.depan.graph.api.Node;
import com.google.devtools.depan.graph.api.Relation;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="leeca@google.com">Lee Carver</a>
 * 
 * @param <T> Node content type.
 */
public class BasicGraph<T> implements Graph<T> {
  
  private final Map<T, BasicNode<? extends T>> nodes = Maps.newHashMap();

  private final Set<BasicEdge<? extends T>> edges = Sets.newHashSet();

  @SuppressWarnings("serial")
  public static class DuplicateNodeException
      extends IllegalArgumentException {

    /**
     * @param id
     */
    public DuplicateNodeException(String nodeId) {
      super("duplicate node id " + nodeId);
    }
  }

  @SuppressWarnings("serial")
  public static class DuplicateEdgeException
      extends IllegalArgumentException {

    /**
     * 
     */
    public DuplicateEdgeException(String relation, String head, String tail) {
      super("edge already exists: "
        + relation + " from " + head + " to " + tail + ".");
    }
  }

  /**
   * 
   */
  public BasicGraph() {
  }

  /////////////////////////////////////
  // Basic Graph<> methods for Nodes

  @Override
  public void addNode(Node<? extends T> node) {
    if (nodes.containsKey(node.getId())) {
      throw new DuplicateNodeException(node.getId().toString());
    }

    nodes.put(node.getId(), (BasicNode<? extends T>) node);
  }

  @Override
  public BasicNode<? extends T> findNode(T id) {
    final BasicNode<? extends T> result = nodes.get(id);
    return result;
  }

  /////////////////////////////////////
  // Basic Graph<> methods for Edges

  @Override
  public void addEdge(Edge<? extends T> edge) {
    edges.add((BasicEdge<? extends T>) edge);
  }

  @Override
  public BasicEdge<? extends T> findEdge(final Relation relation,
      final Node<? extends T> head, final Node<? extends T> tail) {
    for (BasicEdge<? extends T> edge : edges) {
      if ((relation == edge.getRelation()) &&
          (head == edge.getHead()) &&
          (tail == edge.getTail())) {
        return edge;
      }
    }

    // Not found
    return null;
  }

  /////////////////////////////////////
  // Factory methods

  /**
   * Basic Factory method for creating Edges.
   * Extending classes should override this.
   * 
   * @param relation
   * @param head
   * @param tail
   * @return A new {@link BasicEdge}.
   */
  protected BasicEdge<? extends T> createBasicEdge(
      Relation relation, Node<? extends T> head, Node <? extends T>tail) {
    return new BasicEdge<T>(relation, head, tail);
  }

  /////////////////////////////////////
  // Node methods

  /**
   * Properly install the node into the node set.
   * 
   * @param result
   * @return
   */
  protected void addBasicNode(BasicNode<? extends T> addNode) {
    addNode(addNode);
  }

  /**
   * Return an existing node if the newNode is already known to the graph.
   * 
   * @param newNode new Node.
   * @return if newNode matches a known node, the known nodes is returned.
   *   Otherwise newNode is returned.
   */
  public BasicNode<? extends T> mapNode(BasicNode<? extends T> newNode) {
    BasicNode<? extends T> graphNode = findNode(newNode.getId());
    if (graphNode != null) {
      return graphNode;
    }

    addBasicNode(newNode);
    return newNode;
  }

  /**
   * Returns the collection of Nodes in this graph.
   * 
   * @return the collection of Nodes in this graph.
   */
  public Collection<? extends BasicNode<? extends T>> getNodes() {
    return Collections.unmodifiableCollection(nodes.values());
  }

  /////////////////////////////////////
  // Edge methods

  /**
   * @param result
   * @return
   */
  protected void addBasicEdge(BasicEdge<? extends T> newEdge) {
    addEdge(newEdge);
  }

  /**
   * Returns the collection of Nodes in this graph.
   * 
   * @return the collection of Nodes in this graph.
   */
  public Collection<? extends BasicEdge<? extends T>> getEdges() {
    return Collections.unmodifiableCollection(edges);
  }

  /**
   * Basic Factory method for creating Edges.
   * Extending classes should override this.
   */
  protected BasicEdge<? extends T> installEdge(Relation relation,
      Node<? extends T> head, Node <? extends T>tail) {
    BasicEdge<? extends T> result = createBasicEdge(relation, head, tail);
    addBasicEdge(result);
    return result;
  }

  /**
   * @inheritDoc
   */
  public BasicEdge<? extends T> addEdge(Relation relation,
      Node<? extends T> head, Node<? extends T> tail) {
    if (null != findEdge(relation, head, tail)) {
      throw new DuplicateEdgeException(
        relation.toString(), head.toString(), tail.toString());
    }

    return installEdge(relation, head, tail);
  }

  /**
   * @inheritDoc
   */
  public BasicEdge<? extends T> getEdge(Relation relation,
      Node<? extends T> head, Node<? extends T> tail) {
    BasicEdge<? extends T> result = findEdge(relation, head, tail);
    if (null != result) {
      return result;
    }

    // Return a new one
    return installEdge(relation, head, tail);
  }

}
