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

package com.google.devtools.depan.eclipse.visualization.plugins.impl;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.google.devtools.depan.eclipse.visualization.ogl.GLScene;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeRenderingProperty;
import com.google.devtools.depan.eclipse.visualization.ogl.SceneGrip;
import com.google.devtools.depan.eclipse.visualization.plugins.core.NodeRenderingPlugin;

/**
 * A plugin that apply a factor to shrink the node positions. This has the
 * effect of increasing (or decreasing) space between nodes, without drawing
 * them bigger (or smaller).
 *
 * It is also capable of computing the best shrink factor, which makes every
 * nodes fit in the current view (only uses nodes positions, not the node
 * sizes).
 *
 * <p>
 * This plugin uses these key shortcuts:
 * <ul>
 * <li><b>+</b> to apply a factor of 1.1 on both coordinates</li>
 * <li><b>-</b> to apply a factor of 0.9 on both coordinates</li>
 * <li>+ / - used with <b>alt</b> apply the factor only on X coordinates</li>
 * <li>+ / - used with <b>shift</b> apply the factor only on Y coordinates</li>
 * <li><b>a or A</b> to compute the best factor that makes every node fit into
 * the screen</li>
 * </ul>
 */
public class FactorPlugin implements NodeRenderingPlugin {

  private GL gl;
  private GLU glu;
  private SceneGrip grip;

  /**
   * Factor to apply to the x coordinates.
   */
  public float factorX = 1f;

  /**
   * Factor to apply to the y coordinates.
   */
  public float factorY = 1f;

  private enum State {
    /**
     * Normal mode, nothing to do.
     */
    NORMAL,
    /**
     * Recording: records the lower, and higher values for each node position.
     * At the end of this state, compute the best factor value for which every
     * node fit into the screen, and go to {@link State#SET} state.
     */
    RECORDING,
    /**
     * Apply the factor to every node position.
     */
    SET,
  }

  /**
   * Current state for this plugin.
   */
  private State current = State.NORMAL;

  private float[] min = { 0f, 0f };
  private float[] max = { 0f, 0f };

  public FactorPlugin(GL gl, GLU glu, SceneGrip grip) {
    this.gl = gl;
    this.glu = glu;
    this.grip = grip;
  }

  /**
   * Apply a given factor to both X and Y coordinates.
   */
  public void applyFactor(float factor) {
    this.factorX = factor;
    this.factorY = factor;
    this.current = State.SET;
  }

  @Override
  public boolean apply(NodeRenderingProperty p) {
    switch (current) {
    case NORMAL:
      return true;
    case RECORDING:
      // during recording, compute min and max positions of every node.
      min[0] = Math.min(min[0], p.targetPositionX);
      min[1] = Math.min(min[1], p.targetPositionY);
      max[0] = Math.max(max[0], p.targetPositionX);
      max[1] = Math.max(max[1], p.targetPositionY);
      return true;
    case SET:
      // apply the factor.
      p.targetPositionX *= factorX;
      p.targetPositionY *= factorY;
      return true;
    default:
      break;
    }
    return true;
  }

  /**
   * Compute the best scaling value, that makes every node fit in the
   * current view, with the current zoom level. The same scaling factor
   * is applied to both X and Y coordinates, to keep drawing proportions.
   *
   * This computation needs 2 frames to be effective: one to compute the
   * extreme coordinates, and compute the optimal factor, and one to apply
   * this factor.
   */
  public void computeBestScalingFactor() {
    current = State.RECORDING;
  }

  @Override
  public boolean keyPressed(int keycode, char character, boolean ctrl,
      boolean alt, boolean shift) {
    if (character == '+') {
      if (alt && !shift) {
        factorX = 1.1f;
        factorY = 1.0f;
      } else if (!alt && shift) {
        factorX = 1.0f;
        factorY = 1.1f;
      } else {
        factorX = 1.1f;
        factorY = 1.1f;
      }
      current = State.SET;
      return true;
    }
    if (character == '-') {
      if (alt && !shift) {
        factorX = 0.9f;
        factorY = 1.0f;
      } else if (!alt && shift) {
        factorX = 1.0f;
        factorY = 0.9f;
      } else {
        factorX = 0.9f;
        factorY = 0.9f;
      }
      current = State.SET;
      return true;
    }
    if (character == 'a' || character == 'A') {
      current = State.RECORDING;
      return true;
    }
    return false;
  }

  @Override
  public void postFrame() {
    switch (current) {
    case NORMAL:
      break;
    case RECORDING:
      if (!computeFactor()) {
        current = State.NORMAL;
      } else {
        current = State.SET;
      }
      break;
    case SET:
      current = State.NORMAL;
      break;
    default:
      break;
    }
  }

  @Override
  public void preFrame(float elapsedTime) {
    switch (current) {
    case RECORDING:
      min[0] = Float.MAX_VALUE;
      min[1] = Float.MAX_VALUE;
      max[0] = Float.MIN_VALUE;
      max[1] = Float.MIN_VALUE;
      break;
    default:
      break;
    }
  }

  /**
   * Compute a factor that will let every node fit into the current screen
   * (panel). Do not use node size, only positions, to make the calculation.
   *
   * The factor is applied to X and Y coordinates, to keep proportions.
   *
   * @return true if a new factor was calculated, false otherwise.
   */
  private boolean computeFactor() {
    int[] viewport = new int[4];
    // A constant applied to the final factor, to get some margin for instance.
    final double applyToFactor = 0.9f;

    gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
    double[] topLeft =
        GLScene.getOGLPos(gl, glu, grip, viewport[0], viewport[1]);
    double[] bottomRight =
        GLScene.getOGLPos(gl, glu, grip, viewport[2], viewport[3]);

    // compute the optimal factor
    float dx = max[0] - min[0];
    float dy = max[1] - min[1];

    float factor = 0;
    if (Math.abs(dx) < 0.01f && Math.abs(dy) < 0.01f) {
      // All the nodes are at the same position ? or maybe there is only
      // one node... nothing to do then...
      return false;
    } else if (Math.abs(dx) < 0.01f) {
      // dx is 0 (all points have the same X coordinate, only use Y
      double factorY = (topLeft[1] - bottomRight[1]) / dy;
      factor = (float) (applyToFactor * factorY);
    } else if (Math.abs(dy) < 0.01f) {
      // dy is 0 (all points have the same Y coordinate, only use X
      double factorX = (bottomRight[0] - topLeft[0]) / dx;
      factor = (float) (applyToFactor * factorX);
    } else {
      // both dx and dy can be used. use the minumum factor, to let everything
      // fit in both x and y directions.
      double factorX = (bottomRight[0] - topLeft[0]) / dx;
      double factorY = (topLeft[1] - bottomRight[1]) / dy;
      factor = (float) (applyToFactor * Math.min(factorX, factorY));
    }
    // same factor is applied to X and Y, to keep proportions.
    this.factorY = factor;
    this.factorX = factor;
    return true;
  }

  @Override
  public void dryRun(NodeRenderingProperty p) {
    // nothing to do
  }

}
