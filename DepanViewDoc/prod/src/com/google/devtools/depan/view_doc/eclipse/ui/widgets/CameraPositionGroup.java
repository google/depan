/*
 * Copyright 2015 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.devtools.depan.view_doc.eclipse.ui.widgets;

import com.google.devtools.depan.eclipse.visualization.ogl.GLConstants;
import com.google.devtools.depan.platform.ListenerManager;
import com.google.devtools.depan.platform.ListenerManager.Dispatcher;
import com.google.devtools.depan.view_doc.model.CameraPosPreference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import java.awt.geom.Rectangle2D;

/**
 * Provide direct manipulation controls for the camera position.
 * The instance bundles three widgets, one for each of the different axes,
 * as a single control.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class CameraPositionGroup extends Composite {

  public static interface Listener {
    void xChanged(float value);
    void yChanged(float value);
    void zChanged(float value);
  }

  private boolean notifyListeners = true;

  private ListenerManager<Listener> positionListeners =
      new ListenerManager<Listener>();

  private final ModifyListener INPUT_LISTENER = new ModifyListener() {
    @Override
    public void modifyText(ModifyEvent e) {
      if (notifyListeners) {
        handlePositionChange((Spinner) e.widget);
      }
    }
  };

  private PositionInput xposInput;
  private PositionInput yposInput;
  private PositionInput zposInput;

  public CameraPositionGroup(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new FillLayout(SWT.HORIZONTAL));

    Group group = new Group(this, SWT.NONE);
    group.setText("Camera Position");

    RowLayout row = new RowLayout(SWT.HORIZONTAL);
    row.marginTop = 0;
    row.marginBottom = 0;
    row.marginLeft = 0;
    row.marginRight = 0;
    row.justify = true;
    row.pack = false;
    group.setLayout(row);

    xposInput = new PositionInput(group, "X-Pos",
        "Left/Right");
    xposInput.setLayoutData(new RowData());
    xposInput.addModifyListener(INPUT_LISTENER);

    yposInput = new PositionInput(group, "Y-Pos",
        "Up/Down");
    yposInput.setLayoutData(new RowData());
    yposInput.addModifyListener(INPUT_LISTENER);

    zposInput = new PositionInput(group, "Z-Pos",
        "Page-Up/Page-Dn");
    zposInput.setLayoutData(new RowData());
    zposInput.setLimits((int) GLConstants.ZOOM_MAX, (int) (GLConstants.Z_FAR - 1));
    zposInput.addModifyListener(INPUT_LISTENER);
  }

  /////////////////////////////////////
  // Public API

  /**
   * Update the position as shown in the controls, but don't notify listeners.
   * For use when other mechanisms (init, scrollbar, etc.) have already
   * updated the OGL camera.
   * 
   * This is normally called only when the scene is stable.
   * 
   * @param pos
   */
  public void updateCameraPosition(CameraPosPreference pos) {
    notifyListeners = false;
    xposInput.updateInput(pos.getX());
    yposInput.updateInput(pos.getY());
    zposInput.updateInput(pos.getZ());
    notifyListeners = true;
  }

  /**
   * Use the drawing size to define the limits for the x and y axis
   * controls.
   * 
   * As the drawing limits call-back, this is invoked for every frame.
   * It should never generate a notification or event.
   */
  public void updateDrawingLimits(Rectangle2D drawing) {
    int xmin = (int) (drawing.getX() - (drawing.getWidth() / 2));
    int xmax = xmin + (int) (drawing.getWidth() * 2);
    xposInput.setLimits(xmin, xmax);

    int ymin = (int) (drawing.getY() - (drawing.getHeight() / 2));
    int ymax = ymin + (int) (drawing.getHeight() * 2);
    yposInput.setLimits(ymin, ymax);
  }

  private static class PositionInput extends Composite {

    private final Spinner input;

    public PositionInput(Composite parent, String label, String info) {
      super(parent, SWT.NONE);
      GridLayout grid = new GridLayout(2, false);
      grid.marginTop = 0;
      grid.marginBottom = 0;
      grid.marginLeft = 0;
      grid.marginRight = 0;
      setLayout(grid);

      Label xdirLabel = setupLabel(this, label);
      xdirLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, true));

      input = setupPosition(this);
      input.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, true));

      Label xdirInfo = setupLabel(this, info);
      xdirInfo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 2, 1));
      }

    public void setLimits(int min, int max) {
      input.setMinimum(min);
      input.setMaximum(max);
    }

    private Label setupLabel(Composite parent, String text) {
      Label result = new Label(parent, SWT.NONE);
      result.setText(text);
      return result;
    }

    private Spinner setupPosition(Composite parent) {
      Spinner result = new Spinner(parent, SWT.NONE);
      result.setIncrement(1);
      result.setPageIncrement(10);
      return result;
    }

    public void addModifyListener(ModifyListener listener) {
      input.addModifyListener(listener);
    }

    public void updateInput(float value) {
      int posX = (int) value;
      if (posX != input.getSelection()) {
        input.setSelection(posX);
      }
    }

    public boolean isControl(Control control) {
      return input == control;
      
    }
  }

  /////////////////////////////////////
  // Listener

  public void addPositionChangeListener(Listener listener) {
    positionListeners.addListener(listener);
  }

  public void removeSelectionChangeListener(Listener listener) {
    positionListeners.removeListener(listener);
  }

  private void handlePositionChange(Spinner input) {

    // Ignore bad input
    float value;
    try {
      String text = input.getText();
      value = Float.parseFloat(text);
    } catch (NumberFormatException e) {
      return;
    }

    if (xposInput.isControl(input)) {
      fireXChanged(value);
    } else if (yposInput.isControl(input)) {
      fireYChanged(value);
    } else if (zposInput.isControl(input)) {
      fireZChanged(value);
    }
  }

  private static abstract class SimpleDispatcher implements Dispatcher<Listener>{

    @Override
    public void captureException(RuntimeException errAny) {
    }
  }

  private void fireXChanged(final Float value) {

    positionListeners.fireEvent(new SimpleDispatcher() {
      @Override
      public void dispatch(Listener listener) {
        listener.xChanged(value);
      }
    });
  }

  private void fireYChanged(final Float value) {

    positionListeners.fireEvent(new SimpleDispatcher() {
      @Override
      public void dispatch(Listener listener) {
        listener.yChanged(value);
      }
    });
  }
  private void fireZChanged(final Float value) {

    positionListeners.fireEvent(new SimpleDispatcher() {
      @Override
      public void dispatch(Listener listener) {
        listener.zChanged(value);
      }
    });
  }
}
