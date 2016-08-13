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
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.nodes.filters.eclipse.ui.widgets.FilterEditorDialog;
import com.google.devtools.depan.nodes.filters.eclipse.ui.widgets.NodeKindFilterEditorControl;
import com.google.devtools.depan.nodes.filters.model.ContextualFilter;
import com.google.devtools.depan.nodes.filters.sequence.NodeKindFilter;
import com.google.devtools.depan.nodes.filters.sequence.RelationCountFilter;

import com.google.common.collect.ImmutableList;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Provides labels, {@link Form}s, factories, and dialog editors
 * for {@link NodeKindFilter}s.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class NodeKindContribution
  extends DefaultingFilterContribution<NodeKindFilter> {

  @Override
  public String getLabel() {
    return "Filter by node kind";
  }

  @Override
  public Form getForm() {
    return Form.ELEMENT;
  }

  @Override
  public NodeKindFilter createElementFilter() {
    return new NodeKindFilter(ImmutableList.<Class<? extends Element>>of());
  }

  @Override
  public boolean handlesFilterInstance(ContextualFilter filter) {
    if (isAssignableAs(filter, NodeKindFilter.class)) {
      return true;
    }
    return false;
  }

  @Override
  public FilterEditorDialog<NodeKindFilter> buildEditorDialog(
      Shell shell, ContextualFilter filter,
      DependencyModel model, IProject project) {
    if (handlesFilterInstance(filter)) {
      return new ContributionEditorDialog(
          shell, (NodeKindFilter) filter, model, project);
    }
    throw buildNotAssignable(filter, RelationCountFilter.class);
  }

  private static class ContributionEditorDialog
      extends FilterEditorDialog<NodeKindFilter> {

    private NodeKindFilterEditorControl editor;

    protected ContributionEditorDialog(
        Shell parentShell, NodeKindFilter filter,
        DependencyModel model, IProject project) {
      super(parentShell, filter, model, project);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
      editor = new NodeKindFilterEditorControl(parent);
      editor.setInput(getFilter(), getModel(), getProject());
      return editor;
    }

    @Override
    protected NodeKindFilter buildFilter() {
      return editor.buildFilter();
    }
  }
}
