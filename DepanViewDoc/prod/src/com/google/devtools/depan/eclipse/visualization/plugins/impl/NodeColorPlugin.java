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

import com.google.devtools.depan.eclipse.visualization.ogl.NodeColorSupplier;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeRenderingProperty;
import com.google.devtools.depan.eclipse.visualization.plugins.core.NodeRenderingPlugin;
import com.google.devtools.depan.view_doc.model.NodeColorMode;

import com.google.common.collect.Maps;

import java.awt.Color;
import java.util.Map;

/**
 * A plugin coloring a node and it's stroke. This plugin must be the last one,
 * because it can return false based on the color and the visibility. If it is
 * not, after becoming invisible, a node could not be shown again by a plugin
 * coming later.
 *
 * @author Yohann Coppel
 */
public class NodeColorPlugin extends NodeRenderingPlugin.Simple {

  private boolean isColorEnabled = true;

  private Color defaultColor = Color.BLUE;

  private NodeColorMode nodeMode = null;

  private NodeColorMode rootMode = null;

  public NodeColorPlugin() {
  }

  /**
   * Run every time, because {@link NodeRenderingProperty#isSelected()}
   * can change for may reasons (selection, collapse).
   */
  @Override
  public boolean apply(NodeRenderingProperty p) {
    if (!isColorEnabled) {
      p.targetFillColor = defaultColor;
      return true;
    }

    NodeColorSupplier supplier = getColorSupplier(p);
    Color stroke = supplier.getStrokeColor();
    Color fill = supplier.getFillColor();

    if (null != p.overriddenColor) {
      fill = p.overriddenColor;
    }

    if (p.isSelected()) {
      p.targetStrokeColor = Color.red;
      p.targetFillColor = fill.darker().darker();
      return true;
    }

    p.targetStrokeColor = stroke;
    p.targetFillColor = fill;
    return true;
  }

  private NodeColorSupplier getColorSupplier(NodeRenderingProperty p) {
    @SuppressWarnings("unchecked")
    Map<NodeColorMode, NodeColorSupplier> modeMap =
        (Map<NodeColorMode, NodeColorSupplier>) p.pluginStore.get(this);
    if (null == modeMap) {
      return NodeColorSupplier.DEFAULT;
    }

    NodeColorSupplier result = null;
    if (null != rootMode) {
      result = modeMap.get(rootMode);
    }
    if (null == result) {
      result = modeMap.get(nodeMode);
    }
    if (null != result) {
      return result;
    }
    return NodeColorSupplier.DEFAULT;
  }

  //////////////////////////////////////
  // Rendering attributes

  public void setNodeSelected() {
  }

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
  public void setNodeColorMode(NodeColorMode nodeMode) {
    this.nodeMode = nodeMode;
  }

  public void setRootColorMode(NodeColorMode rootMode) {
    this.rootMode = rootMode;
  }

  public void setNodeColorByMode(
      NodeRenderingProperty p, NodeColorMode mode, NodeColorSupplier supplier) {
    @SuppressWarnings("unchecked")
    Map<NodeColorMode, NodeColorSupplier> modeMap =
        (Map<NodeColorMode, NodeColorSupplier>) p.pluginStore.get(this);
    if (null == modeMap) {
      modeMap = Maps.newHashMap();
      p.pluginStore.put(this, modeMap);
    }
    modeMap.put(mode, supplier);
  }
}
