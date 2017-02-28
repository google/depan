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

import com.google.devtools.depan.nodes.filters.eclipse.ui.plugins.ContextualFilterContributor;
import com.google.devtools.depan.nodes.filters.eclipse.ui.plugins.ContextualFilterContributors;
import com.google.devtools.depan.nodes.filters.eclipse.ui.plugins.ContextualFilterRegistry;
import com.google.devtools.depan.nodes.filters.model.ContextualFilter;
import com.google.devtools.depan.nodes.filters.model.ContextualFilterDocument;
import com.google.devtools.depan.nodes.filters.sequence.SteppingFilter;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;

import com.google.common.collect.Lists;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import java.util.List;

/**
 * Enhances {@link SteppingFilter} editing with a
 * {@link FilterTableControl}.
 * 
 * Show a table of {@link ContextualFilter}s, suitable for editing.
 * 
 * This class could be named equivalently as
 * {@code SteppingFilterEditorControl}.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class FilterTableEditorControl
    extends FilterEditorControl<SteppingFilter> {

  /////////////////////////////////////
  // UX Elements

  private FilterTableControl filterControl;

  private FilterPluginsListControl filterChoice;

  /////////////////////////////////////
  // Public methods

  public FilterTableEditorControl(Composite parent) {
    super(parent);
  }

  /////////////////////////////////////
  // Control management

  @Override
  protected void updateControls() {
    filterControl.setInput(getFilter().getSteps());
  }

  @Override
  protected void setupControls(Composite parent) {
    filterControl = new FilterTableControl(this);
    filterControl.setLayoutData(Widgets.buildGrabFillData());

    Composite commands = setupCommandButtons(this);
    commands.setLayoutData(Widgets.buildHorzFillData());
  }

  /////////////////////////////////////
  // UX Setup

  private Composite setupCommandButtons(Composite parent) {
    Composite result = Widgets.buildGridContainer(parent, 2);

    Composite edits = setupEditButtons(result);
    edits.setLayoutData(Widgets.buildHorzFillData());

    Composite order = setupOrderButtons(result);
    order.setLayoutData(Widgets.buildTrailFillData());
    return result;
  }

  private Composite setupEditButtons(Composite parent) {
    Composite result = Widgets.buildGridContainer(parent, 6);

    Button editButton = Widgets.buildCompactPushButton(result, "Edit...");
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
    filterChoice.setLayoutData(new GridData());

    Button addButton = Widgets.buildCompactPushButton(result, "Add");
    addButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        addNewFilter();
      }
    });

    Button wrapButton = Widgets.buildCompactPushButton(result, "Wrap");
    wrapButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        wrapFilter();
      }
    });

    Button importButton = Widgets.buildCompactPushButton(result, "Import");
    importButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        importFilter();
      }
    });

    Button removeButton = Widgets.buildCompactPushButton(result, "Remove");
    removeButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        removeFilter();
      }
    });

    return result;
  }

  private Composite setupOrderButtons(Composite parent) {
    Composite result = Widgets.buildGridContainer(parent, 2);

    Button upButton = Widgets.buildTrailPushButton(result, "Up");
    upButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        moveFilterUp();
      }
    });

    Button downButton = Widgets.buildTrailPushButton(result, "Down");
    downButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        moveFilterDown();
      }
    });

    return result;
  }

  /////////////////////////////////////
  // Editor actions

  private void addNewFilter() {
    if (null == getFilter()) {
      return;
    }
    ContextualFilterContributor<? extends ContextualFilter> contrib =
        filterChoice.getChoice();
    if (null == contrib) {
      return;
    }

    ContextualFilter filter = contrib.createElementFilter();
    List<ContextualFilter> result = getUpdatableSteps();
    result.add(filter);

    updateSteps(result);
  }


  private void importFilter() {
    if (null == getFilter()) {
      return;
    }
    ContextualFilterDocument filterDoc =
        ContextualFilterSaveLoadConfig.CONFIG.loadResource(
            getShell(), getProject());
    if (null == filterDoc) {
      return;
    }

    List<ContextualFilter> result = getUpdatableSteps();
    result.add(filterDoc.getInfo());

    updateSteps(result);
  }

  private void wrapFilter() {
    List<ContextualFilter> target = filterControl.getSelectedFilters();
    if (target.isEmpty()) {
      return;
    }
    ContextualFilterContributor<? extends ContextualFilter> contrib =
        filterChoice.getChoice();
    if (null == contrib) {
      return;
    }
    SteppingFilter steps = getFilter();
    if (null == steps) {
      return;
    }

    ContextualFilter filter = target.get(0);
    List<ContextualFilter> result = getUpdatableSteps();
    int index = result.indexOf(filter);

    ContextualFilter wrapper =
        ContextualFilterContributors.createFilter(contrib, target);
    result.set(index, wrapper);

    updateSteps(result);
  }

  private void editFilter() {
    List<ContextualFilter> target = filterControl.getSelectedFilters();
    if (target.isEmpty()) {
      return;
    }
    SteppingFilter steps = getFilter();
    if (null == steps) {
      return;
    }

    List<ContextualFilter> result = getUpdatableSteps();
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
    SteppingFilter steps = getFilter();
    if (null == steps) {
      return;
    }

    List<ContextualFilter> result = getUpdatableSteps();
    List<ContextualFilter> target = filterControl.getSelectedFilters();
    result.removeAll(target);

    updateSteps(result);
  }

  private void moveFilterUp() {
    SteppingFilter steps = getFilter();
    if (null == steps) {
      return;
    }
    List<ContextualFilter> target = filterControl.getSelectedFilters();
    if (target.isEmpty()) {
      return;
    }

    List<ContextualFilter> result = getUpdatableSteps();
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
    SteppingFilter steps = getFilter();
    if (null == steps) {
      return;
    }
    List<ContextualFilter> target = filterControl.getSelectedFilters();
    if (target.isEmpty()) {
      return;
    }

    List<ContextualFilter> result = getUpdatableSteps();
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

  private ContextualFilter editFilter(ContextualFilter filter) {
    ContextualFilterContributor<?> contrib =
        ContextualFilterRegistry.findRegistryContributor(filter);
    if (null == contrib) {
      return null;
    }

    FilterEditorDialog<?> dialog =
        contrib.buildEditorDialog(getShell(), filter, getModel(), getProject());
    if (null == dialog) {
      return null;
    }
    if (Dialog.OK == dialog.open()) {
      return dialog.getResult();
    }
    return null;
  }

  private List<ContextualFilter> getUpdatableSteps() {
    return Lists.newArrayList(getFilter().getSteps());
  }

  /**
   * Only the "steps"-subset of our setInput() method.
   */
  private void updateSteps(List<ContextualFilter> steps) {
    SteppingFilter filter = getFilter();
    filter.setSteps(steps);

    // No need to update name or summary.
    filterControl.setInput(filter.getSteps());
  }
}
