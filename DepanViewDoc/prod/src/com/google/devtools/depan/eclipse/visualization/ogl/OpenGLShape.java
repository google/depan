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

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * A shape ready to be drawn by OpenGL..
 *
 * @author Yohann Coppel
 *
 */
public abstract class OpenGLShape extends GLEntity implements Shape {

  // TODO: use glCallList
  /**
   * A list of control points for this shape.
   */
  protected Point2D.Float[] controlPoints;
  protected int verticeCount = 0;

  /**
   * A general path, for contains / getBounds... operations.
   */
  protected GeneralPath shape = new GeneralPath();

  protected void setControlPointSize(int size) {
    controlPoints = new Point2D.Float[size];
  }

  /**
   * A Double alias for {@link #pushPoint(java.awt.geom.Point2D.Float)}.
   * @see #pushPoint(java.awt.geom.Point2D.Float)
   */
  protected void pushPoint(Point2D.Double p) {
    pushPoint(new Point2D.Float((float) p.x, (float) p.y));
  }

  /**
   * Build the shape (GeneralPath). This method must be called for each control
   * point.
   *
   * @param p the next vertice.
   */
  protected void pushPoint(Point2D.Float p) {
    if (verticeCount == 0) {
      shape.moveTo(p.x, p.y);
    } else if (verticeCount == controlPoints.length - 1) {
      shape.lineTo(p.x, p.y);
      shape.closePath();
    } else {
      shape.lineTo(p.x, p.y);
    }
    controlPoints[verticeCount] = p;
    verticeCount += 1;
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
