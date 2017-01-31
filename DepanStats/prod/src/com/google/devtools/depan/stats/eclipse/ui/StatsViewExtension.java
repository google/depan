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
package com.google.devtools.depan.stats.eclipse.ui;

import com.google.devtools.depan.eclipse.cm.ColorMapDefJet;
import com.google.devtools.depan.eclipse.visualization.ogl.ColorMap;
import com.google.devtools.depan.eclipse.visualization.ogl.GLConstants;
import com.google.devtools.depan.eclipse.visualization.ogl.GLEntity;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeColorSupplier;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeRatioSupplier;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeShapeSupplier;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeSizeSupplier;
import com.google.devtools.depan.eclipse.visualization.ogl.ShapeFactory;
import com.google.devtools.depan.graph.api.EdgeMatcher;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptors;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.eclipse.ui.plugins.ViewExtension;
import com.google.devtools.depan.view_doc.model.NodeColorMode;
import com.google.devtools.depan.view_doc.model.NodeRatioMode;
import com.google.devtools.depan.view_doc.model.NodeShapeMode;
import com.google.devtools.depan.view_doc.model.NodeSizeMode;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.uci.ics.jung.algorithms.importance.KStepMarkov;
import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * Install metrics-base rendering criteria.
 * 
 * @author <a href='mailto:leeca@pnambic.com'>Lee Carver</a>
 */
public class StatsViewExtension implements ViewExtension {

  private static final ColorMap DEFAULT_CM =
      new ColorMap(ColorMapDefJet.CM, 256);
  private static final NodeColorSupplier ROOT_COLOR =
      new NodeColorSupplier.Monochrome(Color.GREEN);

  // Color modes
  public static final NodeColorMode COLOR_DEGREE_MODE_ID =
      new NodeColorMode.Labeled("Degree");

  public static final NodeColorMode COLOR_VOLTAGE_MODE_ID =
      new NodeColorMode.Labeled("Voltage");

  public static final NodeColorMode COLOR_ROOT_MODE_ID =
      new NodeColorMode.Labeled("Root");

  private static final List<NodeColorMode> NODE_COLOR_MODES =
      Lists.newArrayList(
          COLOR_DEGREE_MODE_ID,
          COLOR_VOLTAGE_MODE_ID,
          COLOR_ROOT_MODE_ID);

  // Shape modes
  public static final NodeShapeMode SHAPE_DEGREE_MODE_ID =
      new NodeShapeMode.Labeled("Degree");

  private static final List<NodeShapeMode> NODE_SHAPE_MODES =
      Lists.newArrayList(SHAPE_DEGREE_MODE_ID);

  // Shape modes
  public static final NodeRatioMode RATIO_DEGREE_MODE_ID =
      new NodeRatioMode.Labeled("Degree");

  private static final List<NodeRatioMode> NODE_RATIO_MODES =
      Lists.newArrayList(RATIO_DEGREE_MODE_ID);

  // Size modes
  public static final NodeSizeMode SIZE_DEGREE_MODE_ID =
      new NodeSizeMode.Labeled("Degree");
  public static final NodeSizeMode SIZE_VOLTAGE_MODE_ID =
      new NodeSizeMode.Labeled("Voltage");

  private static final List<NodeSizeMode> NODE_SIZE_MODES =
      Lists.newArrayList(SIZE_DEGREE_MODE_ID, SIZE_VOLTAGE_MODE_ID);

  // TODO: preferences for these MIN / MAX values
  private static final float STD_SIZE = 15f;
  private static final float MIN_SIZE = 0.5f * STD_SIZE;
  private static final float MAX_SIZE = 2f * STD_SIZE;

  /////////////////////////////////////
  // Public API

  @Override
  public Collection<NodeColorMode> getNodeColorModes() {
    return NODE_COLOR_MODES;
  }

  @Override
  public Collection<NodeRatioMode> getNodeRatioModes() {
    return NODE_RATIO_MODES;
  }

  @Override
  public Collection<NodeShapeMode> getNodeShapeModes() {
    return NODE_SHAPE_MODES;
  }

  @Override
  public Collection<NodeSizeMode> getNodeSizeModes() {
    return NODE_SIZE_MODES;
  }

  @Override
  public void deriveDetails(ViewEditor editor) {
  }

  @Override
  public void prepareView(ViewEditor editor) {
    // Step 1: Build Jung graph.
    GraphModel model = editor.getViewGraph();
    EdgeMatcher<String> matcher = getEdgeMatcher(editor);
    DirectedGraph<GraphNode, GraphEdge> jungInfo =
        JungBuilder.build(model, matcher);

    // Step 2: Rank nodes and extract summary statistics.
    int maxDegree = calcMaxDegree(model.getNodes(), jungInfo);

    Map<GraphNode, Double> ranking = rankGraph(model.getNodesSet(), jungInfo);
    Collection<GraphNode> roots = calcRoots(ranking);
    float maxRank = calcMaxRank(ranking);

    // Step 3:  Assign analysis results to rendering properties.
    float maxDegFlt = maxDegree;  // Hoist autoboxing out of loop.
    for (GraphNode node : model.getNodes()) {
      int degree = getNodeDegree(jungInfo, node);

      Double rankVal = ranking.get(node);
      float rank = (null == rankVal) ? 0.0f : rankVal.floatValue();

      NodeColorSupplier degreeColor =
          getColorSupplier(DEFAULT_CM, degree, maxDegFlt);
      editor.setNodeColorByMode(node, COLOR_DEGREE_MODE_ID, degreeColor);
      NodeColorSupplier voltageColor =
          getColorSupplier(DEFAULT_CM, rank, maxRank);
      editor.setNodeColorByMode(node, COLOR_VOLTAGE_MODE_ID, voltageColor);

      editor.setNodeShapeByMode(
          node, SHAPE_DEGREE_MODE_ID, getShapeSupplier(degree));

      NodeSizeSupplier degreeSize = getSizeSupplier(degree, maxDegFlt);
      editor.setNodeSizeByMode(node, SIZE_DEGREE_MODE_ID, degreeSize);
      NodeSizeSupplier voltageSize = getSizeSupplier(rank, maxRank);
      editor.setNodeSizeByMode(node, SIZE_VOLTAGE_MODE_ID, voltageSize);

      NodeRatioSupplier degreeRatio = getRatioSupplier(degree, maxDegFlt);
      editor.setNodeRatioByMode(node, RATIO_DEGREE_MODE_ID, degreeRatio);
    }

    for (GraphNode node : roots) {
      editor.setNodeColorByMode(node, COLOR_ROOT_MODE_ID, ROOT_COLOR);
      
    }
  }

  private NodeColorSupplier getColorSupplier(ColorMap cm, float value, float range) {
    float percent = divSafe(value, range, 0.0f);
    Color color = cm.getColor(percent);
    return new NodeColorSupplier.FillStroke(color, GLConstants.FOREGROUND);
  }

  private NodeRatioSupplier getRatioSupplier(float value, float range) {
    return new NodeRatioSupplier.Fixed(divSafe(value, range, 0.0f));
  }

  private NodeShapeSupplier getShapeSupplier(int degree) {
    GLEntity shape = getShape(degree);
    return new NodeShapeSupplier.Fixed(shape);
  }

  private GLEntity getShape(int degree) {
    if (degree <= 2) {
      return ShapeFactory.createEllipse();
    }
    if (degree <= 4) {
      return ShapeFactory.createRegularPolygon(degree);
    }
    return ShapeFactory.createRegularStar(degree);
  }

  private NodeSizeSupplier getSizeSupplier(float value, float range) {
    float size = getSize(value, range);
    return new NodeSizeSupplier.Fixed(size);
  }

  private static float getSize(float value, float range) {
    float percent = divSafe(value, range, 0.5f);
    if (percent < 0.5f) {
      float scale = 2.0f * percent;
      return (float) (MIN_SIZE + (STD_SIZE - MIN_SIZE) * scale);
    }
    if (percent > 0.5f) {
      float scale = 2.0f * (percent - 0.5f);
      return (float) (STD_SIZE + (MAX_SIZE - STD_SIZE) * scale);
    }
    return STD_SIZE;
  }

  /**
   * In a completely disconnected graph, maxRank and maxDegree can both
   * be zero.  Things should still render properly.
   */
  private static float divSafe(float num, float den, float alt) {
    if (den == 0.0) {
      return alt;
    }
    return num / den;
  }

  private EdgeMatcher<String> getEdgeMatcher(ViewEditor editor) {
    String matcher = editor.getOption(StatsPreferences.STATS_MATCHER_ID);
    return GraphEdgeMatcherDescriptors.FORWARD.getInfo();
  }

  private Map<GraphNode, Double> rankGraph(
      Set<GraphNode> nodes,
      DirectedGraph<GraphNode, GraphEdge> jungInfo) {

    KStepMarkov<GraphNode, GraphEdge> ranker =
        new KStepMarkov<GraphNode, GraphEdge>(jungInfo, nodes, 6, null);
    ranker.setRemoveRankScoresOnFinalize(false);
    ranker.evaluate();

    Map<GraphNode, Double> result = Maps.newHashMap();
    for (GraphNode node : nodes) {
      result.put(node, ranker.getVertexRankScore(node));
    }
    return result;
  }

  /**
   * Degree could vary, based on ins or outs only, or based on edges
   * instead of successors.
   */
  private int getNodeDegree(
      DirectedGraph<GraphNode, GraphEdge> jungInfo, GraphNode node) {
    return jungInfo.getPredecessorCount(node)
        + jungInfo.getSuccessorCount(node);
  }

  private int calcMaxDegree(
      Collection<GraphNode> nodes,
      DirectedGraph<GraphNode, GraphEdge> jungInfo) {
    int result = 0;

    for (GraphNode node : nodes) {
      int degree = getNodeDegree(jungInfo, node);
      result = Math.max(result, degree);
    }
    return result;
  }

  private Collection<GraphNode> calcRoots(Map<GraphNode, Double> ranking) {
    List<GraphNode> result = Lists.newArrayList();
    for (Entry<GraphNode, Double> entry : ranking.entrySet()) {
      if (0.0 == entry.getValue()) {
        result.add(entry.getKey());
      }
    }
    return result;
  }

  private float calcMaxRank(Map<GraphNode, Double> ranking) {
    double result = 0.0;

    for (Double rank : ranking.values()) {
      if (null != rank) {
        result = Math.max(result, rank.doubleValue());
      }
    }

    return (float) result;
  }
}
