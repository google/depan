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

import java.util.Map;

import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds.NodeSize;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeRenderingProperty;
import com.google.devtools.depan.eclipse.visualization.plugins.core.NodeRenderingPlugin;
import com.google.devtools.depan.model.GraphNode;

import edu.uci.ics.jung.graph.Graph;

/**
 * A plugin that modifies node size and ratio.
 *
 * @author Yohann Coppel
 *
 */
public class NodeSizePlugin<E> implements NodeRenderingPlugin {

  private NodeSize nodeSize = NodeSize.getDefault();

  protected boolean resizeEnabled = false;
  protected boolean ratioEnabled = false;

  private int maxDegree = 1;
  private double maxImportance = 0.001;

  private final Graph<GraphNode, E> graph;
  private Map<GraphNode, Double> importances;

  public NodeSizePlugin(Graph<GraphNode, E> graph,
      Map<GraphNode, Double> importances) {
    this.graph = graph;
    this.importances = importances;
  }

  @Override
  public boolean apply(NodeRenderingProperty p) {
    @SuppressWarnings("unchecked")
    NodeSizeData vals = (NodeSizeData) p.pluginStore.get(this);

    // size
    float voltagePercent = (float) (vals.importance / maxImportance);
    float degreePercent = ((float) vals.inDegree) / maxDegree;
    if (p.overriddenSize != null) {
      p.targetSize = p.overriddenSize.getSize(voltagePercent, degreePercent);
    } else if (resizeEnabled) {
      p.targetSize = nodeSize.getSize(voltagePercent, degreePercent);
    } else {
      p.targetSize = NodeSize.getDefaultSize();
    }

    // ratio
    if (ratioEnabled) {
      p.targetRatio = ((float) vals.inDegree + vals.outDegree) / maxDegree;
    } else {
      p.targetRatio = 1.0f;
    }
    return true;
  }

  @Override
  public void dryRun(NodeRenderingProperty p) {
    // during the dry run, compute the degree of every node, and get it's
    // importance. These are costly operations, and values do not change, so
    // we avoid doing them at every frame.

    // find maximum degree
    int inDegree = graph.getPredecessorCount(p.node);
    int outDegree = graph.getSuccessorCount(p.node);
    // for the size, only the in degree is taken into account.
    if (inDegree > maxDegree) {
      maxDegree = inDegree;
    }
    // find maximum importance
    // find maximum importance
    Double impObj = importances.get(p.node);
    double importance = (null == impObj) ? 0.0 : impObj.doubleValue();
    maxImportance = Math.max(maxImportance, importance);

    // store costly informations to retrieve at each frame.
    p.pluginStore.put(this, new NodeSizeData(importance, inDegree, outDegree));
  }

  @Override
  public boolean keyPressed(int keycode, char character, boolean ctrl,
      boolean alt, boolean shift) {
    return false;
  }

  @Override
  public void postFrame() {
  }

  @Override
  public void preFrame(float elapsedTime) {
  }

  //////////////////////////////////////
  // Object for saving in node

  class NodeSizeData {
    double importance;
    int inDegree;
    int outDegree;
    NodeSizeData(double importance, int inDegree, int outDegree) {
      this.importance = importance;
      this.inDegree = inDegree;
      this.outDegree = outDegree;
    }
  }

  // size

  public void setResize(boolean newValue) {
    this.resizeEnabled = newValue;
  }

  public boolean toggleResize() {
    this.resizeEnabled = !this.resizeEnabled;
    return this.resizeEnabled;
  }

  public boolean getResize() {
    return this.resizeEnabled;
  }

  // ratio

  public void setRatio(boolean newValue) {
    this.ratioEnabled = newValue;
  }

  public boolean getRatio() {
    return ratioEnabled;
  }

  public boolean toggleRatio() {
    this.ratioEnabled = !this.ratioEnabled;
    return this.ratioEnabled;
  }

  public void setSizeMode(NodeSize size) {
    this.nodeSize = size;
  }

}
