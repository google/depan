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

package com.google.devtools.depan.view_doc.eclipse.ui.widgets;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.platform.AlphabeticSorter;
import com.google.devtools.depan.platform.Colors;
import com.google.devtools.depan.platform.InverseSorter;
import com.google.devtools.depan.platform.LabelProviderToString;
import com.google.devtools.depan.platform.eclipse.ui.tables.EditColTableDef;
import com.google.devtools.depan.view_doc.model.EdgeDisplayProperty;
import com.google.devtools.depan.view_doc.model.EdgeDisplayRepository;

import com.google.common.collect.Lists;

import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Run a view of the known relations as its own reusable "part".
 */
public class EdgeDisplayTableControl extends Composite {

  public static final String COL_NAME = "Name";
  public static final String COL_SOURCE = "Source";
  public static final String COL_COLOR = "Color";
  public static final String COL_WIDTH = "Width";
  public static final String COL_STYLE = "Style";
  public static final String COL_SHAPE = "Shape";
  public static final String COL_ARROWHEAD = "Arrowhead";

  public static final int INDEX_NAME = 0;
  public static final int INDEX_SOURCE = 1;
  public static final int INDEX_COLOR = 2;
  public static final int INDEX_STYLE = 3;
  public static final int INDEX_ARROWHEAD = 4;

  private static final EditColTableDef[] TABLE_DEF = new EditColTableDef[] {
    new EditColTableDef(COL_NAME, false, COL_NAME, 180),
    new EditColTableDef(COL_SOURCE, false, COL_SOURCE, 80),
    new EditColTableDef(COL_COLOR, true, COL_COLOR, 80),
//    new EditColTableDef(COL_WIDTH, false, COL_WIDTH, 180),
    new EditColTableDef(COL_STYLE, true, COL_STYLE, 60),
    new EditColTableDef(COL_ARROWHEAD, true, COL_ARROWHEAD, 110),
//    new EditColTableDef(COL_SHAPE, false, COL_SHAPE, 180),
  };

  private static final String[] LINE_WIDTHS = {
    "0", "1", "2", "3", "4"
  };

  private static final String[] LINE_SHAPES = {
    "arched", "straight"
  };

  private static final String[] UPDATE_COLUMNS = new String [] {
    COL_COLOR, COL_WIDTH, COL_ARROWHEAD
  };

  private class ControlChangeListener
      implements EdgeDisplayRepository.ChangeListener {

    @Override
    public void edgeDisplayChanged(Relation relation, EdgeDisplayProperty props) {
      propViewer.update(relation, UPDATE_COLUMNS);
    }
  }

  private ControlChangeListener propListener;

  private EdgeDisplayRepository propRepo;

  private TableViewer propViewer;

  public EdgeDisplayTableControl(Composite parent) {
    super(parent, SWT.NONE);

    GridLayout gridLayout = new GridLayout();
    gridLayout.marginHeight = 0;
    gridLayout.marginWidth = 0;
    setLayout(gridLayout);

    propViewer = new TableViewer(this,
        SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
    // set up label provider
    propViewer.setLabelProvider(new EdgeDisplayLabelProvider());

    // Set up layout properties
    Table propTableControl = propViewer.getTable();
    propTableControl.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));
    propTableControl.setToolTipText("Edge Display Properties");

    // initialize the table
    propTableControl.setHeaderVisible(true);
    EditColTableDef.setupTable(TABLE_DEF, propTableControl);

    // Configure cell editing
    CellEditor[] cellEditors = new CellEditor[6];
    cellEditors[INDEX_NAME] = null;
    cellEditors[INDEX_SOURCE] = null;
    cellEditors[INDEX_COLOR] = new ColorCellEditor(propTableControl);
    cellEditors[INDEX_STYLE] = new ComboBoxCellEditor(propTableControl,
        toString(EdgeDisplayProperty.LineStyle.values(), true));
    cellEditors[INDEX_ARROWHEAD] = new ComboBoxCellEditor(propTableControl,
        toString(EdgeDisplayProperty.ArrowheadStyle.values(), true));

    propViewer.setCellEditors(cellEditors);
    propViewer.setColumnProperties(EditColTableDef.getProperties(TABLE_DEF));
    propViewer.setCellModifier(new EdgeDisplayCellModifier());

    // TODO: Add column sorters, filters?
    configSorters(propTableControl);

    // Configure content last: use updateTable() to render relations
    propViewer.setContentProvider(ArrayContentProvider.getInstance());
  }

  private String[] toString(Object[] objs, boolean lowercase) {
    String[] s = new String[objs.length];
    int i = 0;
    for (Object o : objs) {
      s[i++] = lowercase ? o.toString().toLowerCase() : o.toString();
    }
    return s;
  }


  /**
   * Fill the list with {@link Relation}s.
   * Since rendering depends on propRepo, set input after 
   * the propRepo is installed.
   */
  public void setInput(Collection<Relation> relations) {
    propViewer.setInput(relations);
  }

  /**
   * Provide the set of currently selected {@link Relation} rows in
   * the {@link TableViewer}.
   */
  public Collection<Relation> getSelection() {
    ISelection selection = propViewer.getSelection();
    if (!(selection instanceof IStructuredSelection)) {
      return Collections.emptyList();
    }
    List<?> choices = ((IStructuredSelection) selection).toList();
    if (choices.isEmpty()) {
      return Collections.emptyList();
    }
    Collection<Relation> result =
        Lists.newArrayListWithExpectedSize(choices.size());
    for (Object item : choices) {
      if (!(item instanceof Relation)) {
        continue;
      }
      result .add((Relation) item);
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  public Collection<Relation> getInput() {
    return (Collection<Relation>) propViewer.getInput();
  }

  public void setEdgeDisplayRepository(EdgeDisplayRepository edgeDisplayRepo) {
    this.propRepo = edgeDisplayRepo;
    propListener = new ControlChangeListener();
    propRepo.addChangeListener(propListener);
  }

  public void removeEdgeDisplayRepository(EdgeDisplayRepository edgeDisplayRepo) {
    if (null != propListener) {
      this.propRepo.removeChangeListener(propListener);
      propListener = null;
    }
    this.propRepo = null;
  }

  /////////////////////////////////////
  // Property repository methods

  /**
   * Acquire properties directly, avoid setting up a default.
   */
  private void saveDisplayProperty(
      Relation relation, EdgeDisplayProperty props) {
    propRepo.setDisplayProperty(relation, props);
  }

  /**
   * Acquire properties directly, avoid setting up a default.
   */
  private EdgeDisplayProperty loadDisplayProperty(Relation relation) {
    return propRepo.getDisplayProperty(relation);
  }

  /**
   * Utility method for both the label provider and cell modifier.
   * Note that the default constructor for {@link EdgeDisplayProperty}
   * uses the default values for all member elements.
   */
  private EdgeDisplayProperty getDisplayProperty(Relation relation) {
    EdgeDisplayProperty relationProp = loadDisplayProperty(relation);
    if (null != relationProp) {
      return relationProp;
    }
    // Provide the default if none are persisted.
    return new EdgeDisplayProperty();
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
    Table tableControl = (Table) propViewer.getControl();
    if (column != tableControl.getSortColumn()) {
      return SWT.DOWN;
    }
    // If it is unsorted (SWT.NONE), assume down sort
    return (SWT.DOWN == tableControl.getSortDirection())
        ? SWT.UP : SWT.DOWN;
  }

  private void setSortColumn(
      TableColumn column, int colIndex, int direction) {

    ViewerSorter sorter = buildColumnSorter(colIndex);
    if (SWT.UP == direction) {
      sorter = new InverseSorter(sorter);
    }

    Table tableControl = (Table) propViewer.getControl();
    propViewer.setSorter(sorter);
    tableControl.setSortColumn(column);
    tableControl.setSortDirection(direction);
  }

  private ViewerSorter buildColumnSorter(int colIndex) {

    // By default, use an alphabetic sort over the column labels.
    ITableLabelProvider labelProvider =
        (ITableLabelProvider) propViewer.getLabelProvider();
    ViewerSorter result = new AlphabeticSorter(
        new LabelProviderToString(labelProvider, colIndex));
    return result;
  }

  /////////////////////////////////////
  // Label provider for table cell text

  private class EdgeDisplayLabelProvider extends LabelProvider
      implements ITableLabelProvider {

    @Override
    public String getColumnText(Object element, int columnIndex) {
      if (element instanceof Relation) {
        Relation relation = (Relation) element;
        EdgeDisplayProperty prop = getDisplayProperty(relation);
        switch (columnIndex) {
        case INDEX_NAME:
          return relation.toString();
        case INDEX_SOURCE:
          return getSourceLabelForRelation(relation);
        case INDEX_COLOR:
          return getColorName(prop);
        // case INDEX_WIDTH:
        case INDEX_STYLE:
          if (null != prop) {
            return prop.getLineStyle().toString().toLowerCase();
          }
          return null;
        case INDEX_ARROWHEAD:
          if (null != prop) {
            return prop.getArrowhead().toString().toLowerCase();
          }
          return null;
        }
      }
      return null;
    }

    private String getSourceLabelForRelation(Relation relation) {
      ClassLoader loader = relation.getClass().getClassLoader();
      String result = loader.toString();
      int bound = result.length();
      int start = Math.max(0, bound - 12);
      return result.substring(start);

      // TODO: More like this, with an relation registry
      // SourcePlugin plugin = relPlugin.get(relation);
      // SourcePluginRegistry registry = SourcePluginRegistry.getInstance();
      // String sourceId = registry.getPluginId(plugin);
      // return registry.getSourcePluginEntry(sourceId).getSource();
    }

    private String getColorName(EdgeDisplayProperty prop) {
      if (null == prop) {
        return null;
      }
      Color color = prop.getColor();
      if (null == color) {
        return null;
      }
      String result = StringConverter.asString(Colors.rgbFromColor(color));
      return "(" + result + ")";
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
      return null;
    }
  }

  /////////////////////////////////////
  // Value provider/modifier for edit cells

  private class EdgeDisplayCellModifier implements ICellModifier{

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
      EdgeDisplayProperty relProp = getDisplayProperty(relation);
      if (COL_COLOR.equals(property)) {
        Color relColor = relProp.getColor();
        if (null == relColor) {
          return new RGB(0, 0, 0);
        }
         RGB result = Colors.rgbFromColor(relColor);
        return result;
      }
      if (COL_ARROWHEAD.equals(property)) {
        return relProp.getArrowhead().ordinal();
      }
      if (COL_STYLE.equals(property)) {
        return relProp.getLineStyle().ordinal();
      }
      if (COL_SHAPE.equals(property)) {
        
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

      Relation relation = (Relation) modifiedObject;

      EdgeDisplayProperty relProp = loadDisplayProperty(relation);
      if (null == relProp) {
        return; // For example, when there is no editor.
      }

      if (property.equals(COL_STYLE) && (value instanceof Integer)) {
        relProp.setLineStyle(EdgeDisplayProperty.LineStyle.values()[(Integer) value]);
      } else if (property.equals(COL_ARROWHEAD) && (value instanceof Integer)) {
        relProp.setArrowhead(EdgeDisplayProperty.ArrowheadStyle.values()[(Integer) value]);
      } else if (property.equals(COL_COLOR) && (value instanceof RGB)) {
        Color newColor = Colors.colorFromRgb((RGB) value);
        relProp.setColor(newColor);
      }

      saveDisplayProperty(relation, relProp);
      // Viewer update via ChangeListener
    }
  }

  public void setSelection(ISelection selection) {
    propViewer.setSelection(selection);
  }

  public void refresh(boolean refresh) {
    propViewer.refresh(refresh);
  }
}
