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

import java.awt.geom.Point2D;

/**
 * Create a star, or a regular polygon, depending on the insideness value.
 *
 * @author Yohann Coppel
 *
 */
public class Star extends OpenGLShape {

  @SuppressWarnings("unused")
  private static final long serialVersionUID = 0L;

  /**
   * Create a Star, or a regular polygon if insideness is 1.0f.
   *
   * @param name shapeID for this star.
   * @param points for a `points`-pointed star. (e.g. 5-pointed star)
   * @param rotation rotation angle in radian
   * @param insideness inside radius, in percentage of radius.
   */
  public Star(int points, float rotation, float insideness) {
    float x = 0;
    float y = 0;
    float radius = 0.5f;
    points = points < 3 ? 3 : points;

    rotation = rotation + (float) (Math.PI / 2.0);
    // flat means that between each star-point, the line is straight,
    // therefore we have a regular polygon.
    boolean flat = insideness >= 1.0f;

    setControlPointSize(flat ? points : 2 * points);

    final float pi2 = 2 * (float) Math.PI;
    float insideRadius = radius * insideness;

    Point2D.Float pt;

    // compute each vertex of the star
    for (int p = 0; p < points; ++p) {
      double vertex = p;
      double angle = vertex * pi2 / points + rotation;

      pt = new Point2D.Float(
          (float) (x + Math.cos(angle) * radius),
          (float) (y + Math.sin(angle) * radius));

      pushPoint(pt);

      if (!flat) {
        double angle2 = (vertex + 0.5) * pi2 / points + rotation;
        pt = new Point2D.Float(
            (float) (x + Math.cos(angle2) * insideRadius),
            (float) (y + Math.sin(angle2) * insideRadius));
        pushPoint(pt);
      }
    }

  }

  @Override
  public void draw(GL2 gl) {
    //gl.glPushMatrix();
    //gl.glTranslatef(translateX, translateY, translateZ);
    //gl.glScalef(scaleX, scaleY, scaleZ);
    gl.glBegin(GL2.GL_LINE_STRIP);
    for (Point2D.Float p : controlPoints) {
      GLScene.V(gl, p.x * scaleX + translateX, p.y * scaleY + translateY);
      //GLScene.V(gl,p.x, p.y);
    }
    GLScene.V(gl, controlPoints[0].x * scaleX + translateX,
        controlPoints[0].y * scaleY + translateY);
    //GLScene.V(gl,controlPoints[0].x, controlPoints[0].y);
    gl.glEnd();
    //gl.glPopMatrix();
  }

  @Override
  public void fill(GL2 gl) {
    //gl.glPushMatrix();
    //gl.glTranslatef(translateX, translateY, translateZ);
    //gl.glScalef(scaleX, scaleY, scaleZ);
    gl.glBegin(GL2.GL_TRIANGLE_FAN);
    GLScene.V(gl, translateX, translateY); // point 0:0 : center of this star
    //GLScene.V(gl, 0, 0);
    for (Point2D.Float p : controlPoints) {
      GLScene.V(gl, p.x * scaleX + translateX, p.y * scaleY + translateY);
      //GLScene.V(gl,p.x, p.y);
    }
    GLScene.V(gl, controlPoints[0].x * scaleX + translateX,
        controlPoints[0].y * scaleY + translateY);
    //GLScene.V(gl,controlPoints[0].x, controlPoints[0].y);
    gl.glEnd();
    //gl.glPopMatrix();
  }
}

