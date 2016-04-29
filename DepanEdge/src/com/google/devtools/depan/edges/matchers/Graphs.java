package com.google.devtools.depan.edges.matchers;

import com.google.devtools.depan.edges.trees.SuccessorEdges;
import com.google.devtools.depan.edges.trees.SuccessorsMap;
import com.google.devtools.depan.graph.api.EdgeMatcher;
import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphEdgeMatcher;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Collected Edge Matcher algorithms (side-effect free) for DepAn Graphs.
 * 
 * Many of these methods originated from earlier version of GraphModel.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class Graphs {
  private Graphs() {
    // Prevent instantiation of this utility class.
  }

  /**
   * Computes a set of nodes in this graph that satisfy any of the filters in
   * <code>finder</code> using <code>nodeSet</code> as input.
   *
   * @param nodeSet A collection of nodes that are used as starting points.
   * @param finder Finder object that contains the filters.
   * @return A collection of nodes that satisfy filters with the given input.
   */
  static public Collection<GraphNode> getRelated(
      GraphModel model,
      Collection<GraphNode> nodeSet,
      GraphEdgeMatcher edgeMatcher) {
    Collection<GraphNode> result = Sets.newHashSet();

    for (GraphEdge edge : model.getEdges()) {
      if (nodeSet.contains(edge.getHead()) &&
          edgeMatcher.edgeForward(edge)) {
        result.add(edge.getTail());
      }
      if (nodeSet.contains(edge.getTail()) &&
          edgeMatcher.edgeReverse(edge)) {
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
  static public Map<GraphNode, Integer> getForwardRelationCount(
      GraphModel model,
      Collection<GraphNode> headNodes,
      RelationSet relationSet) {

    Map<GraphNode, Integer> result = populateRelationCount(headNodes);
    for (GraphEdge edge : model.getEdges()) {
      GraphNode head = edge.getHead();
      Integer update = result.get(head);
      if (null == update) {
        continue;
      }

      if (!relationSet.contains(edge.getRelation())) {
        continue;
      }
      result.put(head, update + 1);
    }
    return result;
  }

  /**
  * Create a map for each {@code headNode} to it's count of reverse (incoming)
  * edges in the {@code RelationSet}.  Even nodes with zero edge counts are
  * included.
  *
  * @param tailNodes nodes to compute edge count
  * @param relations relations to use for edge count
  * @return {@code Map} of each node to its edge count
  */
  static public Map<GraphNode, Integer> getReverseRelationCount(
      GraphModel model,
      Collection<GraphNode> tailNodes,
      RelationSet relationSet) {

    Map<GraphNode, Integer> result = populateRelationCount(tailNodes);
    for (GraphEdge edge : model.getEdges()) {
      GraphNode tail = edge.getTail();
      Integer update = result.get(tail);
      if (null == update) {
        continue;
      }

      if (!relationSet.contains(edge.getRelation())) {
        continue;
      }
      result.put(tail, update + 1);
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
  static private Map<GraphNode, Integer> populateRelationCount(
      Collection<GraphNode> nodes) {
    Map<GraphNode, Integer> result = Maps.newHashMap();
    for (GraphNode node : nodes) {
      result.put(node, 0);
    }
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
  static public Map<GraphNode, ? extends SuccessorEdges> computeSuccessorHierarchy(
          GraphModel model, EdgeMatcher<String> relations) {

    SuccessorsMap builder = new SuccessorsMap();

    // Only include nodes that participate in the relations.
    for (GraphEdge edge : model.getEdges()) {
      if (relations.edgeForward(edge)) {
        builder.addForwardEdge(edge);
      }
      else if (relations.edgeReverse(edge)) {
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
  static public Map<GraphNode, ? extends SuccessorEdges> computeSpanningHierarchy(
          GraphModel model, EdgeMatcher<String> edgeMatcher) {

    SuccessorsMap builder = new SuccessorsMap();

    Set<GraphNode> visited = Sets.newHashSet();

    // Only include nodes that participate in the relations.
    for (GraphEdge edge : model.getEdges()) {
      // No self-loops in a spanning tree.
      if (edge.getHead() == edge.getTail()) {
        continue;
      }

      // On forward matches, include the link only
      // if the tail has not yet been visited.
      if (edgeMatcher.edgeForward(edge)) {
        if (false == visited.contains((edge.getTail()))) {
          builder.addForwardEdge(edge);
          visited.add((edge.getTail()));
        }
        continue;
      }

      // For spanning hierarchies, each edge gets added only once.
      // And the forward direction is preferred if both are allowed.
      // On reverse matches, include the link only
      // if the head has not yet been visited.
      if (edgeMatcher.edgeReverse(edge)) {
        if (false == visited.contains((edge.getHead()))) {
          builder.addReverseEdge(edge);
          visited.add((edge.getHead()));
        }
        continue;
      }
    }
    return builder.getSuccessorMap();
  }
}
