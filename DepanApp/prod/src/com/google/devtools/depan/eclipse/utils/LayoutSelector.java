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

import com.google.devtools.depan.eclipse.visualization.layout.Layouts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;

/**
 * Standard composite control for a layout drop-down and a label.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class LayoutSelector extends CompositeRowControl {

  private CCombo layoutChoice;

  /**
   * Create the layout selector within the parent control
   * @param parent parent control for the new selector
   */
  public LayoutSelector(Composite parent) {
    super(parent, 2);

    addLabel("Layout to use: ");

    layoutChoice = new CCombo(container, SWT.READ_ONLY | SWT.BORDER);
    for (Layouts l : Layouts.values()) {
      layoutChoice.add(l.toString());
    }

    layoutChoice.select(0);
  }

  /**
   * Provide the user's current Layout choice
   * 
   * @return Currently selected Layout choice
   */
  public Layouts getSelectedLayout() {
    return Layouts.valueOf(
        layoutChoice.getItem(layoutChoice.getSelectionIndex()));
  }
}
