/*
 * Copyright 2007 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.devtools.depan.remap_doc.eclipse.ui.editors;

import com.google.devtools.depan.platform.AlphabeticSorter;
import com.google.devtools.depan.platform.CollectionContentProvider;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Selections;
import com.google.devtools.depan.remap_doc.model.MigrationGroup;
import com.google.devtools.depan.remap_doc.model.MigrationTask;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import java.util.Collection;

/**
 * An Editor for migration groups.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class MigrationGroupEditor extends MigrationTaskAdapter {

  /**
   * Top level widget.
   */
  private Control control;

  /**
   * A list for group selection.
   */
  private ListViewer groupSelect;

  /**
   * Text widget for name editing.
   * TODO(ycoppel): actually update the model when it is modified.
   */
  private Text name;

  /**
   * Checkbox for selecting the groups that must be completed before the
   * selected one.
   * TODO(ycoppel): actually update the model when the selection change.
   */
  private CheckboxTableViewer checkBoxTableViewer;

  /**
   * {@link MigrationTask} being edited (MigrationTask containing the groups
   * displayed in the lists).
   */
  private final MigrationTask task;

  /**
   * Selected MigrationGroup, i.e. the one being edited.
   */
  private MigrationGroup selectedGroup;

  /**
   * A listener for {@link MigrationTask} changes.
   */
  private final MigrationTaskListener listener;

  /**
   * Construct the {@link MigrationGroupEditor}, and initialize the associated
   * GUI.
   *
   * @param task the {@link MigrationTask} we want to edit.
   * @param parent Parent composite.
   * @param listener listener for changes in {@link MigrationTask}s.
   */
  public MigrationGroupEditor(MigrationTask task, Composite parent,
      MigrationTaskListener listener) {
    this.listener = listener;
    this.task = task;
    this.control = createControl(parent);
  }

  /**
   * Create the GUI controls under the given parent.
   * @param parent
   * @return the top level {@link Control}.
   */
  private Control createControl(Composite parent) {
    // widgets
    Composite topLevel = new Composite(parent, SWT.NONE);

    Composite leftPanel = new Composite(topLevel, SWT.NONE);
    Label labelAllGroups = new Label(leftPanel, SWT.NONE);
    Label labelList = new Label(leftPanel, SWT.NONE);
    groupSelect =
        new ListViewer(leftPanel, SWT.V_SCROLL | SWT.BORDER | SWT.SINGLE);
    Button newGroup = new Button(leftPanel, SWT.PUSH);
    Button deleteGroup = new Button(leftPanel, SWT.PUSH);

    Composite rightPanel = new Composite(topLevel, SWT.NONE);
    Label labelGroupDetails = new Label(rightPanel, SWT.NONE);
    Label labelName = new Label(rightPanel, SWT.NONE);
    name = new Text(rightPanel, SWT.BORDER);

    Label labelDependencies = new Label(rightPanel, SWT.NONE);
    Label labelCompletedBefore = new Label(rightPanel, SWT.NONE);
    checkBoxTableViewer =
        CheckboxTableViewer.newCheckList(rightPanel, SWT.V_SCROLL | SWT.BORDER);

    // data
    labelAllGroups.setText("All Groups");
    labelList.setText("Group");
    newGroup.setText("New");
    deleteGroup.setText("Delete");

    labelGroupDetails.setText("Group Details");
    labelName.setText("Name");
    groupSelect.setComparator(new AlphabeticSorter(false));
    labelDependencies.setText("Dependencies");
    labelCompletedBefore.setText("Groups that must be completed before");
    checkBoxTableViewer.setComparator(new AlphabeticSorter(false));

    initGroupLists();

    // layout
    GridLayout layout = new GridLayout(2, false);
    layout.verticalSpacing = 9;
    topLevel.setLayout(layout);

    GridLayout leftLayout = new GridLayout(2, false);
    leftPanel.setLayout(leftLayout);

    GridLayout rightLayout = new GridLayout(2, false);
    rightPanel.setLayout(rightLayout);

    leftPanel.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));
    labelAllGroups.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
    labelList.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
    groupSelect.getControl().setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
    newGroup.setLayoutData(
        new GridData(SWT.FILL, SWT.TOP, false, false));
    deleteGroup.setLayoutData(
        new GridData(SWT.FILL, SWT.TOP, false, false));

    rightPanel.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));
    labelGroupDetails.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
    labelName.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, false, false));
    name.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, true, false));
    labelDependencies.setLayoutData(
        new GridData(SWT.FILL, SWT.TOP, false, false, 1, 2));
    labelCompletedBefore.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, false, false));
    checkBoxTableViewer.getControl().setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));

    // actions
    groupSelect.addSelectionChangedListener(new ISelectionChangedListener() {

      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        newSelection();
      }
    });

    newGroup.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        newGroup();
      }
    });

    deleteGroup.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        deleteSelectedGroup();
      }
    });

    return topLevel;
  }

  /**
   * Fill the group list.
   */
  private void initGroupLists() {
    Collection<MigrationGroup> groups = task.getMigrationGroups();
    CollectionContentProvider<MigrationGroup> provider =
        new CollectionContentProvider<MigrationGroup>(groups);
    groupSelect.setContentProvider(provider);
    checkBoxTableViewer.setContentProvider(provider);
    groupSelect.setInput(groups);
    checkBoxTableViewer.setInput(groups);
  }

  /**
   * Ask the user a name for a new MigrationGroup, and create it.
   * Also call {@link MigrationTaskListener#groupsListUpdated(Object)}.
   */
  protected void newGroup() {
    InputDialog dialog = new InputDialog(control.getShell(),
        "New Migration Group", "Select a name for the group", "",
        new IInputValidator() {

          @Override
          public String isValid(String newText) {
            if (newText.length() <= 0) {
              return "The name must contains at least one character.";
            }
            for (MigrationGroup group : task.getMigrationGroups()) {
              if (group.getName().equals(newText)) {
                return "A Group with this name already exists.";
              }
            }
            return null;
          }
        });
    if (dialog.open() != InputDialog.OK) {
      return;
    }
    MigrationGroup newGroup = new MigrationGroup();
    newGroup.setName(dialog.getValue());
    task.addMigrationGroup(newGroup);
    groupSelect.refresh(false);
    checkBoxTableViewer.refresh(false);
    listener.groupsListUpdated(this);
  }

  /**
   * Delete the selected {@link MigrationGroup}, and call
   * {@link MigrationTaskListener#groupsListUpdated(Object)}.
   */
  protected void deleteSelectedGroup() {
    MigrationGroup group = getSelectedGroup();
    if (null == group) {
      return;
    }
    task.removeMigrationGroup(group);
    groupSelect.refresh(false);
    checkBoxTableViewer.refresh(false);
    listener.groupsListUpdated(this);
  }

  /**
   * @return the currently selected {@link MigrationGroup}.
   */
  private MigrationGroup getSelectedGroup() {
    ISelection selection = groupSelect.getSelection();
    return Selections.getFirstElement(selection, MigrationGroup.class);
  }

  /**
   * Start editing of the selected {@link MigrationGroup}.
   */
  protected void newSelection() {
    MigrationGroup group = getSelectedGroup();
    if (null == group) {
      return;
    }
    this.selectedGroup = group;
    fillData(group);
  }

  /**
   * Fill the editor fields with the given group data.
   *
   * @param group the group we want to edit.
   */
  private void fillData(MigrationGroup group) {
    this.name.setText(group.getName());
  }

  /**
   * @return return the top-level widget for this editor.
   */
  public Control getControl() {
    return control;
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.eclipse.editors.MigrationTaskAdapter
   *      #groupsListUpdated(java.lang.Object)
   */
  @Override
  public void groupsListUpdated(Object source) {
    if (this == source) {
      return;
    }
    groupSelect.refresh(false);
    checkBoxTableViewer.refresh(false);
    Collection<MigrationGroup> groups = task.getMigrationGroups();
    if (!groups.contains(selectedGroup)) {
      if (groups.size() < 1) {
        // the selected group have been deleted, and the list is now empty.
        groupSelect.setSelection(new StructuredSelection());
      } else {
        // the selected group have been deleted, so we select another group.
        MigrationGroup group = task.getMigrationGroups().iterator().next();
        groupSelect.setSelection(new StructuredSelection(group));
        newSelection();
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.eclipse.editors.MigrationTaskAdapter
   *      #groupUpdated(java.lang.Object,
   *      com.google.devtools.depan.tasks.MigrationGroup)
   */
  @Override
  public void groupUpdated(Object source, MigrationGroup group) {
    if (group != selectedGroup) {
      return;
    }
    // refresh the data for the selected group iif it was selected.
    groupSelect.refresh(true);
    checkBoxTableViewer.refresh(true);
    fillData(selectedGroup);
  }
}
