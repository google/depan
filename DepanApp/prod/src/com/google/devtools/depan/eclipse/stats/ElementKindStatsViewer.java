/*
 * Copyright 2010 The Depan Project Authors
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

package com.google.devtools.depan.eclipse.stats;

import com.google.devtools.depan.eclipse.stats.ElementKindStats.Info;
import com.google.devtools.depan.platform.AlphabeticSorter;
import com.google.devtools.depan.platform.InverseSorter;
import com.google.devtools.depan.platform.LabelProviderToString;
import com.google.devtools.depan.platform.eclipse.ui.tables.EditColTableDef;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import java.util.Collection;

/**
 * A control for displaying {@link ElementKindStats}.
 * 
 * <p> This implementation was scavenged from {@link ElementKindPicker},
 * and really should be generalized as a {@code ViewedTable} control with
 * standard support for sortable columns, etc.
 *
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class ElementKindStatsViewer extends Composite {

  // Column labels.
  private static final String ELEMENT_KIND_LABEL = "Element Type";
  private static final String ELEMENT_SOURCE_LABEL = "Source";
  private static final String ELEMENT_COUNT_LABEL = "Count";

  /** Table viewer used to adapt set of known Element kinds for table */
  private TableViewer elementKindViewer = null;

  /** Definition of columns used to display Element kinds. */
  private static final EditColTableDef[] TABLE_DEF =
      new EditColTableDef[] {
          new EditColTableDef(
              ELEMENT_KIND_LABEL, false, ELEMENT_KIND_LABEL, 150),
          new EditColTableDef(
              ELEMENT_SOURCE_LABEL, false, ELEMENT_SOURCE_LABEL, 150),
          new EditColTableDef(
              ELEMENT_COUNT_LABEL, false, ELEMENT_COUNT_LABEL, 75)
      };

  /**
   * Create the viewer, with all sortable column headers.
   *
   * @param parent containing controls
   * @param style standard style bits
   */
  public ElementKindStatsViewer(Composite parent, int style) {
    super(parent, style);

    setLayout(new GridLayout());

    // Initialize the table control
    Table elementKindTable = new Table(
        this, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
    elementKindTable.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));

    elementKindTable.setHeaderVisible(true);
    EditColTableDef.setupTable(TABLE_DEF, elementKindTable);

    // Configure the table viewer
    elementKindViewer = new TableViewer(elementKindTable);
    elementKindViewer.setContentProvider(new ArrayContentProvider());

    LabelProvider labelProvider = new LabelProvider();
    elementKindViewer.setLabelProvider(labelProvider);

    configSorters(elementKindTable);
    setSortColumn(elementKindTable.getColumn(0), 0, SWT.DOWN);
  }

  private void configSorters(Table elementKindTable) {
    TableColumn[] columns = elementKindTable.getColumns();

    int index = 0;
    for (TableColumn column : columns) {
      final int colIndex = index++;

      column.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          updateSortColumn((TableColumn) event.widget, colIndex);
        }
      });
    }
  }

  /**
   * Make the table display the passed-in set of statistics.
   * @param stats statistics to display
   */
  public void setInput(Collection<Info> stats) {
    elementKindViewer.setInput(stats);
  }

  private void updateSortColumn(TableColumn column, int colIndex) {
    setSortColumn(column, colIndex, getSortDirection(column));
  }

  private int getSortDirection(TableColumn column) {
    Table tableControl = (Table) elementKindViewer.getControl();
    if (column != tableControl.getSortColumn()) {
      return SWT.DOWN;
    }
    // If it is unsorted (SWT.NONE), assume down sort
    return (SWT.DOWN == tableControl.getSortDirection())
        ? SWT.UP : SWT.DOWN;
  }

  private void setSortColumn(
      TableColumn column, int colIndex, int direction) {

    ITableLabelProvider labelProvider =
        (ITableLabelProvider) elementKindViewer.getLabelProvider();
    ViewerSorter sorter = new AlphabeticSorter(
        new LabelProviderToString(labelProvider, colIndex));
    if (SWT.UP == direction) {
      sorter = new InverseSorter(sorter);
    }

    Table tableControl = (Table) elementKindViewer.getControl();
    elementKindViewer.setSorter(sorter);
    tableControl.setSortColumn(column);
    tableControl.setSortDirection(direction);
  }

  /**
   * A simple LabelProvider that can unpack a {@link ElementKindStats.Info}.
   */
  private static class LabelProvider extends BaseLabelProvider
      implements ITableLabelProvider {

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
      return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
      if (null == element) {
        return null;
      }

      ElementKindStats.Info item = (ElementKindStats.Info) element;
      switch (columnIndex) {
      case 0:
        return item.getKind().getElementKindName();
      case 1:
        return item.getKind().getPluginName();
      case 2:
        return Integer.toString(item.getCount());
      }

      return null;
    }
  }
}
