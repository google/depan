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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import java.text.MessageFormat;

/**
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
      Shell shell, ContextualFilter filter, DependencyModel model) {
    if (!handlesFilterInstance(filter)) {
      String msg = MessageFormat.format(
          "Filter {0} is assignable as a {1} type.",
          filter.getName(), RelationCountFilter.class.getName());
      throw new IllegalArgumentException(msg);
    }
    return new ContributionEditorDialog(
        shell, (RelationCountFilter) filter, model);
  }

  private static class ContributionEditorDialog
      extends FilterEditorDialog<RelationCountFilter> {

    private final DependencyModel model;

    private RelationCountFilterEditorControl editor;

    protected ContributionEditorDialog(
        Shell parentShell, RelationCountFilter filter, DependencyModel model) {
      super(parentShell, filter);
      this.model = model;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
      editor = new RelationCountFilterEditorControl(parent);
      editor.setInput(getFilter(), model);
      return editor;
    }

    @Override
    protected RelationCountFilter buildFilter() {
      return editor.buildFilter();
    }
  }
}
