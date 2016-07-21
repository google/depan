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

import com.google.devtools.depan.eclipse.visualization.ogl.GLPanel;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeRenderingProperty;
import com.google.devtools.depan.eclipse.visualization.ogl.RendererEvents;
import com.google.devtools.depan.eclipse.visualization.plugins.core.NodeRenderingPlugin;

/**
 * A plugin that apply a factor to shrink the node positions. This has the
 * effect of increasing (or decreasing) space between nodes, without drawing
 * them bigger (or smaller).
 *
 * It is also capable of computing the best shrink factor, which makes every
 * nodes fit in the current view (only uses nodes positions, not the node
 * sizes).
 *
 * <p>
 * This plugin uses these key shortcuts:
 * <ul>
 * <li><b>+</b> to apply a factor of 1.1 on both coordinates</li>
 * <li><b>-</b> to apply a factor of 0.9 on both coordinates</li>
 * <li>+ / - used with <b>alt</b> apply the factor only on X coordinates</li>
 * <li>+ / - used with <b>shift</b> apply the factor only on Y coordinates</li>
 * <li><b>a or A</b> to compute the best factor that makes every node fit into
 * the screen</li>
 * </ul>
 */
public class FactorPlugin extends NodeRenderingPlugin.Simple {

  private final GLPanel panel;

  public FactorPlugin(GLPanel panel) {
    this.panel = panel;
  }

  @Override
  public boolean apply(NodeRenderingProperty p) {
    return true;
  }

  @Override
  public boolean keyPressed(int keycode, char character, boolean ctrl,
      boolean alt, boolean shift) {
    if (character == '+') {
      panel.handleEvent(RendererEvents.ScaleEvents.ZOOM_IN);
      return true;
    }
    if (character == '-') {
      panel.handleEvent(RendererEvents.ScaleEvents.ZOOM_OUT);
      return true;
    }
    if (character == 'a' || character == 'A') {
      panel.handleEvent(RendererEvents.ScaleEvents.SCALE_TO_VIEWPORT);
      return true;
    }
    return false;
  }
}
