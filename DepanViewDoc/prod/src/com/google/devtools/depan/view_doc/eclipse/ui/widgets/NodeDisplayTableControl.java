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
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.platform.AlphabeticSorter;
import com.google.devtools.depan.platform.Colors;
import com.google.devtools.depan.platform.InverseSorter;
import com.google.devtools.depan.platform.LabelProviderToString;
import com.google.devtools.depan.platform.PlatformResources;
import com.google.devtools.depan.platform.eclipse.ui.tables.EditColTableDef;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.model.EdgeDisplayProperty;
import com.google.devtools.depan.view_doc.model.NodeDisplayProperty;
import com.google.devtools.depan.view_doc.model.NodeDisplayRepository;

import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelection;
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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import java.awt.Color;
import java.util.Collection;

/**
 * Run a view of the known edges as its own reusable "part".
 */
/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class NodeDisplayTableControl extends Composite {

  public static final String COL_NAME = "Node";
  protected static final String COL_XPOS = "X";
  protected static final String COL_YPOS = "Y";
  protected static final String COL_VISIBLE = "Visible";
  protected static final String COL_SELECTED = "Selected";
  protected static final String COL_SIZE = "Size";
  public static final String COL_COLOR = "Color";

  public static final int INDEX_NAME = 0;
  public static final int INDEX_XPOS = 1;
  public static final int INDEX_YPOS = 2;
  public static final int INDEX_VISIBLE = 3;
  public static final int INDEX_SIZE = 4;
  public static final int INDEX_COLOR = 5;

  private static final EditColTableDef[] TABLE_DEF = new EditColTableDef[] {
    new EditColTableDef(COL_NAME, false, COL_NAME, 600),
    new EditColTableDef(COL_XPOS, false, COL_XPOS, 100),
    new EditColTableDef(COL_YPOS, false, COL_YPOS, 100),
    new EditColTableDef(COL_VISIBLE, true, COL_VISIBLE, 100),
    new EditColTableDef(COL_SIZE, true, COL_SIZE, 100),
    new EditColTableDef(COL_COLOR, true, COL_COLOR, 180)
  };

  private static final String[] UPDATE_COLUMNS = new String [] {
    COL_VISIBLE, COL_SIZE, COL_COLOR
  };

  private class ControlChangeListener
      implements NodeDisplayRepository.ChangeListener {

    @Override
    public void nodeDisplayChanged(GraphNode node, NodeDisplayProperty props) {
      propViewer.update(node, UPDATE_COLUMNS);
    }
  }

  private final TableViewer propViewer;

  private ControlChangeListener propListener;

  private NodeDisplayRepository propRepo;

  private ViewEditor viewer;

  public NodeDisplayTableControl(Composite parent) {
    super(parent, SWT.NONE);

    GridLayout gridLayout = new GridLayout();
    gridLayout.marginHeight = 0;
    gridLayout.marginWidth = 0;
    setLayout(gridLayout);

    propViewer = new TableViewer(this,
        SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
    // set up label provider
    propViewer.setLabelProvider(new PartLabelProvider());

    // Set up layout properties
    Table propTableControl = propViewer.getTable();
    propTableControl.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));
    propTableControl.setToolTipText("Edge Display Properties");

    // initialize the table
    propTableControl.setHeaderVisible(true);
    EditColTableDef.setupTable(TABLE_DEF, propTableControl);

    // Configure cell editing
    CellEditor[] cellEditors = new CellEditor[TABLE_DEF.length];
    cellEditors[INDEX_NAME] = null;
    cellEditors[INDEX_XPOS] = null;
    cellEditors[INDEX_YPOS] = null;
    cellEditors[INDEX_VISIBLE] = new CheckboxCellEditor(propTableControl);
    cellEditors[INDEX_SIZE] = new ComboBoxCellEditor(propTableControl,
        toString(NodeDisplayProperty.Size.values(), true));
    cellEditors[INDEX_COLOR] = new ColorCellEditor(propTableControl);

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
   * Fill the list with {@link GraphNode}s.
   * Since rendering depends on propRepo, set input after 
   * the propRepo is installed.
   */
  public void setInput(Collection<GraphNode> nodes) {
    propViewer.setInput(nodes);
  }

  @SuppressWarnings("unchecked")
  public Collection<Relation> getInput() {
    return (Collection<Relation>) propViewer.getInput();
  }

  /**
   * @param viewer effective repository for node's (x,y) position
   * @param propRepo source for node display properties
   */
  public void setNodeDisplayRepository(ViewEditor viewer, NodeDisplayRepository propRepo) {
    this.viewer = viewer;
    this.propRepo = propRepo;
    propListener = new ControlChangeListener();
    propRepo.addChangeListener(propListener);
  }

  public void removeNodeDisplayRepository(NodeDisplayRepository propRepo) {
    if (null != propListener) {
      this.propRepo.removeChangeListener(propListener);
      propListener = null;
    }
    this.propRepo = null;
    this.viewer = null;
  }

  /////////////////////////////////////
  // Property repository methods

  /**
   * Acquire properties directly, avoid setting up a default.
   */
  private void saveDisplayProperty(
      GraphNode node, NodeDisplayProperty props) {
    propRepo.setDisplayProperty(node, props);
  }

  /**
   * Acquire properties directly, avoid setting up a default.
   */
  private NodeDisplayProperty loadDisplayProperty(GraphNode node) {
    return propRepo.getDisplayProperty(node);
  }

  /**
   * Utility method for both the label provider and cell modifier.
   * Note that the default constructor for {@link EdgeDisplayProperty}
   * uses the default values for all member elements.
   */
  private NodeDisplayProperty getDisplayProperty(GraphNode node) {
    return loadDisplayProperty(node);
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
    if (INDEX_VISIBLE == colIndex) {
      return new BooleanViewSorter();
    }

    // By default, use an alphabetic sort over the column labels.
    ITableLabelProvider labelProvider =
        (ITableLabelProvider) propViewer.getLabelProvider();
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

    private boolean isVisible(Object item) {
      if (!(item instanceof GraphNode)) {
        return false;
      }
      GraphNode node = (GraphNode) item;
      NodeDisplayProperty prop = propRepo.getDisplayProperty(node);
      return prop.isVisible();
    }
  }

  /////////////////////////////////////
  // Label provider for table cell text

  private class PartLabelProvider extends LabelProvider
      implements ITableLabelProvider {

    @Override
    public String getColumnText(Object element, int columnIndex) {
      if (element instanceof GraphNode) {
        GraphNode node = (GraphNode) element;
        NodeDisplayProperty prop = getDisplayProperty(node);
        switch (columnIndex) {
        case INDEX_NAME:
          return node.toString();
        case INDEX_XPOS:
          return fmtDouble(viewer.getXPos(node));
        case INDEX_YPOS:
          return fmtDouble(viewer.getYPos(node));
        case INDEX_COLOR:
          return getColorName(prop);
        case INDEX_SIZE:
          return getSizeName(prop);
        }
      }
      return null;
    }

    private String fmtDouble(double pos) {
      // TODO: 3 significant digits
      return Double.toString(pos);
    }

    private String getColorName(NodeDisplayProperty prop) {
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

    private String getSizeName(NodeDisplayProperty prop) {
      return prop.getSize().toString().toLowerCase();
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
      if (element instanceof GraphNode) {
        GraphNode node = (GraphNode) element;
        NodeDisplayProperty prop = getDisplayProperty(node);
        switch (columnIndex) {
        case INDEX_VISIBLE:
          return PlatformResources.getOnOff(prop.isVisible());
        }
      }
      // Fall through and unknown type
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
      if (!(element instanceof GraphNode)) {
        return null;
      }
      GraphNode node = (GraphNode) element;
      NodeDisplayProperty nodeProp = getDisplayProperty(node);
      if (COL_COLOR.equals(property)) {
        Color relColor = nodeProp.getColor();
        if (null == relColor) {
          return new RGB(0, 0, 0);
        }
        RGB result = Colors.rgbFromColor(relColor);
        return result;
      }
      if (COL_VISIBLE.equals(property)) {
        return Boolean.valueOf(nodeProp.isVisible());
      }
      if (COL_SIZE.equals(property)) {
        return nodeProp.getSize().ordinal();
      }
      return null;
    }

    @Override
    public void modify(Object element, String property, Object value) {
      if (!(element instanceof TableItem)) {
        return;
      }
      Object modifiedObject = ((TableItem) element).getData();
      if (!(modifiedObject instanceof GraphNode)) {
        return;
      }

      GraphNode node = (GraphNode) modifiedObject;

      NodeDisplayProperty nodeProp = loadDisplayProperty(node);
      if (null == nodeProp) {
        return; // For example, when there is no editor.
      }

      if (property.equals(COL_VISIBLE) && (value instanceof Boolean)) {
        nodeProp.setVisible(((Boolean) value).booleanValue());
      } else if (property.equals(COL_SIZE) && (value instanceof Integer)) {
        nodeProp.setSize(NodeDisplayProperty.Size.values()[(Integer) value]);
      } else if (property.equals(COL_COLOR) && (value instanceof RGB)) {
        Color newColor = Colors.colorFromRgb((RGB) value);
        nodeProp.setColor(newColor);
      }

      saveDisplayProperty(node, nodeProp);
      // Viewer updates via ChangeListener
    }
  }

  public void setSelection(ISelection selection) {
    propViewer.setSelection(selection);
  }

  public void refresh(boolean refresh) {
    propViewer.refresh(refresh);
  }
}
