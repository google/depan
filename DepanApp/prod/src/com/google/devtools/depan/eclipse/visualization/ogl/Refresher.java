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

/**
 * A refresher, that periodically repaint a GLScene.
 *
 * @author Yohann Coppel
 *
 */
public class Refresher extends Thread {
  public static final int DELAY = 10;

  // values to compute the number of FPS.
  private long frames = 0;
  private long time = 0;
  private long lastTime = System.currentTimeMillis();

  private GLScene scene;

  /**
   * Construct a refresher for the given scene.
   *
   * @param scene the GLScene to refresh periodically.
   */
  public Refresher(GLScene scene) {
    this.scene = scene;
  }

  private Runnable action = new Runnable() {
    public void run() {
      if (scene != null && scene.getContext() != null
          && !scene.getContext().isDisposed()) {
        scene.render(DELAY);

        // computations for FPS
        long t = System.currentTimeMillis();
        time += t - lastTime;
        lastTime = t;
        frames++;
        // printing the FPS number every 10 seconds.
        if (time > 10000) {
          /*System.out.println(""
              + ((float) frames / (float) time * 1000f) + " FPS");*/
          time = 0;
          frames = 0;
        }
      }
    }
  };

  @Override
  public void run() {
    while ((this.scene != null) && (this.scene.getContext() != null)
        && (!this.scene.getContext().isDisposed())) {
      this.scene.getContext().getDisplay().syncExec(action);
      try {
        Thread.sleep(DELAY);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
