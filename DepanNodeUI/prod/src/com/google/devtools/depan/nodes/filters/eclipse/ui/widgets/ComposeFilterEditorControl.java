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

import com.google.devtools.depan.nodes.filters.sequence.ComposeFilter;
import com.google.devtools.depan.nodes.filters.sequence.ComposeMode;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;

import org.eclipse.swt.widgets.Composite;

/**
 * Enhances {@link ComposeFilter} editing with selector for
 * {@link ComposeMode}.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class ComposeFilterEditorControl
    extends FilterEditorControl<ComposeFilter> {

  private ComposeModeControl mode;

  /////////////////////////////////////
  // Public methods

  public ComposeFilterEditorControl(Composite parent) {
    super(parent);
  }

  @Override
  public ComposeFilter buildFilter() {
    ComposeFilter result = getFilter();
    updateBasicFields(result);
    result.setMode(mode.getComposeMode());
    return result;
  }

  /////////////////////////////////////
  // Control management

  @Override
  protected void setupControls(Composite parent) {
    Composite matchEditor = setupCompositeEditor(this);
    matchEditor.setLayoutData(Widgets.buildGrabFillData());
  }

  @Override
  protected void updateControls() {
    mode.setComposeMode(getFilter().getMode());
  }

  /////////////////////////////////////
  // UX Setup

  private Composite setupCompositeEditor(Composite parent) {
    Composite result = Widgets.buildGridGroup(parent, "Composite", 2);

    Widgets.buildCompactLabel(result, "Compose mode");
    mode = new ComposeModeControl(result);
    return result;
  }
}
