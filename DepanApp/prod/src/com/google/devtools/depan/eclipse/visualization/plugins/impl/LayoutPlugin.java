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

import com.google.devtools.depan.eclipse.visualization.ogl.EdgeRenderingProperty;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeRenderingProperty;
import com.google.devtools.depan.eclipse.visualization.plugins.core.EdgeRenderingPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.core.NodeRenderingPlugin;
import com.google.devtools.depan.model.GraphNode;

import java.awt.geom.Point2D;
import java.util.Map;

/**
 * Plugin applying a new layout in case of layout change.
 *
 * This plugin has some key shortcuts: Pressing "l" (or 'L') followed by any
 * number from 1 to 9, apply the corresponding layout to the current view. If
 * 'l' is followed by a key that do not correspond to a layout number, the
 * plugins stop waiting for a number.
 *
 * @author Yohann Coppel
 *
 */
public class LayoutPlugin implements NodeRenderingPlugin, EdgeRenderingPlugin {

  private Map<GraphNode, Point2D> layout;
  private boolean hasChanged = false;

  /**
   * Apply a layout to the node in the rendering pipe.
   * The layout, when applied, gives every point a position between -0.5 and
   * 0.5.
   * @param view
   */
  public LayoutPlugin(Map<GraphNode, Point2D> layoutMap) {
    this.layout = layoutMap;
    this.hasChanged = false;
  }

  public void preFrame(float elapsedTime) {
    // nothing to do
  }

  @Override
  public boolean apply(NodeRenderingProperty p) {
    if (!hasChanged) {
      return true;
    }
    Point2D point = layout.get(p.node);
    if (null != point) {
      p.targetPositionX = (float) point.getX();
      p.targetPositionY = (float) point.getY();
    } else {
      p.targetPositionX = 0;
      p.targetPositionY = 0;
    }
    return true;
  }

  @Override
  public boolean apply(EdgeRenderingProperty p) {
    // for edges, always reposition them, because nodes can
    // be moved by the mouse, without notice to this plugin.
    p.p1X = p.node1.positionX;
    p.p1Y = p.node1.positionY;
    p.p2X = p.node2.positionX;
    p.p2Y = p.node2.positionY;
    return true;
  }

  public void postFrame() {
    if (!hasChanged) {
      return;
    }
    hasChanged = false;
  }

  @Override
  public boolean keyPressed(int keycode, char character, boolean ctrl,
      boolean alt, boolean shift) {
    return false;
  }

  @Override
  public void dryRun(EdgeRenderingProperty p) {
    // nothing to do
  }

  @Override
  public void dryRun(NodeRenderingProperty p) {
    // nothing to do
  }

  public void setLayout(Map<GraphNode, Point2D> layoutMap) {
    this.layout = layoutMap;
    this.hasChanged = true;
  }

  public void editLayout(Map<GraphNode, Point2D> newLocations) {
    this.layout.putAll(newLocations);
    this.hasChanged = true;
  }
}
