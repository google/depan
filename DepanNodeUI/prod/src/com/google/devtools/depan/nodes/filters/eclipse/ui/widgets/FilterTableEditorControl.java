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

package com.google.devtools.depan.nodes.filters.eclipse.ui.widgets;

import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.nodes.filters.eclipse.ui.plugins.ContextualFilterContributor;
import com.google.devtools.depan.nodes.filters.eclipse.ui.plugins.ContextualFilterContributors;
import com.google.devtools.depan.nodes.filters.eclipse.ui.plugins.ContextualFilterRegistry;
import com.google.devtools.depan.nodes.filters.model.ContextualFilter;
import com.google.devtools.depan.nodes.filters.sequence.SteppingFilter;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import java.util.List;

/**
 * Show a table of {@link ContextualFilter}, suitable for editing.
 * 
 * This class could be named equivalently as {@code SteppingFilterEditorControl}.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class FilterTableEditorControl extends Composite {

  /////////////////////////////////////
  // UX Elements

  private BasicFilterEditorControl basicControl;

  private FilterTableControl filterControl;

  private SteppingFilter editFilter;

  private FilterPluginsListControl filterChoice;

  private DependencyModel model;

  /////////////////////////////////////
  // Public methods

  public FilterTableEditorControl(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(Widgets.buildContainerLayout(1));

    // Handle common stuff, like name and summary
    basicControl = new BasicFilterEditorControl(this);
    basicControl.setLayoutData(Widgets.buildHorzFillData());

    filterControl = new FilterTableControl(this);
    filterControl.setLayoutData(Widgets.buildGrabFillData());

    Composite commands = setupCommandButtons(this);
    commands.setLayoutData(Widgets.buildHorzFillData());
  }

  public void setInput(SteppingFilter editFilter, DependencyModel model) {
    this.editFilter = editFilter;
    this.model = model;

    updateControls();
  }

  /////////////////////////////////////
  // UX Setup

  private Composite setupCommandButtons(Composite parent) {
    Composite result = Widgets.buildGridContainer(parent, 2);

    Composite actions = setupActionButtons(result);
    actions.setLayoutData(Widgets.buildHorzFillData());

    Composite xfers =  setupSaveButtons(result);
    xfers.setLayoutData(Widgets.buildTrailFillData());

    return result;
  }

  private Composite setupActionButtons(Composite parent) {
    Composite result = Widgets.buildGridContainer(parent, 7);

    Button editButton = Widgets.buildGridPushButton(result, "Edit...");
    editButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        editFilter();
      }
    });

    // Alternative: Add goes to a picker dialog ..
    // Alternative: Add or wrap based on empty selection.
    // Alternative: list in filterChoice varies by filter's form and number
    // of items selected.
    filterChoice = new FilterPluginsListControl(result);
    filterChoice.setLayoutData(Widgets.buildHorzFillData());

    Button addButton = Widgets.buildGridPushButton(result, "Add");
    addButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        addNewFilter();
      }
    });

    Button wrapButton = Widgets.buildGridPushButton(result, "Wrap");
    wrapButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        wrapFilter();
      }
    });

    Button removeButton = Widgets.buildGridPushButton(result, "Remove");
    removeButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        removeFilter();
      }
    });

    Button upButton = Widgets.buildGridPushButton(result, "Up");
    upButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        moveFilterUp();
      }
    });

    Button downButton = Widgets.buildGridPushButton(result, "Down");
    downButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        moveFilterDown();
      }
    });

    return result;
  }

  private Composite setupSaveButtons(Composite parent) {
    Composite result = Widgets.buildGridContainer(parent, 2);

    Button saveButton = Widgets.buildGridPushButton(result, "Save...");
    saveButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        saveFilterTable();
      }
    });

    Button loadButton = Widgets.buildGridPushButton(result, "Load...");
    loadButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        loadFilterTable();
      }
    });

    return result;
  }

  /////////////////////////////////////
  // Persistence integration

  private void saveFilterTable() {
    // TODO Auto-generated method stub
    
  }

  private void loadFilterTable() {
    // TODO Auto-generated method stub
    
  }

  /////////////////////////////////////
  // Editor actions

  private void addNewFilter() {
    if (null == editFilter) {
      return;
    }
    ContextualFilterContributor<? extends ContextualFilter> contrib =
        filterChoice.getChoice();
    if (null == contrib) {
      return;
    }
    ContextualFilter result = contrib.createElementFilter();
    List<ContextualFilter> steps = appendStep(result);

    updateSteps(steps);
  }

  private void wrapFilter() {
    if (null == editFilter) {
      return;
    }
    ContextualFilterContributor<? extends ContextualFilter> contrib =
        filterChoice.getChoice();
    if (null == contrib) {
      return;
    }
    List<ContextualFilter> target = filterControl.getSelectedFilters();
    ContextualFilter wrapper =
        ContextualFilterContributors.createFilter(contrib, target);

    // TODO: Inject at correct location in list 
    List<ContextualFilter> steps = appendStep(wrapper);
    updateSteps(steps);
  }

  private void editFilter() {
    if (null == editFilter) {
      return;
    }
    List<ContextualFilter> result = editFilter.getSteps();
    List<ContextualFilter> target = filterControl.getSelectedFilters();
    if (target.isEmpty()) {
      return;
    }
    ContextualFilter filter = target.get(0);
    int index = result.indexOf(filter);
    ContextualFilter update = editFilter(filter);
    if (null == update) {
      return;
    }
    result.set(index, update);
    updateSteps(result);
  }

  private void removeFilter() {
    if (null == editFilter) {
      return;
    }
    List<ContextualFilter> result = editFilter.getSteps();
    List<ContextualFilter> target = filterControl.getSelectedFilters();
    result.removeAll(target);
    updateSteps(result);
  }

  private void moveFilterUp() {
    if (null == editFilter) {
      return;
    }
    List<ContextualFilter> result = editFilter.getSteps();
    List<ContextualFilter> target = filterControl.getSelectedFilters();
    if (target.isEmpty()) {
      return;
    }
    ContextualFilter above = target.get(0);
    int index = result.indexOf(above);
    if (index < 1) {
      return;
    }
    result.removeAll(target);
    result.addAll(index - 1, target);

    updateSteps(result);
  }

  private void moveFilterDown() {
    if (null == editFilter) {
      return;
    }
    List<ContextualFilter> result = editFilter.getSteps();
    List<ContextualFilter> target = filterControl.getSelectedFilters();
    if (target.isEmpty()) {
      return;
    }
    int bound = target.size();
    ContextualFilter below = target.get(bound - 1);
    int index = result.indexOf(below);
    if ((index + 1) < bound) {
      return;
    }
    result.removeAll(target);
    result.addAll(index + 1, result);

    updateSteps(result);
  }

  /////////////////////////////////////
  // Support methods

  /**
   * @param filter
   */
  private ContextualFilter editFilter(ContextualFilter filter) {
    ContextualFilterContributor<?> contrib =
        ContextualFilterRegistry.findRegistryContributor(filter);
    if (null == contrib) {
      return null;
    }

    FilterEditorDialog<?> dialog =
        contrib.buildEditorDialog(getShell(), filter, model);
    if (Dialog.OK == dialog.open()) {
      return dialog.getFilter();
    }
    return null;
  }

  /**
   * Only the "steps"-subset of our setInput() method.
   * @return 
   */
  private List<ContextualFilter> appendStep(ContextualFilter step) {
    List<ContextualFilter> result = editFilter.getSteps();
    result.add(step);
    return result;
  }

  /**
   * Only the "steps"-subset of our setInput() method.
   */
  private void updateSteps(List<ContextualFilter> steps) {
    editFilter.setSteps(steps);

    updateControls();
  }

  /**
   * Update the controls to the current status of the edit filter.
   */
  private void updateControls() {
    basicControl.setInput(editFilter);
    filterControl.setInput(editFilter.getSteps());
  }
}
