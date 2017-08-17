/*
 * Copyright 2017 The Depan Project Authors
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
import com.google.devtools.depan.nodes.filters.eclipse.ui.widgets.FilterEditorDialog;
import com.google.devtools.depan.nodes.filters.eclipse.ui.widgets.FilterTableEditorControl;
import com.google.devtools.depan.nodes.filters.model.ContextualFilter;
import com.google.devtools.depan.nodes.filters.sequence.SteppingFilter;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import java.util.List;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class SteppingContribution
    extends DefaultingFilterContribution<SteppingFilter> {

  @Override
  public String getLabel() {
    return "Steps";
  }

  @Override
  public Form getForm() {
    return Form.STEPS;
  }

  @Override
  public SteppingFilter createStepsFilter(List<ContextualFilter> filters) {
    SteppingFilter result = new SteppingFilter();
    result.setSteps(filters);
    return result;
  }

  @Override
  public boolean handlesFilterInstance(ContextualFilter filter) {
    if (isAssignableAs(filter, SteppingFilter.class)) {
      return true;
    }
    return false;
  }

  @Override
  public FilterEditorDialog<SteppingFilter> buildEditorDialog(Shell shell,
      ContextualFilter filter, DependencyModel model, IProject project) {

    if (handlesFilterInstance(filter)) {
      return new ContributionEditorDialog(
          shell, (SteppingFilter) filter, model, project);
    }
    throw buildNotAssignable(filter, SteppingFilter.class);
  }

  private static class ContributionEditorDialog
      extends FilterEditorDialog<SteppingFilter> {

    private FilterTableEditorControl editor;

    protected ContributionEditorDialog(
        Shell parentShell, SteppingFilter filter,
        DependencyModel model, IProject project) {
      super(parentShell, filter, model, project);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
      editor = new FilterTableEditorControl(parent);
      editor.setInput(getFilter(), getModel(), getProject());
      return editor;
    }

    @Override
    protected SteppingFilter buildFilter() {
      return editor.buildFilter();
    }
  }
}
