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

package com.google.devtools.depan.eclipse.visualization.plugins.core;

/**
 * A Plugin is inserted into a {@link RenderingPipe}, and it's methods
 * are called at the right moment, on every object that must be rendered.
 *
 * @author Yohann Coppel
 *
 */
public interface Plugin {

  /**
   * Method called before every frame.
   * @param elapsedTime Time elapsed since last frame.
   */
  void preFrame(float elapsedTime);

  /**
   * Method called at the end of every frame.
   */
  void postFrame();

  /**
   * A key has been pressed. The plugin can use it, and to prevent any other
   * plugin to react to this key stroke, can return false. If the plugin don't
   * have any action for this key stroke, it can return <code>true</code> and
   * let the {@link RenderingPipe} see if another plugin can use this key
   * stroke.
   *
   * @param keycode
   * @param character
   * @param ctrl
   * @param alt
   * @param shift
   * @return <code>true</code> if the key stroke was used, <code>false</code>
   * otherwise.
   */
  public boolean keyPressed(int keycode, char character, boolean ctrl,
      boolean alt, boolean shift);
}

