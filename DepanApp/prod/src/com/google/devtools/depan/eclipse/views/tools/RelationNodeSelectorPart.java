/*
 * Copyright 2008 The Depan Project Authors
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

package com.google.devtools.depan.eclipse.views.tools;

import com.google.devtools.depan.eclipse.utils.GraphEdgeMatcherEditorPart;
import com.google.devtools.depan.paths.filters.PathMatcher;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;

import com.google.devtools.edges.matchers.GraphEdgeMatcherDescriptor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import java.util.List;

/**
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class RelationNodeSelectorPart
    implements SelectionEditorTool.NodeSelectorPart {

  private Composite control;
  private GraphEdgeMatcherEditorPart edgeMatcherEditor;
  private boolean recursiveSearch;

  @Override
  public PathMatcher getNodeSelector() {
    return edgeMatcherEditor.createPathMatcherModel(recursiveSearch);
  }

  @Override
  public Composite createControl(
      Composite parent, int style, ViewEditor viewEditor) {
    control = new Composite(parent, style);
    control.setLayout(new GridLayout());

    // Use a standard RelationPicker part
    edgeMatcherEditor = new GraphEdgeMatcherEditorPart();
    Control relationsPanel = edgeMatcherEditor.getControl(control);
    relationsPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    // Add a recursive selection checkbox
    final Button recursiveSelect = new Button(control, SWT.CHECK);
    recursiveSelect.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
    recursiveSelect.setText("Recursive apply expansion");

    recursiveSelect.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        recursiveSearch = recursiveSelect.getSelection();
      }
    });

    return control;
  }

  @Override
  public void updateControl(ViewEditor viewEditor) {
    edgeMatcherEditor.updateTable(viewEditor.getBuiltinAnalysisPlugins());

    GraphEdgeMatcherDescriptor selectedRelSet = viewEditor.getTreeEdgeMatcher();
    List<GraphEdgeMatcherDescriptor> choices = viewEditor.getTreeEdgeMatcherChoices();
    edgeMatcherEditor.updateEdgeMatcherSelector(selectedRelSet, choices );
  }
}
