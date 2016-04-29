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

package com.google.devtools.depan.eclipse.utils;

import com.google.devtools.depan.eclipse.plugins.SourcePlugin;
import com.google.devtools.depan.eclipse.plugins.SourcePluginRegistry;
import com.google.devtools.depan.eclipse.utils.RelationPropertyRelationTableEditor.RelPropRepository;
import com.google.devtools.depan.eclipse.wizards.NewRelationSetWizard;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.model.RelationSets;
import com.google.devtools.depan.relations.RelationSetDescriptor;
import com.google.devtools.depan.relations.RelationSetDescriptors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
public class RelationPropertyEditorPart {

  /////////////////////////////////////
  // UX Elements

  /**
   * Table of relation data.
   */
  private TableViewer table;

  /**
   * Manages the relation set data.
   */
  private RelationPropertyRelationTableEditor viewer;

  /**
   * The {@link RelationshipSetSelector} to choose a named set.
   */
  private RelationSetSelectorControl relationSetSelector;

  /**
   * Shell used to open dialogs (SaveAs dialog in this case).
   */
  protected Shell shell = null;

  public Control getControl(Composite parent, RelPropRepository propRepo) {
    this.shell = parent.getShell();

    Composite topLevel = new Composite(parent, SWT.NONE);
    GridLayout gridLayout = new GridLayout();
    gridLayout.verticalSpacing = 10;
    topLevel.setLayout(gridLayout);

    Composite commands = setupCommandButtons(topLevel);
    commands.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    viewer = new RelationPropertyRelationTableEditor(propRepo);
    table = viewer.setupViewer(topLevel);
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

    Composite pickerRegion = setupRelationSetSelector(result);
    pickerRegion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    return result;
  }

  private Composite setupRelationSetSelector(Composite parent) {
    Composite region = new Composite(parent, SWT.None);
    region.setLayout(new GridLayout(3, false));

    Label pickerLabel = RelationSetSelectorControl.createRelationSetLabel(region);
    pickerLabel.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    relationSetSelector = new RelationSetSelectorControl(region);
    relationSetSelector.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    relationSetSelector.addChangeListener(new RelationSetSelectorListener() {
      @Override
      public void selectedSetChanged(RelationSetDescriptor relationSet) {
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

  private Collection<Relation> buildSelected() {
    RelationSetDescriptor pickerSet = relationSetSelector.getSelection();
    if (null != pickerSet) {
      return buildRelations(pickerSet);
    }
    return Collections.emptyList();
  }

  /**
   * Change listener for RelationSetPickerControl.
   */
  private void handleRelSetPickerChange(RelationSetDescriptor relationSet) {
    if (null != relationSet) {
      selectRelations(buildRelations(relationSet));
    }
  }

  /**
   * Select the lines described by the supplied collection of
   * {@link Relation}s.
   */
  public void selectRelations(Collection<Relation> relations) {
    ISelection selection = new StructuredSelection(relations.toArray());
    table.setSelection(selection);
  }

  private Collection<Relation> buildRelations(RelationSet relationSet) {
    Collection<Relation> result = Lists.newArrayList();
    for (SourcePlugin plugin : SourcePluginRegistry.getInstances()) {
      for (Relation relation : plugin.getRelations()) {
        if (relationSet.contains(relation)) {
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
  public RelationSet buildRelationSet() {
    return RelationSets.createSimple(Sets.newHashSet(buildSelected()));
  }

  /**
   * Returns the <code>RelationshipSet</code> that contains the selected
   * relations in this <code>RelationshipPicker</code>.
   *
   * @return Set of relations selected in this picker. Returns an empty object
   * if a valid {@link RelationshipSetSelector} object is not found.
   */
  public RelationSetDescriptor getSelectedRelationSet() {
    if (relationSetSelector == null) {
      return RelationSetDescriptors.EMPTY;
    }
    return relationSetSelector.getSelection();
  }

  /**
   * Open a dialog to save the current selection under a new name.
   */
  protected void saveSelection() {
    if (null == shell) {
      return;
    }

    NewRelationSetWizard wizard =
        new NewRelationSetWizard(buildRelationSet());
    WizardDialog dialog = new WizardDialog(shell, wizard);
    dialog.open();
  }

  /**
   * Fill the list with {@link Relation}s.
   */
  public void updateTable(List<SourcePlugin> plugins) {
    viewer.updateTable(plugins);
    table.refresh(false);
  }

  public void setRelationSetSelectorInput(
      RelationSetDescriptor selectedRelSet,
      List<RelationSetDescriptor> choices) {

    relationSetSelector.setInput(selectedRelSet, choices);
  }
}
