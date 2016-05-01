/*
 * Copyright 2008 The Depan Project Authors
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

package com.google.devtools.depan.paths.filters;

import com.google.common.collect.Sets;

import com.google.devtools.depan.edges.matchers.Graphs;
import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Match nodes based on the number of edges that they have.
 * In order to be counted, edges must be in the indicated relation set,
 * and forward (departing) and reverse (arriving) edges are counted separately.
 *
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class RelationCountMatcher implements PathMatcher {

  /** Set of relations used to compute all edge counts. */
  private final RelationSet relationSet;

  /** Filter to use for forward edge counts. */
  private final EdgeCountPredicate forwardTest;

  /** Filter to use for reverse edge counts. */
  private final EdgeCountPredicate reverseTest;

  /**
   * Define the set of relations that are used for counts, and the predicates
   * that determine which counts are included.
   * 
   * @param relationSet set of relations used to compute all edge counts
   * @param forwardTest filter for forward edge counts, or {@code null} if
   *     forward edge counts should not be used at all
   * @param reverseTest filter for reverse edge counts, or {@code null} if
   *     forward edge counts should not be used at all
   */
  public RelationCountMatcher(RelationSet relationSet,
      EdgeCountPredicate forwardTest, EdgeCountPredicate reverseTest) {
    this.relationSet = relationSet;
    this.forwardTest = forwardTest;
    this.reverseTest = reverseTest;
  }

  @Override
  public String getDisplayName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Collection<GraphNode> nextMatch(
      GraphModel graph, Collection<GraphNode> input) {
    RelationCountBuilder builder = new RelationCountBuilder();

    if (null != forwardTest) {
      Map<GraphNode, Integer> forwardNodes = 
          Graphs.getForwardRelationCount(graph, input, relationSet);
      builder.select(forwardTest, forwardNodes);
    }

    if (null != reverseTest) {
      Map<GraphNode, Integer> reverseNodes =
          Graphs.getReverseRelationCount(graph, input, relationSet);
      builder.select(reverseTest, reverseNodes);
    }

    return builder.getResult();
  }

  /**
   * Generalized test of edge counts.
   */
  public interface EdgeCountPredicate {

    /**
     * Returns {@code true} iff the provided edge count means that the
     * associated node should be included in the result.
     *
     * @param value count of edges for a node
     * @return {@code true} iff the node for this {@code value}
     *     should be selected
     */
    boolean include(Integer value);
  }

  /**
   * Provides an [closed, open) range-inclusion test for node counts.
   */
  public static class IncludeInRange implements EdgeCountPredicate {
    private final int loLimit;
    private final int hiLimit;

    public IncludeInRange(int loLimit, int hiLimit) {
      this.loLimit = loLimit;
      this.hiLimit = hiLimit;
    }

    @Override
    public boolean include(Integer value) {
      return (value >= loLimit) && (value < hiLimit);
    }
  }

  /**
   * Provides a strictly outside test for defined range. 
   */
  public static class IncludeOutside implements EdgeCountPredicate {
    private final int loLimit;
    private final int hiLimit;

    public IncludeOutside(int loLimit, int hiLimit) {
      this.loLimit = loLimit;
      this.hiLimit = hiLimit;
    }

    @Override
    public boolean include(Integer value) {
      return (value < loLimit) || (value > hiLimit);
    }
  }

  /**
   * Provides a strictly above test for the defined value.
   */
  public static class IncludeAbove implements EdgeCountPredicate {
    private final int loLimit;

    public IncludeAbove(int loLimit) {
      this.loLimit = loLimit;
    }

    @Override
    public boolean include(Integer value) {
      return value > loLimit;
    }
  }

  /**
   * Provides a strictly below test for the defined value.
   */
  public static class IncludeBelow implements EdgeCountPredicate {
    private final int hiLimit;

    public IncludeBelow(int hiLimit) {
      this.hiLimit = hiLimit;
    }

    @Override
    public boolean include(Integer value) {
      return value < hiLimit;
    }
  }

  /**
   * Provides a strict equals test for the defined value.
   */
  public static class IncludeEquals implements EdgeCountPredicate {
    private final int target;

    public IncludeEquals(int target) {
      this.target = target;
    }

    @Override
    public boolean include(Integer value) {
      return value == target;
    }
  }

  /**
   * Construct a set of relation-counted nodes by incrementally filter
   * multiple lists of (node, count) pairs.
   */
  public static class RelationCountBuilder {
    private Set<GraphNode> result = Sets.newHashSet();

    /**
     * Provide the current set of relation-counted nodes.
     * 
     * @return the current set of relation-counted nodes
     */
    public Set<GraphNode> getResult() {
      return result;
    }

    /**
     * Determine which of nodes from the source pass the given predicate.
     * 
     * @param test determines which edge counts are selectable
     * @param source collection of (node, count) pairs to filter
     */
    public void select(
        EdgeCountPredicate test, Map<GraphNode, Integer> source) {
      for (Map.Entry<GraphNode, Integer> entry: source.entrySet()) {
        if (test.include(entry.getValue())) {
          result.add(entry.getKey());
        }
      }
    }
  }
}
