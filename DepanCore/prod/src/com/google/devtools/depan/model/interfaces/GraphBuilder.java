/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.model.interfaces;

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
   * Method to get the Graph Object.
   * 
   * @return a Graph<Element> instance.
   */
  public GraphModel getGraph();

  /**
   * Add the given Edge to the list of edges in the graph.
   * 
   * @param edge new Edge.
   * @return the newly inserted Edge.
   */
  public GraphEdge addEdge(GraphEdge edge);

  /**
   * Insert the given Node in the graph.
   * 
   * @param node new Node.
   * @return the newly inserted Node.
   */
  public GraphNode newNode(GraphNode node);

  /**
   * Return an existing node if the newNode is already known to the graph.
   * 
   * @param newNode new Node.
   * @return if newNode matches a known node, the known nodes is returned.
   *   Otherwise newNode is returned.
   */
  public GraphNode mapNode(GraphNode newNode);
  
}
