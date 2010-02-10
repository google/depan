/*
 * Copyright 2008 Google Inc.
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

import com.google.devtools.depan.eclipse.editors.ViewEditor;
import com.google.devtools.depan.eclipse.utils.RelationshipPicker;
import com.google.devtools.depan.eclipse.utils.relsets.RelSetDescriptor;
import com.google.devtools.depan.filters.PathMatcher;
import com.google.devtools.depan.model.RelationshipSet;

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
  private RelationshipPicker relationshipPicker;
  private boolean recursiveSearch;

  @Override
  public PathMatcher getNodeSelector() {
    relationshipPicker.createPathMatcherModel(recursiveSearch);
    return relationshipPicker.getPathMatcherModel();
  }

  @Override
  public Composite createControl(
      Composite parent, int style, ViewEditor viewEditor) {
    control = new Composite(parent, style);
    control.setLayout(new GridLayout());

    // Use a standard RelationPicker part
    relationshipPicker = new RelationshipPicker();
    Control relationsPanel = relationshipPicker.getControl(control);
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
    relationshipPicker.updateTable(viewEditor.getBuiltinAnalysisPlugins());

    RelationshipSet selectedRelSet = viewEditor.getContainerRelSet();
    List<RelSetDescriptor> choices = viewEditor.getRelSetChoices();
    relationshipPicker.updateRelSetPicker(selectedRelSet, choices );
  }
}
