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

import com.google.devtools.depan.eclipse.cm.ColorMapDefJet;
import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds.NodeColors;
import com.google.devtools.depan.eclipse.visualization.ogl.ColorMap;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeColorSupplier;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeRenderingProperty;
import com.google.devtools.depan.eclipse.visualization.plugins.core.NodeRenderingPlugin;

import java.awt.Color;

/**
 * A plugin coloring a node and it's stroke. This plugin must be the last one,
 * because it can return false based on the color and the visibility. If it is
 * not, after becoming invisible, a node could not be shown again by a plugin
 * coming later.
 *
 * @author Yohann Coppel
 */
public class NodeColorPlugin extends NodeRenderingPlugin.Simple {

  private ColorMap cm = new ColorMap(ColorMapDefJet.CM, 256);

  private boolean isColorEnabled = true;

  private NodeColors nodeColors = NodeColors.getDefault();

  public NodeColorPlugin() {
  }

  @Override
  public boolean apply(NodeRenderingProperty p) {
    if (!isColorEnabled) {
      p.targetFillColor = NodeColors.getDefaultColor();
      return true;
    }

    NodeColorSupplier supplier = (NodeColorSupplier) p.pluginStore.get(this);
    Color stroke = supplier.getStrokeColor(nodeColors, p.node, cm);
    Color fill = supplier.getFillColor(nodeColors, p.node, cm);

    if (p.isSelected()) {
      p.targetStrokeColor = Color.red;
      p.targetFillColor = fill.darker().darker();
      return true;
    }

    p.targetStrokeColor = stroke;
    p.targetFillColor = fill;
    return true;
  }

  //////////////////////////////////////
  // Rendering attributes

  /**
   * Normally set from Eclipse workspace preference
   * {@code NodePreferencesIds.NODE_COLOR_ON}
   */
  public void setColor(boolean on) {
    this.isColorEnabled = on;
  }

  /**
   * Normally set from Eclipse workspace preference
   * {@code NodePreferencesIds.NODE_COLOR}
   */
  public void setColorMode(NodeColors color) {
    this.nodeColors = color;
  }

  public void setColorSupplier(
      NodeRenderingProperty p, NodeColorSupplier supplier) {
    p.pluginStore.put(this, supplier);
  }
}
