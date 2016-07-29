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
import com.google.devtools.depan.graph.registry.RelationRegistry;
import com.google.devtools.depan.model.GraphEdgeMatcher;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.nodes.Graphs;
import com.google.devtools.depan.nodes.filters.model.ContextKey;

import com.google.common.collect.Lists;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class EdgeMatcherFilter extends BasicFilter {

  private GraphEdgeMatcher matcher;

  public EdgeMatcherFilter(GraphEdgeMatcher matcher) {
    this.matcher = matcher;
  }

  public GraphEdgeMatcher getEdgeMatcher() {
    return matcher;
  }

  public void setEdgeMatcher(GraphEdgeMatcher matcher) {
    this.matcher = matcher;
  }

  @Override
  public Collection<GraphNode> computeNodes(Collection<GraphNode> nodes) {
    return Graphs.getRelated(getContextUniverse(), nodes, matcher);
  }

  @Override
  public Collection<? extends ContextKey> getContextKeys() {
    return KEYS_UNIVERSE;
  }

  @Override
  public String buildSummary() {
    List<Relation> fwdRels = buildForwards();
    List<Relation> revRels = buildReverses();
    if (fwdRels.isEmpty() && revRels.isEmpty()) {
      return "- emtpy matcher -";
    }
    if (revRels.isEmpty()) {
      if (1 == fwdRels.size()) {
        return MessageFormat.format(
            "Forward matches {0}", fwdRels.get(0).getForwardName());
      }
      return MessageFormat.format(
          "Matches {0} forward relations", fwdRels.size());
    }
    if (fwdRels.isEmpty()) {
      if (1 == revRels.size()) {
        return MessageFormat.format(
            "Reverse matches {0}", revRels.get(0).getReverseName());
      }
      return MessageFormat.format(
          "Matches {0} reverse relations", revRels.size());
    }
    return MessageFormat.format(
        "Matches {0} forward relations and {0} reverse relations",
        fwdRels.size(), revRels.size());
  }

  private List<Relation> buildForwards() {
    List<Relation> result = Lists.newArrayList();
    for (Relation relation : RelationRegistry.getRegistryRelations()) {
      if (matcher.relationForward(relation)) {
        result.add(relation);
      }
    }
    return result;
  }

  private List<Relation> buildReverses() {
    List<Relation> result = Lists.newArrayList();
    for (Relation relation : RelationRegistry.getRegistryRelations()) {
      if (matcher.relationReverse(relation)) {
        result.add(relation);
      }
    }
    return result;
  }
}
