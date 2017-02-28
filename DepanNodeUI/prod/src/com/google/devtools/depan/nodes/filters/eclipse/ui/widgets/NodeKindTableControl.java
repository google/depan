/*
 * Copyright 2008 The Depan Project Authors
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

import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.platform.AlphabeticSorter;
import com.google.devtools.depan.platform.InverseSorter;
import com.google.devtools.depan.platform.LabelProviderToString;
import com.google.devtools.depan.platform.eclipse.ui.tables.EditColTableDef;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Selections;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import java.util.Collection;

/**
 * A control for selecting a set of Element types.
 * 
 * Based on an earlier {@code ElementKindPicker}.
 *
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class NodeKindTableControl extends Composite {

  // PLACEHOLDER
  public static interface ElementKindDescriptor {

    Class<? extends Element> getElementKind();

    String getElementKindName();

    String getPluginName();
  }

  private static final String COL_KIND = "Element Type";
  private static final String COL_SOURCE = "Source";

  public static final int INDEX_KIND = 0;
  public static final int INDEX_SOURCE = 1;

  private static final EditColTableDef[] TABLE_DEF =
      new EditColTableDef[] {
          new EditColTableDef(COL_KIND, false, COL_KIND, 150),
          new EditColTableDef(COL_SOURCE, false, COL_SOURCE, 150)
      };

  // Only need one.
  private static final ControlLabelProvider LABEL_PROVIDER =
      new ControlLabelProvider();

  /////////////////////////////////////
  // UX Elements

  /** Table viewer used to adapt set of known Element kinds for table */
  private TableViewer kindViewer;

  /////////////////////////////////////
  // Public methods

  /**
   * Create the ElementKindPicker, with all the usual sub-controls.
   *
   * @param parent containing controls
   * @param style standard style bits
   */
  public NodeKindTableControl(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(Widgets.buildContainerLayout(1));

    // Setup selection button bar
    Composite buttonBar = configButtonBar(this);
    buttonBar.setLayoutData(
        new GridData(SWT.LEFT, SWT.CENTER, false, false));

    // Configure the table viewer
    kindViewer = new TableViewer(
        this, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);

    // Set up layout properties.
    Table elementKindTable = kindViewer.getTable();
    elementKindTable.setLayoutData(Widgets.buildGrabFillData());

    // Initialize the table.
    elementKindTable.setHeaderVisible(true);
    elementKindTable.setToolTipText("Node Kind Selector");
    EditColTableDef.setupTable(TABLE_DEF, elementKindTable);

    // Configure the table viewer
    kindViewer.setLabelProvider(LABEL_PROVIDER);
    kindViewer.setColumnProperties(EditColTableDef.getProperties(TABLE_DEF));

    configSorters(elementKindTable);
    setSortColumn(elementKindTable.getColumn(0), 0, SWT.DOWN);

    kindViewer.setContentProvider(new ArrayContentProvider());
  }

  public void setSelection(Collection<ElementKindDescriptor> selection) {
    kindViewer.setSelection(new StructuredSelection(selection.toArray()));
  }

  /**
   * Provide the collection of selected Element kinds.
   * @return collection of selected Element kinds
   */
  public Collection<Class<? extends Element>> getSelectedElementKindSet() {
    Collection<Class<? extends Element>> result = Lists.newArrayList();
    for(ElementKindDescriptor descr : getSelectedDescr()) {
      result.add(descr.getElementKind());
    }
    return result;
  }

  private Collection<ElementKindDescriptor> getSelectedDescr() {
    ISelection selection = kindViewer.getSelection();
    return Selections.getSelection(selection, ElementKindDescriptor.class);
  }

  public void setInput(Collection<ElementKindDescriptor> elementKinds) {
    kindViewer.setInput(elementKinds);
  }

  /**
   * @param nodeKinds
   */
  @SuppressWarnings("unchecked")
  public Collection<ElementKindDescriptor> findDescriptors(
      Collection<Class<? extends Element>> nodeKinds) {

    Collection<ElementKindDescriptor> result =
        Lists.newArrayListWithExpectedSize(nodeKinds.size());
    Collection<ElementKindDescriptor> descrs =
        (Collection<ElementKindDescriptor>) kindViewer.getInput();

    for (ElementKindDescriptor descr : descrs) {
      if (nodeKinds.contains(descr.getElementKind())) {
        result.add(descr);
      }
    }
    return result;
  }

  /**
   * Create a button bar with Select All, Select None, and Invert Selection
   * buttons.  Since the buttons use ElementKindPicker methods to implement
   * the selection methods, the button bar is simply constructed on the
   * current instance (i.e. {@code this}).
   * 
   * @return Composite suitable ready for layout options
   */
  private Composite configButtonBar(Composite parent) {
    Composite result = new Composite(parent, SWT.NONE);
    result.setLayout(new RowLayout());

    Button selectAll = new Button(result, SWT.PUSH);
    selectAll.setText("Select All");
    selectAll.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        StructuredSelection nextSelection =
            new StructuredSelection(getInput().toArray());
        kindViewer.setSelection(nextSelection, true);
      }
    });

    Button selectNone = new Button(result, SWT.PUSH);
    selectNone.setText("Select None");
    selectNone.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        StructuredSelection nextSelection = new StructuredSelection();
        kindViewer.setSelection(nextSelection, true);
      }
    });

    Button reverseAll = new Button(result, SWT.PUSH);
    reverseAll.setText("Invert Selection");
    reverseAll.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        invertSelection();
      }
    });

    return result;
  }

  /////////////////////////////////////
  // UX Actions

  private void invertSelection() {
    Collection<ElementKindDescriptor> currSelection = getSelectedDescr();
    Collection<ElementKindDescriptor> invert = Lists.newArrayList();

    for (ElementKindDescriptor descr: getInput()) {
      if (!currSelection.contains(descr)) {
        invert.add(descr);
      }
    }
    StructuredSelection nextSelection =
        new StructuredSelection(invert.toArray());
    kindViewer.setSelection(nextSelection, true);
  }

  @SuppressWarnings("unchecked")
  private Collection<ElementKindDescriptor> getInput() {
    Collection<ElementKindDescriptor> result = 
        (Collection<ElementKindDescriptor>) kindViewer.getInput();
    if (null == result) {
      return ImmutableList.of();
    }
    return result;
  }

  /////////////////////////////////////
  // Column sorting

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

  private void updateSortColumn(TableColumn column, int colIndex) {
    setSortColumn(column, colIndex, getSortDirection(column));
  }

  private int getSortDirection(TableColumn column) {
    Table tableControl = (Table) kindViewer.getControl();
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
        (ITableLabelProvider) kindViewer.getLabelProvider();
    ViewerComparator sorter = new AlphabeticSorter(
        new LabelProviderToString(labelProvider, colIndex));
    if (SWT.UP == direction) {
      sorter = new InverseSorter(sorter);
    }

    Table tableControl = (Table) kindViewer.getControl();
    kindViewer.setComparator(sorter);
    tableControl.setSortColumn(column);
    tableControl.setSortDirection(direction);
  }

  /////////////////////////////////////
  // Label provider for table cell text

  /**
   * A simple LabelProvider that can unpack a ElementKindDescriptor.
   */
  private static class ControlLabelProvider extends BaseLabelProvider
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

      ElementKindDescriptor item = (ElementKindDescriptor) element;
      switch (columnIndex) {
      case 0:
        return item.getElementKindName();
      case 1:
        return item.getPluginName();
      }

      return null;
    }
  }
}
