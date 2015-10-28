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

import com.google.devtools.depan.eclipse.visualization.ogl.RenderingProperty;
import com.google.devtools.depan.eclipse.visualization.plugins.core.RenderingPlugin;

/**
 * The plugin that makes animations possible ! At every frame, it calls the
 * {@link RenderingProperty#step(float)} method on every RenderingProperty,
 * making the changed values to slowly change to the desired value.
 *
 * @author Yohann Coppel
 *
 */
public class SteperPlugin extends RenderingPlugin {
  private float elapsedTime;

  @Override
  public boolean apply(RenderingProperty p) {
    p.step(elapsedTime);
    return true;
  }

  @Override
  public void postFrame() {
  }

  @Override
  public void preFrame(float elapsedTime) {
    this.elapsedTime = elapsedTime;
  }

  @Override
  public boolean keyPressed(int keycode, char character, boolean ctrl,
      boolean alt, boolean shift) {
    return false;
  }

  @Override
  public void dryRun(RenderingProperty p) {
  }
}

