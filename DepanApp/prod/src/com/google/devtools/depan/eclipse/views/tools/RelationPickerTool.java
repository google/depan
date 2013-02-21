/*
 * Copyright 2007 Google Inc.
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

import com.google.common.collect.Lists;
import com.google.devtools.depan.eclipse.plugins.SourcePlugin;
import com.google.devtools.depan.eclipse.plugins.SourcePluginRegistry;
import com.google.devtools.depan.eclipse.utils.ListContentProvider;
import com.google.devtools.depan.eclipse.utils.RelationshipSelectorListener;
import com.google.devtools.depan.eclipse.utils.RelationshipSetPickerControl;
import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.utils.relsets.RelSetDescriptor;
import com.google.devtools.depan.eclipse.views.ViewEditorTool;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.EdgeIncludePlugin;
import com.google.devtools.depan.eclipse.wizards.NewRelationshipSetWizard;
import com.google.devtools.depan.graph.api.DirectedRelationFinder;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.api.RelationFinder;
import com.google.devtools.depan.graph.basic.MultipleRelationFinder;
import com.google.devtools.depan.graph.basic.ReversedDirectedRelationFinder;
import com.google.devtools.depan.model.RelationshipSet;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
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
import java.util.Iterator;
import java.util.List;

/**
 * Tool for selecting relations that have to be shown.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class RelationPickerTool extends ViewEditorTool
    implements RelationshipSelectorListener {

  /**
   * The {@link RelationshipSetSelector} to choose a named set.
   */
  private RelationshipSetPickerControl relSetPicker;

  /**
   * Shell used to open dialogs (SaveAs dialog in this case).
   */
  protected Shell shell = null;

  /**
   * List of relations types.
   */
  private ListViewer list;

  /**
   * A provider for the list of relationships.
   */
  private ListContentProvider<Relation> contentProvider;

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
    updateView();

    // Update the RelSet picker for auto-collapse.
    RelationshipSet selectedRelSet = getEditor().getEdgeDisplayRelSet();
    List<RelSetDescriptor> choices = getEditor().getRelSetChoices();
    relSetPicker.setInput(selectedRelSet, choices );
  }

  @Override
  public Control setupComposite(Composite parent) {
    this.shell = parent.getShell();

    Composite topLevel = new Composite(parent, SWT.NONE);
    GridLayout gridLayout = new GridLayout();
    gridLayout.verticalSpacing = 10;
    topLevel.setLayout(gridLayout);

    Composite pickerRegion = setupRelationPicker(topLevel);
    pickerRegion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Button reverse = new Button(topLevel, SWT.PUSH);
    reverse.setText("Reverse selection");
    reverse.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));

    Label listLabel = new Label(topLevel, SWT.NONE);
    listLabel.setText("Select relationships to hide :");
    listLabel.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));

    list = new ListViewer(topLevel, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
    // layout
    list.getList().setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));

    // content
    contentProvider = new ListContentProvider<Relation>(list);

    // actions
    list.getList().addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        updateModel();
      }
    });
    reverse.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        reverseSelection();
      }
    });

    fill();
    updateView();

    return topLevel;
  }

  private Composite setupRelationPicker(Composite parent) {
    Composite region = new Composite(parent, SWT.None);
    region.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    region.setLayout(new GridLayout(3, false));

    Label pickerLabel = RelationshipSetPickerControl.createPickerLabel(region);
    pickerLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    relSetPicker = new RelationshipSetPickerControl(region);
    relSetPicker.addChangeListener(this);
    relSetPicker.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    Button save = new Button(region, SWT.PUSH);
    save.setText("Save selection as");
    save.setLayoutData(
      new GridData(SWT.FILL, SWT.FILL, true, false));

    save.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        saveSelection();
      }
    });
    
    return region;
  }

  private void fill() {
    for (SourcePlugin plugin : SourcePluginRegistry.getInstances()) {
      for (Relation r : plugin.getRelations()) {
        contentProvider.add(r);
      }
    }
  }

  /**
   * Update the ViewModel to hide selected relations.
   */
  // suppressWarning because selection.iterator is not parameterized in
  // IStructuredSelection, so we need to infer it.
  @SuppressWarnings("unchecked")
  private void updateModel() {
    if (!hasEditor()) {
      return;
    }
    EdgeIncludePlugin edgeInclude =
        getEditor().getRenderer().getEdgeInclude();

    // show all relations
    for (Relation r : contentProvider.getObjects()) {
      edgeInclude.includeRelation(r);
    }

    // hide selected ones
    IStructuredSelection selection = (IStructuredSelection) list.getSelection();
    //selection.iterator();
    Iterator<Relation> iterator = selection.iterator();
    while (iterator.hasNext()) {
      edgeInclude.rejectRelation(iterator.next());
    }
  }

  /**
   * Update the view after a change in the model.
   */
  private void updateView() {
    if (!hasEditor()) {
      return;
    }
    EdgeIncludePlugin edgeInclude =
        getEditor().getRenderer().getEdgeInclude();

    // clear the list.
    List<Relation> selected = Lists.newArrayList(contentProvider.getObjects());

    // add relations to the list, and saved which one should be selected.
    for (Relation relation : edgeInclude.getVisibleRelations()) {
      selected.remove(relation);
    }

    // select hidden values
    list.setSelection(new StructuredSelection(selected));
  }

  @Override
  public void selectedSetChanged(RelationshipSet set) {
    selectFinder(set);
  }

  /**
   * Select the lines described by the given {@link DirectedRelationFinder}.
   *
   * @param finder finder describing a set of relations.
   */
  public void selectFinder(DirectedRelationFinder finder) {
    List<Relation> relations = Lists.newArrayList();
    for (SourcePlugin plugin : SourcePluginRegistry.getInstances()) {
      for (Relation relation : plugin.getRelations()) {
        if (finder.matchForward(relation) || finder.matchBackward(relation)) {
          relations.add(relation);
        }
      }
    }

    ISelection selection = new StructuredSelection(relations);
    list.setSelection(selection);
    updateModel();
  }

  /**
   * Return a collection of Relations describing the current selection.
   *
   * @return a collection of Relations describing the current selection.
   */
  // suppressWarnings : IStructuredSelection.iterator() is not parameterized.
  @SuppressWarnings("unchecked")
  protected Collection<Relation> getSelectedRelations() {
    IStructuredSelection selection = (IStructuredSelection) list.getSelection();
    Iterator<Relation> iterator = selection.iterator();
    return Lists.newArrayList(iterator);
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

  /**
   * Reverse the selection.
   */
  protected void reverseSelection() {
    selectFinder(new ReversedDirectedRelationFinder(getFinder()));
  }
}
