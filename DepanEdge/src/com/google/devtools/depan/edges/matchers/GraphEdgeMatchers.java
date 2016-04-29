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
  public static GraphEdgeMatcher EMPTY = new GraphEdgeMatcher();

  // Only need one
  public static GraphEdgeMatcher FORWARD = new GraphEdgeMatcher() {

    @Override
    public boolean relationForward(Relation relation) {
      return true;
    }
  };

  public static GraphEdgeMatcher createForwardEdgeMatcher(
      final RelationSet relationSet) {
    return new GraphEdgeMatcher() {
  
        @Override
        public boolean relationForward(Relation relation) {
          return relationSet.contains(relation);
        }};
  }

  public static GraphEdgeMatcher createBinaryEdgeMatcher(
      final RelationSet forward, final RelationSet reverse) {
    return new GraphEdgeMatcher() {
        @Override
        public boolean relationForward(Relation relation) {
          return forward.contains(relation);
        }

        @Override
      public boolean relationReverse(Relation relation) {
        return reverse.contains(relation);
      }};
  }
}
