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
 * Abstract class that represents an arrow head.
 *
 * @author Yohann Coppel
 *
 */
public abstract class ArrowHead extends OpenGLShape {

  protected static final double ANGLE = 20.0;
  protected static final double DEPTH = 0.7;
  protected double rotation = 0;

  /**
   * Calculates the locations of each vertex of this arrow head. For triangular
   * arrow heads there are 3 vertexes while the 'artistic' arrow head (default)
   * contains 4 vertexes.
   *
   * @param numberOfVertexes Number of vertexes of this arrow head. It must be 3
   * for triangular arrow heads, and 4 for 'artistic' arrow head (default).
   */
  protected void setupControlPoints(int numberOfVertexes) {
    // TODO(tugrul): Convert this to use a list of x,y pairs that are rotated
    // into position as needed.
    //
    // initialize from one list or the other.
    // arrow3Point = {{1, 0}, {0, 4}, {-1, 0}};
    // arrow4Point = {{1, 0}, {0, 4}, {-1, 0}, {0, 1}};

    float x = 0f;
    float y = 0f;
    float size = 1f;
    double rotation = 0;

    setControlPointSize(numberOfVertexes);

    //   1
    //   .
    //  / \
    // /.^.\
    //2  3  4   3 = middlePoint.
    final float secondPointAngle = (float) ((270.0 - ANGLE) * 2.0 * Math.PI
        / 360.0 + rotation); // in rad.
    final float fourthPointAngle = (float) ((270.0 + ANGLE) * 2.0 * Math.PI
        / 360.0 + rotation); // in rad.

    // Push Point 1
    pushPoint(new Point2D.Float(x, y));

    // Push Point 2
    pushPoint(new Point2D.Double(x + Math.cos(secondPointAngle) * size, y
        + Math.sin(secondPointAngle) * size));

    // Push Point 3 if necessary (used by Artistic Arrow head)
    if (numberOfVertexes == 4) {
      final float middlePointAngle = (float) (270.0 * 2.0 * Math.PI
          / 360.0 + rotation); // in rad.
      Point2D.Double new2DPoint = new Point2D.Double(
          x + Math.cos(middlePointAngle) * DEPTH * size,
          y + Math.sin(middlePointAngle) * DEPTH * size);
      pushPoint(new2DPoint);
    }

    // Push Point 4
    pushPoint(new Point2D.Double(x + Math.cos(fourthPointAngle) * size, y
        + Math.sin(fourthPointAngle) * size));
  }

  public void setRotation(double rotation) {
    this.rotation = rotation;
  }

  /**
   * Draws an arrowhead on the given <code>GL</code> instantiation. The actual
   * shape depends on the Arrowhead object that is being drawn.
   *
   * @param gl Graphics object that will draw this object.
   */
  @Override
  public void draw(GL2 gl) {
    gl.glPushMatrix();
    float[] translate = GLScene.P(translateX, translateY);
    gl.glTranslatef(translate[0], translate[1], translate[2]);
    gl.glScalef(scaleX, scaleY, scaleZ);
    gl.glRotated(rotation * 180 / Math.PI, 0, 0, 1);
    gl.glBegin(GL2.GL_LINE_STRIP);
    GLPanel.V(gl, controlPoints[0].x, controlPoints[0].y);
    GLPanel.V(gl, controlPoints[1].x, controlPoints[1].y);
    GLPanel.V(gl, controlPoints[2].x, controlPoints[2].y);
    // check if there exists 4 points before drawing the 4th point
    if (controlPoints.length == 4) {
      GLPanel.V(gl, controlPoints[3].x, controlPoints[3].y);
    }
    GLPanel.V(gl, controlPoints[0].x, controlPoints[0].y);
    gl.glEnd();
    gl.glPopMatrix();
  }

  /**
   * Draws and fills an arrowhead on the given <code>GL</code> instantiation.
   * The actual shape depends on the Arrowhead object that is being drawn.
   *
   * @param gl Graphics object that will draw this object.
   */
  @Override
  public void fill(GL2 gl) {
    // points must be use with the correct order (2-3-0-1) to use triangle fan.
    gl.glPushMatrix();
    float[] translate = GLScene.P(translateX, translateY);
    gl.glTranslatef(translate[0], translate[1], translate[2]);
    gl.glScalef(scaleX, scaleY, scaleZ);
    gl.glRotated(rotation * 180 / Math.PI, 0, 0, 1);
    gl.glBegin(GL2.GL_TRIANGLE_FAN);
    GLPanel.V(gl, controlPoints[2].x, controlPoints[2].y);
    // check if there exists 4 points before drawing the 4th point
    if (controlPoints.length == 4) {
      GLPanel.V(gl, controlPoints[3].x, controlPoints[3].y);
    }
    GLPanel.V(gl, controlPoints[0].x, controlPoints[0].y);
    GLPanel.V(gl, controlPoints[1].x, controlPoints[1].y);
    gl.glEnd();
    gl.glPopMatrix();
  }
}
