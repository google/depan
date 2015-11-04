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

import com.jogamp.opengl.GL2;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * A class capable of drawing <strong>simple</strong> AWT shapes on an openGL
 * canvas.
 *
 * @author Yohann Coppel
 *
 */
public class AWTShape extends GLEntity {
  public static double lineFlatness = 0.2;
  public static double shapeFlatness = 0.05;

  /**
   * The AWT shape to render
   */
  private Shape shape;

  public AWTShape(Shape s) {
    this.shape = s;
  }

  @Override
  public void draw(GL2 gl) {
    gl.glPushMatrix();
    gl.glTranslatef(translateX, translateY, translateZ);
    gl.glScalef(scaleX, scaleY, scaleZ);
    draw(gl, shape);
    gl.glPopMatrix();
  }

  @Override
  public void fill(GL2 gl) {
    gl.glPushMatrix();
    gl.glTranslatef(translateX, translateY, translateZ);
    gl.glScalef(scaleX, scaleY, scaleZ);
    fill(gl, shape);
    gl.glPopMatrix();
  }

  /**
   * Static method that can be used to render the path of a normal AWT shape.
   * Only simple shapes can be rendered correctly.
   *
   * @param gl
   * @param s the shape to render.
   */
  public static void draw(GL2 gl, Shape s) {
    gl.glBegin(GL2.GL_LINE_STRIP);
    drawShape(gl, s);
    gl.glEnd();
  }

  /**
   * Static method that can be used to fill a normal AWT shape.
   * Only simple shapes can be rendered correctly.
   *
   * @param gl
   * @param s the shape to render.
   */
  public static void fill(GL2 gl, Shape s) {
    gl.glBegin(GL2.GL_TRIANGLE_FAN);
    drawShape(gl, s);
    gl.glEnd();
  }

  /**
   * Draw the given shape on the given OpenGL object.
   *
   * @param gl
   * @param s
   */
  private static void drawShape(GL2 gl, Shape s) {
    PathIterator it = s.getPathIterator(new AffineTransform(), shapeFlatness);
    float[] lastMoveTo = new float[6];
    float[] f = new float[6];
    while (!it.isDone()) {
      int res = it.currentSegment(f);
      switch (res) {
        case PathIterator.SEG_CLOSE:
          GLPanel.V(gl, lastMoveTo[0], lastMoveTo[1]);
          break;
        case PathIterator.SEG_MOVETO:
          GLPanel.V(gl, f[0], f[1]);
          System.arraycopy(f, 0, lastMoveTo, 0, 6);
          break;
        case PathIterator.SEG_LINETO:
          GLPanel.V(gl, f[0], f[1]);
          break;
        case PathIterator.SEG_CUBICTO:
        CubicCurve2D c = new CubicCurve2D.Float(lastMoveTo[0], lastMoveTo[1],
            f[0], f[1], f[2], f[3], f[4], f[5]);
        drawShape(gl, c);
          break;
        default:
          throw new Error("Error while drawing AWT shape. "
              + "Path iterator setment not handled:" + res);
      }
      it.next();
    }
  }

  //------- Redirect API to the GeneralPath implementation.

  @Override
    public boolean contains(Point2D p) {
      return shape.contains(new Point2D.Float(
          ((float) p.getX() - translateX) / scaleX,
          ((float) p.getY() - translateY) / scaleY));
    }

  @Override
    public boolean contains(Rectangle2D r) {
      return this.contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

  @Override
    public boolean contains(double x, double y) {
      return shape.contains((x - translateX) / scaleX,
          (y - translateY) / scaleY);
    }

  @Override
    public boolean contains(double x, double y, double w, double h) {
      return shape.contains(
          (x - translateX) / scaleX,
          (y - translateY) / scaleY,
          w / scaleX, h / scaleY);
    }

  @Override
    public Rectangle getBounds() {
      return shape.getBounds();
    }

  @Override
    public Rectangle2D getBounds2D() {
      return shape.getBounds2D();
    }

  @Override
    public PathIterator getPathIterator(AffineTransform at) {
      return shape.getPathIterator(at);
    }

  @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
      return shape.getPathIterator(at, flatness);
    }

  @Override
    public boolean intersects(Rectangle2D r) {
      return this.intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

  @Override
    public boolean intersects(double x, double y, double w, double h) {
      return shape.intersects(
          (x - translateX) / scaleX,
          (y - translateY) / scaleY,
          w / scaleX, h / scaleY);
    }

}
