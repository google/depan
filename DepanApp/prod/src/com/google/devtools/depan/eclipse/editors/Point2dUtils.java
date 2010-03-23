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

package com.google.devtools.depan.eclipse.editors;

import java.awt.geom.Point2D;

/**
 * Computes new Point2D values based on delta terms and scaling factors
 * provided at to the constructor.  This is useful when adjusting the position
 * of many nodes may the same terms.
 *
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class Point2dUtils {
  public static final Point2D ZERO_POINT = newZeroPoint();

  // Prevent instantiation of this namespace class.
  private Point2dUtils() {
  }

  public static Point2D newPoint2D(double xPos, double yPos) {
    return new Point2D.Float((float) xPos, (float) yPos);
  }

  public static Point2D newZeroPoint() {
    return new Point2D.Double(0.0, 0.0);
  }

  /////////////////////////////////////
  // Translaters for repetitively doing the same thing to many Point2Ds.

  public interface Translater {
    Point2D translate(Point2D source);
  }

  public static class DeltaTranslater implements Translater {
    private final double deltaX;
    private final double deltaY;

    public DeltaTranslater(double deltaX, double deltaY) {
      this.deltaX = deltaX;
      this.deltaY = deltaY;
    }

    public Point2D translate(Point2D source) {
      if (null == source) {
        return newPoint2D(deltaX, deltaY);
      }
      return newPoint2D(source.getX() + deltaX, source.getY() + deltaY);
    }
  }

  public static class ScaleTranslater implements Translater {
    private final double scaleX;
    private final double scaleY;

    public ScaleTranslater(double scaleX, double scaleY) {
      this.scaleX = scaleX;
      this.scaleY = scaleY;
    }

    public Point2D translate(Point2D source) {
      if (null == source) {
        return newZeroPoint();
      }
      return newPoint2D(source.getX() * scaleX, source.getY() * scaleY);
    }
  }

  /**
   * Compose two {@code Point2D} translaters into one reuasble translater.
   * 
   * <p>A more generic solution would support a list of translaters, but that's
   * overkill for our current needs [Mar 2010].  This can be used to build
   * arbitrarily complex translaters if necessary.  If speed turns out to be
   * critical, a custom translater might be needed anyway.
   */
  public static class DoubleTranslater implements Translater {
    private final Translater first;
    private final Translater last;

    public DoubleTranslater(Translater first, Translater last) {
      this.first = first;
      this.last = last;
    }

    public Point2D translate(Point2D source) {
      return last.translate(first.translate(source));
    }
  }

  public static Translater newDeltaTranslater(
      double deltaX, double deltaY) {
    return new DeltaTranslater(deltaX, deltaY);
  }

  public static Translater newScaleTranslater(
      double scaleX, double scaleY) {
    return new ScaleTranslater(scaleX, scaleY);
  }

  public static Translater newAdjustTranslater(
      double deltaX, double deltaY,
      double scaleX, double scaleY) {
    return new DoubleTranslater(
        newDeltaTranslater(deltaX, deltaY),
        newScaleTranslater(scaleX, scaleY));
  }

}
