/*
 * Copyright 2018 The Depan Project Authors
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

import java.awt.geom.Point2D;

/**
 * @author Lee Carver
 */
public class ArcBuilder {

  private static float FLOAT_DIAMETER = (float) (Math.PI * 2.0d);

  private final Vec2 vec1;
  private final Vec2 vec2;

  private Point2D[] arcPoints;

  public ArcBuilder(Vec2 vec1, Vec2 vec2) {
    this.vec1 = vec1;
    this.vec2 = vec2;
  }

  public Point2D getPoint(int index) {
    return arcPoints[index];
  }

  public Point2D[] getPoints() {
    return arcPoints;
  }

  public int getHeadSegment(GLEntity shape1) {
    int next = trimHead(shape1);
    int head = next - 1;
    arcPoints[head] = findBoundary(shape1, arcPoints[head], arcPoints[next]);
    return head;
  }

  public int getTailSegment(GLEntity shape1) {
    int next = trimTail(shape1);
    int tail = next + 1;
    arcPoints[tail] = findBoundary(shape1, arcPoints[tail], arcPoints[next]);
    return tail;
  }

  public void calcSegments() {
    Vec2 dir = vec2.minus(vec1);

    Vec2 offset = new Vec2(-dir.y, dir.x).div(1.05f);
    Vec2 center = vec1.plus(vec2).div(2.0f).plus(offset);

    Vec2 norm1 = vec1.minus(center);
    float radius1 = norm1.normalize();
    float initAngle = norm1.radians();

    Vec2 norm2 = vec2.minus(center);
    norm2.normalize();
    float arcAngle = calcArcAngle(norm1, norm2, initAngle);
    int segments = Integer.max(100, (int) radius1);
    arcPoints = buildArcPoints(
        center.x, center.y, radius1, initAngle, arcAngle, segments);
  }

  public Point2D midpoint(int headSeg, int tailSeg) {
    int total = headSeg + tailSeg;
    int middle = total / 2;
    if ((middle * 2) == total) {
      return arcPoints[middle];
    }
    Point2D lo = arcPoints[middle];
    Point2D hi = arcPoints[middle + 1];
    double midX = (lo.getX() + hi.getX()) / 2.0;
    double midY = (lo.getY() + hi.getY()) / 2.0;
    return new Point2D.Double(midX, midY);
  }

  private float calcArcAngle(Vec2 vec1, Vec2 vec2,  float initAngle) {
    float termAngle = vec2.radians();
    float result = termAngle - initAngle;
    if (vec1.mult(vec2) > 0) {
      if (termAngle < initAngle) {
        return FLOAT_DIAMETER + result;
      }
    } else {
      if (initAngle < termAngle) {
        return FLOAT_DIAMETER + result;
      }
    }
    return result;
  }

  /**
   * @see http://slabode.exofire.net/circle_draw.shtml
   */
  private Point2D[] buildArcPoints(double cx, double cy, double r,
      double start_angle, double arc_angle, int num_segments) {
    // theta is now calculated from the arc angle instead.
    // The - 1 bit comes from the fact that the arc is open
    Point2D.Double[] result = new Point2D.Double[num_segments];
    double theta = arc_angle / (double) (num_segments - 1);

    double tangetial_factor = Math.tan(theta);

    double radial_factor = Math.cos(theta);

    // we now start at the start angle
    double x = r * Math.cos(start_angle);
    double y = r * Math.sin(start_angle);

    for (int ii = 0; ii < num_segments; ii++) {
      Point2D.Double point = new Point2D.Double(cx + x, cy + y);
      result[ii] = point;

      double tx = -y;
      double ty = x;

      x += tx * tangetial_factor;
      y += ty * tangetial_factor;

      x *= radial_factor;
      y *= radial_factor;
    }
    return result;
  }

  private Point2D findBoundary(
      GLEntity shape, Point2D point1, Point2D point2) {
    Vec2 vec1 = new Vec2(point1);
    Vec2 vec2 = new Vec2(point2);
    Vec2 dir = vec2.minus(vec1);
    float shift = 0.5f;
    Vec2 move = dir.mult(shift);
    while (move.length() > AWTShape.lineFlatness) {
      vec1 = vec1.plus(move);
      shift = shift * 0.5f;
      if (shape.contains(vec1.x, vec1.y)) {
        move = dir.mult(shift);
      } else {
        move = dir.mult(- shift);
      }
    }

    return new Point2D.Double(vec1.x, vec1.y);
  }

  private int trimHead(GLEntity shape1) {
    for (int ndx = 1; ndx < arcPoints.length; ndx++ ) {
      if (!shape1.contains(arcPoints[ndx])) {
        return ndx;
      }
    }

    return arcPoints.length - 1;
  }

  private int trimTail(GLEntity shape1) {
    for (int ndx = arcPoints.length - 2; ndx >= 0; ndx-- ) {
      if (!shape1.contains(arcPoints[ndx])) {
        return ndx;
      }
    }

    return 0;
  }
}
