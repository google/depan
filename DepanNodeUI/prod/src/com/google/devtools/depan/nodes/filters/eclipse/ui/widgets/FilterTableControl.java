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

package com.google.devtools.depan.nodes.filters.eclipse.ui.widgets;

import com.google.devtools.depan.nodes.filters.model.ContextualFilter;
import com.google.devtools.depan.nodes.filters.sequence.BasicFilter;
import com.google.devtools.depan.platform.eclipse.ui.tables.EditColTableDef;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Selections;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import java.util.Collection;
import java.util.List;

/**
 * Display and edit a list of {@link ContextualFilter}
 * 
 * Assumes the set of {@link ContextualFilter}s being shown will not
 * change externally. For {@link FilterTableControl}s, these kinds of changes
 * are expected when parent {@link Control} changes the underlying document,
 * or if a list editor adds or removes elements.  Update the control for
 * these external changes via the {@link #setInput(Collection)} method. 
 * 
 * Editor changes to any members of the input {@link ContextualFilter} set
 * are made in place.  Individual members may be altered or changed, but the
 * list itself reused, and no member are added or removed. A {@link List}
 * retained by the caller should refect any recent edits.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class FilterTableControl extends Composite {

  public static final String COL_NAME = "Name";
  public static final String COL_SUMMARY = "Summary";

  public static final int INDEX_NAME = 0;
  public static final int INDEX_SUMMARY = 1;

  public static final EditColTableDef[] TABLE_DEF = new EditColTableDef[] {
    new EditColTableDef(COL_NAME, true, COL_NAME, 300),
    new EditColTableDef(COL_SUMMARY, true, COL_SUMMARY, 700),
  };

  private static final String[] UPDATE_NAME_COLUMNS = { COL_NAME };

  private static final String[] UPDATE_SUMMARY_COLUMNS = { COL_SUMMARY };

  // Only need one.
  private static final LabelProvider LABEL_PROVIDER =
      new ControlLabelProvider();

  /////////////////////////////////////
  // UX Elements

  private TableViewer filterViewer;

  /////////////////////////////////////
  // Public methods

  public FilterTableControl(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(Widgets.buildContainerLayout(1));

    filterViewer = 
        new TableViewer(this, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI );

    // Set up layout properties.
    Table filterTable = filterViewer.getTable();
    filterTable.setLayoutData(Widgets.buildGrabFillData());

    // Initialize the table.
    filterTable.setHeaderVisible(true);
    filterTable.setToolTipText("Node Filter Editor");
    EditColTableDef.setupTable(TABLE_DEF, filterTable);

    CellEditor[] cellEditors = new CellEditor[TABLE_DEF.length];
    cellEditors[INDEX_NAME] = new TextCellEditor(filterTable);
    cellEditors[INDEX_SUMMARY] = new NodeFilterCellEditor(filterTable);

    // Configure table properties.
    filterViewer.setCellEditors(cellEditors);
    filterViewer.setLabelProvider(LABEL_PROVIDER);
    filterViewer.setColumnProperties(EditColTableDef.getProperties(TABLE_DEF));
    filterViewer.setCellModifier(new ControlCellModifier());

    // Since the order is significant, no sorting capabilities.

    // Avoid setInput() invocations that come with
    // TableContentProvider.initViewer
    filterViewer.setContentProvider(ArrayContentProvider.getInstance());
  }

  /**
   * Fill the list with {@link ContextualFilter}s.
   */
  public void setInput(List<ContextualFilter> filterInfo) {
    filterViewer.setInput(filterInfo);
    filterViewer.refresh(true);
  }

  public List<ContextualFilter> getSelectedFilters() {
    ISelection selection = filterViewer.getSelection();
    return Selections.getSelectionList(selection, ContextualFilter.class);
  }

  public void createContextMenu(MenuManager mgr) {
    Table filterTable = filterViewer.getTable();
    Menu menu = mgr.createContextMenu(filterTable);
    filterTable.setMenu(menu);
  }

  /////////////////////////////////////
  // Label provider for table cell text

  private static class ControlLabelProvider extends LabelProvider
      implements ITableLabelProvider {

    @Override
    public String getColumnText(Object element, int columnIndex) {
      if (!(element instanceof ContextualFilter)) {
        return "";
      }
      ContextualFilter filter = ((ContextualFilter) element);
      switch (columnIndex) {
      case INDEX_NAME:
        return filter.getName();
      case INDEX_SUMMARY:
        return filter.getSummary();
      }
      return "";
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
      return null;
    }
  }

  /////////////////////////////////////
  // Value provider/modifier for edit cells

  private class ControlCellModifier implements ICellModifier {

    @Override
    public boolean canModify(Object element, String property) {
      return EditColTableDef.get(TABLE_DEF, property).isEditable();
    }

    @Override
    public Object getValue(Object element, String property) {
      if (!(element instanceof ContextualFilter)) {
        return null;
      }
      ContextualFilter filter = ((ContextualFilter) element);
      if (property.equals(COL_NAME)) {
        return filter.getName();
      }
      if (property.equals(COL_SUMMARY)) {
        return filter.getSummary();
      }
      return null;
    }

    @Override
    public void modify(Object element, String property, Object value) {
      if (!(element instanceof TableItem)) {
        return;
      }
      Object o = ((TableItem) element).getData();
      if (!(o instanceof ContextualFilter)) {
        return;
      }

      ContextualFilter filter = ((ContextualFilter) o);
      if (updateBasicFilter(filter, property, value)) {
        return;
      }
    }
  }

  private boolean updateBasicFilter(
      ContextualFilter filter, Object property, Object value) {
    if (!(filter instanceof BasicFilter)) {
      return false;
    }

    BasicFilter basic = (BasicFilter) filter;
    if (property.equals(COL_NAME)) {
      basic.setName((String) value);
      filterViewer.update(basic, UPDATE_NAME_COLUMNS);

      return true;
    }
    if (property.equals(COL_SUMMARY)) {
      basic.setSummary((String) value);
      filterViewer.update(basic, UPDATE_SUMMARY_COLUMNS);
      return true;
    }

    return false;
  }
}
