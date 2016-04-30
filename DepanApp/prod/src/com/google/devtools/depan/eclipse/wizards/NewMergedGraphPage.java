/*
 * Copyright 2009 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.devtools.depan.eclipse.wizards;

import com.google.devtools.depan.eclipse.Project;
import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.platform.TableContentProvider;
import com.google.devtools.depan.platform.wizards.AbstractAnalysisPage;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import java.util.List;

/**
 * Wizard page with user-selected options for merging dependency graphs.
 * 
 * Currently implements just basic selection of multiple dependency graphs.
 * Future enhancements should allow for:
 * - user-selectable option to control whether duplicate edges from different
 *   dependency graphs are included.
 * - options for renaming nodes from one dependency graph to match identities in
 *   another graph (e.g. add parent directories to Directory and File nodes).
 * - richer UI presentation of .dgi resources from multiple projects.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class NewMergedGraphPage extends AbstractAnalysisPage {

  private static final String PAGE_LABEL = "Merge Dependency Graphs";

  private String errorMsg;

  private TableViewer graphView;

  /**
   * Create a Wizard page with the normal title and help text.
   * 
   * @param selection current selection when Wizard was opened.
   */
  public NewMergedGraphPage(ISelection selection) {
    super(selection, PAGE_LABEL,
        "This wizard creates a new dependency graph"
        + " by merging existing dependency graphs.",
        createFilename("Merged"));
  }

  @Override
  public String getAnalysisSourceErrorMsg() {
    return errorMsg;
  }

  @Override
  public Composite createSourceControl(Composite parent) {
    Group inputs = new Group(parent, SWT.NONE);
    inputs.setText("Dependency Graphs to Merge");
    FillLayout layout = new FillLayout(SWT.VERTICAL);
    layout.marginHeight = 3;
    layout.marginWidth = 3;
    inputs.setLayout(layout);

    graphView = setupTableViewer(inputs);

    setPageComplete(false);
    return inputs;
  }

  @SuppressWarnings("unchecked")
  public List<IResource> getMergeGraphs() {
    IStructuredSelection selection =
        (IStructuredSelection) graphView.getSelection();
    return selection.toList();
  }

  private TableViewer setupTableViewer(Composite container) {
    TableViewer graphTable = new TableViewer(
        container, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);

    graphTable.setLabelProvider(new WorkbenchLabelProvider());

    TableContentProvider<IResource> graphTableContents =
        buildGraphChoices();
    graphTableContents.initViewer(graphTable);

    graphTable.addSelectionChangedListener(new ISelectionChangedListener() {
      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        dialogChanged();
      }
    });

    return graphTable;
  }

  private TableContentProvider<IResource> buildGraphChoices() {
    TableContentProvider<IResource> result =
        new TableContentProvider<IResource>();

    for (Project project : Project.getProjects()) {
      for (IResource resource : project.listFiles(GraphDocument.EXTENSION)) {
        result.add(resource);
      }
    }
    return result;
  }

  /**
   * Ensures that all input fields are complete and consistent.
   */
  private void dialogChanged() {
    errorMsg = validateInputs();
    updateStatus(errorMsg);
  }

  private String validateInputs() {
    IStructuredSelection selection =
        (IStructuredSelection) graphView.getSelection();
    if (selection.size() < 2) {
      return "Select at least two dependency graphs.";
    }

    // No errors detected
    return null;
  }
}
