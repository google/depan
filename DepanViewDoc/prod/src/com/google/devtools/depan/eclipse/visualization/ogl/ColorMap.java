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

/**
 * Class creating a colormap from a definition. Some definitions can be found in
 * the package com.google.devtools.depan.eclipse.cm, or retrieved from the class
 * {@link com.google.devtools.depan.eclipse.cm.ColorMapDef}.
 *
 * Here is how a color map is defined: by an array of floats.
 *
 * the array consist of 4 arrays for each color component, in the order: red,
 * green, blue, and alpha.
 *
 * Here is a simple example:
 * </code>
 *   {
 *       { // red
 *         {0.0f, 0.0f, 0.0f}, // first control point for the red
 *         {0.5f, 1.0f, 0.7f},
 *         {1.0f, 1.0f, 1.0f}}, // last control point for red
 *       { // green
 *         {0.0f, 0.0f, 0.0f},
 *         {0.5f, 1.0f, 0.7f},
 *         {1.0f, 1.0f, 1.0f}},
 *       { // blue
 *         {0.0f, 0.0f, 0.0f},
 *         {0.5f, 1.0f, 0.7f},
 *         {1.0f, 1.0f, 1.0f}},
 *       { // alpha
 *         {0.0f, 1.0f, 1.0f},   // here, alpha have the minimum number of
 *         {1.0f, 1.0f, 1.0f}}}; // control points: 2: at position 0f, and 1f.
 * </code>
 *
 * Then, for each color, we have a list of control points. We need at lest 2
 * control points per color.
 *
 * Each control point is defined as follow:
 * {position, color value on the left, color value on the right}.
 *
 * Control point positions must be given in increasing order.
 *
 * Therefore, each color must have one control point at the position 0.0f, and
 * one at the position 1.0f.
 *
 * Finally, every value must be in the range [0:1] to be valid.
 *
 * See ColorMap.DEFAULT_MAP for a definition map example.
 *
 * @author Yohann Coppel
 * @see com.google.devtools.depan.eclipse.cm.ColorMapDef
 */
public class ColorMap {

  public final float[][][] definition;
  public final Color[] data;

  /**
   * Build a color map with the given number of steps (different colors) with
   * the given color map definition.
   *
   * If the color map definition is not valid, ColorMap.DEFAULT_MAP is used.
   *
   * @param definition
   * @param steps
   */
  public ColorMap(float[][][] definition, int steps) {
    if (definition == null || !check(definition)) {
      System.err.println("ColorMap: Incorrect color map definition. "
          + "Using default map.");
      definition = DEFAULT_MAP;
    }

    this.definition = definition;

    if (steps < 1) {
      steps = 1;
    }
    this.data = new Color[steps];

    computeSteps();
  }

  /**
   * Return the color corresponding to the given percentage value.
   *
   * @param percent value between 0.0f and 1.0f (boundaries included).
   * @return the corresponding color in this ColorMap.
   */
  public Color getColor(float percent) {
    // limit percent to [0.0, 1.0] range.
    percent = Math.min(1.0f, Math.max(0.0f, percent));
    int nb = (int) (percent * ((float) data.length - 1));
    return data[nb];
  }

  /**
   * Check if the given definition is valid.
   *
   * @param definition
   */
  private static boolean check(float[][][] definition) {
    // need red, green, blue and alpha definition
    if (definition.length != 4) {
      return false;
    }

    for (float[][] color : definition) {
      // for each component, we need at least 2 control points
      if (color.length < 2) {
        return false;
      }

      for (int i = 0; i < color.length; ++i) {
        float[] controlPoint = color[i];

        // for each control point, 3 values.
        if (controlPoint.length != 3) {
          return false;
        }

        if (i == 0) {
          // first control point must be 0.0f;
          if (controlPoint[0] != 0.0f) {
            return false;
          }
        } else if (i == color.length - 1) {
          // last one must be 1.0f;
          if (controlPoint[0] != 1.0f) {
            return false;
          }
        } else {
          // in between, the first value must be greater than the previous one
          // and strictly lower than 1.0f.
          if (controlPoint[0] < color[i - 1][0] || controlPoint[0] >= 1.0f) {
            return false;
          }
        }

        // color values must be in [0.0, 1.1].
        if (controlPoint[1] < 0.0f || controlPoint[1] > 1.0f) {
          return false;
        }
        if (controlPoint[2] < 0.0f || controlPoint[2] > 1.0f) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Compute the colorMap.
   */
  private void computeSteps() {
    int totalSteps = data.length;
    for (int step = 0; step < totalSteps; ++step) {
      float pos = (float) step / (float) totalSteps;
      float red   = computeColor(definition[0], pos);
      float green = computeColor(definition[1], pos);
      float blue  = computeColor(definition[2], pos);
      float alpha = computeColor(definition[3], pos);
      data[step] = new Color(red, green, blue, alpha);
    }
  }

  /**
   * Compute the steps for one given color, at the given position. For instance
   * compute the red component of the color at position 0.8 in the range [0:1]
   *
   * @param colorDef the selected color definition
   * @param position position in the range [0:1].
   * @return a float representing the value of this color component.
   */
  private float computeColor(float[][] colorDef, float position) {
    float leftPos = 0.0f;
    float rightPos = 0.0f;
    float left = 0.0f;
    float right = 0.0f;

    for (int i = 0; i < colorDef.length; ++i) {
      float[] checkPoint = colorDef[i];
      float pos = checkPoint[0];
      float leftLimit = checkPoint[1];
      float rightLimit = checkPoint[2];

      if (position > pos) {
        leftPos = pos;
        left = rightLimit;
      } else if (position < pos) {
        rightPos = pos;
        right = leftLimit;
        break;
      } else if (position == pos) {
        return leftLimit;
      }
    }

    // values: ---[leftPos]-----[position]-----[rightPos]-----
    // color : ---[ left  ]-----[   ?    ]-----[ right  ]-----
    // Need to find the value for "?"
    float valDiff = rightPos - leftPos;
    float posRelative = position - leftPos;
    float fraction = posRelative / valDiff;
    float colorDiff = right - left;
    return colorDiff * fraction + left;
  }

  /**
   * A default color map example.
   */
  public static final float[][][] DEFAULT_MAP = {
      { // red
        {0.0f, 0.0f, 0.0f},
        {0.5f, 1.0f, 0.7f},
        {1.0f, 1.0f, 1.0f}},
      { // green
        {0.0f, 0.0f, 0.0f},
        {0.5f, 1.0f, 0.7f},
        {1.0f, 1.0f, 1.0f}},
      { // blue
        {0.0f, 0.0f, 0.0f},
        {0.5f, 1.0f, 0.7f},
        {1.0f, 1.0f, 1.0f}},
      { // alpha
        {0.0f, 1.0f, 1.0f},
        {1.0f, 1.0f, 1.0f}}};

}

