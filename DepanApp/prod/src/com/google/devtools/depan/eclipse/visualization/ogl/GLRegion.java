/*
 * Copyright 2010 Google Inc.
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

import com.google.devtools.depan.eclipse.editors.Point2dUtils;

import java.awt.geom.Point2D;

/**
 * Represents a rectangular region in OpenGL coordinates.  This objects serves
 * mostly as a data records, so its fields are public final.  Although OpenGL
 * tends to use float types internally, Java tends to stick with doubles.  So
 * we leave things in double values until just before the head into OpenGL.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class GLRegion {
  public final double left;
  public final double top;
  public final double right;
  public final double bottom;

  public GLRegion(double left, double top, double right, double bottom) {
    this.left = left;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
  }

  /**
   * Provide the distance from the right edge to the left edge of this region.
   * 
   * @return distance from the right edge to the left edge
   */
  public double getRangeX() {
    return right - left;
  }

  /**
   * Provide the X coordinate for the center of this region.
   * 
   * @return X coordinate for the center of this region
   */
  public double getCenterX() {
    return (right + left) / 2;
  }

  /**
   * Provide the distance from the top edge to the bottom edge of this region.
   * 
   * @return distance from the top edge to the bottom edge 
   */
  public double getRangeY() {
    return top - bottom;
  }

  /**
   * Provide the Y coordinate for the center of this region.
   * 
   * @return Y coordinate for the center of this region
   */
  public double getCenterY() {
    return (top + bottom) / 2;
  }

  /**
   * Provide a new {@code Point2D} for the point in the center of this region.
   * 
   * @return a {@code Point2D} for the center of this region
   */
  public Point2D getCenter() {
    return Point2dUtils.newPoint2D(getCenterX(), getCenterY());
  }

  /**
   * Generate a new region of the same size that is centered at the origin.
   * 
   * @return a new region of the same size that is centered at the origin
   */
  public GLRegion newOriginRegion() {
    double rangeX = getRangeX();
    double rangeY = getRangeY();
    double newLeft = -rangeX / 2;
    double newBottom = -rangeY / 2;
    return new GLRegion(
        newLeft, rangeY + newBottom, rangeX + newLeft, newBottom);
  }
}
