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

package com.google.devtools.depan.eclipse.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.google.devtools.depan.eclipse.utils.elementkinds.ElementKindDescriptor;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.platform.AlphabeticSorter;
import com.google.devtools.depan.platform.InverseSorter;
import com.google.devtools.depan.platform.LabelProviderToString;
import com.google.devtools.depan.platform.eclipse.ui.tables.EditColTableDef;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import java.util.Collection;

/**
 * A control for selecting a set of Element types.
 *
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class ElementKindPicker extends Composite {

  private static final String ELEMENT_KIND_LABEL = "Element Type";
  private static final String ELEMENT_SOURCE_LABEL = "Source";

  /** Table viewer used to adapt set of known Element kinds for table */
  private TableViewer elementKindViewer = null;

  /** Definition of columns used to display Element kinds. */
  private static final EditColTableDef[] TABLE_DEF =
      new EditColTableDef[] {
          new EditColTableDef(
              ELEMENT_KIND_LABEL, false, ELEMENT_KIND_LABEL, 150),
          new EditColTableDef(
              ELEMENT_SOURCE_LABEL, false, ELEMENT_SOURCE_LABEL, 150)
      };

  /**
   * Create the ElementKindPicker, with all the usual sub-controls.
   *
   * @param parent containing controls
   * @param style standard style bits
   */
  public ElementKindPicker(Composite parent, int style) {
    super(parent, style);

    setLayout(new GridLayout());

    // Setup selection button bar
    Composite buttonBar = configButtonBar();
    buttonBar.setLayoutData(
        new GridData(SWT.LEFT, SWT.CENTER, false, false));

    // Initialize the table control
    Table elementKindTable = new Table(
        parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
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

  /**
   * Create a button bar with Select All, Select None, and Invert Selection
   * buttons.  Since the buttons use ElementKindPicker methods to implement
   * the selection methods, the button bar is simply constructed on the
   * current instance (i.e. {@code this}).
   * 
   * @return Composite suitable ready for layout options
   */
  private Composite configButtonBar() {
    Composite buttonBar = new Composite(this, SWT.None);
    buttonBar.setLayout(new RowLayout());

    Button selectAll = new Button(buttonBar, SWT.PUSH);
    selectAll.setText("Select All");
    selectAll.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        StructuredSelection nextSelection =
            new StructuredSelection(getInput().toArray());
        elementKindViewer.setSelection(nextSelection, true);
      }
    });

    Button selectNone = new Button(buttonBar, SWT.PUSH);
    selectNone.setText("Select None");
    selectNone.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        StructuredSelection nextSelection = new StructuredSelection();
        elementKindViewer.setSelection(nextSelection, true);
      }
    });

    Button reverseAll = new Button(buttonBar, SWT.PUSH);
    reverseAll.setText("Invert Selection");
    reverseAll.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        invertSelection();
      }
    });

    return buttonBar;
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
   * Provide the collection of selected Element kinds.
   * @return collection of selected Element kinds
   */
  public Collection<Class<? extends Element>> getSelectedElementKindSet() {
    // JFace guarantees that TableViewers always provide a structured selection
    IStructuredSelection selectedElementKinds = getSelection();

    Collection<Class<? extends Element>> result = Lists.newArrayList();
    for(Object obj : selectedElementKinds.toList()) {
      ElementKindDescriptor item = (ElementKindDescriptor) obj;
      result.add(item.getElementKind());
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  private Collection<ElementKindDescriptor> getInput() {
    Collection<ElementKindDescriptor> result = 
        (Collection<ElementKindDescriptor>) elementKindViewer.getInput();
    if (null == result) {
      return ImmutableList.of();
    }
    return result;
  }

  public void setInput(Collection<ElementKindDescriptor> elementKinds) {
    elementKindViewer.setInput(elementKinds);
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

  private void invertSelection() {
    Collection<?> currSelection = getSelection().toList();
    Collection<ElementKindDescriptor> invert = Lists.newArrayList();
    for (ElementKindDescriptor descr: getInput()) {
      if (!currSelection.contains(descr)) {
        invert.add(descr);
      }
    }
    StructuredSelection nextSelection =
        new StructuredSelection(invert.toArray());
    elementKindViewer.setSelection(nextSelection, true);
  }

  private IStructuredSelection getSelection() {
    IStructuredSelection selectedElementKinds = 
        (IStructuredSelection) elementKindViewer.getSelection();
    return selectedElementKinds;
  }

  /**
   * A simple LabelProvider that can unpack a ElementKindDescriptor.
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
