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

package com.google.devtools.depan.nodes.trees;

import com.google.common.collect.Maps;

import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphNode;

import java.util.Map;

/**
 * Maintain the successor map for a set of nodes.
 * <p>
 * By default, any generated SuccessorEdge instances are Frugal
 * SuccessorEdges.
 * Extending classes can modify this by overriding
 * {@link #createSuccessorEdge()}.
 * 
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
public class SuccessorsMap {

  /** The essential map of Nodes to SuccessorEdges. */
  private Map<GraphNode, SuccessorEdges.Mutable> successorMap;

  /**
   * Generate a standard Map of SuccessorEdges.
   * 
   * @return Map from Nodes to SuccessorEdges
   */
  public SuccessorsMap() {
    this.successorMap = Maps.newHashMap();
  }

  /**
   * Provide a reference to the internal Map of Nodes to SuccessorEdges.
   * Changes to this SuccessorMap will be reflected in the return value.
   * 
   * @return the successorMap
   */
  public Map<GraphNode, ? extends SuccessorEdges> getSuccessorMap() {
    return successorMap;
  }

  /**
   * Add the Edge to the forward Collection for the head Node.
   * 
   * Since this is a forward edge, the head is the start of the path.
   * Therefore, the forward edge is a forward successor for the head.
   * 
   * @param edge Edge to add
   */
  public void addForwardEdge(GraphEdge edge) {
    // Since this is a forward edge, the head is the start of the path.
    // Therefore, the forward edge is a forward successor for the head.
    SuccessorEdges.Mutable successors = getNodeSuccessors(edge.getHead());
    successors.addForwardEdge(edge);
  }

  /**
   * Add the Edge to the reverse Collection for the tail Node.
   * 
   * Since this is a reverse edge, the tail is the start of the path.
   * Therefore, the reverse edge is a reverse successor for the tail.
   * 
   * @param edge Edge to add
   */
  public void addReverseEdge(GraphEdge edge) {
    SuccessorEdges.Mutable successors = getNodeSuccessors(edge.getTail());
    successors.addReverseEdge(edge);
  }

  /**
   * Factory method for generating new SuccessorEdges on demand.
   * This is also a hook method for the internal getNodeSuccessors()
   * Template method that is used during Edge insertion.
   * Extending classes may override as desired.
   * 
   * @return new SuccessorEdges instance
   */
  protected SuccessorEdges.Mutable createSuccessorEdge() {
    return new SuccessorEdges.Frugal();
  }

  /**
   * Utility method to ensure that a node's successor object is
   * present is the successor map.
   * 
   * @param result successor map that should contain a node entry
   * @param node key for successor object
   * @return successor object for node
   */
  private SuccessorEdges.Mutable getNodeSuccessors(GraphNode node) {
    SuccessorEdges.Mutable successors = successorMap.get(node);

    if (null == successors) {
      // TODO(leeca): If we ever need a real SuccessorMap class,
      // this should be a factory method instead of hard-wired
      // to a specific constructor of a specific class.
      successors = createSuccessorEdge();
      successorMap.put(node, successors);
    }
    return successors;
  }
}
