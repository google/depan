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

package com.google.devtools.depan.eclipse.visualization.ogl;

import java.awt.Color;

public abstract class RenderingProperty {

  public GLEntity shape;
  public final int shapeId;

  /**
   * Constructs a new instance.
   */
  public RenderingProperty(int shapeId) {
    this.shapeId = shapeId;
  }

  public abstract void step(float elapsedTime);

  /**
   * Compute a step for a color.
   *
   * @param current current color
   * @param target target color
   * @param speed speed of movement: each step perform 1/speed of the remaining
   *        distance
   * @param elapsedTime
   * @return the color after the step.
   */
  public static Color colorStep(Color current, Color target, float speed,
      float elapsedTime) {
    int red =
        (int) (current.getRed() + (target.getRed() - current.getRed()) / speed);
    int green = (int) (current.getGreen()
        + (target.getGreen() - current.getGreen()) / speed);
    int blue = (int) (current.getBlue()
        + (target.getBlue() - current.getBlue()) / speed);
    int alpha = (int) (current.getAlpha()
        + (target.getAlpha() - current.getAlpha()) / speed);
    return new Color(red, green, blue, alpha);
  }

}

