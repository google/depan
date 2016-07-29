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

import com.google.devtools.depan.graph.api.EdgeMatcher;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.GraphEdgeMatcher;
import com.google.devtools.depan.platform.PlatformResources;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import java.util.Collection;

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
public class EdgeMatcherEditorControl extends Composite {

  /////////////////////////////////////
  // UX Elements

  private EdgeMatcherTableControl editor;

  /////////////////////////////////////
  // Public methods

  /**
   * return a {@link Control} for this widget, containing every useful buttons,
   * labels, table... necessary to use this component.
   *
   * @param parent the parent.
   * @return a {@link Control} containing this widget.
   */
  public EdgeMatcherEditorControl(Composite parent) {
    super(parent, SWT.BORDER);
    setLayout(Widgets.buildContainerLayout(1));

    Composite allRels = setupAllRelsButtons(this);
    allRels.setLayoutData(Widgets.buildHorzFillData());

    editor = new EdgeMatcherTableControl(this);
    editor.setLayoutData(Widgets.buildGrabFillData());

    Composite toggles = setupRelationToggles(this);
    toggles.setLayoutData(Widgets.buildHorzFillData());
  }

  public void registerModificationListener(
      ModificationListener<Relation, Boolean> listener) {
    editor.registerModificationListener(listener);
  }

  public void unregisterModificationListener(
      ModificationListener<Relation, Boolean> listener) {
    editor.unregisterModificationListener(listener);
  }

  /**
   * Fill the list with {@link Relation}s.
   */
  public void updateTable(Collection<Relation> relations) {
    editor.updateTableRows(relations);
  }

  public void updateEdgeMatcher(EdgeMatcher<String> edgeMatcher) {
    editor.updateEdgeMatcher(edgeMatcher);
  }

  public GraphEdgeMatcher buildEdgeMatcher() {
    return editor.buildEdgeMatcher();
  }

  /////////////////////////////////////
  // UX Setup

  private Composite setupAllRelsButtons(Composite parent) {
    Composite result = Widgets.buildGridContainer(parent, 3);

    Button clearAll =
        Widgets.buildGridPushButton(result, "Clear all lines");
    clearAll.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        editor.clearRelations();
      }
    });

    Button reverseAll =
        Widgets.buildGridPushButton(result, "Reverse all lines");
    reverseAll.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        editor.reverseRelations();
      }
    });

    Button invertAll = Widgets.buildGridPushButton(result, "Invert all lines");
    invertAll.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        editor.invertRelations();
      }
    });

    return result;
  }

  @SuppressWarnings("unused")
  private Composite setupRelationToggles(Composite parent) {
    Composite result = Widgets.buildGridContainer(parent, 1);

    Label optionsLabel = Widgets.buildCompactLabel(result, "For selected lines:");

    Composite relOps = setupRelationOps(result);
    relOps.setLayoutData(Widgets.buildHorzFillData());

    Composite toggles = setupToggleOps(result);
    toggles.setLayoutData(Widgets.buildHorzFillData());

    return result;
  }

  private Composite setupRelationOps(Composite parent) {
    Composite result = Widgets.buildGridContainer(parent, 2);

    Button groupReverse = Widgets.buildGridPushButton(result, "Reverse");
    groupReverse.setLayoutData(Widgets.buildHorzFillData());

    Button groupInvert = Widgets.buildGridPushButton(result, "Invert");
    groupInvert.setLayoutData(Widgets.buildHorzFillData());

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
    return result;
  }

  private Composite setupToggleOps(Composite parent) {
    Composite result = Widgets.buildGridContainer(parent, 8);

    Label forward = new Label(result, SWT.NONE);
    forward.setText("Forward");
    forward.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

    Button forwardAll = Widgets.buildGridPushButton(result);
    forwardAll.setImage(PlatformResources.IMAGE_ON);

    Button forwardNone = Widgets.buildGridPushButton(result);
    forwardNone.setImage(PlatformResources.IMAGE_OFF);

    Button forwardInvert = Widgets.buildGridPushButton(result, "Invert");

    Label backward = new Label(result, SWT.NONE);
    backward.setText("Backward");
    backward.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

    Button backwardAll = Widgets.buildGridPushButton(result);
    backwardAll.setImage(PlatformResources.IMAGE_ON);

    Button backwardNone = Widgets.buildGridPushButton(result);
    backwardNone.setImage(PlatformResources.IMAGE_OFF);

    Button backwardInvert = Widgets.buildGridPushButton(result, "Invert");

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
}
