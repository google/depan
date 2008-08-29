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

import java.awt.Color;
import java.awt.geom.Point2D;

import javax.media.opengl.GL;

import com.google.devtools.depan.eclipse.visualization.plugins.core.EdgeRenderingPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.core.NodeRenderingPlugin;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

/**
 * Plugin drawing nodes and edges on an openGL canvas.
 *
 * @author Yohann Coppel
 *
 */
public class DrawingPlugin implements NodeRenderingPlugin, EdgeRenderingPlugin {

  private GL gl;
  private GLScene scene;

  public DrawingPlugin(GL gl, GLScene scene) {
    this.gl = gl;
    this.scene = scene;
  }

  /**
   * Draw a node.
   */
  public boolean apply(NodeRenderingProperty property) {
    if (!property.isVisible) {
      return false;
    }
    gl.glPushMatrix();
    gl.glPushName(property.shapeId);

    // move
    property.shape.setTranslation(property.positionX * GLScene.FACTOR,
        property.positionY * GLScene.FACTOR, 0f);

    // scale
    property.shape.setScale(property.size,
        property.size * property.ratio, property.size);

    // fill the shape
    if (property.isFilled) {
      gl.glColor4f(property.fillColor.getRed() / 255f,
          property.fillColor.getGreen() / 255f,
          property.fillColor.getBlue() / 255f,
          property.fillColor.getAlpha() / 255f);
      property.shape.fill(gl);
    }

    // draw the border
    if (property.strokeWidth > 0.0f) {
      gl.glLineWidth(property.strokeWidth);
      gl.glColor4f(property.strokeColor.getRed() / 255f,
          property.strokeColor.getGreen() / 255f,
          property.strokeColor.getBlue() / 255f,
          property.strokeColor.getAlpha() / 255f);
      property.shape.draw(gl);
    }

    // we don't want the label to be clickable,
    // so we just pop the name before drawing it.
    gl.glPopName();

    // draw the label
    if (property.isTextVisible) {
      paintLabel(property);
    }

    // draw a little "+" on the top right corner of the node if it has
    // nodes collapsed under.
    if (property.hasCollapsedNodeUnder) {
      double centerX = property.positionX+property.size/2;
      double centerY = property.positionY+property.size/2;
      double halfWidth = 0.7;
      double halfHeight = 4;
      gl.glBegin(GL.GL_QUADS);
      gl.glColor4f(1f, 1f, 1f, 0.5f);
      // vertical line
      gl.glVertex2d(centerX - halfWidth, centerY + halfHeight);
      gl.glVertex2d(centerX + halfWidth, centerY + halfHeight);
      gl.glVertex2d(centerX + halfWidth, centerY - halfHeight);
      gl.glVertex2d(centerX - halfWidth, centerY - halfHeight);
      // left part of horizontal line
      gl.glVertex2d(centerX - halfHeight, centerY + halfWidth);
      gl.glVertex2d(centerX - halfWidth, centerY + halfWidth);
      gl.glVertex2d(centerX - halfWidth, centerY - halfWidth);
      gl.glVertex2d(centerX - halfHeight, centerY - halfWidth);
      // right part.
      gl.glVertex2d(centerX + halfWidth, centerY + halfWidth);
      gl.glVertex2d(centerX + halfHeight, centerY + halfWidth);
      gl.glVertex2d(centerX + halfHeight, centerY - halfWidth);
      gl.glVertex2d(centerX + halfWidth, centerY - halfWidth);
      gl.glVertex3d(0, 0, 0);
      gl.glEnd();
    }


    gl.glPopMatrix();

    return true;
  }

  /**
   * Draw an edge.
   */
  public boolean apply(EdgeRenderingProperty property) {
    if (!property.node1.isVisible || !property.node2.isVisible) {
      return false;
    }
    gl.glPushName(property.shapeId);
    if (property.strokeWidth > 0.0f) {
      gl.glLineWidth(property.strokeWidth);
      gl.glColor4f(property.strokeColor.getRed() / 255f,
          property.strokeColor.getGreen() / 255f,
          property.strokeColor.getBlue() / 255f,
          property.strokeColor.getAlpha() / 255f);

      // get the real endpoints for the edge. Necessary to have the
      // real shapes if the nodes are collapsed.
      NodeRenderingProperty node1 = property.node1.isCompletelyCollapsed() ?
          property.node1.collapsedUnder : property.node1;
      NodeRenderingProperty node2 = property.node2.isCompletelyCollapsed() ?
          property.node2.collapsedUnder : property.node2;

      Point2D middle = ((Arrow) property.shape).linkShapes(gl,
          new Point2D.Float(property.p1X * GLScene.FACTOR,
              property.p1Y * GLScene.FACTOR),
          new Point2D.Float(property.p2X * GLScene.FACTOR,
              property.p2Y * GLScene.FACTOR),
          node1.shape, node2.shape, property.deviation);

      if (property.isTextVisible) {
        paintLabel(property, middle);
      }
    }
    gl.glPopName();
    return true;
  }

  /**
   * Redner a label for the given edge at the given position (center of the
   * label)
   * @param property
   * @param p center point where to draw the label.
   */
  private void paintLabel(EdgeRenderingProperty property, Point2D p) {
    // Use the GL_MODULATE texture function to effectively multiply
    // each pixel in the texture by the current alpha value
    gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);

    if (property.textIsDirty) {
      // recreate the texture, and save it.
      property.textTexture = property.textRenderer.getTexture();
      property.textIsDirty = false;
    }

    renderTexture(property.textTexture, p.getX(), p.getY());
  }

  /**
   * Render the label for the given {@link NodeRenderingProperty}.
   * @param property
   */
  private void paintLabel(NodeRenderingProperty property) {
    // Use the GL_MODULATE texture function to effectively multiply
    // each pixel in the texture by the current alpha value
    gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);

    if (property.textIsDirty) {
      // recreate the texture, and save it.
      property.textTexture = property.textRenderer.getTexture();
      property.textIsDirty = false;
    }

    float halfWidth = (float) property.textTexture.getWidth() / 4f;
    float halfHeight = (float) property.textTexture.getHeight() / 4f;
    float centerX = (property.size + halfWidth) * property.textDx;
    float centerY = (property.size * property.ratio + halfHeight)
        * property.textDy;

    centerX = property.positionX * GLScene.FACTOR + centerX;
    centerY = property.positionY * GLScene.FACTOR + centerY;

    renderTexture(property.textTexture, centerX, centerY);
  }

  /**
   * Render a texture to the given position.
   *
   * @param texture Texture to draw
   * @param centerX X coordinate for the center of the texture
   * @param centerY Y coordinate for the center of the texture
   */
  private void renderTexture(Texture texture, double centerX, double centerY) {
    TextureCoords tc = texture.getImageTexCoords();
    float tx1 = tc.left();
    float ty1 = tc.top();
    float tx2 = tc.right();
    float ty2 = tc.bottom();
    float halfWidth = (float) texture.getWidth() / 4f;
    float halfHeight = (float) texture.getHeight() / 4f;
    texture.bind();
    texture.enable();

    Color foreground = scene.getForegroundColor();
    gl.glColor4f(foreground.getRed() / 255f,
        foreground.getGreen() / 255f,
        foreground.getBlue() / 255f,
        foreground.getAlpha() / 255f);

    gl.glPushMatrix();
    float[] translate = GLScene.P((float) centerX, (float) centerY);
    gl.glTranslatef(translate[0], translate[1], translate[2]);
    gl.glBegin(GL.GL_QUADS);
    // divided by 2 to get nicer textures
    // divided by 4 when we center it : 1/2 on each side of x axis for instance.
    gl.glTexCoord2f(tx1, ty1);
    GLScene.V(gl, -halfWidth, halfHeight);
    gl.glTexCoord2f(tx2, ty1);
    GLScene.V(gl, halfWidth,  halfHeight);
    gl.glTexCoord2f(tx2, ty2);
    GLScene.V(gl, halfWidth, -halfHeight);
    gl.glTexCoord2f(tx1, ty2);
    GLScene.V(gl, -halfWidth, -halfHeight);
    gl.glEnd();
    gl.glPopMatrix();

    texture.disable();
  }

  @Override
  public void postFrame() {
  }

  @Override
  public void preFrame(float elapsedTime) {
  }

  @Override
  public void dryRun(EdgeRenderingProperty p) {
  }

  @Override
  public void dryRun(NodeRenderingProperty p) {
  }

  public boolean keyPressed(int keycode, char character, boolean ctrl,
      boolean alt, boolean shift) {
    return false;
  }
}

