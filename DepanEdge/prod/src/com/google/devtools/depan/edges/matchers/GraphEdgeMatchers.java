/**
 * 
 */
package com.google.devtools.depan.edges.matchers;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.model.GraphEdgeMatcher;

/**
 * @author Standard Lee
 *
 */
public class GraphEdgeMatchers {

  private GraphEdgeMatchers() {
    // Prevent instantiation
  }

  // Only need one
  public static final GraphEdgeMatcher EMPTY = new GraphEdgeMatcher();

  // Only need one
  public static final GraphEdgeMatcher FORWARD = new GraphEdgeMatcher() {

    @Override
    public boolean relationForward(Relation relation) {
      return true;
    }
  };

  public static GraphEdgeMatcher createForwardEdgeMatcher(
      final RelationSet relationSet) {
    return new ForwardEdgeMatcher(relationSet);
  }

  public static GraphEdgeMatcher createBinaryEdgeMatcher(
      final RelationSet forward, final RelationSet reverse) {
    return new BinaryEdgeMatcher(forward, reverse);
  }
}
