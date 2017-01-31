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
import com.google.devtools.depan.nodes.trees.SuccessorEdges;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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

  private boolean hasChanged = true;

  protected final GLPanel view;

  protected Map<GraphNode, ? extends SuccessorEdges> edgeMap;

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
    p.pluginStore.put(this, buildNeighbors(p));
  }

  private List<NodeRenderingProperty> buildNeighbors(NodeRenderingProperty p) {
    SuccessorEdges edges = edgeMap.get(p.node);
    if (null == edges) {
      return Collections.emptyList();
    }

    List<NodeRenderingProperty> result = Lists.newArrayList();
    for (GraphEdge edge : edges.getForwardEdges()) {
      result.add(view.node2property(edge.getTail()));
    }
    for (GraphEdge edge : edges.getReverseEdges()) {
      result.add(view.node2property(edge.getHead()));
    }
    return result;
  }

  @Override
  public boolean apply(NodeRenderingProperty p) {
    if (!hasChanged) {
      return true;
    }

    p.targetStrokeWidth = getStrokeWidth(p);
    return true;
  }

  @Override
  public void postFrame() {
    hasChanged = false;
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
    hasChanged = true;
  }

  public void setNodeNeighbors(
      Map<GraphNode, ? extends SuccessorEdges> edgeMap) {
    this.edgeMap = edgeMap;
  }
}
