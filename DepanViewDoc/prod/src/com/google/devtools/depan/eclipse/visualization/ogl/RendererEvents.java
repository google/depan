/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.eclipse.visualization.ogl;

/**
 * Define a set of user action requests that the OGL layer will detect
 * and that require action by the containing system.
 * 
 * Mostly, these are based on keyboard or mouse gestures within the
 * OGL canvas (and therefore are not handled by the Eclipse RCP keyboard
 * shortcut mechanisms).
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class RendererEvents {

  private RendererEvents() {
    // Prevent instantiation.
  }

  public static enum ScaleEvents implements RendererEvent {
    ZOOM_IN, ZOOM_OUT, SCALE_TO_VIEWPORT
  }

  /**
   * Historically, only numbers 1 though 9 are recognized as layout numbers.
   */
  public static enum LayoutEvents implements RendererEvent {
    LAYOUT_1, LAYOUT_2, LAYOUT_3, LAYOUT_4, LAYOUT_5,
    LAYOUT_6, LAYOUT_7, LAYOUT_8, LAYOUT_9
  }
}
