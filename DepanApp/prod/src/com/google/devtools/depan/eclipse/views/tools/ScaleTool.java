/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.eclipse.views.tools;

import com.google.devtools.depan.eclipse.editors.DrawingListener;
import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.views.ViewEditorTool;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class ScaleTool extends ViewEditorTool {

  private Label topDrawing;
  private Label leftDrawing;
  private Label rightDrawing;
  private Label bottomDrawing;

  private Label topViewport;
  private Label leftViewport;
  private Label rightViewport;
  private Label bottomViewport;

  private Label frameRate;
  private int frameUpdate;
  private int framePrev;
  private long timePrev;
  private int frameCount;

  private DrawingListener drawingListener;

  private DecimalFormat fpsFormat = new DecimalFormat("###.00");

  @Override
  public Image getIcon() {
    return Resources.IMAGE_ZOOM;
  }

  @Override
  public String getName() {
    return Resources.NAME_ZOOM;
  }

  @Override
  public Control setupComposite(Composite parent) {
    Composite baseComposite = new Composite(parent, SWT.NONE);

    // zoom components
    new Label(baseComposite, SWT.NONE).setText("Zoom %");
    final Combo zoomPercents =
        new Combo(baseComposite, SWT.SINGLE | SWT.BORDER);
    new Label(baseComposite, SWT.NONE).setText("%");
    Button applyZoom = new Button(baseComposite, SWT.PUSH);

    // stretch components
    new Label(baseComposite, SWT.NONE).setText("Stretch %");
    final Combo percents = new Combo(baseComposite, SWT.SINGLE | SWT.BORDER);
    new Label(baseComposite, SWT.NONE).setText("%");
    Button apply = new Button(baseComposite, SWT.PUSH);

    Button autoStretch = new Button(baseComposite, SWT.PUSH);
    Label help = new Label(baseComposite, SWT.WRAP);

    for (String text : new String[] {
        "100", "800", "600", "400", "200", "150", "100", "90", "80", "70",
        "60", "50", "40"}) {
      percents.add(text);
      zoomPercents.add(text);
    }
    percents.pack();
    zoomPercents.pack();

    apply.setText("Ok");
    applyZoom.setText("Ok");
    autoStretch.setText("Automatic stretch (a)");
    help.setText("If you are \"lost\", setting the zoom level to 100%, then "
        + "applying an automatic scaling should restore a good view.\n"
        + "\n"
        + "The stretch value is relative to the previously applied stretch "
        + "value. Therefore, applying a stretch value of 100% will never do "
        + "anything, and a stretch value of 200% always makes the graph two "
        + "times larger even if applied multiple times consecutivelly.\n");

    Composite metrics = setupMetrics(baseComposite);
    Composite rate = setupFrameRate(baseComposite);

    // layout
    baseComposite.setLayout(new GridLayout(4, false));
    autoStretch.setLayoutData(
        new GridData(SWT.RIGHT, SWT.FILL, false, false, 4, 1));
    help.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));
    percents.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));
    zoomPercents.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));
    metrics.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));
    rate.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));

    // actions
    apply.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        try {
          float size = Float.parseFloat(percents.getText());
          size /= 100.0f; // we have percents, we need a /1 scale.
          applyScale(size);
        } catch (NumberFormatException ex) {
          // error in the user input. ignore the zoom level.
          System.out.println("Error in the number format. Must be float.");
        }
      }
    });

    applyZoom.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        try {
          float size = Float.parseFloat(zoomPercents.getText());
          size /= 100.0f; // we have percents, we need a /1 scale.
          applyZoom(size);
        } catch (NumberFormatException ex) {
          // error in the user input. ignore the zoom level.
          System.out.println("Error in the number format. Must be float.");
        }
      }
    });

    autoStretch.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        automaticScaling();
      }
    });

    return baseComposite;
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

  private void layoutRow(Label label, Label forDrawing, Label forViewPort) {
    label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
    forDrawing.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    forViewPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
  }

  public void updateMetrics(Rectangle2D drawing, Rectangle2D viewport) {
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

  @Override
  protected void acquireResources() {
    super.acquireResources();

    getEditor().addDrawingListener(drawingListener);
  }

  @Override
  protected void releaseResources() {
    if (hasEditor()) {
      getEditor().removeDrawingListener(drawingListener);
    }

    super.releaseResources();
  }

  /**
   * Apply a scaling factor to the current view.
   * @param scale
   */
  private void applyScale(float scale) {
    if (!hasEditor()) {
      return;
    }
    getEditor().layoutScale(scale, scale);
  }

  /**
   * Set the zoom value for the current view (basically move the camera).
   * @param scale
   */
  private void applyZoom(float scale) {
    if (!hasEditor()) {
      return;
    }
    getEditor().getRenderer().getScene().setZoom(scale);
  }

  /**
   * Run the algorithm trying to find the best scaling value for everything
   * to fit into the current view, with the current zoom level.
   */
  private void automaticScaling() {
    if (!hasEditor()) {
      return;
    }
    getEditor().layoutBestFit();
  }
}
