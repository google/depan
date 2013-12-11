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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * A class handling basic mouse events, keyboard events, and camera position of
 * a {@link GLScene}.
 *
 * @author Yohann Coppel
 */
public class SceneGrip extends MouseAdapter
    implements KeyListener, Listener,
    MouseListener, MouseMoveListener, MouseWheelListener {

  /** eye position */
  private float xrot;
  private float zrot;
  private float zoff;
  public float xoff;
  public float yoff;

  /** Target for eye position, for smooth moves */
  private float targetXrot;
  private float targetZrot;
  private float targetZoff;
  private float targetXoff;
  private float targetYoff;

  /** speed for moves */
  private static final int SPEED = 5;

  /** Camera z position, if zoom value is set to "100%". */
  private static final float HUNDRED_PERCENT_ZOOM = 300f;

  /** GL scene to control */
  private GLScene scene;

  /** Modifier keys states. */
  private boolean keyCtrlState = false;
  private boolean keyShiftState = false;
  private boolean keyAltState = false;

  /** Mouse button states. */
  private boolean[] mouseButtonState = {false, false, false, false, false};

  /** mouse position. */
  int mouseX = -1;
  int mouseY = -1;

  /** mouse position at last mouseDown event */
  int mouseDownX = -1;
  int mouseDownY = -1;

  private enum State {
    None, Moving, MovingObject, RectangleSelection
  }

  private State state = State.None;

  public SceneGrip(GLScene scene) {
    this.scene = scene;
    // Start with the drawing properly zoomed.
    this.zoff = HUNDRED_PERCENT_ZOOM;
    targetHome();
  }

  protected void targetHome() {
    this.targetXrot = 0.0f;
    this.targetXoff = 0.0f;
    this.targetZrot = 0.0f;
    this.targetYoff = 0.0f;

    this.targetZoff = HUNDRED_PERCENT_ZOOM;
  }

  /**
   * Set the zoom to the given value. 1.0 is 100%.
   * @param scale
   */
  public void setZoom(float scale) {
    this.targetZoff = HUNDRED_PERCENT_ZOOM / scale;
  }

  @Override
  public void keyPressed(KeyEvent e) {
    switch (e.keyCode) {
      case SWT.ARROW_UP:
        if ((e.stateMask & SWT.CTRL) != 0) {
          this.targetXrot -= 0.5f;
        } else {
          this.targetYoff -= 5f * Math.cos(Math.toRadians(zrot));
          this.targetXoff -= 5f * Math.sin(Math.toRadians(zrot));
        }
        break;
      case SWT.ARROW_DOWN:
        if ((e.stateMask & SWT.CTRL) != 0) {
          this.targetXrot += 0.5f;
        } else {
          this.targetYoff += 5f * Math.cos(Math.toRadians(zrot));
          this.targetXoff += 5f * Math.sin(Math.toRadians(zrot));
        }
        break;
      case SWT.ARROW_LEFT:
        if ((e.stateMask & SWT.CTRL) != 0) {
          this.targetZrot -= 0.5f;
        } else {
          this.targetXoff -= 5f * Math.sin(Math.toRadians(zrot-90));
          this.targetYoff -= 5f * Math.cos(Math.toRadians(zrot-90));
        }
        break;
      case SWT.ARROW_RIGHT:
        if ((e.stateMask & SWT.CTRL) != 0) {
          this.targetZrot += 0.5f;
        } else {
          this.targetXoff += 5f * Math.sin(Math.toRadians(zrot-90));
          this.targetYoff += 5f * Math.cos(Math.toRadians(zrot-90));
        }
        break;
      case SWT.PAGE_UP:
        this.targetZoff += 5f;
        break;
      case SWT.PAGE_DOWN:
        this.targetZoff -= 5f;
        break;
      case SWT.HOME:
        targetHome();
        break;
      case SWT.SHIFT:
        this.keyShiftState = true;
        break;
      case SWT.CTRL:
        this.keyCtrlState = true;
        break;
      case SWT.ALT:
        this.keyAltState = true;
        break;
      default:
        // uncaught key, transmit it to lower level for handling.
        scene.uncaughtKey(e.keyCode, e.character, keyCtrlState, keyAltState,
            keyShiftState);
    }
  }

  /**
   * Perform a step: move the camera if necessary
   */
  private void step() {
    if (zoff < targetZoff) {
      zoff += (targetZoff - zoff) / SPEED;
    } else if (zoff > targetZoff) {
      zoff -= (zoff - targetZoff) / SPEED;
    }
    if (yoff < targetYoff) {
      yoff += (targetYoff - yoff) / SPEED;
    } else if (yoff > targetYoff) {
      yoff -= (yoff - targetYoff) / SPEED;
    }
    if (xoff < targetXoff) {
      xoff += (targetXoff - xoff) / SPEED;
    } else if (xoff > targetXoff) {
      xoff -= (xoff - targetXoff) / SPEED;
    }
    if (zrot < targetZrot) {
      zrot += (targetZrot - zrot) / SPEED;
    } else if (zrot > targetZrot) {
      zrot -= (zrot - targetZrot) / SPEED;
    }
    if (xrot < targetXrot) {
      xrot += (targetXrot - xrot) / SPEED;
    } else if (xrot > targetXrot) {
      xrot -= (xrot - targetXrot) / SPEED;
    }
  }

  /**
   * Adjust the position of the camera.
   */
  public void adjust() {
    step();

    if (!GLScene.hyperbolic) {
      scene.gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f);
      scene.gl.glRotatef(zrot, 0.0f, 0.0f, 1.0f);
      scene.gl.glTranslatef(this.xoff, this.yoff, -this.zoff);
    } else {
      scene.glu.gluLookAt(0, 0, zoff, 0, 0, zoff - 1, 0, 1, 0);
    }
    //gl.glRotatef(this.xrot, 1.0f, 0.0f, 0.0f);
    //gl.glRotatef(this.yrot, 0.0f, 1.0f, 0.0f);
  }

  /**
   * @return the camera (eye) position.
   */
  public float[] getCameraPosition() {
    return new float[] {xoff, yoff, zoff};
  }

  @Override
  public void mouseMove(MouseEvent e) {
    if (mouseButtonState[0]) { // button1 pressed
      switch (state) {
      case Moving:
        // translate
        int dx = e.x - mouseX;
        int dy = e.y - mouseY;

        xoff -= dx * Math.sin(Math.toRadians(zrot-90));
        yoff -= dx * Math.cos(Math.toRadians(zrot-90));

        yoff -= dy * Math.cos(Math.toRadians(zrot));
        xoff -= dy * Math.sin(Math.toRadians(zrot));

        targetXoff = xoff;
        targetYoff = yoff;
        break;
      case MovingObject:
        scene.moveSelectedObjectsTo(mouseX - e.x, mouseY - e.y);
        break;
      case RectangleSelection:
        // draw selection rectangle
        scene.activateSelectionRectangle(mouseDownX, mouseDownY, e.x, e.y);
        break;
      default:
      }
    } else if (mouseButtonState[2]) {
      switch (state) {
      case Moving:
        int dx = mouseX - e.x;
        int dy = e.y - mouseY;
        xrot += dy / 10f;
        targetXrot = xrot;
        zrot += -dx / 10f;
        targetZrot = zrot;
        break;
      default:
        // Explicitly ignore other state
        break;
      }
    }
    mouseX = e.x;
    mouseY = e.y;
  }

  @Override
  public void mouseDown(MouseEvent e) {
    if (!(e.button - 1 < mouseButtonState.length && e.button > 0)) {
      return;
    }

    mouseButtonState[e.button - 1] = true;
    mouseDownX = e.x;
    mouseDownY = e.y;

    int[] hits = scene.pickObjectsAt(mouseDownX, mouseDownY);

    // The user clicked on an object without control or shift
    // Entry move mode, and make the picked node the select node if it
    // is not part of the current selection.
    if (hits.length > 0 && !keyCtrlState && !keyShiftState) {
      if (!scene.isSelected(hits[0])) {
        // ..not already selected: make it the new selection
        scene.setSelection(new int[]{hits[0]});
      }
      state = State.MovingObject;
    }

    // Start a rectangle selection
    else if (hits.length > 0 && keyCtrlState) {
      state = State.RectangleSelection;
    }

    // Clicked with shift: start moving camera
    else if (keyShiftState) {
      state = State.Moving;
    } else {
      // clicked on the background.
      state = State.RectangleSelection;
    }
  }

  @Override
  public void mouseUp(MouseEvent e) {
    if (!(e.button - 1 < mouseButtonState.length
        && e.button > 0 && mouseButtonState[e.button - 1])) {
      return;
    }
    mouseButtonState[e.button - 1] = false;

    if (e.button == 1) {
      if (mouseDownX == e.x && mouseDownY == e.y
          && state == State.MovingObject) {
        // instead of moving the node, the mouse stayed at the same place.
        // we replace the selection.
        scene.setSelection(scene.pickObjectsAt(e.x, e.y));
      }
      else if (mouseDownX == e.x && mouseDownY == e.y
          && state == State.RectangleSelection) {
        // a rectangle when the mouse hasn't moved... select nothing
        if (!keyCtrlState) {
          int[] newHits = new int[0];
          scene.setSelection(newHits);
        }
        else if (keyAltState) {
          scene.reduceSelection(scene.pickObjectsAt(e.x, e.y));
        }
        else {
          scene.extendSelection(scene.pickObjectsAt(e.x, e.y));
        }
      }
      else if (state == State.RectangleSelection) {
        int[] picked = scene.pickRectangle(mouseDownX, mouseDownY, e.x, e.y);
        if (!keyCtrlState) {
          scene.setSelection(picked);
        }
        else if (keyAltState) {
          scene.reduceSelection(picked);
        }
        else
          scene.extendSelection(picked);
      }
    }
    state = State.None;
  }

  @Override
  public void handleEvent(Event event) {
  }

  @Override
  public void keyReleased(KeyEvent e) {
    switch (e.keyCode) {
      case SWT.SHIFT:
        this.keyShiftState = false;
        break;
      case SWT.CTRL:
        this.keyCtrlState = false;
        break;
      case SWT.ALT:
        this.keyAltState = false;
        break;
      default:
        break;
    }
  }

  @Override
  public void mouseScrolled(MouseEvent event) {
    int amount = event.count;
    if (amount > 0) {
      zoomAt(event.x, event.y, 10f);
    } else if (amount < 0) {
      zoomAt(event.x, event.y, -10f);
    }
    // do nothing if amount == 0...
  }

  /**
   * Zoom at the given cursor position.
   *
   * @param xPos X cursor coordinate
   * @param yPos Y cursor coordinate
   * @param zoomValue value added to the current zoom level.
   */
  public void zoomAt(int xPos, int yPos, float zoomValue) {
    double[] target = scene.getOGLPos(xPos, yPos);
    zoomAt(target[0], target[1], target[2], zoomValue);
  }

  /**
   * Move the eye in straight line toward the given word position, reducing
   * (or augmenting) the distance between eye and point of zoomValue.
   *
   * @param x
   * @param y
   * @param z
   * @param zoomValue
   */
  public void zoomAt(double x, double y, double z, double zoomValue) {
    double[] diff = {targetXoff - x, targetYoff - y, z - targetZoff};
    double length = Math.sqrt(diff[0] * diff[0] + diff[1] * diff[1]
        + diff[2] * diff[2]);
    if (length == 0) {
      length = 1;
    }
    double[] normalized = {diff[0] / length, diff[1] / length,
        diff[2] / length};
    // calculate zoom such that the difference on the Z axis is equal to
    // zoomValue
    double percent = zoomValue / normalized[2];
    this.targetXoff += normalized[0] * percent;
    this.targetYoff += normalized[1] * percent;
    this.targetZoff += zoomValue;
    this.targetZoff = Math.max(targetZoff, 1.1f);
  }

  /**
   * Center the camera to the given point (in openGL coordinates)
   *
   * @param camX x coordinate
   * @param camY y coordinate
   */
  public void setCameraCenterTo(float camX, float camY) {
    this.targetXoff = camX;
    this.targetYoff = camY;
  }
}
