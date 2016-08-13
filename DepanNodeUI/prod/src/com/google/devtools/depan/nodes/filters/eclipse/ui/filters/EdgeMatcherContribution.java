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

import com.google.devtools.depan.edges.matchers.GraphEdgeMatchers;
import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.nodes.filters.eclipse.ui.widgets.EdgeMatcherFilterEditorControl;
import com.google.devtools.depan.nodes.filters.eclipse.ui.widgets.FilterEditorDialog;
import com.google.devtools.depan.nodes.filters.model.ContextualFilter;
import com.google.devtools.depan.nodes.filters.sequence.EdgeMatcherFilter;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Provides labels, {@link Form}s, factories, and dialog editors
 * for {@link EdgeMatcherFilter}s.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class EdgeMatcherContribution
  extends DefaultingFilterContribution<EdgeMatcherFilter> {

  @Override
  public String getLabel() {
    return "Filter by edges";
  }

  @Override
  public Form getForm() {
    return Form.ELEMENT;
  }

  @Override
  public EdgeMatcherFilter createElementFilter() {
    return new EdgeMatcherFilter(GraphEdgeMatchers.FORWARD);
  }

  @Override
  public boolean handlesFilterInstance(ContextualFilter filter) {
    if (isAssignableAs(filter, EdgeMatcherFilter.class)) {
      return true;
    }
    return false;
  }

  @Override
  public FilterEditorDialog<EdgeMatcherFilter> buildEditorDialog(
      Shell shell, ContextualFilter filter,
      DependencyModel model, IProject project) {
    if (handlesFilterInstance(filter)) {
      return new ContributionEditorDialog(
          shell, (EdgeMatcherFilter) filter, model, project);
    }
    throw buildNotAssignable(filter, EdgeMatcherFilter.class);
  }

  private static class ContributionEditorDialog
      extends FilterEditorDialog<EdgeMatcherFilter> {

    private EdgeMatcherFilterEditorControl editor;

    protected ContributionEditorDialog(
        Shell parentShell, EdgeMatcherFilter filter,
        DependencyModel model, IProject project) {
      super(parentShell, filter, model, project);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
      editor = new EdgeMatcherFilterEditorControl(parent);
      editor.setInput(getFilter(), getModel(), getProject());
      return editor;
    }

    @Override
    protected EdgeMatcherFilter buildFilter() {
      return editor.buildFilter();
    }
  }
}
