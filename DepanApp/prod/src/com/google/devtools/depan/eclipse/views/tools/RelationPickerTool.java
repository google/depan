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

package com.google.devtools.depan.eclipse.views.tools;

import com.google.devtools.depan.eclipse.editors.ViewEditor;
import com.google.devtools.depan.eclipse.plugins.SourcePlugin;
import com.google.devtools.depan.eclipse.plugins.SourcePluginRegistry;
import com.google.devtools.depan.eclipse.utils.RelationshipSelectorListener;
import com.google.devtools.depan.eclipse.utils.RelationshipSetPickerControl;
import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.utils.relsets.RelSetDescriptor;
import com.google.devtools.depan.eclipse.views.ViewEditorTool;
import com.google.devtools.depan.eclipse.views.tools.RelEditorTableView.RelPropRepository;
import com.google.devtools.depan.eclipse.wizards.NewRelationshipSetWizard;
import com.google.devtools.depan.graph.api.DirectedRelationFinder;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.api.RelationFinder;
import com.google.devtools.depan.graph.basic.MultipleRelationFinder;
import com.google.devtools.depan.graph.basic.ReversedDirectedRelationFinder;
import com.google.devtools.depan.model.RelationshipSet;
import com.google.devtools.depan.view.EdgeDisplayProperty;

import com.google.common.collect.Lists;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Tool for selecting relations that have to be shown.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class RelationPickerTool extends ViewEditorTool {

  /**
   * The {@link RelationshipSetSelector} to choose a named set.
   */
  private RelationshipSetPickerControl relSetPicker;

  /**
   * Shell used to open dialogs (SaveAs dialog in this case).
   */
  protected Shell shell = null;

  /**
   * Table of relation data.
   */
  private TableViewer table;

  @Override
  public Image getIcon() {
    return Resources.IMAGE_RELATIONPICKER;
  }

  @Override
  public String getName() {
    return Resources.NAME_RELATIONPICKERTOOL;
  }

  @Override
  protected void clearControls() {
    updateView();
  }

  @Override
  protected void updateControls() {
    super.updateControls();

    // RelationSet picker first
    RelationshipSet selectedRelSet = getEditor().getDisplayRelationSet();
    List<RelSetDescriptor> choices = getEditor().getRelSetChoices();
    relSetPicker.setInput(selectedRelSet, choices);

    updateView();
  }

  @Override
  public Control setupComposite(Composite parent) {
    this.shell = parent.getShell();

    Composite topLevel = new Composite(parent, SWT.NONE);
    GridLayout gridLayout = new GridLayout();
    gridLayout.verticalSpacing = 10;
    topLevel.setLayout(gridLayout);

    Composite commands = setupCommandButtons(topLevel);
    commands.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    table = setupRelationList(topLevel);
    table.getControl().setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));

    Composite saves = setupSaveButtons(topLevel);
    saves.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    return topLevel;
  }

  private Composite setupCommandButtons(Composite parent) {
    Composite result = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    result.setLayout(layout);

    Composite pickerRegion = setupRelationPicker(result);
    pickerRegion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Composite selectVis =  setupSelectionVisible(result);
    selectVis.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    Composite tableVis =  setupTableVisible(result);
    tableVis.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    return result;
  }

  private Composite setupRelationPicker(Composite parent) {
    Composite region = new Composite(parent, SWT.None);
    region.setLayout(new GridLayout(3, false));

    Label pickerLabel = RelationshipSetPickerControl.createPickerLabel(region);
    pickerLabel.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    relSetPicker = new RelationshipSetPickerControl(region);
    relSetPicker.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    relSetPicker.addChangeListener(new RelationshipSelectorListener() {
      @Override
      public void selectedSetChanged(RelationshipSet set) {
        handleRelSetPickerChange(set);
      }
    });

    Button reverse = new Button(region, SWT.PUSH);
    reverse.setText("Reverse selection");
    reverse.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    reverse.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        reverseSelection();

        // Invalidate relation set on manual relation selection
        relSetPicker.clearSelection();
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
        checkVisibleSelection();
      }
    });

    Button clear = new Button(result, SWT.PUSH);
    clear.setText("clear selected");
    clear.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    clear.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        clearVisibleSelection();
      }
    });

    Button invert = new Button(result, SWT.PUSH);
    invert.setText("invert selected");
    invert.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    invert.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        invertVisibleSelection();
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
        checkVisibleTable();
      }
    });

    Button clear = new Button(result, SWT.PUSH);
    clear.setText("clear all");
    clear.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    clear.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        clearVisibleTable();
      }
    });

    Button invert = new Button(result, SWT.PUSH);
    invert.setText("invert all");
    invert.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    invert.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        invertVisibleTable();
      }
    });

    return result;
  }

  private TableViewer setupRelationList(Composite parent) {
    RelEditorTableView viewer = new RelEditorTableView(
        new RelPropRepository() {
          @Override
          public EdgeDisplayProperty getDisplayProperty(Relation rel) {
            if (!hasEditor()) {
              return null;
            }

            ViewEditor editor = getEditor();
            return editor.getRelationProperty(rel);
          }

          @Override
          public void setDisplayProperty(Relation rel, EdgeDisplayProperty prop) {
            if (!hasEditor()) {
              return;
            }

            ViewEditor editor = getEditor();
            editor.setRelationProperty(rel, prop);
          }
        });

    TableViewer result = viewer.setupViewer(parent);
    return result;
  }

  private Composite setupSaveButtons(Composite parent) {
    Composite result = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout(2, false);
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    result.setLayout(layout);

    Button saveRels = new Button(result, SWT.PUSH);
    saveRels.setText("Save selected relations as...");
    saveRels.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    saveRels.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        saveSelection();
      }
    });

    Button saveProps = new Button(result, SWT.PUSH);
    saveProps.setText("Save selected properties as...");
    saveProps.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    saveProps.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        saveSelection();
      }
    });

    return result;
  }

  /////////////////////////////////////
  // Handlers for visibility events

  private void clearVisibleSelection() {
    if (!hasEditor()) {
      return;
    }

    Collection<Relation> relations = getSelectedRelations();
    clearRelations(relations);
  }

  private void invertVisibleSelection() {
    if (!hasEditor()) {
      return;
    }

    Collection<Relation> relations = getSelectedRelations();
    invertRelations(relations);
  }

  private void checkVisibleSelection() {
    if (!hasEditor()) {
      return;
    }

    Collection<Relation> relations = getSelectedRelations();
    checkRelations(relations);
  }

  private void clearVisibleTable() {
    if (!hasEditor()) {
      return;
    }

    Collection<Relation> relations = getAllRelations();
    clearRelations(relations);
  }

  private void invertVisibleTable() {
    if (!hasEditor()) {
      return;
    }

    Collection<Relation> relations = getAllRelations();
    invertRelations(relations);
  }

  private void checkVisibleTable() {
    if (!hasEditor()) {
      return;
    }

    Collection<Relation> relations = getAllRelations();
    checkRelations(relations);
  }

  private Collection<Relation> getSelectedRelations() {
    ISelection sel = table.getSelection();
    if (!(sel instanceof IStructuredSelection)) {
      return Collections.emptyList();
    }
    IStructuredSelection selection = (IStructuredSelection) sel;
    List<Relation> result =
        Lists.newArrayListWithExpectedSize(selection.size());
    for (Object item : selection.toList()) {
      if (item instanceof Relation) {
        result.add((Relation) item);
      }
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  private Collection<Relation> getAllRelations() {
    return (Collection<Relation>) table.getInput();
  }

  private void clearRelations(Collection<Relation> relations) {
    for (Relation relation : relations) {
      EdgeDisplayProperty prop = loadRelationProperty(relation);
      if (null == prop) {
        continue;
      }
      prop.setVisible(false);
      saveRelationProperty(relation, prop);
      table.update(relation, RelEditorTableView.UPDATE_VISIBLE);
    }
  }

  private void invertRelations(Collection<Relation> relations) {
    for (Relation relation : relations) {
      EdgeDisplayProperty prop = getRelationProperty(relation);
      prop.setVisible(!prop.isVisible());
      saveRelationProperty(relation, prop);
      table.update(relation, RelEditorTableView.UPDATE_VISIBLE);
    }
  }

  private void checkRelations(Collection<Relation> relations) {
    for (Relation relation : relations) {
      EdgeDisplayProperty prop = getRelationProperty(relation);
      prop.setVisible(true);
      saveRelationProperty(relation, prop);
      table.update(relation, RelEditorTableView.UPDATE_VISIBLE);
    }
  }

  private void saveRelationProperty(
      Relation relation, EdgeDisplayProperty prop) {
    getEditor().setRelationProperty(relation, prop);
  }

  private EdgeDisplayProperty loadRelationProperty(Relation relation) {
    return getEditor().getRelationProperty(relation);
  }

  private EdgeDisplayProperty getRelationProperty(Relation relation) {
    EdgeDisplayProperty result = loadRelationProperty(relation);
    if (null != result) {
      return result;
    }

    return new EdgeDisplayProperty();
  }

  /////////////////////////////////////

  /**
   * Update the view after a change in the model.
   */
  private void updateView() {
    if (!hasEditor()) {
      return;
    }

    table.refresh();
    table.setSelection(new StructuredSelection(buildSelected()));
  }

  private Collection<Relation> buildSelected() {
    if (!hasEditor()) {
        return Collections.emptyList();
    }

    RelationshipSet pickerSet = relSetPicker.getSelection();
    if (null != pickerSet) {
      return buildRelations(pickerSet);
    }

    // Build selection from list of visible relations.
    Collection<Relation> relations = getEditor().getDisplayRelations();
    List<Relation> result =
        Lists.newArrayListWithExpectedSize(relations.size());
    for (Relation relation : relations) {
      EdgeDisplayProperty edgeProp = getEditor().getRelationProperty(relation);
      if (edgeProp.isVisible()) {
        result.add(relation);
      }
    }
    return result;
  }

  /**
   * Change listener for RelationSetPickerControl.
   */
  private void handleRelSetPickerChange(RelationshipSet set) {
    if (null != set) {
      selectFinder(set);
    }
  }

  /**
   * Reverse the selection.
   */
  protected void reverseSelection() {
    selectFinder(new ReversedDirectedRelationFinder(getFinder()));
  }

  /**
   * Select the lines described by the given {@link DirectedRelationFinder}.
   *
   * @param finder finder describing a set of relations.
   */
  private void selectFinder(DirectedRelationFinder finder) {
    List<Relation> relations = buildRelations(finder);
    ISelection selection = new StructuredSelection(relations);
    table.setSelection(selection);
  }

  private List<Relation> buildRelations(DirectedRelationFinder finder) {
    List<Relation> result = Lists.newArrayList();
    for (SourcePlugin plugin : SourcePluginRegistry.getInstances()) {
      for (Relation relation : plugin.getRelations()) {
        if (finder.matchForward(relation) || finder.matchBackward(relation)) {
          result.add(relation);
        }
      }
    }
    return result;
  }

  /**
   * Return a {@link RelationFinder} describing the current selection.
   *
   * @return Return a {@link RelationFinder} describing the current selection.
   */
  protected RelationFinder getFinder() {
    return new MultipleRelationFinder(getSelectedRelations());
  }

  /**
   * Open a dialog to save the current selection under a new name.
   */
  protected void saveSelection() {
    if (null == shell) {
      return;
    }

    NewRelationshipSetWizard wizard = new NewRelationshipSetWizard(getFinder());
    WizardDialog dialog = new WizardDialog(shell, wizard);
    dialog.open();
  }
}
