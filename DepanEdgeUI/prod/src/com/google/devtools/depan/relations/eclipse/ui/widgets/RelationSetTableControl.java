/*
 * Copyright 2014 The Depan Project Authors
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

package com.google.devtools.depan.relations.eclipse.ui.widgets;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.graph.registry.RelationRegistry;
import com.google.devtools.depan.platform.AlphabeticSorter;
import com.google.devtools.depan.platform.InverseSorter;
import com.google.devtools.depan.platform.LabelProviderToString;
import com.google.devtools.depan.platform.PlatformResources;
import com.google.devtools.depan.platform.eclipse.ui.tables.EditColTableDef;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Selections;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.relations.models.RelationSetRepository;

import com.google.common.collect.Sets;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import java.util.Collection;

/**
 * Show a table of {@link Relation}s, marked visible/included.  The
 * {@link Relation}s shown depend on a universe thats is defined externally
 * (via the {@link #setInput(Collection)} method). The {@link Relation}s
 * marked as included depend on the supplied {@link RelationSet}.
 * 
 * Assumes the set of {@link Relation}s being shown (the universe) will not
 * change externally.  Update the control for external changes via the
 * {@link #setInput(Collection)} method. This might be part of an document
 * change in a containing {@link Control}.
 * 
 * Changes to the set of visible {@link Relation}s are managed through the
 * {@link RelationSetRepository.ChangeListener} and the 
 * {@link #setVisibiltyRepository(RelationSetRepository)} and
 * other feature specific methods.
 * 
 * Although the caller sets the range of displayed relations via the
 * {@link #setInput(Collection)} method, this {@link Control} hides this
 * notion of a fixed "universe" from normal use.  This facilitates the use
 * of "intentional" {@link RelationSet}s (e.g. {@code ALL}, {@code EMPTY},
 * {@code EVEN}) in concert with the commonly used "extensional"
 * {@link RelationSet}s (e.g. enumerated {@link Relation}s). The method
 * {@link #selectedInvertRelations()} 
 */
public class RelationSetTableControl extends Composite {

  public static final String COL_NAME = "Name";
  public static final String COL_SOURCE = "Source";
  public static final String COL_VISIBLE = "Visible";

  public static final int INDEX_NAME = 0;
  public static final int INDEX_SOURCE = 1;
  public static final int INDEX_VISIBLE = 2;

  private static final EditColTableDef[] TABLE_DEF = new EditColTableDef[] {
    new EditColTableDef(COL_NAME, false, COL_NAME, 350),
    new EditColTableDef(COL_SOURCE, false, COL_SOURCE, 180),
    new EditColTableDef(COL_VISIBLE, true, COL_VISIBLE, 180),
  };

  /////////////////////////////////////
  // RelationSet integration

  public static final String[] UPDATE_VISIBLE = {COL_VISIBLE};

  private class ControlRelationVisibleListener
      implements RelationSetRepository.ChangeListener {

    @Override
    public void includedRelationChanged(Relation relation, boolean visible) {
      updateRelationColumns(relation, UPDATE_VISIBLE);
    }

    @Override
    public void relationsChanged() {
      refresh();
    }
  }

  private ControlRelationVisibleListener relSetListener;

  private RelationSetRepository relSetRepo;

  /////////////////////////////////////
  // UX Elements

  private TableViewer relSetViewer;

  /////////////////////////////////////
  // Public methods

  public RelationSetTableControl(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(Widgets.buildContainerLayout(1));

    // Layout embedded table
    relSetViewer = new TableViewer(this,
        SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);

    // Set up layout properties
    Table relSetTable = relSetViewer.getTable();
    relSetTable.setLayoutData(Widgets.buildGrabFillData());

    // initialize the table
    relSetTable.setHeaderVisible(true);
    relSetTable.setToolTipText("List of Relations");
    EditColTableDef.setupTable(TABLE_DEF, relSetTable);

    // Configure cell editing
    CellEditor[] cellEditors = new CellEditor[TABLE_DEF.length];
    cellEditors[INDEX_NAME] = null;
    cellEditors[INDEX_SOURCE] = null;
    cellEditors[INDEX_VISIBLE] = new CheckboxCellEditor(relSetTable);

    // Configure table properties.
    relSetViewer.setCellEditors(cellEditors);
    relSetViewer.setLabelProvider(new ControlLabelProvider());
    relSetViewer.setColumnProperties(EditColTableDef.getProperties(TABLE_DEF));
    relSetViewer.setCellModifier(new ControlCellModifier());
    relSetViewer.setContentProvider(ArrayContentProvider.getInstance());

    configSorters(relSetTable);
  }

  /**
   * Fill the list with {@link Relation}s.  This is normally the universe
   * of relations for the current document.
   * 
   * Whether they show on or off depends on the separately supplied
   * {@link RelationSetRepository}.
   */
  public void setInput(Collection<Relation> relations) {
    relSetViewer.setInput(relations);
  }

  public void setVisibiltyRepository(RelationSetRepository visRepo) {
    this.relSetRepo = visRepo;
    relSetListener = new ControlRelationVisibleListener();
    visRepo.addChangeListener(relSetListener);
  }

  public void removeRelSetRepository(RelationSetRepository visRepo) {
    if (null != relSetListener) {
      this.relSetRepo.removeChangeListener(relSetListener);
    }
    relSetListener = null;
    this.relSetRepo = null;
  }

  public void refresh() {
    relSetViewer.refresh();
  }

  @SuppressWarnings("unchecked")
  private Collection<Relation> getInput() {
    return (Collection<Relation>) relSetViewer.getInput();
  }

  private void updateRelationColumns(Relation relation, String[] cols) {
    relSetViewer.update(relation, cols);
  }

  public void addSelectionChangedListener(
      ISelectionChangedListener listener) {
    relSetViewer.addSelectionChangedListener(listener);
  }

  public void removeSelectionChangedListener(
      ISelectionChangedListener listener) {
    relSetViewer.removeSelectionChangedListener(listener);
  }

  /////////////////////////////////////
  // External actions
  // Useful for buttons external to the table (e.g. clear all)

  public void clearAll() {
    clearRelations(getInput());
  }

  public void invertAll() {
    invertRelations(getInput());
  }

  public void checkAll() {
    checkRelations(getInput());
  }

  public Collection<Relation> getSelectedRelations() {
    ISelection selection = relSetViewer.getSelection();
    return Selections.getSelection(selection, Relation.class);
  }

  public void setSelectedRelations(
      Collection<Relation> relations, boolean reveal) {
    StructuredSelection nextSelection =
        new StructuredSelection(relations.toArray());
    relSetViewer.setSelection(nextSelection, reveal);
  }

  public void selectInverseRelations(Collection<Relation> relations) {
    Collection<Relation> inverse = computeInverseRelations(relations);
    setSelectedRelations(inverse, true);
  }

  private Collection<Relation> computeInverseRelations(
      Collection<Relation> relations) {

    Collection<Relation> universe = getInput();
    if (relations.isEmpty()) {
      return universe;
    }

    Collection<Relation> result = Sets.newHashSet(universe);
    result.removeAll(relations);
    return result;
  }

  /////////////////////////////////////
  // Property repository methods

  public void clearRelations(Collection<Relation> relations) {
    for (Relation relation : relations) {
      relSetRepo.setRelationChecked(relation, false);
    }
  }

  public void invertRelations(Collection<Relation> relations) {
    for (Relation relation : relations) {
      boolean wasChecked = relSetRepo.isRelationIncluded(relation);
      relSetRepo.setRelationChecked(relation, !wasChecked);
    }
  }

  public void checkRelations(Collection<Relation> relations) {
    for (Relation relation : relations) {
      relSetRepo.setRelationChecked(relation, true);
    }
  }

  /////////////////////////////////////
  // Column sorting

  private void configSorters(Table table) {
    int index = 0;
    for (TableColumn column : table.getColumns()) {
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
    Table tableControl = (Table) relSetViewer.getControl();
    if (column != tableControl.getSortColumn()) {
      return SWT.DOWN;
    }
    // If it is unsorted (SWT.NONE), assume down sort
    return (SWT.DOWN == tableControl.getSortDirection())
        ? SWT.UP : SWT.DOWN;
  }

  private void setSortColumn(
      TableColumn column, int colIndex, int direction) {

    ViewerComparator sorter = buildColumnSorter(colIndex);
    if (SWT.UP == direction) {
      sorter = new InverseSorter(sorter);
    }

    Table tableControl = (Table) relSetViewer.getControl();
    relSetViewer.setComparator(sorter);
    tableControl.setSortColumn(column);
    tableControl.setSortDirection(direction);
  }

  private ViewerComparator buildColumnSorter(int colIndex) {
    if (INDEX_VISIBLE == colIndex) {
      return new BooleanViewSorter();
    }

    // By default, use an alphabetic sort over the column labels.
    ITableLabelProvider labelProvider =
        (ITableLabelProvider) relSetViewer.getLabelProvider();
    ViewerComparator result = new AlphabeticSorter(
        new LabelProviderToString(labelProvider, colIndex));
    return result;
  }

  private class BooleanViewSorter extends ViewerComparator {

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
      boolean vis1 = isVisible(e1);
      boolean vis2 = isVisible(e2);
      return Boolean.compare(vis1, vis2);
    }

    private boolean isVisible(Object e1) {
      if (!(e1 instanceof Relation)) {
        return false;
      }
      return relSetRepo.isRelationIncluded((Relation) e1);
    }
  }

  /////////////////////////////////////
  // Label provider for table cell text

  /**
   * Cannot be singleton/{@code static} since it relies on {@link #relSetRepo}
   * for field values.
   */
  private class ControlLabelProvider extends LabelProvider
      implements ITableLabelProvider {

    @Override
    public String getColumnText(Object element, int columnIndex) {
      if (element instanceof Relation) {
        Relation relation = (Relation) element;
        switch (columnIndex) {
        case INDEX_NAME:
          return relation.toString();
        case INDEX_SOURCE:
          return getSourceLabelForRelation(relation);
        }
      }
      return null;
    }

    private String getSourceLabelForRelation(Relation relation) {
      return RelationRegistry.getRegistryRelationSource(relation);
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
      if (!(element instanceof Relation)) {
        return null;
      }

      switch (columnIndex) {
      case INDEX_VISIBLE:
        boolean isVis = relSetRepo.isRelationIncluded((Relation) element);
        return PlatformResources.getOnOff(isVis);
      }
      return null;
    }
  }

  /////////////////////////////////////
  // Value provider/modifier for edit cells

  private class ControlCellModifier implements ICellModifier{

    @Override
    public boolean canModify(Object element, String property) {
      return EditColTableDef.get(TABLE_DEF, property).isEditable();
    }

    @Override
    public Object getValue(Object element, String property) {
      if (!(element instanceof Relation)) {
        return null;
      }
      Relation relation = (Relation) element;
      if (COL_VISIBLE.equals(property)) {
        return relSetRepo.isRelationIncluded(relation);
      }
      return null;
    }

    @Override
    public void modify(Object element, String property, Object value) {
      if (!(element instanceof TableItem)) {
        return;
      }
      Object modifiedObject = ((TableItem) element).getData();
      if (!(modifiedObject instanceof Relation)) {
        return;
      }
      if (property.equals(COL_VISIBLE) && (value instanceof Boolean)) {
        Relation relation = (Relation) modifiedObject;
        relSetRepo.setRelationChecked(
            relation, ((Boolean) value).booleanValue());
        relSetViewer.update(relation, UPDATE_VISIBLE);
      }
    }
  }
}
