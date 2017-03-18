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
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.stats.jung.JungStatistics;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.eclipse.ui.plugins.ViewExtension;
import com.google.devtools.depan.view_doc.eclipse.ui.plugins.ViewExtensionRegistry;
import com.google.devtools.depan.view_doc.model.NodeColorMode;
import com.google.devtools.depan.view_doc.model.NodeRatioMode;
import com.google.devtools.depan.view_doc.model.NodeShapeMode;
import com.google.devtools.depan.view_doc.model.NodeSizeMode;

import com.google.common.collect.Lists;

import java.awt.Color;
import java.util.Collection;
import java.util.List;

/**
 * Install metrics-base rendering criteria.
 * 
 * @author <a href='mailto:leeca@pnambic.com'>Lee Carver</a>
 */
public class StatsViewExtension implements ViewExtension {

  public static final String EXTENSION_ID =
      "com.google.devtools.depan.stats.eclipse.ui.StatsViewExtension";

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

  public static StatsViewExtension getInstance() {
    ViewExtension result =
        ViewExtensionRegistry.getRegistryExtension(EXTENSION_ID);
    return (StatsViewExtension) result;
  }

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
    StatsExtensionData data = StatsExtensionData.getStatsData(editor, this);
    data.calcJungStatistics(model);
    JungStatistics stats = data.getJungStatistics();

    // Step 2: Extract summary statistics.
    int maxDegree = stats.getMaxDegree(model.getNodes());
    double maxRank = stats.getMaxRank(model.getNodes());

    // Hoist auto-boxing out of loop.
    float maxDegreeFlt = (float) maxDegree;
    float maxRankFlt = (float) maxRank;

    // Step 3:  Assign analysis results to rendering properties.
    for (GraphNode node : model.getNodes()) {
      int degree = stats.getDegree(node);
      double rank = stats.getRank(node);

      float degreeFlt = (float) degree;
      float rankFlt = (float) rank;

      NodeColorSupplier degreeColor =
          getColorSupplier(DEFAULT_CM, degreeFlt, maxDegreeFlt);
      editor.setNodeColorByMode(node, COLOR_DEGREE_MODE_ID, degreeColor);
      NodeColorSupplier voltageColor =
          getColorSupplier(DEFAULT_CM, rankFlt, maxRankFlt);
      editor.setNodeColorByMode(node, COLOR_VOLTAGE_MODE_ID, voltageColor);

      editor.setNodeShapeByMode(
          node, SHAPE_DEGREE_MODE_ID, getShapeSupplier(degree));

      NodeSizeSupplier degreeSize = getSizeSupplier(degreeFlt, maxDegreeFlt);
      editor.setNodeSizeByMode(node, SIZE_DEGREE_MODE_ID, degreeSize);
      NodeSizeSupplier voltageSize = getSizeSupplier(rankFlt, maxRankFlt);
      editor.setNodeSizeByMode(node, SIZE_VOLTAGE_MODE_ID, voltageSize);

      NodeRatioSupplier degreeRatio = getRatioSupplier(degreeFlt, maxDegreeFlt);
      editor.setNodeRatioByMode(node, RATIO_DEGREE_MODE_ID, degreeRatio);
    }

    for (GraphNode node : stats.getRootNodes()) {
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
}
