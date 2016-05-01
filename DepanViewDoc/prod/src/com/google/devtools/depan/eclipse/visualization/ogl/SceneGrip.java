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
 * Convert user input events - mouse, keyboard, etc. - into changes of the
 * {@link GLScene}.
 *
 * @author Yohann Coppel
 */
public class SceneGrip extends MouseAdapter
    implements KeyListener, Listener,
    MouseListener, MouseMoveListener, MouseWheelListener {

  /** GL scene to control */
  private final GLScene scene;

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
  }

  @Override
  public void keyPressed(KeyEvent event) {
    switch (event.keyCode) {
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
        scene.uncaughtKey(event, keyCtrlState, keyAltState,
            keyShiftState);
    }
  }

  @Override
  public void mouseMove(MouseEvent e) {
    if (mouseButtonState[0]) { // button1 pressed
      switch (state) {
      case Moving:
        scene.truckCamera(mouseX - e.x);
        scene.pedestalCamera(mouseY - e.y);
        break;
      case MovingObject:
        scene.moveSelectedObjectsTo(mouseX - e.x, mouseY - e.y);
        break;
      case RectangleSelection:
        scene.activateSelectionRectangle(mouseDownX, mouseDownY, e.x, e.y);
        break;
      default:
      }
    } else if (mouseButtonState[2]) {
      switch (state) {
      case Moving:
        int dx = mouseX - e.x;
        int dy = e.y - mouseY;
        scene.rotateCamera(dy / 10f, 0.0f, dx / 10f);
        // prevent animation
        scene.cutCamera();
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
  private void zoomAt(int xPos, int yPos, float zoomValue) {
    double[] target = scene.getOGLPos(xPos, yPos);
    scene.zoomAt(target[0], target[1], target[2], zoomValue);
  }
}
