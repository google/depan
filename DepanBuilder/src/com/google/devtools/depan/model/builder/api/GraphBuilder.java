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

package com.google.devtools.depan.model.builder.api;

import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;

/**
 * Interface for a Class capable of constructing a graph.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public interface GraphBuilder {
  /**
   * Add the given Edge to the list of edges in the graph.
   * 
   * @param edge new Edge.
   * @return the newly inserted Edge.
   */
  public GraphEdge addEdge(GraphEdge edge);

  /**
   * Provide a {@link GraphNode} that a matches the supplied id.
   * A {@code null} indicates that the supplied {@code node} is not part of the
   * graph.
   * 
   * @param id Node to find
   * @return a for the supplied id, or {@code null} if no {@link GraphNode}
   *   for the id is present in the graph.
   */
  public GraphNode findNode(String id);

  /**
   * Insert the given Node in the graph.  The node must not exist in the graph
   * prior to this call.  Use {@link #mapNode(GraphNode)} if an
   * {@link GraphNode} for the supplied node is required to add an edge.
   * 
   * @param node new Node.
   * @return the newly inserted Node.
   */
  public GraphNode newNode(GraphNode node);

  /**
   * Return an existing node if the newNode is already known to the graph.
   * 
   * @param newNode new Node.
   * @return if newNode matches a known node, the known node is returned.
   *   Otherwise, newNode added to the graph and returned.
   */
  public GraphNode mapNode(GraphNode newNode);

  /**
   * Provide the complete GraphModel containing the added nodes and edges.
   * Multiple calls have undefined results.  A {@link GraphBuilder} is expected
   * to be used once, and return an immutable graph.
   */
  public GraphModel createGraphModel();
}
