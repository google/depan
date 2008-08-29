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

package com.google.devtools.depan.eclipse.visualization.ogl;

import com.google.devtools.depan.view.EdgeDisplayProperty.ArrowheadStyle;

import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

import javax.media.opengl.GL;

/**
 * An arrow shape: the line, and the head.
 *
 * @author Yohann Coppel
 *
 */
public class Arrow extends OpenGLShape {

  /**
   * Arrow head of this arrow.
   */
  private ArrowHead head;

  /**
   * Shows whether the arrow is dashed or solid.
   */
  private boolean dashed = false;

  /**
   * Constructs a new <code>Arrow</code> with the default {@link ArrowHead}.
   */
  public Arrow() {
    this.head = ArrowHead.createNewArrowhead(ArrowheadStyle.getDefault());
  }

  /**
   * Links the two given shapes (and their centers) with this arrow. Shapes are
   * used to determine if points are inside the shapes, or outside. This is
   * useful to draw an arrow that starts and end at the edge of the shape, and
   * not in the middle of the shape.
   *
   * @param gl GL object where to draw this shape.
   * @param center1 origin point of the arrow
   * @param center2 target point for this arrow
   * @param shape1 shape at the starting point
   * @param shape2
   * @param deviation
   * @return
   */
  public Point2D linkShapes(GL gl, Point2D center1, Point2D center2,
      GLEntity shape1, GLEntity shape2, float deviation) {
    Vec2 c1 = new Vec2(center1);
    Vec2 c2 = new Vec2(center2);

    Vec2 dir = c2.minus(c1);
    Vec2 norm = new Vec2(dir.y, -dir.x).mult(deviation);
    Vec2 middle = c1.plus(c2).div(2.0f).plus(norm);

    QuadCurve2D curve = new QuadCurve2D.Float(c1.x, c1.y, middle.x, middle.y,
        c2.x, c2.y);
    QuadCurve2D last = new QuadCurve2D.Float();

    // enable GL_LINE_STIPPLE if edge must be dashed
    if (dashed) {
      gl.glEnable(GL.GL_LINE_STIPPLE);
      gl.glLineStipple(1, (short) 0xf0f0);
    }
    boolean ok = drawCurve(gl, center1, center2, curve, shape1, shape2, last);

    // now disable GL_LINE_STIPPLE if it was enabled
    if (dashed) {
      gl.glDisable(GL.GL_LINE_STIPPLE);
    }

    if (ok) {
      // draw the head at the last position, trying to orient it so that it
      // follows the last segment of the "line".
      double x1 = last.getP1().getX();
      double y1 = last.getP1().getY();
      double x2 = last.getP2().getX();
      double y2 = last.getP2().getY();

      double slope = (y2 - y1) / (x2 - x1);
      double angle = (float) Math.tanh(slope) - Math.PI / 2.0;
      if (x2 < x1) {
        angle += Math.PI;
      }

      head.setTranslation((float) (last.getP2().getX()), (float) (last.getP2()
          .getY()), 0f);
      head.setScale(8f, 8f, 8f);
      head.setRotation(angle);
      head.fill(gl);
    }

    QuadCurve2D left = new QuadCurve2D.Float();
    QuadCurve2D right = new QuadCurve2D.Float();
    curve.subdivide(left, right);

    return right.getP1(); // new Point2D.Float(middle.x, middle.y);
  }

  @Override
  public void setTranslation(float x, float y, float z) {
    super.setTranslation(x, y, z);
    head.setTranslation(x, y, z);
  }

  @Override
  public void setScale(float x, float y, float z) {
    super.setScale(x, y, z);
    head.setScale(x, y, z);
  }

  /**
   * Link the two points with the given curve, by subdividing, until the length
   * of each segment is lower or equal to AWTShape.LINE_FLATNESS. The last curve
   * painted is returned in the last argument, <code>curve</code>. It can
   * be used to know the real endpoint, ad the edge of the second curve.
   *
   * @param gl GL object to draw
   * @param center1 start point
   * @param center2 end point
   * @param curve curve to follow
   * @param shape1 shape at starting point
   * @param shape2 shape at end point
   * @param last last curve painted.
   * @return
   */
  public boolean drawCurve(GL gl, Point2D center1, Point2D center2,
      QuadCurve2D curve, GLEntity shape1, GLEntity shape2, QuadCurve2D last) {
    double p1X = curve.getP1().getX();
    double p1Y = curve.getP1().getY();
    double p2X = curve.getP2().getX();
    double p2Y = curve.getP2().getY();

    // different cases to handle are represented visually with the following
    // convertion: [ ] represent a shape, and --- a segment.
    // [ ---]--- is a segment with starting point inside a shape, end endpoint
    // outside...

    if (shape1.contains(p2X, p2Y)) {
      // [ -- ] [ ]
      // first shape contains end point. don't do anything.
      return false;
    }
    if (shape2.contains(p1X, p1Y)) {
      // [ ] [ -- ]
      // second shape contains starting point. don't do anything
      return false;
    }
    if (shape1.contains(p1X, p1Y)) {
      if (shape2.contains(p2X, p2Y)) {
        // [ ---]----[--- ]
        // subdivide to conquer...
        return divideAndDraw(gl, center1, center2, curve, shape1, shape2,
            last);
      } else {
        // [ ---]-- [ ]
        if (new Vec2(curve.getP2()).minus(new Vec2(curve.getP1())).length()
            < AWTShape.lineFlatness) {
          // segment small enough
          AWTShape.draw(gl, curve);
          return false;
        } else {
          // segment to large. divide it.
          divideAndDraw(gl, center1, center2, curve, shape1, shape2, last);
          return false;
        }
      }
    } else if (shape2.contains(p2X, p2Y)) {
      // [ ] --[--- ]
      if (new Vec2(curve.getP2()).minus(new Vec2(curve.getP1())).length()
          < AWTShape.lineFlatness) {
        // segment small enough
        last.setCurve(curve);
        AWTShape.draw(gl, curve);
        return true;
      } else {
        // segment to large. divide it.
        return divideAndDraw(gl, center1, center2, curve, shape1, shape2,
            last);
      }
    } else {
      // [ ] -- [ ]
      AWTShape.draw(gl, curve);
    }
    return false;
  }

  /**
   * Divide the given curve in two half, and call drawCurve on each part.
   */
  public boolean divideAndDraw(GL gl, Point2D center1, Point2D center2,
      QuadCurve2D curve, GLEntity shape1, GLEntity shape2, QuadCurve2D last) {
    QuadCurve2D left = new QuadCurve2D.Float();
    QuadCurve2D right = new QuadCurve2D.Float();
    curve.subdivide(left, right);
    boolean resL = drawCurve(gl, center1, center2, left, shape1, shape2, last);
    boolean resR = drawCurve(gl, center1, center2, right, shape1, shape2, last);
    return resL || resR;
  }

  @Override
  public void draw(GL gl) {
    // drawing this shape is more complex. must be drawn with linkShapes.
  }

  @Override
  public void fill(GL gl) {
    // can not fill this shape. nothing to do.
  }

  /**
   * Returns if this arrow is dashed.
   *
   * @return <code>true</code> if this arrow is dashed, <code>false</code> if
   * solid.
   */
  public boolean isDashed() {
    return dashed;
  }

  /**
   * Sets if this arrow is dashed.
   *
   * @param dashed <code>true</code> if this arrow is set to dashed,
   * <code>false</code> if set to solid.
   */
  public void setDashed(boolean dashed) {
    this.dashed = dashed;
  }

  /**
   * Sets the {@link ArrowHead} of this object to the given arrow head.
   *
   * @param arrowhead New {@link ArrowHead} of this arrow.
   */
  public void setArrowhead(ArrowHead arrowhead) {
    head = arrowhead;
  }
}
