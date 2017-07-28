/*
 * Copyright 2010 The Depan Project Authors
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

package com.google.devtools.depan.view_doc.model;

import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view_doc.layout.LayoutScaler;

import com.google.common.collect.Maps;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Map;

/**
 * Computes new Point2D values based on delta terms and scaling factors
 * provided at to the constructor.  This is useful when adjusting the position
 * of many nodes may the same terms.
 *
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class Point2dUtils {
  public static final Point2D ZERO_POINT = newZeroPoint();

  private static final double ZERO_THRESHOLD = 0.1;

  // Prevent instantiation of this utility class.
  private Point2dUtils() {
  }

  public static Point2D newPoint2D(double xPos, double yPos) {
    return new Point2D.Float((float) xPos, (float) yPos);
  }

  public static Point2D newZeroPoint() {
    return newPoint2D(0.0, 0.0);
  }

  /////////////////////////////////////
  // Rectangle tools

  public static Rectangle2D scaleRectangle(Rectangle2D base, Double scale) {
    double width = base.getWidth() * scale;
    double height = base.getHeight() * scale;
    double shiftX = (base.getWidth() - width ) / 2.0;
    double shiftY = (base.getHeight() - height ) / 2.0;

    return new Rectangle2D.Double(
        base.getX() + shiftX, base.getY() + shiftY, width, height);
  }

  /////////////////////////////////////
  // Translaters for repetitively doing the same thing to many Point2Ds.

  public static interface Translater {

 /*
     * Must be prepared to handle a null point, which should be treated
     * as the origin (0.0, 0.0).
     */
    Point2D translate(Point2D source);
  }

  public static class DeltaTranslater implements Translater {
    private final double deltaX;
    private final double deltaY;

    public DeltaTranslater(double deltaX, double deltaY) {
      this.deltaX = deltaX;
      this.deltaY = deltaY;
    }

    @Override
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

    @Override
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

    @Override
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

  /**
   * Provide a square region that is centered on the supplied from region,
   * and is a minimal cover for the from region.  The provided region is at
   * least (1.0, 1.0), even if the from region is empty.
   */
  public static Rectangle2D newOrthoRegion(Rectangle2D from) {
    // Don't move the origin, 
    double width = from.getWidth();
    double height = from.getHeight();

    if ((width < ZERO_THRESHOLD) && (height < ZERO_THRESHOLD)) {
      width = 1.0;
      height = 1.0;
    }

    width = Math.max(width, height);
    height = Math.max(width, height);
    
    double x = from.getX() - (width - from.getWidth()) / 2;
    double y = from.getY() - (height - from.getHeight()) / 2;
    return new Rectangle2D.Double(x, y, width, height);
  }

  /**
   * Provide a translater that maps nodes in the from region to the
   * into region.  Nodes in the center (and corners) go to the same location
   * in the into regions.  Other nodes are shifted proportionately.
   */
  public static Translater newIntoRegion(
      Rectangle2D into, Rectangle2D from) {

    double scaleX = into.getWidth() / from.getWidth();
    double scaleY = into.getHeight() / from.getHeight();

    double deltaX = into.getCenterX() - (scaleX * from.getCenterX());
    double deltaY = into.getCenterY() - (scaleY * from.getCenterY());
    return new Point2dUtils.DoubleTranslater(
        Point2dUtils.newScaleTranslater(scaleX, scaleY),
        Point2dUtils.newDeltaTranslater(deltaX, deltaY));
  }

  /**
   * Update positions of moveNodes using the supplied translator.
   * 
   * The map of node positions is changed in place.  Move nodes without
   * a position are not added.
   */
  public static void translatePos(
      Collection<GraphNode> moveNodes, Map<GraphNode, Point2D> positions,
      Translater intoRegion) {

    for (GraphNode node : moveNodes) {
      Point2D location = positions.get(node);
      if (null != location) {
        positions.put(node, intoRegion.translate(location));
      }
    }
  }

  /**
   * Compute positions of moveNodes using the supplied translator.
   * 
   * A new map of node positions is provided.  Move nodes without a position
   * are omitted from the result.
   */
  public static Map<GraphNode, Point2D> translateNodes(
      Collection<GraphNode> moveNodes, Map<GraphNode, Point2D> positions,
      Translater translater) {

    Map<GraphNode, Point2D> result =
        Maps.newHashMapWithExpectedSize(moveNodes.size());

    for (GraphNode node : moveNodes) {
      Point2D location = positions.get(node);
      result.put(node, translater.translate(location));
    }
    return result;
  }

  public static void translatePos(
      Rectangle2D into, Collection<GraphNode> nodes, Map<GraphNode, Point2D> result) {
    // Translate node locations to region
    LayoutScaler scaler = new LayoutScaler(nodes, result);
    Translater intoRegion = scaler.intoRegion(into);
  
    translatePos(nodes, result, intoRegion);
  }
}
