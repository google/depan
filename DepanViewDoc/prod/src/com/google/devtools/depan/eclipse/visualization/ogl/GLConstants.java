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

import java.awt.Color;

/**
 * Define constants for rendering without any OGL member variables.
 * This helps defer JOGL initialization until the first instance of
 * a {@link GLScene} is created.
 * 
 * @author Lee Carver
 */
public class GLConstants {

  public static final float FACTOR = 1f;

  /** speed for moves */
  public static final int SPEED = 5;

  /** Camera z position, if zoom value is set to "100%". */
  public static final float HUNDRED_PERCENT_ZOOM = 2000.0f;

  public static final float[] DEFAULT_CAMERA_POSITION = {
    0.0f, 0.0f, HUNDRED_PERCENT_ZOOM
  };
  public static final float[] DEFAULT_CAMERA_DIRECTION = {
    0.0f, 0.0f, 0.0f
  };

  // Full laptop screen vertical space:
  // 7" vertical from 22" is ~ 20 degrees.
  public static final float FOV = 20.0f;

  public static final float Z_NEAR = 0.4f;
  public static final float Z_FAR = 30000.0f;

  // Various rendering minimums
  public static final float PIXEL_QUANTA = 0.1f;
  public static final float ZOOM_QUANTA = 1.0f;
  public static final float ZOOM_MAX = 1.1f;
  public static final float ROTATE_QUANTA = 0.001f;

  public static final Color FOREGROUND = Color.BLUE;

  private GLConstants() {
    // Prevent instantiation.
  }
}
