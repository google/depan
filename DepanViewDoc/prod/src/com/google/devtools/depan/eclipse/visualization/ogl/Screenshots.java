/*
 * Copyright 2014 The Depan Project Authors
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

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES3;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.GLPixelStorageModes;
import com.jogamp.opengl.util.awt.ImageUtil;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;

/**
 * Isolate deprecation warning for current screenshot mechanism.
 */
public class Screenshots {

  private Screenshots() {
    // Prevent instantiation
  }

  public static BufferedImage grab(int sizeX, int sizeY) {
    return readToBufferedImage(0, 0, sizeX, sizeY, false);
  }

  /**
   * Stolen from com.jogamp.opengl.util.awt.Screenshot.readToBufferedImage()
   * 
   * JOGL 2.1.2
   */
  private static BufferedImage readToBufferedImage(
      int x,int y, int width, int height, boolean alpha) throws GLException {

    int bufImgType = (alpha ? BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR);
    int readbackType = (alpha ? GL2.GL_ABGR_EXT : GL2ES3.GL_BGR);

    // Allocate necessary storage
    BufferedImage image = new BufferedImage(width, height, bufImgType);

    GLContext glc = GLContext.getCurrent();
    GL gl = glc.getGL();

    // Set up pixel storage modes
    GLPixelStorageModes psm = new GLPixelStorageModes();
    psm.setPackAlignment(gl, 1);

    // read the BGR values into the image
    gl.glReadPixels(x, y, width, height, readbackType,
        GL.GL_UNSIGNED_BYTE,
        ByteBuffer.wrap(((DataBufferByte) image.getRaster().getDataBuffer()).getData()));

    // Restore pixel storage modes
    psm.restore(gl);

    if( glc.getGLDrawable().isGLOriented() ) {
      // Must flip BufferedImage vertically for correct results
      ImageUtil.flipImageVertically(image);
    }
    return image;
  }
}
