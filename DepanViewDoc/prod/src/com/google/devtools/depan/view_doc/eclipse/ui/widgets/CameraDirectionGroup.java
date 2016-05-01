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

import com.google.devtools.depan.platform.ListenerManager;
import com.google.devtools.depan.platform.ListenerManager.Dispatcher;
import com.google.devtools.depan.view_doc.model.CameraDirPreference;

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

/**
 * Provide direct manipulation controls for the camera direction.
 * The instance bundles three widgets, one for each of the different axes,
 * as a single control.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class CameraDirectionGroup extends Composite {

  public static interface Listener {
    void xChanged(float value);
    void yChanged(float value);
    void zChanged(float value);
  }

  private boolean notifyListeners = true;

  private ListenerManager<Listener> directionListeners =
      new ListenerManager<Listener>();

  private final ModifyListener INPUT_LISTENER = new ModifyListener() {
    @Override
    public void modifyText(ModifyEvent e) {
      if (notifyListeners) {
        handleDirectionChange((Spinner) e.widget);
      }
    }
  };

  private DirectionInput xdirInput;
  private DirectionInput ydirInput;
  private DirectionInput zdirInput;

  public CameraDirectionGroup(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new FillLayout(SWT.HORIZONTAL));

    Group group = new Group(this, SWT.NONE);
    group.setText("Camera Direction");

    RowLayout row = new RowLayout(SWT.HORIZONTAL);
    row.marginTop = 0;
    row.marginBottom = 0;
    row.marginLeft = 0;
    row.marginRight = 0;
    row.justify = true;
    row.pack = false;
    group.setLayout(row);

    xdirInput = new DirectionInput(group, "X-Dir",
        "Ctrl + Up/Down"
        + "\nShift + Mouse-Left"
        + "\nVertical rotates X");
    xdirInput.setLayoutData(new RowData());
    xdirInput.addModifyListener(INPUT_LISTENER);

    ydirInput = new DirectionInput(group, "Y-Dir", "");
    ydirInput.setLayoutData(new RowData());
    ydirInput.addModifyListener(INPUT_LISTENER);

    zdirInput = new DirectionInput(group, "Z-Dir",
        "Ctrl + Left/Right"
        + "\nShift + Mouse-Left"
        + "\nHorizontal rotates Z");
    zdirInput.setLayoutData(new RowData());
    zdirInput.addModifyListener(INPUT_LISTENER);
  }

  private static class DirectionInput extends Composite {

    private final Spinner input;

    public DirectionInput(Composite parent, String label, String info) {
      super(parent, SWT.NONE);
      GridLayout grid = new GridLayout(2, false);
      grid.marginTop = 0;
      grid.marginBottom = 0;
      grid.marginLeft = 0;
      grid.marginRight = 0;
      setLayout(grid);

      Label xdirLabel = setupLabel(this, label);
      xdirLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, true));

      input = setupDirection(this);
      input.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, true));

      Label xdirInfo = setupLabel(this, info);
      xdirInfo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 2, 1));
      }

    private Label setupLabel(Composite parent, String text) {
      Label result = new Label(parent, SWT.NONE);
      result.setText(text);
      return result;
    }

    private Spinner setupDirection(Composite parent) {
      Spinner result = new Spinner(parent, SWT.NONE);
      result.setMinimum(-180);
      result.setMaximum(180);
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
  // Public API

  public void updateCameraDirection(CameraDirPreference dir) {
    notifyListeners = false;
    xdirInput.updateInput(dir.getX());
    ydirInput.updateInput(dir.getY());
    zdirInput.updateInput(dir.getZ());
    notifyListeners = true;
  }

  /////////////////////////////////////
  // Listener

  public void addPositionChangeListener(Listener listener) {
    directionListeners.addListener(listener);
  }

  public void removeSelectionChangeListener(Listener listener) {
    directionListeners.removeListener(listener);
  }

  private void handleDirectionChange(Spinner input) {

    // Ignore bad input
    float value;
    try {
      String text = input.getText();
      value = Float.parseFloat(text);
    } catch (NumberFormatException e) {
      return;
    }

    if (xdirInput.isControl(input)) {
      fireXChanged(value);
    } else if (ydirInput.isControl(input)) {
      fireYChanged(value);
    } else if (zdirInput.isControl(input)) {
      fireZChanged(value);
    }
  }

  private static abstract class SimpleDispatcher implements Dispatcher<Listener>{

    @Override
    public void captureException(RuntimeException errAny) {
    }
  }

  private void fireXChanged(final Float value) {

    directionListeners.fireEvent(new SimpleDispatcher() {
      @Override
      public void dispatch(Listener listener) {
        listener.xChanged(value);
      }
    });
  }

  private void fireYChanged(final Float value) {

    directionListeners.fireEvent(new SimpleDispatcher() {
      @Override
      public void dispatch(Listener listener) {
        listener.yChanged(value);
      }
    });
  }

  private void fireZChanged(final Float value) {

    directionListeners.fireEvent(new SimpleDispatcher() {
      @Override
      public void dispatch(Listener listener) {
        listener.zChanged(value);
      }
    });
  }
}
