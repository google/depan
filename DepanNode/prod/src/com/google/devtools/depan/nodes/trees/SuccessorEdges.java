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

import com.google.common.collect.Lists;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphNode;

import java.util.Collection;
import java.util.Collections;

/**
 * SuccessorEdges provides a simple representation of complex
 * bi-directional relationships between nodes.
 * <p>
 * In addition to the basic "read-only" interface (SuccessorEdges),
 * this class includes a number of use abstract and concrete supporting
 * classes.  These include a basic Mutable class that defines the
 * abstract API for adding edges, and two implementation classes (Simple
 * and Frugal) that provide support for mutable SuccessorEdges.
 * 
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
public abstract class SuccessorEdges {

  /**
   * Provide the current Collection of forward Edges.
   * <p>
   * The returned collection and the members of that collection
   * may vary on each call.
   *
   * @return the forwardEdges
   */
  public abstract Collection<GraphEdge> getForwardEdges();

  /**
   * Provide the current Collection of reverse Edges.
   * <p>
   * The returned collection and the members of that collection
   * may vary on each call.
   *
   * @return the reverseEdges
   */
  public abstract Collection<GraphEdge> getReverseEdges();

  /**
   * Indicate whether there are any successor edges at all.
   * This avoids computing the set of successor nodes unless it is needed.
   * 
   * @return true iff successor edges are defined
   */
  public boolean hasSuccessors() {
    if (getForwardEdges().size() > 0) {
      return true;
    }
    if (getReverseEdges().size() > 0) {
      return true;
    }
    return false;
  }

  /**
   * Generate the current Collection of successor Nodes
   * from the Collection of successor Edges.
   * 
   * @return Collection of successor Nodes
   */
  public Collection<GraphNode> computeSuccessorNodes() {
    Collection<GraphNode> result = Lists.newArrayList();
    for (GraphEdge edge : getForwardEdges()) {
      result.add(edge.getTail());
    }
    for (GraphEdge edge : getReverseEdges()) {
      result.add(edge.getHead());
    }
    return result;
    
  }

  public static class Empty extends SuccessorEdges {

    @Override
    public Collection<GraphEdge> getForwardEdges() {
      return Collections.emptyList();
    }

    @Override
    public Collection<GraphEdge> getReverseEdges() {
      return Collections.emptyList();
    }
  }

  // Only need one instance of the empty SucccessorEdge
  public static final Empty EMPTY = new Empty();

  /**
   * A standard API for mutable variations. 
   */
  public static abstract class Mutable extends SuccessorEdges {

    /**
     * Add an Edge to the forward Collection.
     * 
     * @param edge Edge to add
     */
    public abstract void addForwardEdge(GraphEdge edge);

    /**
     * Add an Edge to the reverseCollection.
     * 
     * @param edge Edge to add
     */
    public abstract void addReverseEdge(GraphEdge edge);
  }

  /**
   * A basic implementation that allows for edge addition.
   */
  public static abstract class MutableEdges extends SuccessorEdges.Mutable {

    protected Collection<GraphEdge> forwardEdges;
    protected Collection<GraphEdge> reverseEdges;

    /**
     * Create a SuccessorEdges instance from two Edge Collections.
     * <p>
     * In normal use, the Collection provided to the constructor should
     * not be modified after the instance is created.
     * Any changes to the provided collections are reflected in the
     * SuccessorEdges instance.
     *
     * @param forwardEdges Collection of forward Edges
     * @param reverseEdges Collection of reverse Edges
     */
    protected MutableEdges(
        Collection<GraphEdge> forwardEdges,
        Collection<GraphEdge> reverseEdges) {
      this.forwardEdges = forwardEdges;
      this.reverseEdges = reverseEdges;
    }

    /**
     * @return the forwardEdges
     */
    @Override
    public Collection<GraphEdge> getForwardEdges() {
      return forwardEdges;
    }

    /**
     * @return the reverseEdges
     */
    @Override
    public Collection<GraphEdge> getReverseEdges() {
      return reverseEdges;
    }
  }

  /**
   * A basic implementation that allows for edge addition.
   */
  public static class Basic extends SuccessorEdges.MutableEdges {

    /**
     * Create a SuccessorEdges instance from two Edge Collections.
     * <p>
     * In normal use, the Collection provided to the constructor should
     * not be modified after the instance is created.
     * Any changes to the provided collections are reflected in the
     * SuccessorEdges instance.
     *
     * @param forwardEdges Collection of forward Edges
     * @param reverseEdges Collection of reverse Edges
     */
    public Basic(
        Collection<GraphEdge> forwardEdges,
        Collection<GraphEdge> reverseEdges) {
      super(forwardEdges, reverseEdges);
    }

    /**
     * Add an Edge to the forward Collection.
     * 
     * @param edge Edge to add
     */
    @Override
    public void addForwardEdge(GraphEdge edge) {
      this.getForwardEdges().add(edge);
    }

    /**
     * Add an Edge to the reverseCollection.
     * 
     * @param edge Edge to add
     */
    @Override
    public void addReverseEdge(GraphEdge edge) {
      this.getReverseEdges().add(edge);
    }
  }

  /**
   * Lightweight creation of successor edges, with minimal memory
   * allocation for empty directions.
   */
  public static class Frugal extends SuccessorEdges.MutableEdges {

    /**
     * Create a SuccessorEdges instance with empty Edge collections.
     * <p>
     * This constructor is suited for incremental construction of
     * a SuccessorEdge.
     * The underlying Collections use ArrayLists.
     */
    public Frugal() {
      super(newEmptyList(), newEmptyList());
    }

    protected static Collection<GraphEdge> newEmptyList() {
      return Collections.<GraphEdge>emptyList();
    }

    protected Collection<GraphEdge> newEdgeList() {
      return Lists.<GraphEdge>newArrayList();
    }

    /**
     * Add an Edge to the forward Collection.
     * 
     * @param edge Edge to add
     */
    @Override
    public void addForwardEdge(GraphEdge edge) {
      if (forwardEdges.isEmpty()) {
        this.forwardEdges = newEdgeList();
      }
      this.forwardEdges.add(edge);
    }

    /**
     * Add an Edge to the reverseCollection.
     * 
     * @param edge Edge to add
     */
    @Override
    public void addReverseEdge(GraphEdge edge) {
      if (this.reverseEdges.isEmpty()) {
        this.reverseEdges = newEdgeList();
      }
      this.reverseEdges.add(edge);
    }
  }
}
 