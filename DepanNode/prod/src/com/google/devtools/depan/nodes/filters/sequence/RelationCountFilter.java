/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.nodes.filters.sequence;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.graph.registry.RelationRegistry;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.RelationSets;
import com.google.devtools.depan.nodes.Graphs;
import com.google.devtools.depan.nodes.filters.model.ContextKey;

import com.google.common.collect.Sets;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class RelationCountFilter extends BasicFilter {

  /** Set of relations used to compute all edge counts. */
  private RelationSet relationSet;

  /** Filter to use for forward edge counts. */
  private final CountPredicate forwardTest;

  /** Filter to use for reverse edge counts. */
  private final CountPredicate reverseTest;

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
  public RelationCountFilter(
      RelationSet relationSet,
      CountPredicate forwardTest,
      CountPredicate reverseTest) {
    super("- unnamed relation count filter -", null);
    this.relationSet = relationSet;
    this.forwardTest = forwardTest;
    this.reverseTest = reverseTest;
  }

  @Override
  public String buildSummary() {
    if (null == relationSet) {
      // Should never happen ...
      return "- null relation set -";
    }
    if (RelationSets.ALL.equals(relationSet)) {
      return "- All relations -";
    }
    if (RelationSets.EMPTY.equals(relationSet)) {
      return "- No relations -";
    }
    int count = 0;
    for (Relation relation : RelationRegistry.getRegistryRelations()) {
      if (relationSet.contains(relation)) {
        count++;
      }
    }
    return MessageFormat.format("Tests {0} relations", count);
  }


  @Override
  public Collection<? extends ContextKey> getContextKeys() {
    return KEYS_UNIVERSE;
  }

  @Override
  public Collection<GraphNode> computeNodes(Collection<GraphNode> nodes) {
    RelationCountBuilder builder = new RelationCountBuilder();

    if (null != forwardTest) {
      Map<GraphNode, Integer> forwardNodes =
          Graphs.getForwardRelationCount(
              getContextUniverse(), nodes, relationSet);
      builder.select(forwardTest, forwardNodes);
    }

    if (null != reverseTest) {
      Map<GraphNode, Integer> reverseNodes =
          Graphs.getReverseRelationCount(
              getContextUniverse(), nodes, relationSet);
      builder.select(reverseTest, reverseNodes);
    }

    return builder.getResult();
  }

  public RelationSet getRelationSet() {
    return relationSet;
  }

  public void setRelationSet(RelationSet relationSet) {
    this.relationSet = relationSet;
  }

  public CountPredicate getForwardTest() {
    return forwardTest;
  }

  public CountPredicate getReverseTest() {
    return reverseTest;
  }

  /**
   * Construct a set of relation-counted nodes by incrementally filter
   * multiple lists of (node, count) pairs.
   */
  private static class RelationCountBuilder {
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
        CountPredicate test, Map<GraphNode, Integer> source) {
      for (Map.Entry<GraphNode, Integer> entry: source.entrySet()) {
        if (test.include(entry.getValue())) {
          result.add(entry.getKey());
        }
      }
    }
  }
}
