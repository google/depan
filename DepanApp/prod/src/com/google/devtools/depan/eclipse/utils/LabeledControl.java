/*
 * Copyright 2010 Google Inc.
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

package com.google.devtools.depan.eclipse.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * A composite control that provides a label for an additional, to-be-added
 * control.
 * 
 * <p>Additional static methods provide several common labeled controls for
 * DepAn.
 *
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class LabeledControl extends Composite {
  /**
   * @param parent
   * @param style
   */
  public LabeledControl(Composite parent, int style, String labelText) {
    super(parent, style);
    setLayout(new GridLayout(2, false));

    // Add label as initial member
    Label label = new Label(this, SWT.NONE);
    label.setText(labelText);
    label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
  }

  public static LayoutPickerControl createLabeledLayoutPicker(
      Composite parent, int style, String labelText, boolean allowFixed) {
    LabeledControl container = new LabeledControl(parent, style, labelText);
    LayoutPickerControl result = new LayoutPickerControl(container, allowFixed);
    result.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, false, false));
    return result;
  }
}