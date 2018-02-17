/*
 * Copyright 2007 The Depan Project Authors
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

package com.google.devtools.depan.view_doc.eclipse.ui.views;

import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.view_doc.eclipse.ViewDocLogger;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.DrawingListener;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.eclipse.ui.widgets.CameraDirectionGroup;
import com.google.devtools.depan.view_doc.eclipse.ui.widgets.CameraPositionGroup;
import com.google.devtools.depan.view_doc.model.CameraDirPreference;
import com.google.devtools.depan.view_doc.model.CameraPosPreference;
import com.google.devtools.depan.view_doc.model.ScenePreferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class ScenePrefsViewPart extends AbstractViewDocViewPart {

  private static final String[] FACTORS = new String[] {
      "1000", "800", "600", "400", "200", "150",
      "100", "90", "80", "70", "60", "50", "40"};

  private Label topDrawing;
  private Label leftDrawing;
  private Label rightDrawing;
  private Label bottomDrawing;

  private Label topViewport;
  private Label leftViewport;
  private Label rightViewport;
  private Label bottomViewport;

  private CameraPositionGroup position;
  private CameraDirectionGroup direction;

  private Label frameRate;
  private int frameUpdate;
  private int framePrev;
  private long timePrev;
  private int frameCount;

  private DrawingListener drawingListener;

  private DecimalFormat fpsFormat = new DecimalFormat("###.00");
  private ScenePreferences.Listener sceneListener;

  @Override
  public void createGui(Composite parent) {
    Composite baseComposite = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout(1, true);
    baseComposite.setLayout(layout);

    Group zoomScale = setupZoomScale(baseComposite);
    zoomScale.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    position = setupCameraPositionGroup(baseComposite);
    position.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    direction = setupCameraDirectionGroup(baseComposite);
    direction.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Composite info = setupInfo(baseComposite);
    info.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    // Composite debug = setupDebug(baseComposite);
    // debug.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
  }

  @Override
  protected void disposeGui() {
    releaseResources();
  }

  private Group setupZoomScale(Composite parent) {
    Group result = new Group(parent, SWT.NONE);
    result.setText("Zoom and Stretch");
    result.setLayout(new GridLayout(3, false));

    // zoom components
    new Label(result, SWT.NONE).setText("Zoom %");
    final Combo zoomFactors =
        new Combo(result, SWT.SINGLE | SWT.BORDER);
    zoomFactors.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));
    installFactors(zoomFactors);

    Button applyZoom = new Button(result, SWT.PUSH);
    applyZoom.setText("Ok");
    applyZoom.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        try {
          float size = Float.parseFloat(zoomFactors.getText());
          size /= 100.0f; // we have percents, we need a /1 scale.
          applyZoom(size);
        } catch (NumberFormatException ex) {
          ViewDocLogger.LOG.warn("Zoom value must be float.");
        }
      }
    });

    // stretch components
    new Label(result, SWT.NONE).setText("Stretch %");
    final Combo stretchFactors = new Combo(result, SWT.SINGLE | SWT.BORDER);
    stretchFactors.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));
    installFactors(stretchFactors);

    Button applyStretch = new Button(result, SWT.PUSH);
    applyStretch.setText("Ok");
    applyStretch.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        try {
          float size = Float.parseFloat(stretchFactors.getText());
          size /= 100.0f; // we have percents, we need a /1 scale.
          scaleLayout(size);
        } catch (NumberFormatException ex) {
          ViewDocLogger.LOG.warn("Scaling value must be float.");
        }
      }
    });

    Button autoStretch = new Button(result, SWT.PUSH);
    autoStretch.setLayoutData(
        new GridData(SWT.RIGHT, SWT.FILL, false, false, 3, 1));
    autoStretch.setText("Automatic stretch (a)");
    autoStretch.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        scaleToViewport();
      }
    });

    Label help = new Label(result, SWT.WRAP);
    help.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
    help.setText("If you are \"lost\", setting the zoom level to 100%, then "
        + "applying an automatic scaling should restore a good view.\n"
        + "\n"
        + "The stretch value is relative to the previously applied stretch "
        + "value. Therefore, applying a stretch value of 100% will never do "
        + "anything, and a stretch value of 200% always makes the graph two "
        + "times larger even if applied multiple times consecutivelly.\n");

    return result;
  }

  private void installFactors(Combo combo) {
    for (String text : FACTORS) {
      combo.add(text);
    }
    combo.pack();
  }

  private CameraPositionGroup setupCameraPositionGroup(Composite parent) {
    CameraPositionGroup result = new CameraPositionGroup(parent);

    result.addPositionChangeListener(new CameraPositionGroup.Listener() {

      @Override
      public void xChanged(float value) {
        if (!hasEditor()) {
          return;
        }

        ViewEditor editor = getEditor();
        ScenePreferences scene = editor.getScenePrefs();
        editor.moveToCamera(value, scene.getCameraPos().getY());
      }

      @Override
      public void yChanged(float value) {
        if (!hasEditor()) {
          return;
        }

        ViewEditor editor = getEditor();
        ScenePreferences scene = editor.getScenePrefs();
        editor.moveToCamera(scene.getCameraPos().getX(), value);
      }

      @Override
      public void zChanged(float value) {
        if (!hasEditor()) {
          return;
        }

        ViewEditor editor = getEditor();
        editor.zoomToCamera(value);
      }

    });

    return result;
  }

  private CameraDirectionGroup setupCameraDirectionGroup(Composite parent) {
    CameraDirectionGroup result = new CameraDirectionGroup(parent);

    result.addPositionChangeListener(new CameraDirectionGroup.Listener() {

      @Override
      public void xChanged(float value) {
        if (!hasEditor()) {
          return;
        }

        ViewEditor editor = getEditor();
        ScenePreferences scene = editor.getScenePrefs();
        CameraDirPreference dir = scene.getCameraDir();
        editor.rotateToDirection(value, dir.getY(), dir.getZ());
      }

      @Override
      public void yChanged(float value) {
        if (!hasEditor()) {
          return;
        }

        ViewEditor editor = getEditor();
        ScenePreferences scene = editor.getScenePrefs();
        CameraDirPreference dir = scene.getCameraDir();
        editor.rotateToDirection(dir.getX(), value, dir.getZ());
      }

      @Override
      public void zChanged(float value) {
        if (!hasEditor()) {
          return;
        }

        ViewEditor editor = getEditor();
        ScenePreferences scene = editor.getScenePrefs();
        CameraDirPreference dir = scene.getCameraDir();
        editor.rotateToDirection(dir.getX(), dir.getY(), value);
      }

    });

    return result;
  }

  private Composite setupInfo(Composite parent) {

    Group result = new Group(parent, SWT.NONE);

    GridLayout layout = new GridLayout(1, true);
    result.setLayout(layout);

    result.setText("Rendering Info");
    Composite metrics = setupMetrics(result);
    metrics.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Composite rate = setupFrameRate(result);
    rate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    return result;
  }

  private Composite setupMetrics(Composite parent) {
    Composite baseComposite = new Composite(parent, SWT.NONE);
    baseComposite.setLayout(new GridLayout(3, false));

    Label headerLabel = new Label(baseComposite, SWT.NONE);
    Label headerDrawing = new Label(baseComposite, SWT.RIGHT);
    headerDrawing.setText("Drawing");
    Label headerViewPort = new Label(baseComposite, SWT.RIGHT);
    headerViewPort.setText("Viewport");
    layoutRow(headerLabel, headerDrawing, headerViewPort);

    Label topLabel = new Label(baseComposite, SWT.NONE);
    topLabel.setText("Top");
    topDrawing = new Label(baseComposite, SWT.RIGHT);
    topViewport = new Label(baseComposite, SWT.RIGHT);
    layoutRow(topLabel, topDrawing, topViewport);

    Label leftLabel = new Label(baseComposite, SWT.NONE);
    leftLabel.setText("Left");
    leftDrawing = new Label(baseComposite, SWT.RIGHT);
    leftViewport = new Label(baseComposite, SWT.RIGHT);
    layoutRow(leftLabel, leftDrawing, leftViewport);

    Label rightLabel = new Label(baseComposite, SWT.NONE);
    rightLabel.setText("Right");
    rightDrawing = new Label(baseComposite, SWT.RIGHT);
    rightViewport = new Label(baseComposite, SWT.RIGHT);
    layoutRow(rightLabel, rightDrawing, rightViewport);

    Label bottomLabel = new Label(baseComposite, SWT.NONE);
    bottomLabel.setText("Bottom");
    bottomDrawing = new Label(baseComposite, SWT.RIGHT);
    bottomViewport = new Label(baseComposite, SWT.RIGHT);
    layoutRow(bottomLabel, bottomDrawing, bottomViewport);

    drawingListener = new DrawingListener() {
      @Override
      public void updateDrawingBounds(Rectangle2D drawing, Rectangle2D viewport) {
        updateMetrics(drawing, viewport);
      }
    };

    return baseComposite;
  }

  @SuppressWarnings("unused")
  private Composite setupDebug(Composite parent) {
    Composite result = Widgets.buildGridGroup(parent, "Debug", 2);
    Button dumpBtn = Widgets.buildCompactPushButton(result, "Dump Rendering Properties");
    Label dumpLbl = Widgets.buildCompactLabel(result, "Display all the rendering properties");

    dumpBtn.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        dumpRenderingProperties();
      }
    });
    return result;
  }

  private void layoutRow(Label label, Label forDrawing, Label forViewPort) {
    label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
    forDrawing.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    forViewPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
  }

  private Composite setupFrameRate(Composite parent) {
    Composite baseComposite = new Composite(parent, SWT.NONE);
    baseComposite.setLayout(new GridLayout(2, false));

    Label rateLabel = new Label(baseComposite, SWT.NONE);
    rateLabel.setText("Frames per second");
    frameRate = new Label(baseComposite, SWT.RIGHT);

    rateLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
    frameRate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

    frameCount = 0;
    frameUpdate = frameCount + 20;
    framePrev = frameCount;
    timePrev = System.currentTimeMillis();

    return baseComposite;
  }

  private void updateCameraPosition(CameraPosPreference pos) {
    position.updateCameraPosition(pos);
  }

  private void updateCameraDirection(CameraDirPreference dir) {
    direction.updateCameraDirection(dir);
  }

  public void updateMetrics(Rectangle2D drawing, Rectangle2D viewport) {
    position.updateDrawingLimits(drawing);

    topDrawing.setText(Double.toString(drawing.getMaxY()));
    leftDrawing.setText(Double.toString(drawing.getMinX()));
    rightDrawing.setText(Double.toString(drawing.getMaxX()));
    bottomDrawing.setText(Double.toString(drawing.getMinY()));

    topViewport.setText(Double.toString(viewport.getMaxY()));
    leftViewport.setText(Double.toString(viewport.getMinX()));
    rightViewport.setText(Double.toString(viewport.getMaxX()));
    bottomViewport.setText(Double.toString(viewport.getMinY()));

    frameCount++;
    if (frameCount > frameUpdate) {
      long timeFrame = System.currentTimeMillis();
      frameRate.setText(calcFpsDisplay(timeFrame));

      frameUpdate = frameCount + 30;
      framePrev = frameCount;
      timePrev = timeFrame;
    }
  }

  private String calcFpsDisplay(long timeFrame) {
    double interval = (double) (timeFrame - timePrev);
    if (0.0 == interval) {
      return "Bad fps";
    }

    double fpsCalc = 1000.0 * ((double) (frameCount - framePrev)) / interval;
    return fpsFormat.format(fpsCalc);
  }

  /////////////////////////////////////
  // ViewDoc/Editor integration

  @Override
  protected void acquireResources() {
    ViewEditor editor = getEditor();
    editor.addDrawingListener(drawingListener);

    ScenePreferences camera = editor.getScenePrefs();
    sceneListener = new ScenePreferences.Listener() {

      @Override
      public void positionChanged(ScenePreferences camera) {
        updateCameraPosition(camera.getCameraPos());
      }

      @Override
      public void directionChanged(ScenePreferences camera) {
        updateCameraDirection(camera.getCameraDir());
      }
    };
    editor.addSceneListener(sceneListener);
    updateCameraPosition(camera.getCameraPos());
  }

  @Override
  protected void releaseResources() {
    if (hasEditor()) {
      ViewEditor editor = getEditor();
      editor.removeDrawingListener(drawingListener);
      editor.removeSceneListener(sceneListener);
    }
  }

  /////////////////////////////////////
  // View/Editor actions

  /**
   * Apply a scaling factor to the current view.
   * @param scale
   */
  private void scaleLayout(float scale) {
    if (!hasEditor()) {
      return;
    }
    getEditor().scaleLayout(scale, scale);
  }

  /**
   * Set the zoom value for the current view (basically move the camera).
   * @param scale
   */
  private void applyZoom(float scale) {
    if (!hasEditor()) {
      return;
    }

    getEditor().setZoom(scale);
  }

  /**
   * Run the algorithm trying to find the best scaling value for everything
   * to fit into the current view, with the current zoom level.
   */
  private void scaleToViewport() {
    if (!hasEditor()) {
      return;
    }
    getEditor().scaleToViewport();
  }

  private void dumpRenderingProperties() {
    if (!hasEditor()) {
      return;
    }
    getEditor().dumpRenderingProperties();
  }

}
