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

import com.google.devtools.depan.platform.CollectionContentProvider;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Selections;
import com.google.devtools.depan.remap_doc.model.MigrationGroup;
import com.google.devtools.depan.remap_doc.model.MigrationTask;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * A simple {@link TableViewer} for {@link MigrationGroup}s contained in a
 * {@link MigrationTask}. Provide the widget and the methods to select a
 * {@link MigrationGroup}, and to retrieve the selected one.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class MigrationGroupListViewer {

  /**
   * Top level widget.
   */
  private Control control;

  /**
   * {@link TableViewer} displaying the {@link MigrationGroup}s.
   */
  private TableViewer table;

  /**
   * The {@link MigrationTask} containing the listed {@link MigrationGroup}s.
   */
  private final MigrationTask task;

  /**
   * Construct a {@link MigrationGroupListViewer}.
   *
   * @param parent Parent composite.
   * @param selectionListener A listener for the selection changes in the table.
   * @param task the {@link MigrationTask}.
   */
  public MigrationGroupListViewer(Composite parent,
      SelectionListener selectionListener, MigrationTask task) {
    this.task = task;
    this.control = createControl(parent, selectionListener);
  }

  /**
   * Create the GUI Control under the given parent.
   * @param parent the GUI parent.
   * @param listener {@link SelectionListener} for selection callbacks.
   * @return the top level Control for this widget.
   */
  public Control createControl(Composite parent, SelectionListener listener) {
    table = new TableViewer(parent, SWT.V_SCROLL | SWT.BORDER);
    table.setContentProvider(
        CollectionContentProvider.newProvider(task.getMigrationGroups()));
    table.getTable().addSelectionListener(listener);
    return table.getControl();
  }

  /**
   * @return return the top level {@link Control} for this widget.
   */
  public Control getControl() {
    return control;
  }

  /**
   * Refreshes the table.
   */
  public void refresh() {
    table.refresh(true);
  }

  /**
   * @return the first element selected in the list, or null if none.
   */
  public MigrationGroup getSelected() {
    ISelection selection = table.getSelection();
    return Selections.getFirstElement(selection, MigrationGroup.class);
  }

  /**
   * Set the selection in the list to the given {@link MigrationGroup}.
   * @param group the {@link MigrationGroup} we want to select.
   */
  public void setSelected(MigrationGroup group) {
    IStructuredSelection selection = new StructuredSelection(group);
    table.setSelection(selection);
  }
}
