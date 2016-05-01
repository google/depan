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

import com.google.devtools.depan.eclipse.visualization.ogl.EdgeRenderingProperty;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeRenderingProperty;
import com.google.devtools.depan.eclipse.visualization.plugins.core.EdgeRenderingPlugin;

import java.awt.Color;

/**
 * Plugin coloring edges.
 *
 * @author Yohann Coppel
 *
 */
public class EdgeColorPlugin implements EdgeRenderingPlugin {

  public EdgeColorPlugin() {
  }

  @Override
  public boolean apply(EdgeRenderingProperty p) {
    NodeRenderingProperty node1 = p.node1;
    NodeRenderingProperty node2 = p.node2;

    if (node1.isSelected() && node2.isSelected()) {
      // both ends of the edge is selected.
      p.targetStrokeColor = Color.DARK_GRAY;
    } else if (node1.isSelected()) {
      // edges to the child of the selected node in blue
      p.targetStrokeColor = Color.RED;
    } else if (node2.isSelected()) {
      // edges to the parent of the selected node in red
      p.targetStrokeColor = Color.BLUE;
    } else if (p.overriddenStrokeColor != null) {
      p.targetStrokeColor = p.overriddenStrokeColor;
// TODO(yohann): Reimplement this with help from plugins
/*
    } else if (containerFinder.matchForward(p.edge.getRelation())
        || containerFinder.matchBackward(p.edge.getRelation())) {
      // not picked, draw container edges with distinctive color
      p.targetStrokeColor = Color.GRAY;
*/
    } else {
      p.targetStrokeColor = Color.LIGHT_GRAY;
    }
    return true;
  }

  @Override
  public void dryRun(EdgeRenderingProperty p) {
    // nothing to do
  }

  @Override
  public boolean keyPressed(int keycode, char character, boolean ctrl,
      boolean alt, boolean shift) {
    // no key bindings for this plugin.
    return false;
  }

  @Override
  public void postFrame() {
    // nothing to do
  }

  @Override
  public void preFrame(float elapsedTime) {
    // nothing to do
  }

}
