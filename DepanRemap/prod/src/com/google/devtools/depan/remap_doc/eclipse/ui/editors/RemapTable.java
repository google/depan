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

import com.google.devtools.depan.platform.TableContentProvider;
import com.google.devtools.depan.platform.eclipse.ui.tables.EditColTableDef;
import com.google.devtools.depan.remap_doc.model.MigrationRule;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;

import java.util.Collection;

/**
 * A Table showing a set of {@link MigrationRule}s.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class RemapTable {

  protected static final String COL_SOURCE = "Source";
  protected static final String COL_TARGET = "Target";

  private static final EditColTableDef[] TABLE_DEF = new EditColTableDef[] {
    new EditColTableDef(COL_SOURCE, false, COL_SOURCE, 300),
    new EditColTableDef(COL_TARGET, false, COL_TARGET, 300)
  };

  private Control control = null;

  private TableViewer tableViewer = null;
  private TableContentProvider<MigrationRule<?>> remapContent;

  public RemapTable(Composite parent) {
    control = setupControl(parent);
  }

  private Control setupControl(Composite parent) {
    Composite topLevel = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    topLevel.setLayout(layout);

    tableViewer =
      new TableViewer(topLevel, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);

    Table tableData = tableViewer.getTable();
    tableData.setHeaderVisible(true);
    EditColTableDef.setupTable(TABLE_DEF, tableData);

    CellEditor[] cellEditors = new CellEditor[4];
    cellEditors[0] = null;
    cellEditors[1] = null;
    cellEditors[2] = new TextCellEditor(tableData);
    cellEditors[3] = new TextCellEditor(tableData);

    tableViewer.setCellEditors(cellEditors);
    tableViewer.setColumnProperties(EditColTableDef.getProperties(TABLE_DEF));

    RemapTableHelper cellHelper = new RemapTableHelper();
    tableViewer.setLabelProvider(cellHelper);

    tableData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    remapContent = new TableContentProvider<MigrationRule<?>>();
    remapContent.initViewer(tableViewer);

    return topLevel;
  }

  /**
   * Set the collection of MigrationRule to show.
   *
   * @param collection Collection of MigrationRules to show.
   */
  public void setData(Collection<MigrationRule<?>> collection) {
    remapContent.clear();
    for (MigrationRule<?> rule : collection) {
      remapContent.add(rule);
    }
    tableViewer.refresh(false);
  }

  /**
   * Refresh the tableViewer, and update if necessary the labels in the table.
   *
   * @param updateLabels if <code>true</code>, will update the labels.
   */
  public void refresh(boolean updateLabels) {
    tableViewer.refresh(updateLabels);
  }

  /**
   * @return the top level Control for this widget.
   */
  public Control getControl() {
    return control;
  }

}
