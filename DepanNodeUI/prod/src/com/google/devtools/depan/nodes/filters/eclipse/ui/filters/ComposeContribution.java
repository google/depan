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

package com.google.devtools.depan.nodes.filters.eclipse.ui.filters;

import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.nodes.filters.eclipse.ui.widgets.ComposeFilterEditorControl;
import com.google.devtools.depan.nodes.filters.eclipse.ui.widgets.FilterEditorDialog;
import com.google.devtools.depan.nodes.filters.model.ContextualFilter;
import com.google.devtools.depan.nodes.filters.sequence.ComposeFilter;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Provides labels, {@link Form}s, factories, and dialog editors
 * for {@link ComposeFilter}s.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class ComposeContribution
    extends DefaultingFilterContribution<ComposeFilter> {

  @Override
  public String getLabel() {
    return "Compose";
  }

  @Override
  public Form getForm() {
    return Form.WRAPPER;
  }

  @Override
  public ComposeFilter createWrapperFilter(ContextualFilter filter) {
    ComposeFilter result = new ComposeFilter();
    result.setFilter(filter);
    return result;
  }

  @Override
  public boolean handlesFilterInstance(ContextualFilter filter) {
    if (isAssignableAs(filter, ComposeFilter.class)) {
      return true;
    }
    return false;
  }

  @Override
  public FilterEditorDialog<ComposeFilter> buildEditorDialog(Shell shell,
      ContextualFilter filter, DependencyModel model, IProject project) {

    if (handlesFilterInstance(filter)) {
      return new ContributionEditorDialog(
          shell, (ComposeFilter) filter, model, project);
    }
    throw buildNotAssignable(filter, ComposeFilter.class);
  }

  private static class ContributionEditorDialog
      extends FilterEditorDialog<ComposeFilter> {

    private ComposeFilterEditorControl editor;

    protected ContributionEditorDialog(
        Shell parentShell, ComposeFilter filter,
        DependencyModel model, IProject project) {
      super(parentShell, filter, model, project);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
      editor = new ComposeFilterEditorControl(parent);
      editor.setInput(getFilter(), getModel(), getProject());
      return editor;
    }

    @Override
    protected ComposeFilter buildFilter() {
      return editor.buildFilter();
    }
  }
}
