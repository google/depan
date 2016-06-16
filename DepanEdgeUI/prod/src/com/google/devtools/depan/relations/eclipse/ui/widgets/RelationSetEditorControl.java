/*
 * Copyright 2015 The Depan Project Authors
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

package com.google.devtools.depan.relations.eclipse.ui.widgets;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.graph.registry.RelationRegistry;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.models.RelationSetRepository;

import com.google.common.collect.Lists;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import java.util.Collection;
import java.util.List;

/**
 * A GUI tool to display relations.
 *
 * To use it, call {@link #getControl(Composite)} to retrieve the widget.
 *
 * Based heavily on the legacy RelationshipPicker and the RelationPickerTool
 * types.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class RelationSetEditorControl extends Composite {

  /////////////////////////////////////
  // UX Elements
  /**
   * Manages the relation set data.
   */
  private RelationSetTableControl viewer;

  /**
   * The {@link RelationshipSetSelector} to choose a named set.
   */
  private RelationSetSelectorControl relationSetSelector;

  public RelationSetEditorControl(Composite parent) {
    super(parent, SWT.NONE);

    GridLayout gridLayout = new GridLayout();
    gridLayout.verticalSpacing = 10;
    setLayout(gridLayout);

    Composite commands = setupCommandButtons(this);
    commands.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    viewer = new RelationSetTableControl(this);
    viewer.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));
  }

  public void setInput(Collection<Relation> relations) {
    viewer.setInput(relations);
  }

  public void setRelationSetRepository(
      RelationSetRepository visRepo) {
    viewer.setVisibiltyRepository(visRepo);
  }

  public void removeRelationSetRepository(
      RelationSetRepository relSetRepo) {
    viewer.removeRelSetRepository(relSetRepo);
  }

  private Composite setupCommandButtons(Composite parent) {
    Composite result = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    result.setLayout(layout);

    Composite pickerRegion = setupRelationSetSelector(result);
    pickerRegion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Composite selectVis =  setupSelectionVisible(result);
    selectVis.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    Composite tableVis =  setupTableVisible(result);
    tableVis.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    return result;
  }

  private Composite setupRelationSetSelector(Composite parent) {
    Composite region = new Composite(parent, SWT.None);
    region.setLayout(new GridLayout(3, false));

    Label pickerLabel = new Label(region, SWT.NONE);
    pickerLabel.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));
    pickerLabel.setText("Select Relations: ");

    relationSetSelector = new RelationSetSelectorControl(region);
    relationSetSelector.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    relationSetSelector.addChangeListener(
        new RelationSetSelectorControl.SelectorListener() {
      @Override
      public void selectedRelationSetChanged(RelationSetDescriptor relationSet) {
        handleRelSetPickerChange(relationSet);
      }
    });

    Button reverse = new Button(region, SWT.PUSH);
    reverse.setText("Reverse selection");
    reverse.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    reverse.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        viewer.invertSelectedRelations();

        // Invalidate relation set on manual relation selection
        relationSetSelector.clearSelection();
      }
    });

    return region;
  }

  private Composite setupSelectionVisible(Composite parent) {
    Composite result = new Composite(parent, SWT.None);
    GridLayout layout = new GridLayout(3, false);
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    result.setLayout(layout);

    Button check = new Button(result, SWT.PUSH);
    check.setText("check selected");
    check.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    check.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        viewer.checkVisibleSelection();
      }
    });

    Button clear = new Button(result, SWT.PUSH);
    clear.setText("clear selected");
    clear.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    clear.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        viewer.clearVisibleSelection();
      }
    });

    Button invert = new Button(result, SWT.PUSH);
    invert.setText("invert selected");
    invert.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    invert.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        viewer.invertVisibleSelection();
      }
    });

    return result;
  }

  private Composite setupTableVisible(Composite parent) {
    Composite result = new Composite(parent, SWT.None);
    GridLayout layout = new GridLayout(3, false);
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    result.setLayout(layout);

    Button check = new Button(result, SWT.PUSH);
    check.setText("check all");
    check.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    check.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        viewer.checkVisibleTable();
      }
    });

    Button clear = new Button(result, SWT.PUSH);
    clear.setText("clear all");
    clear.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    clear.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        viewer.clearVisibleTable();
      }
    });

    Button invert = new Button(result, SWT.PUSH);
    invert.setText("invert all");
    invert.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    invert.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        viewer.invertVisibleTable();
      }
    });

    return result;
  }

  /////////////////////////////////////

  /**
   * Change listener for RelationSetPickerControl.
   */
  private void handleRelSetPickerChange(RelationSetDescriptor relationSet) {
    if (null != relationSet) {
      selectRelations(buildRelations(relationSet.getRelationSet()));
    }
  }

  /**
   * Select the lines described by the supplied collection of
   * {@link Relation}s.
   */
  public void selectRelations(Collection<Relation> relations) {
    ISelection selection = new StructuredSelection(relations.toArray());
    viewer.setSelection(selection);
  }

  private Collection<Relation> buildRelations(RelationSet relationSet) {
    Collection<Relation> result = Lists.newArrayList();
    for (Relation relation : RelationRegistry.getRegistryRelations()) {
      if (relationSet.contains(relation)) {
        result.add(relation);
      }
    }
    return result;
  }

  /**
   * Fill the list with {@link Relation}s.
   */
  public void updateTable(Collection<Relation> relations) {
    viewer.setInput(relations);
    viewer.refresh(false);
  }

  public void setRelationSetSelectorInput(
      RelationSetDescriptor selectedRelSet,
      List<RelationSetDescriptor> choices) {

    relationSetSelector.setInput(selectedRelSet, choices);
  }
}
