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

import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.nodes.filters.model.ContextualFilter;
import com.google.devtools.depan.nodes.filters.sequence.BasicFilter;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Define standard editing services for filters that have no configurable
 * elements.  The name and summary can be changed, and the filters can
 * be saved or loaded as independent entities.
 * 
 * Suitable for {@link ContextualFilter}s that have no user-editable
 * properties beyond name and summary.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class SimpleFilterEditorDialog<T extends BasicFilter>
    extends FilterEditorDialog<T> {

  //////////////////////////////////
  // UX Elements

  SimpleFilterEditorControl<T> editor;

  /////////////////////////////////////
  // Public methods

  public SimpleFilterEditorDialog(
      Shell shell, T filter, DependencyModel model, IProject project) {
    super(shell, filter, model, project);
  }

  @Override
  protected Control createDialogArea(Composite parent) {
    Composite resourceGrp = setupResourceEditor(parent);
    resourceGrp.setLayoutData(Widgets.buildGrabFillData());

    editor.setInput(getFilter(), getModel(), getProject());
    return editor;
  }

  @Override
  protected T buildFilter() {
    return editor.buildFilter();
  }

  /////////////////////////////////////
  // UX Setup

  private Composite setupResourceEditor(Composite parent) {
    Composite result = Widgets.buildGridGroup(parent, "Resource", 1);

    // Just name and summary.
    editor = new SimpleFilterEditorControl<T>(result);
    editor.setLayoutData(Widgets.buildGrabFillData());

    return result;
  }
}
