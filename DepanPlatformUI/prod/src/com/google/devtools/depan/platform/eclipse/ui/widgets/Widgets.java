/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.platform.eclipse.ui.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Utility methods for SWT widgets and JFace viewers.
 * 
 * Many {@link Grid} and {@link GridLayout} factory methods. 
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class Widgets {

  private Widgets() {
    // Prevent instantiation.
  }

  /////////////////////////////////////
  // Control Factories

  /**
   * Provide a {@link Composite} control with the {@link GridLayout} configured
   * for zero margins around the embedded controls.  Normal grid spacing
   * applies between children controls.
   * 
   * The provided {@link Composite} can effectively layout sets of controls
   * that are treated as a unit.
   */
  public static Composite buildGridContainer(Composite parent, int columns) {
    Composite result = new Composite(parent, SWT.NONE);
    result.setLayout(buildContainerLayout(columns));
    return result;
  }

  public static Group buildGridGroup(
      Composite parent, String label, int columns) {
    Group result = new Group(parent, SWT.NONE);
    result.setText(label);
    result.setLayout(new GridLayout(columns, false));
    return result;
  }

  public static Label buildCompactLabel(Composite parent, String label) {
    Label result = new Label(parent, SWT.NONE);
    result.setLayoutData(new GridData());
    result.setText(label);
    return result;
  }

  public static Label buildGridLabel(Composite parent, String label) {
    Label result = new Label(parent, SWT.NONE);
    result.setLayoutData(buildHorzFillData());
    result.setText(label);
    return result;
  }

  public static Text buildGridText(Composite parent) {
    Text result = new Text(parent, SWT.NONE);
    result.setLayoutData(buildHorzFillData());
    return result;
  }

  public static Text buildGridBoxedText(Composite parent) {
    Text result = new Text(parent, SWT.BORDER | SWT.SINGLE);
    result.setLayoutData(buildHorzFillData());
    return result;
  }

  /**
   * Use directly for image buttons without text.
   */
  public static Button buildGridPushButton(Composite parent) {
    Button result = new Button(parent, SWT.PUSH);
    result.setLayoutData(buildHorzFillData());
    return result;
  }

  public static Button buildGridPushButton(Composite parent, String label) {
    Button result = buildGridPushButton(parent);
    result.setText(label);
    return result;
  }

  public static Button buildCompactCheckButton(Composite parent, String label) {
    Button result = new Button(parent, SWT.CHECK);
    result.setLayoutData(new GridData());
    result.setText(label);
    return result;
  }

  public static Button buildCompactPushButton(Composite parent, String label) {
    Button result = new Button(parent, SWT.PUSH);
    result.setLayoutData(new GridData());
    result.setText(label);
    return result;
  }

  public static Button buildTrailPushButton(Composite parent, String label) {
    Button result = new Button(parent, SWT.PUSH);
    result.setText(label);
    result.setLayoutData(buildTrailFillData());
    return result;
  }

  public static Button buildCompactRadio(Composite parent, String label) {
    Button result = new Button(parent, SWT.RADIO);
    result.setLayoutData(new GridData());
    result.setText(label);
    return result;
  }

  /////////////////////////////////////
  // Layout Factories

  /**
   * Provide a {@link GridLayout} configured for zero margins around the
   * embedded controls.
   * 
   * The provided {@link GridLayout} supports derived {@link Composite}
   * controls that package there embedded controls as a unit.
   */
  public static GridLayout buildContainerLayout(int columns) {
    GridLayout layout = new GridLayout(columns, false);
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    return layout;
  }

  /**
   * Provide a GridData instance that should expand to fill any available
   * horizontal space.
   */
  public static GridData buildHorzFillData() {
    return new GridData(SWT.FILL, SWT.FILL, true, false);
  }

  /**
   * Provide a GridData instance that spans multiple horizontal columns.
   */
  public static GridData buildHorzSpanData(int span) {
    GridData result = Widgets.buildHorzFillData();
    result.horizontalSpan = span;
    return result;
  }

  /**
   * Provide a GridData instance that should expand to fill any available
   * horizontal and vertical space.
   */
  public static GridData buildGrabFillData() {
    return new GridData(SWT.FILL, SWT.FILL, true, true);
  }

  /**
   * Provide a GridData for trailing (right justified) horizontal filled
   * {@link Control}s.
   */
  public static GridData buildTrailFillData() {
    return new GridData(SWT.TRAIL, SWT.FILL, true, false);
  }

  /**
   * When you need to tweak a layout.
   */
  public static GridData getLayoutData(Control control) {
    return (GridData) control.getLayoutData();
  }
}
