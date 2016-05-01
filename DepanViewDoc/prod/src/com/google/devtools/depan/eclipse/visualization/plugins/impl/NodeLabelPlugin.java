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

import com.google.devtools.depan.eclipse.preferences.LabelPreferencesIds.LabelPosition;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeRenderingProperty;
import com.google.devtools.depan.eclipse.visualization.plugins.core.NodeRenderingPlugin;

/**
 * Plugin that setup the visibility and position for node labels.
 *
 * @author Yohann Coppel
 *
 */
public class NodeLabelPlugin implements NodeRenderingPlugin {

  private LabelPosition labelPosition = LabelPosition.getDefault();

  @Override
  public boolean apply(NodeRenderingProperty p) {
    switch (labelPosition) {
    case IFSELECTED:
      if (p.isSelected()) {
        setLabelToDefault(p);
      } else {
        p.isTextVisible = false;
      }
      break;
    case NOLABEL:
      p.isTextVisible = false;
      break;
    case INSIDE:
      p.isTextVisible = true;
      //TODO(YC): Scale the shape until it is big enough so that the
      //           label can fit inside.
      break;
    default:
      setLabelToDefault(p);
      break;
    }
    return true;
  }

  private void setLabelToDefault(NodeRenderingProperty p) {
    p.isTextVisible = true;
    double[] dxdy = labelPosition.getDxDy();
    p.targetTextDx = (float) dxdy[0];
    p.targetTextDy = (float) dxdy[1];
  }

  @Override
  public void dryRun(NodeRenderingProperty p) {
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

  //////////////////////////////////////
  // Rendering attributes

  /**
   * Normally set from Eclipse workspace preference
   * {@code LabelPreferencesIds.LABEL_POSITION}
   */
  public void setLabelPosition(LabelPosition labelPosition) {
    this.labelPosition = labelPosition;
  }
}
