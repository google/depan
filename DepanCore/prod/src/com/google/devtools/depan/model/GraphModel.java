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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.devtools.depan.graph.api.DirectedRelationFinder;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.api.RelationFinder;
import com.google.devtools.depan.graph.basic.BasicGraph;
import com.google.devtools.depan.model.interfaces.GraphBuilder;
import com.google.devtools.depan.view.SuccessorEdges;
import com.google.devtools.depan.view.SuccessorsMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Main Graph implementation. Also implements GraphBuilder, so it takes care of
 * building the graph.
 *
 * This class handle all the operations related to the Graph itself. A graph is
 * like a transition matrix (even it is not stored like that). it stores a list
 * of nodes, and a list of edges.
 *
 * It also offers convenient methods to look for a node, an edge, successors
 * and predecessors of a node given a set of relations, roots of the graph.
 *
 * Finally, it maintains a list of views opened for this graph, so we can apply
 * binary operations on views easily.
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
  public GraphModel() {
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
    Map<String, GraphNode> result = Maps.newHashMap();
    for (GraphNode node : getNodes()) {
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

  /**
   * Build a graph given a collection of edges. Nodes are those involved in at
   * least one relation.
   *
   * @param sourceEdges list of edges in the graph
   * @return a GraphModel made from the given collection of Edge, and Node
   *         involved in those relations.
   */
  public static GraphModel buildFromEdges(
      Collection<GraphEdge> sourceEdges) {

    GraphModel result = new GraphModel();

    for (GraphEdge e : sourceEdges) {
      GraphNode head = (GraphNode) result.mapNode(e.getHead());
      GraphNode tail = (GraphNode) result.mapNode(e.getTail());
      result.addEdge(e.getRelation(), head, tail);
    }

    return result;
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.graph.api.Graph
   *      #addEdge(com.google.devtools.depan.graph.api.Relation,
   *      com.google.devtools.depan.graph.api.Node,
   *      com.google.devtools.depan.graph.api.Node)
   */
  public GraphEdge addEdge(
      Relation relation,
      GraphNode head,
      GraphNode tail) {
    GraphEdge result = new GraphEdge(head, tail, relation);
    addEdge(result);
    return result;
  }

  /**
   * Compute a map of nodes to their successor edges
   * for the given relationship finder.
   * All matched edges are retained as forward edges.
   * <p>
   * If a node does not participate in the relationships, it is not
   * included in the map.
   *
   * @param relations relations to include in result map
   * @return map of nodes to successors
   */
  // TODO(leeca):  Move this to
  // RelationFinder.computeSuccessorHierarchy(Collection<Edge<? extends Element>>)
  public Map<GraphNode, ? extends SuccessorEdges>
      computeSuccessorHierarchy(RelationFinder relations) {

    SuccessorsMap builder = new SuccessorsMap();

    // Only include nodes that participate in the relations.
    for (GraphEdge edge : getEdges()) {
      if (relations.match(edge.getRelation())) {
        builder.addForwardEdge(edge);
      }
    }
    return builder.getSuccessorMap();
  }

  /**
   * Compute a map of nodes to their successor edges
   * for the given relationship finder.
   * All matched edges are retained as forward edges.
   * <p>
   * If a node does not participate in the relationships, it is not
   * included in the map.
   *
   * @param relations relations to include in result map
   * @return map of nodes to successors
   */
  // TODO(leeca):  Move this to
  // RelationFinder.computeSuccessorHierarchy(Collection<Edge<? extends Element>>)
  public Map<GraphNode, ? extends SuccessorEdges>
      computeSuccessorHierarchy(DirectedRelationFinder relations) {

    SuccessorsMap builder = new SuccessorsMap();

    // Only include nodes that participate in the relations.
    for (GraphEdge edge : getEdges()) {
      if (relations.matchForward(edge.getRelation())) {
        builder.addForwardEdge(edge);
      }
      else if (relations.matchBackward(edge.getRelation())) {
        builder.addReverseEdge(edge);
      }
    }
    return builder.getSuccessorMap();
  }

  /**
   * Compute a map of nodes to their successor edges
   * for the given relationship finder.
   * This successor map is strictly hierarchical, defining a spanning tree
   * over the graph.
   * No node is in the successor list of multiple parent nodes, so this is
   * safe for algorithms and tree renderings that don't handle loops or
   * dags well.
   * <p>
   * If a node does not participate in the relationships, it is not
   * included in the map.
   *
   * @param relations relations to include in result map
   * @return map of nodes to successors
   */
  // TODO(leeca):  Move this to
  // RelationFinder.computeSuccessorHierarchy(Collection<Edge<? extends Element>>)
  public Map<GraphNode, ? extends SuccessorEdges>
      computeSpanningHierarchy(DirectedRelationFinder relations) {

    SuccessorsMap builder = new SuccessorsMap();

    Set<GraphNode> visited = Sets.newHashSet();

    // Only include nodes that participate in the relations.
    for (GraphEdge edge : getEdges()) {

      // On forward matches, include the link only
      // if the tail has not yet been visited.
      if (relations.matchForward(edge.getRelation())) {
        if (false == visited.contains((edge.getTail()))) {
          builder.addForwardEdge(edge);
          visited.add((edge.getTail()));
        }
      }

      // For spanning hierarchies, each edge gets added only once.
      // And the forward direction is preferred if both are allowed.
      // On reverse matches, include the link only
      // if the head has not yet been visited.
      else if (relations.matchBackward(edge.getRelation())) {
        if (false == visited.contains((edge.getHead()))) {
          builder.addReverseEdge(edge);
          visited.add((edge.getHead()));
        }
      }
    }
    return builder.getSuccessorMap();
  }

  /**
   * Computes a set of nodes in this graph that satisfy any of the filters in
   * <code>finder</code> using <code>nodeSet</code> as input.
   *
   * @param nodeSet A collection of nodes that are used as starting points.
   * @param finder Finder object that contains the filters.
   * @return A collection of nodes that satisfy filters with the given input.
   */
  public Iterable<GraphNode> getRelated(
      Collection<GraphNode> nodeSet, DirectedRelationFinder finder) {
    Collection<GraphNode> result = Sets.newHashSet();

    for (GraphEdge edge : getEdges()) {
      if (nodeSet.contains(edge.getHead()) &&
          finder.matchForward(edge.getRelation())) {
        result.add(edge.getTail());
      }
      if (nodeSet.contains(edge.getTail()) &&
          finder.matchBackward(edge.getRelation())) {
        result.add(edge.getHead());
      }
    }
    return result;
  }

  /**
   * Create a map for each {@code headNode} to it's count of forward (departing)
   * edges in the {@code RelationSet}.  Even nodes with zero edge counts are
   * included.
   *
   * @param headNodes nodes to compute edge count
   * @param relations relations to use for edge count
   * @return {@code Map} of each node to its edge count
   */
  public Map<GraphNode, Integer> getForwardRelationCount(
    Collection<GraphNode> headNodes, RelationshipSet relations) {
    Map<GraphNode, Integer> result = populateRelationCount(headNodes);

    for (GraphEdge edge : getEdges()) {
      if (headNodes.contains(edge.getHead())
          && relations.matchForward(edge.getRelation())) {
        GraphNode head = edge.getHead();
        result.put(head, result.get(head) + 1);
      }
    }
    return result;
  }

  /**
   * Create a map for each {@code tailNode} to it's count of reverse (arriving)
   * edges in the {@code RelationSet}.  Even nodes with zero edge counts are
   * included.
   *
   * @param headNodes nodes to compute edge count
   * @param relations relations to use for edge count
   * @return {@code Map} of each node to its edge count
   */
  public Map<GraphNode, Integer> getReverseRelationCount(
    Collection<GraphNode> tailNodes, RelationshipSet relations) {
    Map<GraphNode, Integer> result = populateRelationCount(tailNodes);

    for (GraphEdge edge : getEdges()) {
      if (tailNodes.contains(edge.getTail())
          && relations.matchBackward(edge.getRelation())) {
        GraphNode tail = edge.getTail();
        result.put(tail, result.get(tail) + 1);
      }
    }
    return result;
  }

  /**
   * Populate a map of nodes to edge counts with zero as the count for every
   * node.  This ensures that nodes with no edges are included, and avoids a
   * test to check if the node is already in the result set.
   * 
   * @param nodes collection of nodes to include in set
   * @return map of all input nodes to the count zero
   */
  private Map<GraphNode, Integer> populateRelationCount(
      Collection<GraphNode> nodes) {
    Map<GraphNode, Integer> result = Maps.newHashMap();
    for (GraphNode node : nodes) {
      result.put(node, 0);
    }
    return result;
  }

  /////////////////////////////////////
  // Expanded Graph methods.
  // These should probably be pushed up into the Graph interface.

  public GraphModel newView() {
    return new GraphModel();
  }

  /**
   * Populate the subview with every edge in this Graph.
   * @param subview subview GraphModel to populate.
   */
  public void populateRelations(GraphModel subview) {
    Set<GraphNode> subviewNodes = subview.getNodesSet();
    GraphBuilder builder = subview.getBuilder();

    for (GraphEdge edge : getEdges()) {
      if (subviewNodes.contains(edge.getHead()) &&
          subviewNodes.contains(edge.getTail())) {
        builder.addEdge(edge);
      }
    }
  }

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

  public Collection<GraphEdge> getEdges(
      Collection<GraphNode> children) {
    Set<GraphNode> lookup = Sets.newHashSet(children);
    List<GraphEdge> result = Lists.newArrayList();

    for (GraphEdge edge : getEdges()) {
      if (lookup.contains(edge.getHead())) {
        result.add(edge);
      } else if (lookup.contains(edge.getTail())) {
        result.add(edge);
      }
    }
    return result;
  }

///////////////////////////////////////
// Builder Interface

  private class Builder extends GraphModelBuilder {

    @Override
    protected void addGraphEdge(GraphEdge edge) {
      GraphModel.this.addBasicEdge(edge);
    }

    @Override
    protected void addGraphNode(GraphNode node) {
      GraphModel.this.addBasicNode(node);
    }

    @Override
    protected GraphModel getGraphModel() {
      return GraphModel.this;
    }

  }

  /**
   * Creates and returns a new <code>GraphBuilder</code> object.
   *
   * @return A new Builder object
   */
  public GraphBuilder getBuilder() {
    return new Builder();
  }
}
