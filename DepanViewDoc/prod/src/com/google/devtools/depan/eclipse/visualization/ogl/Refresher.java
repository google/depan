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

import com.google.common.base.Preconditions;

import org.eclipse.swt.opengl.GLCanvas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A refresher that periodically repaints a GLScene.
 *
 * @author Yohann Coppel
 */
public class Refresher extends Thread {
  private static Logger LOG =
      LoggerFactory.getLogger(Refresher.class.getName());

  public static final int DELAY = 10;

  private final GLScene scene;

  /**
   * Construct a refresher for the given scene.
   *
   * @param scene the GLScene to refresh periodically.
   */
  public Refresher(GLScene scene) {
    Preconditions.checkNotNull(scene);
    this.scene = scene;
  }

  private static class SceneRefresher implements Runnable {

    private final GLScene scene;

    // values to compute the number of FPS.
    private long lastTime = System.currentTimeMillis();

    /**
     * @param scene
     */
    public SceneRefresher(GLScene scene) {
      this.scene = scene;
    }

    public boolean isDrawable() {
      GLCanvas canvas = scene.getContext();
      if (canvas == null) {
        return false;
      }
      return !canvas.isDisposed();
    }

    @Override
    public void run() {
      if (!isDrawable()) {
        return;
      }
      if (!scene.getContext().isVisible()) {
        return;
      }

      long currTime = System.currentTimeMillis();
      long interval = currTime - lastTime;
      lastTime = currTime;

      scene.render(interval);
    }
  }

  @Override
  public void run() {
    SceneRefresher sceneRefresher = new SceneRefresher(scene);

    while (sceneRefresher.isDrawable()) {
      scene.getContext().getDisplay().syncExec(sceneRefresher);
      try {
        Thread.sleep(DELAY);
      } catch (InterruptedException err) {
        LOG.error("Rendering loop interrupted", err);
      }
    }
  }
}
