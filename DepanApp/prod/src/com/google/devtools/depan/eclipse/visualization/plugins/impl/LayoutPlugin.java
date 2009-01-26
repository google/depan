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

import com.google.devtools.depan.eclipse.visualization.View;
import com.google.devtools.depan.eclipse.visualization.layout.Layouts;
import com.google.devtools.depan.eclipse.visualization.ogl.EdgeRenderingProperty;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeRenderingProperty;
import com.google.devtools.depan.eclipse.visualization.plugins.core.EdgeRenderingPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.core.NodeRenderingPlugin;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view.ViewModel;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.util.IterativeContext;

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

  Map<GraphNode, Point2D> layout;
  private ViewModel viewModel;
  private View view;
  public boolean hasChanged = false;

  private enum KeyState {
    /**
     * Waiting for a 'l' or 'L' to be pressed
     */
    WAITING,
    /**
     * Waiting for a number 1 to 9 to be pressed. if any other key is pressed,
     * return in WAITING.
     */
    LISTENING
  }
  private KeyState keyState = KeyState.WAITING;

  /**
   * Apply a layout to the node in the rendering pipe.
   * The layout, when applied, gives every point a position between -0.5 and
   * 0.5.
   * @param view
   */
  public LayoutPlugin(View view) {
  this.view = view;
    this.viewModel = view.getViewModel();
    this.layout = viewModel.getLayoutMap();
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

  public boolean keyPressed(int keycode, char character, boolean ctrl,
      boolean alt, boolean shift) {
    switch (this.keyState) {
    case WAITING:
      if (character == 'l' || character == 'L') {
        this.keyState = KeyState.LISTENING;
        System.out.println("Listening for a layout number. If the next key "
            + "stroke is a number, the corresonding layout will be applied. if"
            + "not, stop listening.");
        return true;
      }
      break;
    case LISTENING:
      this.keyState = KeyState.WAITING;
      if (character >= '1' && character <= Layouts.values().length + '0') {
        System.out.println(
            "Apply layout " + Layouts.values()[character - '1'].name());
        view.applyLayout(Layouts.values()[character - '1']);
        return true;
      }
      break;
      default:
        break;
    }
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

  /**
   * Apply the given layout to the nodes. After applying the layout, each node's
   * position is scaled and set to be in the [-0.5:0.5] range.
   *
   * @param newLayout
   */
  public Map<GraphNode, Point2D> setLayout(AbstractLayout<GraphNode, GraphEdge> newLayout) {
    if (newLayout instanceof IterativeContext) {
      IterativeContext it = (IterativeContext) newLayout;
      int maxSteps = 1000;
      while (maxSteps > 0 && !it.done()) {
        it.step();
        maxSteps--;
      }
    }

    layout.clear();
    double[] min = new double[2];
    double[] max = new double[2];
    min[0] = Double.MAX_VALUE;
    min[1] = Double.MAX_VALUE;
    max[0] = Double.MIN_VALUE;
    max[1] = Double.MIN_VALUE;
    // compute the min and max values for each coordinate of all points
    for (GraphNode n : viewModel.getNodes()) {
      double x = newLayout.getX(n);
      double y = newLayout.getY(n);
      min[0] = Math.min(min[0], x);
      min[1] = Math.min(min[1], y);
      max[0] = Math.max(max[0], x);
      max[1] = Math.max(max[1], y);
      layout.put(n, new Point2D.Double(x, y));
    }
    // scale everything to [-0.5:0.5] range
    // find the range for each dimension
    double rangeX = max[0] - min[0];
    double rangeY = max[1] - min[1];
    double factor;
    // determine which one to use, to keep the same proportions.
    if (rangeX == 0f && rangeY == 0f) {
      factor = 1;
    } else if (rangeX == 0) {
      factor = rangeY;
    } else if (rangeY == 0) {
      factor = rangeX;
    } else {
      factor = Math.max(rangeX, rangeY);
    }

    if (factor != 1) {
      for (Point2D p : layout.values()) {
        // scale everything, and center (-rangeX/factor/2)
        double newX = (p.getX() - min[0]) / factor - (rangeX / factor / 2);
        double newY = (p.getY() - min[1]) / factor - (rangeY / factor / 2);
        // minus Y, since in openGL, y coordinates are inverted
        p.setLocation(new Point2D.Double(newX, -newY));
      }
    }
    this.hasChanged = true;
    return layout;
  }

  public void setLayout(Map<GraphNode, Point2D> layoutMap) {
    this.layout = layoutMap;
    this.hasChanged = true;
  }
}

