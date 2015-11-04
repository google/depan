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

package com.google.devtools.depan.eclipse.views.tools;

import com.google.devtools.depan.eclipse.editors.EdgeDisplayProperty;
import com.google.devtools.depan.eclipse.plugins.SourcePlugin;
import com.google.devtools.depan.eclipse.plugins.SourcePluginEntry;
import com.google.devtools.depan.eclipse.plugins.SourcePluginRegistry;
import com.google.devtools.depan.eclipse.utils.AlphabeticSorter;
import com.google.devtools.depan.eclipse.utils.EditColTableDef;
import com.google.devtools.depan.eclipse.utils.InverseSorter;
import com.google.devtools.depan.eclipse.utils.LabelProviderToString;
import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.utils.Tools;
import com.google.devtools.depan.graph.api.Relation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import java.awt.Color;
import java.util.List;
import java.util.Map;

/**
 * Run a view of the known relations as its own reusable "part".
 */
public class RelEditorTableView {

  public static final String COL_NAME = "Name";
  public static final String COL_SOURCE = "Source";
  public static final String COL_VISIBLE = "Visible";
  public static final String COL_COLOR = "Color";
  public static final String COL_WIDTH = "Width";
  public static final String COL_STYLE = "Style";
  public static final String COL_SHAPE = "Shape";
  public static final String COL_ARROWHEAD = "Arrowhead";

  /** To indicate that the "Visible" column should be updated. */
  public static final String[] UPDATE_VISIBLE = {COL_VISIBLE};

  public static final int INDEX_NAME = 0;
  public static final int INDEX_SOURCE = 1;
  public static final int INDEX_VISIBLE = 2;
  public static final int INDEX_COLOR = 3;
//  public static final int INDEX_WIDTH = 2;
  public static final int INDEX_STYLE = 4;
  public static final int INDEX_ARROWHEAD = 5;
//  public static final int INDEX_SHAPE = 0;

  private static final EditColTableDef[] TABLE_DEF = new EditColTableDef[] {
    new EditColTableDef(COL_NAME, false, COL_NAME, 180),
    new EditColTableDef(COL_SOURCE, false, COL_SOURCE, 80),
    new EditColTableDef(COL_VISIBLE, true, COL_VISIBLE, 70),
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

  /**
   * Abstract repository that provides access to the relation properties.
   * 
   * The normal one, from RelationPickerTool, is editor aware.
   */
  public static interface RelPropRepository {

    /**
     * Provide the display properties for the supplied relation.
     */
    EdgeDisplayProperty getDisplayProperty(Relation rel);

    /**
     * Change the display properties for the supplied relation to the new
     * values.
     */
    void setDisplayProperty(Relation rel, EdgeDisplayProperty prop);
  }

  private final RelPropRepository propRepo;
  private TableViewer viewer;
  private Map<Relation, SourcePluginEntry> relPlugin = Maps.newHashMap();;

  public RelEditorTableView(RelPropRepository propRepo) {
    this.propRepo = propRepo;
  }

  public TableViewer setupViewer(Composite parent) {
    viewer = new TableViewer(parent,
        SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
    // set up label provider
    viewer.setLabelProvider(new RelEditorLabelProvider());

    // Set up layout properties
    Table relTableControl = viewer.getTable();
    relTableControl.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));
    relTableControl.setToolTipText("List of Relations");

    // initialize the table
    relTableControl.setHeaderVisible(true);
    EditColTableDef.setupTable(TABLE_DEF, relTableControl);

    // Configure cell editing
    CellEditor[] cellEditors = new CellEditor[6];
    cellEditors[INDEX_NAME] = null;
    cellEditors[INDEX_SOURCE] = null;
    cellEditors[INDEX_VISIBLE] = new CheckboxCellEditor(relTableControl);
    cellEditors[INDEX_COLOR] = new ColorCellEditor(relTableControl);
    cellEditors[INDEX_STYLE] = new ComboBoxCellEditor(relTableControl,
        Tools.toString(EdgeDisplayProperty.LineStyle.values(), true));
    cellEditors[INDEX_ARROWHEAD] = new ComboBoxCellEditor(relTableControl,
        Tools.toString(EdgeDisplayProperty.ArrowheadStyle.values(), true));

    viewer.setCellEditors(cellEditors);
    viewer.setColumnProperties(EditColTableDef.getProperties(TABLE_DEF));
    viewer.setCellModifier(new RelEditorCellModifierHandler());

    // TODO: Add column sorters, filters?
    configSorters(relTableControl);

    // Configure content last
    viewer.setContentProvider(ArrayContentProvider.getInstance());
    viewer.setInput(buildRelations());

    return viewer;
  }

  private List<Relation> buildRelations() {
    List<Relation> result = Lists.newArrayList();

    // Populate the list viewer with all known relations.
    for (SourcePluginEntry entry : SourcePluginRegistry.getEntries()) {
      SourcePlugin plugin;
      try {
        plugin = entry.getInstance();
      } catch (CoreException e) {
        e.printStackTrace();
        continue;
      }
      for (Relation r : plugin.getRelations()) {
        relPlugin.put(r, entry);
        result.add(r);
      }
    }

    return result;
  }

  /**
   * Utility method for both the label provider and cell modifier.
   * Note that the default constructor for {@link EdgeDisplayProperty}
   * uses the default values for all member elements.
   */
  private EdgeDisplayProperty getDisplayProperty(Relation rel) {
    EdgeDisplayProperty relProp = propRepo.getDisplayProperty(rel);
    if (null != relProp) {
      return relProp;
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
    Table tableControl = (Table) viewer.getControl();
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

    Table tableControl = (Table) viewer.getControl();
    viewer.setSorter(sorter);
    tableControl.setSortColumn(column);
    tableControl.setSortDirection(direction);
  }

  private ViewerSorter buildColumnSorter(int colIndex) {
    // if (INDEX_VISIBLE == colIndex) {
    //   return new BooleanViewSorter();
    // }
    if (INDEX_VISIBLE == colIndex) {
      return new BooleanViewSorter();
    }

    // By default, use an alphabetic sort over the column labels.
    ITableLabelProvider labelProvider =
        (ITableLabelProvider) viewer.getLabelProvider();
    ViewerSorter result = new AlphabeticSorter(
        new LabelProviderToString(labelProvider, colIndex));
    return result;
  }

  private class BooleanViewSorter extends ViewerSorter {

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
      Relation rel = (Relation) e1;
      EdgeDisplayProperty prop = getDisplayProperty(rel);
      return prop.isVisible();
    }
  }

  /////////////////////////////////////
  // Label provider for table cell text

  private class RelEditorLabelProvider extends LabelProvider
      implements ITableLabelProvider {

    @Override
    public String getColumnText(Object element, int columnIndex) {
      if (element instanceof Relation) {
        Relation rel = (Relation) element;
        EdgeDisplayProperty prop = getDisplayProperty(rel);
        switch (columnIndex) {
        case INDEX_NAME:
          return rel.toString();
        case INDEX_SOURCE:
          return relPlugin.get(rel).getSource();
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
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
      if (!(element instanceof Relation)) {
        return null;
      }

      switch (columnIndex) {
      case INDEX_VISIBLE:
        Relation rel = (Relation) element;
        EdgeDisplayProperty prop = getDisplayProperty(rel);
        return Resources.getOnOff(prop.isVisible());
      }
      return null;
    }

    private String getColorName(EdgeDisplayProperty prop) {
      if (null == prop) {
        return null;
      }
      Color color = prop.getColor();
      if (null == color) {
        return null;
      }
      String result = StringConverter.asString(Tools.rgbFromColor(color));
      return "(" + result + ")";
    }
  }

  /////////////////////////////////////
  // Value provider/modifier for edit cells

  private class RelEditorCellModifierHandler implements ICellModifier{

    @Override
    public boolean canModify(Object element, String property) {
      return EditColTableDef.get(TABLE_DEF, property).isEditable();
    }

    @Override
    public Object getValue(Object element, String property) {
      if (element instanceof Relation) {
        Relation rel = (Relation) element;
        EdgeDisplayProperty relProp = getDisplayProperty(rel);
        if (COL_VISIBLE.equals(property)) {
          return relProp.isVisible();
        }
        if (COL_COLOR.equals(property)) {
          Color relColor = relProp.getColor();
          if (null == relColor) {
            return new RGB(0, 0, 0);
          }
          RGB result = Tools.rgbFromColor(relColor);
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
      }
      // TODO Auto-generated method stub
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

      Relation rel = (Relation) modifiedObject;

      EdgeDisplayProperty relProp = propRepo.getDisplayProperty(rel);
      if (null == relProp) {
        return; // For example, when there is no editor.
      }

      if (property.equals(COL_VISIBLE) && (value instanceof Boolean)) {
        relProp.setVisible((Boolean) value);
      } else if (property.equals(COL_STYLE) && (value instanceof Integer)) {
        relProp.setLineStyle(EdgeDisplayProperty.LineStyle.values()[(Integer) value]);
      } else if (property.equals(COL_ARROWHEAD) && (value instanceof Integer)) {
        relProp.setArrowhead(EdgeDisplayProperty.ArrowheadStyle.values()[(Integer) value]);
      } else if (property.equals(COL_COLOR) && (value instanceof RGB)) {
        Color newColor = Tools.colorFromRgb((RGB) value);
        relProp.setColor(newColor);
      }

      propRepo.setDisplayProperty(rel, relProp);
      viewer.update(rel, new String[] {property});
    }
  }
}
