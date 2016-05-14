/*
 * Copyright 2007 The Depan Project Authors
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

package com.google.devtools.depan.matchers.eclipse.ui.widgets;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.platform.PlatformResources;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import java.util.List;

/**
 * A GUI tool to display an edge matcher, with a selector for forward
 * and backward directions. This is a editable table (directions are editable,
 * content and names are not).
 *
 * To use it, call {@link #getControl(Composite)} to retrieve the widget.
 *
 * @since 2015 Built from pieces of the legacy RelationshipPicker.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class GraphEdgeMatcherEditorPart {

  /////////////////////////////////////
  // UX Elements

  private GraphEdgeMatcherRelationTableEditor editor;

  /////////////////////////////////////

  /**
   * return a {@link Control} for this widget, containing every useful buttons,
   * labels, table... necessary to use this component.
   *
   * @param parent the parent.
   * @return a {@link Control} containing this widget.
   */
  public Control getControl(Composite parent) {
    // component
    Composite panel = new Composite(parent, SWT.BORDER);
    panel.setLayout(new GridLayout());

    // components inside the panel
    Composite allRels = setupAllRelsButtons(panel);
    allRels.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    editor = new GraphEdgeMatcherRelationTableEditor();
    TableViewer tableViewer = editor.setupTableViewer(panel);
    tableViewer.getTable().setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));

    Composite toggles = setupRelationToggles(panel);
    toggles.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    return panel;
  }

  public void registerModificationListener(
      ModificationListener<Relation, Boolean> listener) {
    editor.registerModificationListener(listener);
  }

  public void unregisterModificationListener(
      ModificationListener<Relation, Boolean> listener) {
    editor.unregisterModificationListener(listener);
  }

  private Composite setupAllRelsButtons(Composite parent) {
    Composite result = new Composite(parent, SWT.NONE);
    result.setLayout(new GridLayout(2, false));

    Button reverseAll = new Button(result, SWT.PUSH);
    reverseAll.setText("Reverse all lines");
    reverseAll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Button invertAll = new Button(result, SWT.PUSH);
    invertAll.setText("Invert all lines");
    invertAll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    reverseAll.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        editor.reverseRelations();
      }
    });

    invertAll.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        editor.invertRelations();
      }
    });

    return result;
  }

  private Composite setupRelationToggles(Composite parent) {
    Composite result = new Composite(parent, SWT.NONE);
    GridLayout togglesLayout = new GridLayout(1, false);
    togglesLayout.verticalSpacing = 0;
    result.setLayout(togglesLayout);

    Label optionsLabel = new Label(result, SWT.NONE);
    optionsLabel.setText("For selected lines:");
    optionsLabel.setLayoutData(
        new GridData(SWT.LEFT, SWT.FILL, false, false));

    // Relation operations
    Composite group = new Composite(result, SWT.NONE);
    group.setLayout(new GridLayout(2, false));
    group.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    Button groupReverse = new Button(group, SWT.PUSH);
    groupReverse.setText("Reverse");
    groupReverse.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Button groupInvert = new Button(group, SWT.PUSH);
    groupInvert.setText("Invert");
    groupInvert.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    groupReverse.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        editor.reverseSelectedRelations();
      }
    });
    groupInvert.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        editor.invertSelectedRelations();
      }
    });

    // Toggle operations
    Composite toggles = new Composite(result, SWT.NONE);
    toggles.setLayout(new GridLayout(8, false));
    toggles.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    Label forward = new Label(toggles, SWT.NONE);
    forward.setText("Forward");
    forward.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

    Button forwardAll = new Button(toggles, SWT.PUSH);
    forwardAll.setImage(PlatformResources.IMAGE_ON);
    forwardAll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Button forwardNone = new Button(toggles, SWT.PUSH);
    forwardNone.setImage(PlatformResources.IMAGE_OFF);
    forwardNone.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Button forwardInvert = new Button(toggles, SWT.PUSH);
    forwardInvert.setText("Invert");
    forwardInvert.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Label backward = new Label(toggles, SWT.NONE);
    backward.setText("Backward");
    backward.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

    Button backwardAll = new Button(toggles, SWT.PUSH);
    backwardAll.setImage(PlatformResources.IMAGE_ON);
    backwardAll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Button backwardNone = new Button(toggles, SWT.PUSH);
    backwardNone.setImage(PlatformResources.IMAGE_OFF);
    backwardNone.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Button backwardInvert = new Button(toggles, SWT.PUSH);
    backwardInvert.setText("Invert");
    backwardInvert.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    // actions
    forwardAll.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        editor.setForwardSelectedRelations(true);
      }
    });

    forwardNone.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        editor.setForwardSelectedRelations(false);
      }
    });

    forwardInvert.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        editor.invertForwardSelectedRelations();
      }
    });

    backwardAll.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        editor.setReverseSelectedRelations(true);
      }
    });

    backwardNone.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        editor.setReverseSelectedRelations(false);
      }
    });

    backwardInvert.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        editor.invertReverseSelectedRelations();
      }
    });
    return result;
  }

  /**
   * Fill the list with {@link Relation}s.
   */
  public void updateTable(List<Relation> relations) {
    editor.updateTableRows(relations);
  }
}
