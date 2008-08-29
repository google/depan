/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.eclipse.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Utility class for assembling a linear control with multiple components.
 *
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public abstract class CompositeRowControl {

  protected final Composite container;

  /**
   * Create the outermost container Composite.  Under normal
   * circumstances, this Composite uses a Grid layout and tries
   * to center fields.
   *
   * @param parent Parent control for new Composite control.
   */
  public CompositeRowControl(Composite parent, int fields) {
    container = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout(fields, false);
    container.setLayout(layout);
  }

  /**
   * Set the layout data for the composite control object.
   *
   * @param gridData
   */
  public void setLayoutData(Object layoutData) {
    container.setLayoutData(layoutData);

  }

  /**
   * Add a label Label component to the composite.
   *
   * @param labelText Text for label to display
   */
  protected void addLabel(String labelText) {
    Label label = new Label(container, SWT.NONE);
    label.setText(labelText);
    label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
  }
}
