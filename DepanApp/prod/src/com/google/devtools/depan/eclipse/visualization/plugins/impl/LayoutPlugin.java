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
import com.google.devtools.depan.eclipse.visualization.plugins.core.NodeRenderingPlugin;

/**
 * Plugin applying a new layout in case of layout change.
 *
 * @author Yohann Coppel
 */
public class LayoutPlugin implements NodeRenderingPlugin, EdgeRenderingPlugin {

  @Override
  public void preFrame(float elapsedTime) {
    // nothing to do
  }

  @Override
  public boolean apply(NodeRenderingProperty p) {
    return true;
  }

  @Override
  public boolean apply(EdgeRenderingProperty p) {
    // for edges, always reposition them, because nodes can
    // be moved by the mouse, without notice to this plugin.
    // TODO(leeca): Is this still necessary?
    p.p1X = p.node1.positionX;
    p.p1Y = p.node1.positionY;
    p.p2X = p.node2.positionX;
    p.p2Y = p.node2.positionY;
    return true;
  }

  @Override
  public void postFrame() {
  }

  @Override
  public boolean keyPressed(int keycode, char character, boolean ctrl,
      boolean alt, boolean shift) {
    // TODO(leeca): restore support for keyboard layout shortcuts.
    // See revision 100
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
}
