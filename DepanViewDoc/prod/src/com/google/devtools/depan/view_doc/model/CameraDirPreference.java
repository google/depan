/*
 * Copyright 2015 The Depan Project Authors
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
package com.google.devtools.depan.view_doc.model;

import com.google.devtools.depan.eclipse.visualization.ogl.GLConstants;

/**
 * Capture the camera direction as type.
 * A dedicated type make XStream serialization work better.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class CameraDirPreference {
  private float x;
  private float y;
  private float z;

  public CameraDirPreference(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  public float getZ() {
    return z;
  }

  public void setX(float x) {
    this.x = x;
  }

  public void setY(float y) {
    this.y = y;
  }

  public void setZ(float z) {
    this.z = z;
  }

  public static CameraDirPreference getDefaultCameraPos() {
    float[] pos = GLConstants.DEFAULT_CAMERA_DIRECTION;
    return new CameraDirPreference(pos[0], pos[1], pos[2]);
  }
}
