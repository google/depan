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

import java.awt.Shape;

import javax.media.opengl.GL2;

/**
 * Abstract class for any shape, that can be rendered on an openGL canvas.
 *
 * @author Yohann Coppel
 *
 */
public abstract class GLEntity implements Shape {

  protected float translateX = 0f, translateY = 0f, translateZ = 0f;
  protected float scaleX = 1f, scaleY = 1f, scaleZ = 1f;

  public void setTranslation(float x, float y, float z) {
    this.translateX = x;
    this.translateY = y;
    this.translateZ = z;
  }

  public void setScale(float x, float y, float z) {
    this.scaleX = x;
    this.scaleY = y;
    this.scaleZ = z;
  }

  public abstract void draw(GL2 gl);

  public abstract void fill(GL2 gl);

}

