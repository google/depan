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

import com.google.devtools.depan.eclipse.editors.ViewEditor;
import com.google.devtools.depan.eclipse.visualization.layout.LayoutGenerator;
import com.google.devtools.depan.eclipse.visualization.layout.LayoutGenerators;
import com.google.devtools.depan.eclipse.visualization.plugins.core.Plugin;

import java.util.List;

/**
 * Plugin that implements some editor shortcuts for layout options.
 *
 * Pressing "l" (or "L") followed by any number from 1 to 9, indicates that
 * the editor should update the nodes to the corresponding layout. If 'l' is 
 * followed by a key that does not correspond to a layout number, the plugin
 * stops waiting for a number.
 *
 * @author Yohann Coppel
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a> (extracted from {@link LayoutPlugin})
 */
public class LayoutShortcutsPlugin implements Plugin {

  private final ViewEditor editor;

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
  public LayoutShortcutsPlugin(ViewEditor editor) {
    this.editor = editor;
  }

  @Override
  public void preFrame(float elapsedTime) {
  }

  @Override
  public void postFrame() {
  }

  @Override
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
      String layoutName = getLayoutName(character);
      if (null == layoutName)
        break;

      System.out.println("Apply layout " + layoutName);
      LayoutGenerator layout = LayoutGenerators.getByName(layoutName);
      editor.applyLayout(layout);
      return true;

    default:
      break;
    }
    return false;
  }

  private String getLayoutName(int character) {
    if (character < '1') {
      return null;
    }
    if (character > '9') {
      return null;
    }

    int index = '1' - character;
    List<String> choices = LayoutGenerators.getLayoutNames();
    if (index >= choices.size()) {
      return null;
    }

    return choices.get(index);
  }
}
