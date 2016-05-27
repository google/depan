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

package com.google.devtools.depan.eclipse.visualization.plugins.impl;

import com.google.common.collect.Lists;

import com.google.devtools.depan.eclipse.visualization.ogl.GLPanel;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeRenderingProperty;
import com.google.devtools.depan.eclipse.visualization.plugins.core.NodeRenderingPlugin;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphNode;

import edu.uci.ics.jung.graph.Graph;

import java.util.List;

/**
 * A plugin that modifies the stroke width and color for nodes.
 *
 * @author Yohann Coppel
 *
 */
public class NodeStrokePlugin extends NodeRenderingPlugin.Simple {

  protected static final float HEAVY = 3.0f;
  protected static final float MEDIUM = 2.0f;
  protected static final float LIGHT = 1.0f;

  protected final GLPanel view;

  protected Graph<GraphNode, GraphEdge> jungGraph;

  /**
   * Say if node stroke should highlight the selection state.
   */
  protected boolean highlight = true;

  public NodeStrokePlugin(GLPanel view) {
    this.view = view;
  }

  @Override
  public void dryRun(NodeRenderingProperty p) {
    // during the dry run, store a list of neighbors in the node properties.
    List<NodeRenderingProperty> props = Lists.newArrayList();
    for (GraphNode node : jungGraph.getNeighbors(p.node)) {
      props.add(view.node2property(node));
    }
    p.pluginStore.put(this, props);
  }

  @Override
  public boolean apply(NodeRenderingProperty p) {
    p.targetStrokeWidth = getStrokeWidth(p);
    return true;
  }

  private float getStrokeWidth(NodeRenderingProperty p) {
    if (!highlight) {
      return LIGHT;
    }

    if (p.isSelected()) {
      return HEAVY;
    }

    @SuppressWarnings("unchecked")
    List<NodeRenderingProperty> neighbors =
        (List<NodeRenderingProperty>) p.pluginStore.get(this);
    for (NodeRenderingProperty n : neighbors) {
      if (n.isSelected()) {
        return MEDIUM;
      }
    }

    return LIGHT;
  }

  //////////////////////
  // enable / disable plugin

  public void activate(boolean on) {
    highlight = on;
  }

  public void setNodeNeighbors(Graph<GraphNode, GraphEdge> jungGraph) {
    this.jungGraph = jungGraph;
  }
}
