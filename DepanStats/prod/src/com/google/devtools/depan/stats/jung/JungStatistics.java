/*
 * Copyright 2017 The Depan Project Authors
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
package com.google.devtools.depan.stats.jung;

import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.uci.ics.jung.algorithms.importance.KStepMarkov;
import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * Compute and hold Jung-based statistics on a graph.
 * 
 * @author <a href='mailto:leeca@pnambic.com'>Lee Carver</a>
 */
public class JungStatistics {

  private final DirectedGraph<GraphNode, GraphEdge> jungInfo;

  private final Map<GraphNode, Double> ranking;

  private final Collection<GraphNode> roots;

  /////////////////////////////////////
  // Public API
  public JungStatistics(
      DirectedGraph<GraphNode, GraphEdge> jungInfo,
      Map<GraphNode, Double> ranking,
      Collection<GraphNode> roots) {
    this.jungInfo = jungInfo;
    this.ranking = ranking;
    this.roots = roots;
  }

  public static JungStatistics build(
      GraphModel model, GraphEdgeMatcherDescriptor matcher) {
    DirectedGraph<GraphNode, GraphEdge> jungInfo =
        JungBuilder.build(model, matcher.getInfo());
    Map<GraphNode, Double> ranking = rankGraph(model.getNodesSet(), jungInfo);
    Collection<GraphNode> roots = calcRoots(ranking);
    return new JungStatistics(jungInfo, ranking, roots);
  }

  private static Map<GraphNode, Double> rankGraph(
      Set<GraphNode> nodes,
      DirectedGraph<GraphNode, GraphEdge> jungInfo) {

    KStepMarkov<GraphNode, GraphEdge> ranker =
        new KStepMarkov<GraphNode, GraphEdge>(jungInfo, nodes, 6, null);
    ranker.setRemoveRankScoresOnFinalize(false);
    ranker.evaluate();

    Map<GraphNode, Double> result = Maps.newHashMap();
    for (GraphNode node : jungInfo.getVertices()) {
      result.put(node, ranker.getVertexRankScore(node));
    }
    return result;
  }

  private static Collection<GraphNode> calcRoots(
      Map<GraphNode, Double> ranking) {
    List<GraphNode> result = Lists.newArrayList();
    for (Entry<GraphNode, Double> entry : ranking.entrySet()) {
      if (0.0 == entry.getValue()) {
        result.add(entry.getKey());
      }
    }
    return result;
  }

  /////////////////////////////////////
  // Accessors

  public Double getRank(GraphNode node) {
    Double rank = ranking.get(node);
    if (null != rank) {
      return rank.doubleValue();
    }
    return 0.0;
  }

  public Collection<GraphNode> getRankedNodes() {
    return ImmutableList.copyOf(ranking.keySet());
  }

  public int getDegree(GraphNode node) {
    return getPredecessorCount(node) + getSuccessorCount(node);
  }

  public int getPredecessorCount(GraphNode node) {
    if (jungInfo.containsVertex(node)) {
      return jungInfo.getPredecessorCount(node);
    }
    return 0;
  }

  public int getSuccessorCount(GraphNode node) {
    if (jungInfo.containsVertex(node)) {
      return jungInfo.getSuccessorCount(node);
    }
    return 0;
  }

  public Collection<GraphNode> getRootNodes() {
    return ImmutableList.copyOf(roots);
  }

  public boolean isRoot(GraphNode node) {
    return roots.contains(node);
  }

  public int getMaxDegree(Collection<GraphNode> nodes) {
    int result = 0;

    for (GraphNode node : nodes) {
      int degree = getDegree(node);
      result = Math.max(result, degree);
    }
    return result;
  }

  public double getMaxRank(Collection<GraphNode> nodes) {
    double result = 0.0;

    for (GraphNode node : nodes) {
      Double rank = getRank(node);
      result = Math.max(result, rank);
    }

    return (double) result;
  }
}
