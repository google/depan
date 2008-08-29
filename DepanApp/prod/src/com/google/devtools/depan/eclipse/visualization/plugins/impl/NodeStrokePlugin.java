/*
 * Copyright 2008 Yohann R. Coppel
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

import java.util.List;

import com.google.devtools.depan.collect.Lists;
import com.google.devtools.depan.eclipse.visualization.ogl.GLPanel;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeRenderingProperty;
import com.google.devtools.depan.eclipse.visualization.plugins.core.NodeRenderingPlugin;
import com.google.devtools.depan.model.GraphNode;

import edu.uci.ics.jung.graph.Graph;

/**
 * A plugin that modifies the stroke width and color for nodes.
 *
 * @author Yohann Coppel
 *
 */
public class NodeStrokePlugin<E> implements NodeRenderingPlugin {

  /**
   * Say if node stroke should highlight the selection state.
   */
  protected boolean highlight = true;

  protected final Graph<GraphNode, E> graph;
  protected final GLPanel view;

  protected static final float HEAVY = 3.0f;
  protected static final float MEDIUM = 2.0f;
  protected static final float LIGHT = 1.0f;

  public NodeStrokePlugin(GLPanel view, Graph<GraphNode, E> graph) {
    this.view = view;
    this.graph = graph;
  }

  @Override
  public void dryRun(NodeRenderingProperty p) {
    // during the dry run, store a list of neibourgh in the node properties.
    List<NodeRenderingProperty> props = Lists.newArrayList();
    for (GraphNode node : graph.getNeighbors(p.node)) {
      props.add(view.node2property(node));
    }
    p.pluginStore.put(this, props);
  }

  @Override
  public boolean apply(NodeRenderingProperty p) {
    if (!highlight) {
      p.targetStrokeWidth = LIGHT;
      return true;
    }

    if (p.isSelected()) {
      p.targetStrokeWidth = HEAVY;
    } else {
      boolean neighborIsSelected = false;

      @SuppressWarnings("unchecked")
      List<NodeRenderingProperty> neighbors =
        (List<NodeRenderingProperty>) p.pluginStore.get(this);
      for (NodeRenderingProperty n : neighbors) {
        if (n.isSelected()) {
          neighborIsSelected = true;
          break;
        }
      }

      if (neighborIsSelected) {
        p.targetStrokeWidth = MEDIUM;
      } else {
        p.targetStrokeWidth = LIGHT;
      }
    }
    return true;
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

  //////////////////////
  // enable / disable plugin

  public void activate(boolean on) {
    highlight = on;
  }

  public boolean isActivated() {
    return highlight;
  }

  public boolean toggle() {
    highlight = !highlight;
    return highlight;
  }

}
