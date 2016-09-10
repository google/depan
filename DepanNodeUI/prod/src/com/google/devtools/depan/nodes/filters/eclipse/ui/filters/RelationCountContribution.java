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

import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.model.RelationSets;
import com.google.devtools.depan.nodes.filters.eclipse.ui.widgets.FilterEditorDialog;
import com.google.devtools.depan.nodes.filters.eclipse.ui.widgets.RelationCountFilterEditorControl;
import com.google.devtools.depan.nodes.filters.model.ContextualFilter;
import com.google.devtools.depan.nodes.filters.sequence.CountPredicate;
import com.google.devtools.depan.nodes.filters.sequence.CountPredicates;
import com.google.devtools.depan.nodes.filters.sequence.RelationCountFilter;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Provides labels, {@link Form}s, factories, and dialog editors
 * for {@link RelationCountFilter}s.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class RelationCountContribution
  extends DefaultingFilterContribution<RelationCountFilter> {

  @Override
  public String getLabel() {
    return "Filter by relation count";
  }

  @Override
  public Form getForm() {
    return Form.ELEMENT;
  }

  @Override
  public RelationCountFilter createElementFilter() {
    RelationSet relationSet = RelationSets.ALL;
    CountPredicate forwardTest = new CountPredicates.IncludeAbove(0);
    CountPredicate reverseTest = new CountPredicates.IncludeAbove(0);
    return new RelationCountFilter(relationSet, forwardTest, reverseTest);
  }

  @Override
  public boolean handlesFilterInstance(ContextualFilter filter) {
    if (isAssignableAs(filter, RelationCountFilter.class)) {
      return true;
    }
    return false;
  }

  @Override
  public FilterEditorDialog<RelationCountFilter> buildEditorDialog(
      Shell shell, ContextualFilter filter,
      DependencyModel model, IProject project) {
    if (handlesFilterInstance(filter)) {
      return new ContributionEditorDialog(
          shell, (RelationCountFilter) filter, model, project);
    }
    throw buildNotAssignable(filter, RelationCountFilter.class);
  }

  private static class ContributionEditorDialog
      extends FilterEditorDialog<RelationCountFilter> {

    private RelationCountFilterEditorControl editor;

    protected ContributionEditorDialog(
        Shell parentShell, RelationCountFilter filter,
        DependencyModel model, IProject project) {
      super(parentShell, filter, model, project);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
      editor = new RelationCountFilterEditorControl(parent);
      editor.setInput(getFilter(), getModel(), getProject());
      return editor;
    }

    @Override
    protected RelationCountFilter buildFilter() {
      return editor.buildFilter();
    }
  }
}
