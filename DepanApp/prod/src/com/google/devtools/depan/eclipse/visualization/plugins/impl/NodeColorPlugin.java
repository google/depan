/*
 * Copyright 2008 Yohann R. Coppel.
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

package com.google.devtools.depan.eclipse.visualization.plugins.impl;

import java.awt.Color;
import java.util.Map;

import com.google.devtools.depan.eclipse.cm.ColorMapDefJet;
import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds.NodeColors;
import com.google.devtools.depan.eclipse.visualization.ogl.ColorMap;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeRenderingProperty;
import com.google.devtools.depan.eclipse.visualization.plugins.core.NodeRenderingPlugin;
import com.google.devtools.depan.model.GraphNode;

import edu.uci.ics.jung.graph.Graph;


/**
 * A plugin coloring a node and it's stroke. This plugin must be the last one,
 * because it can return false based on the color and the visibility. If it is
 * not, after becoming invisible, a node could not be shown again by a plugin
 * comming later.
 *
 * @author Yohann Coppel
 */
public class NodeColorPlugin<E> implements NodeRenderingPlugin {

  protected boolean isColorEnabled = true;
  protected boolean seedColoring = false;

  private int nodeNumber = 0;
  private int maxDegree = 0;
  private double maxImportance = 0;

  private final Graph<GraphNode, E> graph;
  private Map<GraphNode, Double> importances;

  private ColorMap cm = new ColorMap(ColorMapDefJet.CM, 256);
  private NodeColors nodeColors = NodeColors.getDefault();


  public NodeColorPlugin(Graph<GraphNode, E> graph,
      Map<GraphNode, Double> importances) {
    this.graph = graph;
    this.importances = importances;
    this.nodeNumber = 0;
  }

  @Override
  public void dryRun(NodeRenderingProperty p) {
    this.nodeNumber++;

    // find maximum degree
    int degree = graph.getPredecessorCount(p.node);
    maxDegree = Math.max(maxDegree, degree);

    // find maximum importance
    Double impObj = importances.get(p.node);
    double importance = (null == impObj) ? 0.0 : impObj.doubleValue();
    maxImportance = Math.max(maxImportance, importance);

    // store costly informations to retrieve at each frame.
    p.pluginStore.put(this, new NodeColorData(importance, degree));
  }

  @Override
  public boolean apply(NodeRenderingProperty p) {
    if (!isColorEnabled) {
      p.targetFillColor = NodeColors.getDefaultColor();
      return true;
    }
    @SuppressWarnings("unchecked")
    NodeColorData vals = (NodeColorData) p.pluginStore.get(this);

    Color color = guessColor(p);

    if (p.isSelected()) {
      p.targetStrokeColor = Color.red;
      p.targetFillColor = color.darker().darker();
      return true;
    } else if (seedColoring && vals.degree == 0) {
      p.targetStrokeColor = Color.green;
      p.targetFillColor = Color.green; //c.brighter().brighter();
      return true;
    }

    p.targetFillColor = color;
    p.targetStrokeColor = Color.blue;
    return true;
  }

  /**
   * Returns a <code>Color</code> which would be used as the node color under
   * normal circumstances (e.g. node is not selected)
   *
   * @param p Contains various display properties for the node in question.
   * @return <code>Color</code> to be used as display color.
   */
  private Color guessColor(NodeRenderingProperty p) {
    if (p.overriddenColor != null) {
      return p.overriddenColor;
    } else {
      NodeColorData vals = (NodeColorData) p.pluginStore.get(this);
      float voltagePercent = (float) (vals.importance / maxImportance);
      float degreePercent = ((float) vals.degree) / maxDegree;
      return nodeColors.getColor(p.node, cm, voltagePercent, degreePercent);
    }
  }

  @Override
  public void preFrame(float elapsedTime) {
  }

  public void postFrame() {
  }

  public boolean keyPressed(int keycode, char character, boolean ctrl,
      boolean alt, boolean shift) {
    return false;
  }

  //////////////////////////////////////
  // Object for saving in node

  private class NodeColorData {
    double importance;
    int degree;
    public NodeColorData(double importance, int degree) {
      this.importance = importance;
      this.degree = degree;
    }
  }

  //////////////////////////////////////
  // Basic getters / setters / toggle

  public void setColor(boolean on) {
    this.isColorEnabled = on;
  }

  public boolean getColor() {
    return isColorEnabled;
  }

  public boolean toggleColor() {
    isColorEnabled = !isColorEnabled;
    return isColorEnabled;
  }

  public void setSeedColoring(boolean on) {
    this.seedColoring = on;
  }

  public boolean getSeedColoring() {
    return seedColoring;
  }

  public boolean toggleSeedColoring() {
    seedColoring = !seedColoring;
    return seedColoring;
  }

  public void setColorMode(NodeColors color) {
    this.nodeColors = color;
  }
}

