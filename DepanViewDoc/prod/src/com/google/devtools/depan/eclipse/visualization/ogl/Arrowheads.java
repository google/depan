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

/**
 * Holds a number of Arrowhead implementation. This class cannot be
 * instantiated.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public class Arrowheads {

  private Arrowheads() { /* no instances */ }

  /**
   * This should be aligned with the default value in
   * {@code EdgeDisplayProperty.ArrowheadStyles}.
   */
  public static ArrowHead buildDefault() {
    return new ArtisticArrowhead();
  }

  /**
   * A 4-vertex old style artistic arrow head used with edges.
   */
  public static class ArtisticArrowhead extends ArrowHead {
    /**
     * Constructs a regular 4-vertex arrow head.
     */
    public ArtisticArrowhead() {
      setupControlPoints(4);
    }

    /**
     * Draws an artistic arrow head on the given <code>GL</code> instantiation.
     * Note that this method <b>does</b> fill the arrow head.
     *
     * @param gl Graphics object that will draw this object.
     */
    @Override
    public void draw(GL2 gl) {
      super.fill(gl);
    }
  }

  /**
   * A triangular filled arrow head used with edges.
   */
  public static class FilledArrowhead extends ArrowHead {
    /**
     * Constructs a triangular filled arrow head.
     */
    public FilledArrowhead() {
      setupControlPoints(3);
    }

    /**
     * Draws and fills a triangular arrow head on the given <code>GL</code>
     * instantiation. Note that this method <b>does</b> fill the arrow head.
     *
     * @param gl Graphics object that will draw this object.
     */
    @Override
    public void draw(GL2 gl) {
      super.fill(gl);
    }
  }

  /**
   * A triangular open arrow head used with only two edges of a triangle.
   */
  public static class OpenArrowhead extends ArrowHead {
    /**
     * Constructs a triangular open arrow head with only two edges of a triangle.
     */
    public OpenArrowhead() {
      setupControlPoints(3);
    }

    /**
     * Draws a triangular open arrow head on the given <code>GL</code>
     * instantiation.
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
      GLPanel.V(gl, controlPoints[1].x, controlPoints[1].y);
      GLPanel.V(gl, controlPoints[0].x, controlPoints[0].y);
      GLPanel.V(gl, controlPoints[2].x, controlPoints[2].y);
      gl.glEnd();
      gl.glPopMatrix();
    }

    /**
     * Draws a triangular open arrow head on the given <code>GL</code>
     * instantiation. Note that this method does <b>not</b> actually fill the
     * arrow head.
     *
     * @param gl Graphics object that will draw this object.
     */
    @Override
    public void fill(GL2 gl) {
      draw(gl);
    }
  }

  /**
   * A triangular unfilled arrow head used with edges.
   */
  public static class UnfilledArrowhead extends ArrowHead {
    /**
     * Constructs a triangular unfilled arrow head.
     */
    public UnfilledArrowhead() {
      setupControlPoints(3);
    }

    /**
     * Draws an unfilled triangular arrow head on the given <code>GL</code>
     * instantiation. Note that this method does <b>not</b> actually fill the
     * arrow head.
     *
     * @param gl Graphics object that will draw this object.
     */
    @Override
    public void fill(GL2 gl) {
      super.draw(gl);
    }
  }
}
