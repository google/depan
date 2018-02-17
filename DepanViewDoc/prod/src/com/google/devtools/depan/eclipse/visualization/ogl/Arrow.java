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
    this.head = Arrowheads.buildDefault();
  }

  /**
   * Links the two given shapes (and their centers) with this arrow. Shapes are
   * used to determine if points are inside the shapes, or outside. This is
   * useful to draw an arrow that starts and end at the edge of the shape, and
   * not in the middle of the shape.
   *
   * @param gl GL object where to draw this shape.
   * @param arcInfo the arc to draw.
   */
  public void linkShapes(GL2 gl, ArcInfo arcInfo) {

    // enable GL_LINE_STIPPLE if edge must be dashed
    if (dashed) {
      gl.glEnable(GL2.GL_LINE_STIPPLE);
      gl.glLineStipple(1, (short) 0xf0f0);
    }

    drawCurve(gl, arcInfo);

    // now disable GL_LINE_STIPPLE if it was enabled
    if (dashed) {
      gl.glDisable(GL2.GL_LINE_STIPPLE);
    }

    drawHead(gl, arcInfo);
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

  @Override
  public void draw(GL2 gl) {
    // drawing this shape is more complex. must be drawn with linkShapes.
  }

  @Override
  public void fill(GL2 gl) {
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

  // private void drawCurve(GL2 gl, ArcBuilder builder, int headSeg, int tailSeg) {
  private void drawCurve(GL2 gl, ArcInfo arcInfo) {

    gl.glBegin(GL2.GL_LINE_STRIP);
    for (int segIndex = arcInfo.headSeg; segIndex <= arcInfo.tailSeg; segIndex++) {
      Point2D point = arcInfo.getPoint(segIndex);
      gl.glVertex2d(point.getX(), point.getY());
    }
    gl.glEnd();
  }

  private void drawHead(GL2 gl, ArcInfo arcInfo) {
    Vec2 tail = new Vec2(arcInfo.getPoint(arcInfo.tailSeg));
    Vec2 next = new Vec2(arcInfo.getPoint(arcInfo.tailSeg - 1));
    Vec2 dir = tail.minus(next);

    double slope = dir.y / dir.x;
    double angle = (float) Math.tanh(slope) - Math.PI / 2.0;
    if (tail.x < next.x) {
      angle += Math.PI;
    }

    head.setTranslation((float) (tail.x), (float) (tail.y), 0f);
    head.setScale(8f, 8f, 8f);
    head.setRotation(angle);
    head.fill(gl);
  }
}
