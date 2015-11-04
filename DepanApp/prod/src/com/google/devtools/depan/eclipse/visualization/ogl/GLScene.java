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

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.glu.GLU;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import java.util.logging.Logger;

/**
 * An abstract GLScene. Handling OpenGL specific details.
 *
 * @author Yohann Coppel
 */
public abstract class GLScene {
  public static final float FACTOR = 1f;

  private static final int[] EMPTY_HIT_LIST = new int[0];

  private static final Logger logger =
      Logger.getLogger(GLScene.class.getName());

  // For OSX, get profile early
  private static GLProfile DEFAULT_PROFILE = GLProfile.getDefault();

  private GLCanvas canvas;
  private final GLContext context;
  public final GL2 gl;
  public final GLU glu;
  private final SceneGrip grip;

  /** eye position */
  private float xoff;
  private float yoff;
  private float zoff;

  private float targetXoff;
  private float targetYoff;
  private float targetZoff;

  /** eye direction */
  private float xrot;
  private float yrot;
  private float zrot;

  private float targetXrot;
  private float targetYrot;
  private float targetZrot;

  private boolean isSceneChanged = false;

  /** speed for moves */
  public static final int SPEED = 5;

  /** Camera z position, if zoom value is set to "100%". */
  public static final float HUNDRED_PERCENT_ZOOM = 2000.0f;

  public static final float[] DEFAULT_CAMERA_POSITION = {
    0.0f, 0.0f, HUNDRED_PERCENT_ZOOM
  };

  // Full laptop screen vertical space:
  // 7" vertical from 22" is ~ 20 degrees.
  public static final float FOV = 20.0f;

  public static final float Z_NEAR = 0.4f;

  public static final float Z_FAR = 30000.0f;

  public static final float PIXEL_QUANTA = 0.1f;

  public static final float ZOOM_QUANTA = 1.0f;

  public static final float ZOOM_MAX = 1.1f;

  public static final float ROTATE_QUANTA = 0.001f;
  
  public static boolean hyperbolic = false;

  /** Latest received mouse positions. */
  private int mouseX, mouseY;

  /** Latest mouse coordinate when rectangle selection started */
  private boolean drawSelectRectangle = false;
  private int startSelectX, startSelectY;
  private int selectionWidth, selectionHeight;

  private Color foregroundColor = Color.WHITE;

  /**
   * Turn off SWT-display wheel listening for the canvas.
   * OGL rendering uses a separate mouse listening mechanism, and SWT
   * generates conflicting scroll-bar move events.
   */
  private final Listener wheelListener = new Listener() {
    @Override
    public void handleEvent(Event event) {
      if (event.widget != canvas) {
        return;
      }
      event.doit = false;
    }
  };

  public GLScene(Composite parent) {
    GLData data = new GLData();
    data.doubleBuffer = true;
    data.depthSize = 16;
    canvas = new GLCanvas(parent, SWT.NO_BACKGROUND, data);
    canvas.setCurrent();

    context = createGLContext();

    GL rawGL = context.getGL();
    if (rawGL.isGL2()) {
      gl = (GL2) rawGL;
    } else {
      gl = null;
    }
    glu = new GLU();

    canvas.addControlListener(new ControlAdapter() {
      @Override
      public void controlResized(ControlEvent e) {
        resizeScene();
      }
    });
    canvas.addDisposeListener(new DisposeListener() {
      @Override
      public void widgetDisposed(DisposeEvent e) {
        dispose();
      }
    });

    grip  = new SceneGrip(this);

    canvas.addMouseListener(grip);
    canvas.addMouseMoveListener(grip);
    canvas.addKeyListener(grip);
    canvas.addMouseWheelListener(grip);

    // Disable display support for mouse-wheel.
    Display display = canvas.getDisplay();
    display.addFilter(SWT.MouseVerticalWheel, wheelListener);
    display.addFilter(SWT.MouseHorizontalWheel, wheelListener);

    // Start with the drawing properly zoomed.
    homeCamera();
    cutCamera();

    this.init();
  }

  private GLContext createGLContext() {

    try {
      logger.info("Create context...");
      GLDrawableFactory drawableFactory = GLDrawableFactory.getFactory(DEFAULT_PROFILE);
      GLContext result = drawableFactory.createExternalGLContext();
      logger.info("    Done.");

      return result;
    } catch (Throwable errGl) {
      errGl.printStackTrace();
      throw new RuntimeException(errGl);
    }
  }

  protected void resizeScene() {
    canvas.setCurrent();
    context.makeCurrent();

    Rectangle rect = canvas.getClientArea();
    gl.glViewport(0, 0, rect.width, rect.height);

    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();
    updateViewpoint(rect);

    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glLoadIdentity();

    context.release();
  }

  private void updateViewpoint(Rectangle rect) {
    int width = rect.width;
    int height = Math.max(rect.height, 1);
    float aspect = (float) width / (float) height;

    glu.gluPerspective(FOV, aspect, Z_NEAR, Z_FAR);
  }

  protected void dispose() {
    if (canvas != null) {

      Display display = canvas.getDisplay();
      display.removeFilter(SWT.MouseVerticalWheel, wheelListener);
      display.removeFilter(SWT.MouseHorizontalWheel, wheelListener);

      canvas.dispose();
      canvas = null;
    }
  }

  protected void init() {
    initGLContext();
    initGL();
  }

  protected void initGLContext() {
    canvas.setCurrent();
  }

  protected void initGL() {
    context.makeCurrent();
    /*
    //TODO: add options for this parameters: it looks way nicer,
    //but is way slower ;)
    gl.glEnable(GL.GL_LINE_SMOOTH);
    gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
    gl.glEnable(GL.GL_POLYGON_SMOOTH);
    gl.glHint(GL.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);
    */
    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    gl.glClearDepth(1.0f);
    gl.glEnable(GL2.GL_DEPTH_TEST);
    gl.glEnable(GL2.GL_BLEND);
    gl.glEnable(GL2.GL_TEXTURE_2D);
    gl.glDepthFunc(GL2.GL_LEQUAL);
    gl.glShadeModel(GL2.GL_SMOOTH);
    gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
    gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
    context.release();
  }

  public void prepareResources() {
    context.makeCurrent();
    allocateResources();
    context.release();
  }

  /**
   * Hook method for derived classes to allocate resources within the
   * GLContenxt
   */
  protected void allocateResources() {
  }

  /**
   * @param elapsedTime time since previous frame.
   */
  public void render(float elapsedTime) {
    if (!canvas.isCurrent()) {
      canvas.setCurrent();
    }
    context.makeCurrent();
    gl.glPushMatrix();
    drawScene(elapsedTime);
    gl.glPopMatrix();
    if (drawSelectRectangle) {
      drawRectangle();
    }
    canvas.swapBuffers();
    context.release();
  }

  private int[] renderWithPicking() {
    if (!canvas.isCurrent()) {
      canvas.setCurrent();
    }
    context.makeCurrent();

    int[] viewPort = new int[4];
    gl.glGetIntegerv(GL2.GL_VIEWPORT, viewPort, 0);

    IntBuffer selectBuffer = getSelectBuffer();
    gl.glSelectBuffer(selectBuffer.capacity(), selectBuffer);

    // setup the view
    gl.glRenderMode(GL2.GL_SELECT);
    gl.glInitNames();

    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glPushMatrix();
    gl.glLoadIdentity();

    glu.gluPickMatrix(mouseX, viewPort[3] - mouseY, selectionWidth,
        selectionHeight, viewPort, 0);
    updateViewpoint(canvas.getClientArea());

    drawScene(0f);

    gl.glPopMatrix();
    gl.glFlush();
    int hits = gl.glRenderMode(GL2.GL_RENDER);
    gl.glMatrixMode(GL2.GL_MODELVIEW);

    context.release();
    return processHits(hits, selectBuffer);
  }

  /////////////////////////////////////
  // Camera (and drawing) management

  /**
   * Provide the coordinates for the camera.  These are the OGL coordinates
   * at which the camera is placed.  In rendering terms, that's the negative
   * of the (x,y) translation transform.
   * 
   * @return the camera (eye) position.
   */
  public float[] getCameraPosition() {
    return new float[] {-xoff, -yoff, zoff};
  }

  public void homeCamera() {
    targetXrot = 0.0f;
    targetYoff = 0.0f;
    targetZrot = 0.0f;

    moveToCamera(0.0f, 0.0f);
    zoomToCamera(HUNDRED_PERCENT_ZOOM);
  }

  /**
   * Center the camera over the given point (in openGL coordinates)
   *
   * @param camX x coordinate
   * @param camY y coordinate
   */
  public void moveToCamera(float camX, float camY) {
    targetXoff = -camX;
    targetYoff = -camY;
    isSceneChanged = true;
  }

  /**
   * Avoid moving the camera too far or too close. so the scene
   * stays within the frustum for the perspective transform.
   * 
   * No direct assignments to targetZoff.
   */
  public void zoomToCamera(float zOffset) {
    if (zOffset > (GLScene.Z_FAR - 1.0)) {
      zOffset = GLScene.Z_FAR - 1.0f;
      logger.info("clamped zoom at " + zOffset);
    }
    if (zOffset < (ZOOM_MAX)) {
      zOffset = ZOOM_MAX;
      logger.info("clamped zoom at " + zOffset);
    }

    targetZoff = zOffset;
    isSceneChanged = true;
  }

  public boolean isNowStable() {
    // With no changes, 
    if (!isSceneChanged)
      return false;

    if (xoff != targetXoff)
      return false;
    if (yoff != targetYoff)
      return false;
    if (zoff != targetZoff)
      return false;

    clearChanges();
    return true;
  }

  public void clearChanges() {
    isSceneChanged = false;
  }

  /**
   * Set the zoom to the given value. 1.0 is 100%.
   * @param scale
   */
  public void setZoom(float scale) {
    zoomToCamera(HUNDRED_PERCENT_ZOOM / scale);
  }

  /**
   * Set the zoom to the given value. 1.0 is 100%.
   * @param scale
   */
  public void zoomCamera(float size) {
    zoomToCamera(targetZoff += size);
  }

  /**
   * Move the eye in straight line toward the given world position, reducing
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

    moveToCamera(
        - (float) (targetXoff + normalized[0] * percent),
        - (float) (targetYoff + normalized[1] * percent));
    zoomToCamera((float) (targetZoff + normalized[2] * percent));
  }

  /**
   * Change direction of camera (pan -u/d, scan - l/r, or turn - c/cc).
   * 
   * TODO: Make sure these match their behaviors
   * @param xRot - amount to tilt up or down
   * @param yRot - amount to pan left or right
   * @param zRot - amount to turn/twist clockwise or counterclockwise.
   */
  public void rotateCamera(float xRot, float yRot, float zRot) {
    targetXrot += xRot;
    targetYrot += yRot;
    targetZrot += zRot;
  }

  /**
   * Change camera position vertically (up or down)
   */
  public void pedestalCamera(float size) {
    moveToCamera(
        - (float) (targetXoff + (size * Math.sin(Math.toRadians(zrot)))),
        - (float) (targetYoff + (size * Math.cos(Math.toRadians(zrot)))));
  }

  /**
   * Change camera position horizontally (left or right).
   */
  public void truckCamera(float size) {
    // TODO:  Isn't the -90 just swapping sin/cos?
    moveToCamera(
        - (float) (targetXoff + (size * Math.sin(Math.toRadians(zrot - 90)))),
        - (float) (targetYoff + (size * Math.cos(Math.toRadians(zrot - 90)))));
  }

  /**
   * Cut the camera immediately to the target location.  No animation.
   */
  public void cutCamera() {
    xrot = targetXrot;
    yoff = targetYoff;
    zrot = targetZrot;

    xoff = targetXoff;
    yoff = targetYoff;
    zoff = targetZoff;
  }

  /**
   * Perform a step: move the camera if necessary
   */
  private void step() {
    if (isPixelEpsilon(xoff, targetXoff)) {
      xoff = targetXoff;
    } else {
      xoff += (targetXoff - xoff) / SPEED;
    }
    if (isPixelEpsilon(yoff, targetYoff)) {
      yoff = targetYoff;
    } else {
      yoff += (targetYoff - yoff) / SPEED;
    }

    if (isZoomEpsilon(zoff, targetZoff)) {
      zoff = targetZoff;
    } else {
      zoff += (targetZoff - zoff) / SPEED;
    }

    if (isRotateEpsilon(xrot, targetXrot)) {
      xrot = targetXrot;
    } else {
      xrot += (targetXrot - xrot) / SPEED;
    }
    if (isRotateEpsilon(yrot, targetYrot)) {
      yrot = targetYrot;
    } else {
      yrot += (targetYrot - yrot) / SPEED;
    }
    if (isRotateEpsilon(zrot, targetZrot)) {
      zrot = targetZrot;
    } else {
      zrot += (targetZrot - zrot) / SPEED;
    }
  }

  private boolean isPixelEpsilon(float left, float right) {
    return isEpsilon(left, right, PIXEL_QUANTA);
  }

  private boolean isZoomEpsilon(float left, float right) {
    return isEpsilon(left, right, ZOOM_QUANTA);
  }

  private boolean isRotateEpsilon(float left, float right) {
    return isEpsilon(left, right, ROTATE_QUANTA);
  }
  
  private boolean isEpsilon(float left, float right, float epsilon) {
    return Math.abs(left - right) < epsilon;
  }

  /**
   * Establish the position and direction of the camera.
   */
  private void prepareCamera() {
    step();

    if (!GLScene.hyperbolic) {
      gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f);
      gl.glRotatef(zrot, 0.0f, 0.0f, 1.0f);
      gl.glTranslatef(xoff, yoff, -zoff);
    } else {
      glu.gluLookAt(0, 0, zoff, 0, 0, zoff - 1, 0, 1, 0);
    }
    //gl.glRotatef(this.xrot, 1.0f, 0.0f, 0.0f);
    //gl.glRotatef(this.yrot, 0.0f, 1.0f, 0.0f);
  }

  /////////////////////////////////////
  // Draw the scene

  /**
   * Clear the scene and adjust the camera position.
   * 
   * Hook method for derived types to render the image.  Extending types
   * should call the super-method first to establish a clear image and
   * the proper grip.
   * 
   * @param elapsedTime time since previous frame.
   */
  protected void drawScene(float elapsedTime) {
    gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
    prepareCamera();
  }

  public void printMousePos(int x, int y) {
    double[] m = getOGLPos(x, y);
    logger.info("Mouse " + m[0] + " : " + m[1] + " - " + m[2]);
  }

  /**
   * Convert screen coordinates (x, y) to OGL coordinates.
   * 
   * In screen coordinates, y increases from top to bottom.
   * Screen coordinates are often pixels from a boundary.
   * In OGL coordinates, y increases from bottom to top.
   * 
   * @param x screen x coordinate
   * @param y screen x coordinate
   * @return corresponding OGL coordinate for current view
   */
  public double[] getOGLPos(int x, int y) {
    int[] viewport = new int[4];
    double[] modelview = new double[16];
    double[] projection = new double[16];
    double[] wcoord0 = new double[3];
    double[] wcoord1 = new double[3];

    gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelview, 0);
    gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projection, 0);
    gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);

    double winX = x;
    double winY = (double) viewport[3] - (double) y;

    // UnProject twice, once with z = 0 (zNear), and once with
    // z = 1 (zFar).
    glu.gluUnProject(winX, winY, 0,
        modelview, 0, projection, 0, viewport, 0, wcoord0, 0);
    glu.gluUnProject(winX, winY, 1.0,
        modelview, 0, projection, 0, viewport, 0, wcoord1, 0);

    // compute the vector between the two results.
    double[] vector = {wcoord1[0] - wcoord0[0], wcoord1[1] - wcoord0[1],
        wcoord1[2] - wcoord0[2]};
    // normalize it
    double[] norm = {vector[0] / vector[2], vector[1] / vector[2], 1.0f};
    // then we have 1 point (the camera), and one vector.
    // we can therefore compute the position of the point where
    // z = 0, for the line passing by the camera position, and
    // directed by the vector.
    double[] res = {-xoff + (-zoff) * norm[0],
        -yoff + (-zoff) * norm[1], 0f};
    return res;
  }

  public int[] getViewport() {
    int[] viewPort = new int[4];
    gl.glGetIntegerv(GL2.GL_VIEWPORT, viewPort, 0);
    return viewPort;
  }

  public Rectangle2D getOGLViewport() {
    int[] osViewport = getViewport();
    double[] bottomLeft = getOGLPos(osViewport[0], osViewport[3] - osViewport[1]);
    double[] topRight = getOGLPos(osViewport[0] + osViewport[2], 0);
    return new Rectangle2D.Double(
        bottomLeft[0], bottomLeft[1] /* left, bottom */ ,
        topRight[0] - bottomLeft[0] /* width */,
        topRight[1] - bottomLeft[1] /* height */ );
  }

  /**
   * Provide an IntBuffer that can be used to select from the current scene.
   * It should have enough room to allow every OGL entity in the scene to
   * be selected.
   * <p>
   * Ownership of the buffer remains with the provider (e.g. derived class),
   * and access is thread-safe.  But it should be a good place to dump the OGL
   * selection data.
   *
   * @return IntBuffer to collect OGL selection data
   */
  protected abstract IntBuffer getSelectBuffer();

  protected abstract boolean isSelected(int id);

  /**
   * Activate selection rendering for the exactly the listed nodes.
   */
  protected abstract void setSelection(int[] ids);

  /**
   * Turn on selection rendering for the listed nodes.
   */
  protected abstract void extendSelection(int[] ids);

  /**
   * Turn off selection rendering for the listed nodes.
   */
  protected abstract void reduceSelection(int[] ids);

  /**
   * Move every selected objects relatively. Movement is given relatively
   * to the openGL coordinates.
   *
   * @param dx movement on x axis.
   * @param dy movement on y axis.
   */
  protected abstract void moveSelectionDelta(double dx, double dy);

  /**
   * Move every selected objects relatively. Movement is given relatively
   * to window coordinates.
   *
   * @param x movement on x axis.
   * @param y movement on y axis.
   */
  public void moveSelectedObjectsTo(int x, int y) {
    double[] worldPosOrigin = getOGLPos(0, 0);
    double[] worldPos = getOGLPos(x, y);
    moveSelectionDelta(
        -worldPos[0] + worldPosOrigin[0],
        -worldPos[1] + worldPosOrigin[1]);
  }

  /**
   * A key stroke was received by the OpenGL canvas but it wasn't used by the
   * previous layers.  Give someone else a chance to use it.
   * 
   * This is intended to be overridden, and {@code super.uncaughtKey()}
   * should be the final statement of such methods.  Normally, this is the
   * "last gasp" key handler.
   */
  public void uncaughtKey(KeyEvent event,
      boolean keyCtrlState, boolean keyAltState, boolean keyShiftState) {

    switch (event.keyCode) {
      case SWT.ARROW_UP:
        if ((event.stateMask & SWT.CTRL) != 0) {
          rotateCamera(-0.5f, 0.0f, 0.0f);
        } else {
          pedestalCamera(-5.0f);
        }
        break;
      case SWT.ARROW_DOWN:
        if ((event.stateMask & SWT.CTRL) != 0) {
          rotateCamera(0.5f, 0.0f, 0.0f);
        } else {
          pedestalCamera(5.0f);
        }
        break;

      case SWT.ARROW_LEFT:
        if ((event.stateMask & SWT.CTRL) != 0) {
          rotateCamera(0.0f, 0.0f, -0.5f);
        } else {
          truckCamera(-5.0f);
        }
        break;
      case SWT.ARROW_RIGHT:
        if ((event.stateMask & SWT.CTRL) != 0) {
          rotateCamera(0.0f, 0.0f, 0.5f);
        } else {
          truckCamera(5.0f);
        }
        break;

      case SWT.PAGE_UP:
        zoomCamera(5.0f);
        break;
      case SWT.PAGE_DOWN:
        zoomCamera(-5.0f);
        break;
      case SWT.HOME:
        homeCamera();
        break;

      default:
        logUncaughtKey(event.keyCode, event.character, keyCtrlState, keyAltState, keyShiftState);
    }

  }

  private void logUncaughtKey(int keyCode, char character,
      boolean keyCtrlState, boolean keyAltState, boolean keyShiftState) {
    StringBuffer buf = new StringBuffer();
    buf.append("Lost key press: ");
    buf.append(keyCode);

    buf.append(" (");
    buf.append(character);

    if (keyCtrlState) {
      buf.append(" CTRL");
    }
    if (keyAltState) {
      buf.append(" ALT");
    }
    if (keyShiftState) {
      buf.append(" SHFT");
    }

    buf.append(")");
    logger.info(buf.toString());
  }

  /**
   * Retrieve the openGL ids that were hited by a previous picking operation.
   *
   * @param hits
   * @param buffer
   * @return
   */
  private int[] processHits(int hits, IntBuffer buffer) {
    if (hits == 0) {
      return EMPTY_HIT_LIST;
    }
    if (hits < 0) {
      logger.warning("Too many hits!!" +
          " IntBuffer capacity = " + buffer.capacity());
      return EMPTY_HIT_LIST;
    }
    int[] hitsResults = new int[hits];
    int offset = 0;
    int names;
    for (int i = 0; i < hits; i++) {
      names = buffer.get(offset); offset++;
      offset++; // z1 (first z)
      offset++; // z2 (last z)

      for (int j = 0; j < names; j++) {
        if (j == (names - 1)) {
          hitsResults[i] = buffer.get(offset);
        }
        offset++;
      }
    }
    logger.fine("hits = " + hits + "; offset = " + offset);
    return hitsResults;
  }

  /**
   * Pick the object at the given mouse position (in window-relative
   * coordinates)
   *
   * @param mouseX
   * @param mouseY
   * @return ids of picked objects.
   */
  public int[] pickObjectsAt(int mouseX, int mouseY) {
    this.mouseX = mouseX;
    this.mouseY = mouseY;
    this.selectionWidth = 1;
    this.selectionHeight = 1;
    return renderWithPicking();
  }

  /**
   * The same as pickObjectAt, but with a rectangle.
   *
   * @param startX
   * @param startY
   * @param toX
   * @param toY
   * @return ids of picked objects.
   */
  public int[] pickRectangle(int startX, int startY, int toX, int toY) {
    if (startX < toX) {
      this.selectionWidth = toX - startX;
      this.mouseX = startX + selectionWidth / 2;
    } else {
      this.selectionWidth  = startX - toX;
      this.mouseX = toX + selectionWidth / 2;
    }
    if (startY < toY) {
      this.selectionHeight = toY - startY;
      this.mouseY = startY + selectionHeight / 2;
    } else {
      this.selectionHeight  = startY - toY;
      this.mouseY = toY + selectionHeight / 2;
    }
    drawSelectRectangle = false;
    return renderWithPicking();
  }

  /**
   * Activate and define an overlay rectangle that shows the selection area.
   *
   * @param fromX
   * @param fromY
   * @param mouseX
   * @param mouseY
   */
  public void activateSelectionRectangle(
      int fromX, int fromY, int mouseX, int mouseY) {
    drawSelectRectangle = true;
    this.mouseX = mouseX;
    this.mouseY = mouseY;
    this.startSelectX = fromX;
    this.startSelectY = fromY;
  }

  /**
   * Start 2D rendering mode.
   */
  protected void go2D() {
    /* Disable depth testing */
    gl.glDisable(GL2.GL_DEPTH_TEST);
    /* Select The Projection Matrix */
    gl.glMatrixMode(GL2.GL_PROJECTION);
    /* Store The Projection Matrix */
    gl.glPushMatrix();
    /* Reset The Projection Matrix */
    gl.glLoadIdentity();
    /* Set Up An Ortho Screen */
    glu.gluOrtho2D(0.0, 1.0d, 0, 1.0d);
    /* Select The Modelview Matrix */
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    /* Stor the Modelview Matrix */
    gl.glPushMatrix();
    /* Reset The Modelview Matrix */
    gl.glLoadIdentity();
  }

  /**
   * Return to 3D rendering mode.
   */
  protected void end2D() {
    /* Select The Projection Matrix */
    gl.glMatrixMode(GL2.GL_PROJECTION);
    /* Restore The Old Projection Matrix */
    gl.glPopMatrix();
    /* Select the Modelview Matrix */
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    /* Restore the Old Projection Matrix */
    gl.glPopMatrix();
    /* Re-enable Depth Testing */
    gl.glEnable(GL2.GL_DEPTH_TEST);
  }

  /**
   * Draw the 2D selection rectangle on top of everything.
   */
  protected void drawRectangle() {
    int[] viewPort = new int[4];
    gl.glGetIntegerv(GL2.GL_VIEWPORT, viewPort, 0);

    // start point
    float sx = (float) startSelectX / (float) viewPort[2];
    float sy = (float) (viewPort[3] - startSelectY) / (float) viewPort[3];
    // end point
    float ex = (float) mouseX / (float) viewPort[2];
    float ey = (float) (viewPort[3] - mouseY) / (float) viewPort[3];

    go2D();

    gl.glColor4f(0.0f, 0.0f, 0.8f, 0.3f);
    gl.glBegin(GL2.GL_QUADS);
    gl.glVertex2f(sx, sy);
    gl.glVertex2f(ex, sy);
    gl.glVertex2f(ex, ey);
    gl.glVertex2f(sx, ey);
    gl.glEnd();

    gl.glColor4f(0.0f, 0.0f, 0.6f, 1.0f);
    gl.glLineWidth(1.0f);
    gl.glBegin(GL2.GL_LINE_STRIP);
    gl.glVertex2f(sx, sy);
    gl.glVertex2f(ex, sy);
    gl.glVertex2f(ex, ey);
    gl.glVertex2f(sx, ey);
    gl.glVertex2f(sx, sy);
    gl.glEnd();

    end2D();
  }

  /**
   * Setup scene colors.
   *
   * @param back
   * @param front
   */
  public void setColors(Color back, Color front) {
    gl.glClearColor(back.getRed() / 255f,
        back.getGreen() / 255f,
        back.getBlue() / 255f,
        back.getAlpha() / 255f);
    this.foregroundColor = front;
  }

  public Color getForegroundColor() {
    return foregroundColor;
  }

  public GLCanvas getContext() {
    return canvas;
  }

  public BufferedImage takeScreenshot() {
    if (!this.canvas.isCurrent()) {
      this.canvas.setCurrent();
    }
    context.makeCurrent();
    Point size = canvas.getSize();
    BufferedImage image = Screenshots.grab(size.x, size.y);
    context.release();
    return image;
  }

  ////////////////////////
  // Static functions for hyperbolic view coordinates calculations.


  public static float[] P(float x, float y) {
    if (!hyperbolic) {
      return new float[]{x, y, 0.0f};
    } else {
      // FIXME: uncomment this line, when fixed. we must use xoff here...
//      return convertVertex(x+grip.xoff - FACTOR/2.0f, y+grip.yoff-FACTOR/2.0f);
      return convertVertex(x - FACTOR/2.0f, y-FACTOR/2.0f);
    }
  }

  public static void V(GL2 gl, float x, float y) {
    if (!hyperbolic) {
      gl.glVertex2f(x, y);
    } else {
      //FIXME: uncomment. when grip static is totally fixed
      //x = x+grip.xoff;
      //y = y+grip.yoff;
      convertGLVertex(gl, x-FACTOR/2.0f, y-FACTOR/2.0f);
    }
  }

  public static void V(GL2 gl, double x, double y) {
    V(gl, (float)x, (float)y);
  }

  public static float[] convertVertex(float x, float y) {
    return convertVertex(x,y,0,1,1,1,0,0,0);
  }

  public static float[] convertVertex(float x, float y, float z) {
    return convertVertex(x,y,z,1,1,1,0,0,0);
  }

  public static float[] convertVertex(float x, float y, float z, float a, float b, float c, float k, float h, float p) {
    // solve the equation:
    // - (x-k)^2 / a^2 - (y-h)^2 / b^2 + (z-l)^2 / c^2 = 1
    // to get z.
    float zoom = 1.0f;
    x /= zoom;
    y /= zoom;
    z +=
      -Math.sqrt(
          c*c* (
            (x-k) * (x-k) / (a*a)
            + (y-h) * (y-h) / (b*b)
            + 1))
      + p;

    return new float[]{x, y, z};
  }

  public static void convertGLVertex(GL2 gl, float x, float y) {
    convertGLVertex(gl, x,y,0,1,1,1,0,0,0);
  }

  public static void convertGLVertex(GL2 gl, float x, float y, float z) {
    convertGLVertex(gl, x,y,z,1,1,1,0,0,0);
  }

  public static void convertGLVertex(GL2 gl, float x, float y, float z, float a, float b, float c, float k, float h, float p) {
    // solve the equation:
    // - (x-k)^2 / a^2 - (y-h)^2 / b^2 + (z-l)^2 / c^2 = 1
    // to get z.
    float zoom = 1.0f;
    x /= zoom;
    y /= zoom;
    z +=
      -Math.sqrt(
          c*c* (
            (x-k) * (x-k) / (a*a)
            + (y-h) * (y-h) / (b*b)
            + 1))
      + p;

    //System.out.println(""+x+":"+y+":"+z);
    gl.glVertex3f(x,y,z);
  }

}
