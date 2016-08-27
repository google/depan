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

package com.google.devtools.depan.nodes.filters.eclipse.ui.widgets;

import com.google.devtools.depan.nodes.filters.sequence.ClosureFilter;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;

import org.eclipse.swt.widgets.Composite;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class ClosureFilterEditorControl
    extends FilterEditorControl<ClosureFilter> {

  /////////////////////////////////////
  // Public methods

  public ClosureFilterEditorControl(Composite parent) {
    super(parent);
  }

  @Override
  public ClosureFilter buildFilter() {
    ClosureFilter result = getFilter();
    updateBasicFields(result);
    return result;
  }

  /////////////////////////////////////
  // Control management

  @Override
  protected void setupControls(Composite parent) {
    Composite matchEditor = setupClosureEditor(this);
    matchEditor.setLayoutData(Widgets.buildGrabFillData());
  }

  @Override
  protected void updateControls() {
    // TODO: Provide user editable properties:
    // - Edit/Modify/Load/Save embedded filter
    // - Map/Transform Context in interesting ways (e.g. VIEW => UNIVERSE)
  }

  /////////////////////////////////////
  // UX Setup

  private Composite setupClosureEditor(Composite parent) {
    Composite result = Widgets.buildGridGroup(parent, "Close over", 1);

    Widgets.buildGridLabel(parent,
        "At this time, closure filters have no editable properties");

    return result;
  }
}
