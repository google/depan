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

import com.google.devtools.depan.eclipse.visualization.layout.LayoutGenerator;
import com.google.devtools.depan.eclipse.visualization.layout.LayoutGenerators;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import java.util.List;

/**
 * A control for selecting a {@link Layouts } (graph layout) option.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class LayoutPickerControl extends Composite {

  private static final String LAYOUT_KEEP = "Keep positions";

  private final boolean allowKeep;

  private final Combo layoutChoice;

  private final List<String> layoutNames;

  /**
   * @param parent Containing composite
   * @param allowKeep use {@code true} if "Keep positions" is a valid choice.
   */
  public LayoutPickerControl(Composite parent, boolean allowKeep) {
    super(parent, SWT.NONE);
    setLayout(new FillLayout());

    this.allowKeep = allowKeep;

    layoutNames = LayoutGenerators.getLayoutNames();
    layoutChoice = new Combo(this, SWT.READ_ONLY | SWT.BORDER);
    layoutChoice.setVisibleItemCount(1);

    // Populate the dropdown's choices.
    if (allowKeep) {
      layoutChoice.add(LAYOUT_KEEP);
    }

    for (String name : layoutNames) {
      layoutChoice.add(name);
    }

    layoutChoice.select(0);
  }

  /**
   * Set the control to the selected {@code Layouts}.
   * 
   * @param layout selected {@code Layouts} to show in control
   */
  public void setLayoutChoice(String layoutName) {
    if (allowKeep && (null == layoutName)) {
      layoutChoice.select(0);
      return;
    }
    if (null == layoutName) {
      return;
    }

    int base = (allowKeep) ? 1 : 0;
    layoutChoice.select(base + LayoutGenerators.getLayoutIndex(layoutName));
  }

  public String getLayoutName() {
    int index = layoutChoice.getSelectionIndex();
    if (allowKeep && (index == 0)) {
      return null;
    }
    if (allowKeep) {
      index--;
    }
    if (index >= layoutNames.size()) {
      return null;
    }

    return layoutNames.get(index);
  }

  /**
   * Determine the {@code Layouts} instance chosen by the user.
   * 
   * @return a Layouts object, or null if positions should be retained.
   * @throws IllegalArgumentException if the current selection is not a
   *     valid {@link Layouts} instance.
   */
  public LayoutGenerator getLayoutChoice() {
    return LayoutGenerators.getByName(getLayoutName());
  }
}
