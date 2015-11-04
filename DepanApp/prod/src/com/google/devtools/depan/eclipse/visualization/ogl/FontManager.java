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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.awt.TextureRenderer;

/**
 * Simple class handling the drawing of text on an OpenGl canvas.
 *
 * @author Yohann Coppel
 *
 */
public final class FontManager {

  private FontManager() { }

  public static TextRenderer textRenderer = new TextRenderer(new Font(
      "SansSerif", Font.BOLD, 18), true, true);

  public static void print(GL2 gl, String text, float x, float y, float z,
      float magnify) {
    FontManager.textRenderer.beginRendering(10, 10);
    FontManager.textRenderer.draw(text, (int) x, (int) y);
    FontManager.textRenderer.endRendering();
  }

  /**
   * Render the given text directly to a texture using java2D, and return this
   * texture. The text is painted in white, using transparency. One can then
   * redefine the color just before rendering, with a glColor call.
   *
   * @param text text to paint.
   * @return
   */
  public static TextureRenderer makeText(String text) {
    // create texture for the text
    TextureRenderer textRenderer = new TextureRenderer(1, 3, false);
    Graphics2D g2d = textRenderer.createGraphics();
    FontRenderContext frc = g2d.getFontRenderContext();
    Font f = new Font("Arial", Font.BOLD, 18);
    String s = new String(text);
    TextLayout tl = new TextLayout(s, f, frc);
    // get the necessary size for the text to be rendered
    Rectangle2D bounds = tl.getBounds();
    // add some margin...
    int width = (int) bounds.getWidth() + 6;
    int height = (int) bounds.getHeight() + 6;
    // create a new texture renderer with the exact correct width and height.
    textRenderer = new TextureRenderer(width, height, true);
    textRenderer.setSmoothing(true);
    g2d = textRenderer.createGraphics();
    g2d.setColor(Color.white);
    //coordinate are inversed: 0:0 is top-left.
    tl.draw(g2d, 3, height - 3);

    textRenderer.markDirty(0, 0, width, height);
    return textRenderer;
  }

}
