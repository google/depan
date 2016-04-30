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

package com.google.devtools.depan.eclipse.views.tools;

import com.google.devtools.depan.eclipse.editors.EdgeDisplayProperty;
import com.google.devtools.depan.eclipse.editors.EdgeDisplayProperty.ArrowheadStyle;
import com.google.devtools.depan.eclipse.editors.EdgeDisplayProperty.LineStyle;
import com.google.devtools.depan.eclipse.editors.ViewEditor;
import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.utils.Tools;
import com.google.devtools.depan.eclipse.views.ViewEditorTool;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.platform.Colors;
import com.google.devtools.depan.platform.TableContentProvider;
import com.google.devtools.depan.platform.tables.EditColTableDef;

import com.google.common.collect.Maps;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import java.awt.Color;
import java.util.Map;

/**
 * Tool for edge edition. Associate each edge with a {@link EdgeDisplayProperty}
 * and provide a GUI to edit them.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public class EdgeEditorTool extends ViewEditorTool implements ICellModifier {

  /*
   * Constants for creating the table in this tool.
   */
  public static final String COL_SOURCE = "Source";
  public static final String COL_RELATION = "Relation";
  public static final String COL_TARGET = "Target";
  public static final String COL_LINE_STYLE = "Style";
  public static final String COL_ARROWHEAD = "Arrowhead";
  public static final String COL_LINE_COLOR =
      "Color (R,G,B - empty = default)";

  public static final int INDEX_SOURCE = 0;
  public static final int INDEX_RELATION = 1;
  public static final int INDEX_TARGET = 2;
  public static final int INDEX_LINE_STYLE = 3;
  public static final int INDEX_ARROWHEAD = 4;
  public static final int INDEX_LINE_COLOR = 5;

  private static final EditColTableDef[] TABLE_DEF = new EditColTableDef[] {
    new EditColTableDef(COL_SOURCE, false, COL_SOURCE, 180),
    new EditColTableDef(COL_RELATION, false, COL_RELATION, 180),
    new EditColTableDef(COL_TARGET, false, COL_TARGET, 100),
    new EditColTableDef(COL_LINE_STYLE, true, COL_LINE_STYLE, 70),
    new EditColTableDef(COL_ARROWHEAD, true, COL_ARROWHEAD, 70),
    new EditColTableDef(COL_LINE_COLOR, true, COL_LINE_COLOR, 60)
  };

  /**
   * The table that lists all selected {@link EdgeEditorTerm} objects.
   */
  private TableViewer edgeTable;

  /**
   * The content provider of table that stores {@link EdgeEditorTerm} objects.
   */
  private TableContentProvider<GraphEdge> edgeTableContent;

  /**
   * Source of information about display properties of an edge.  This is
   * cleared whenever we change editors.
   */
  private Map<GraphEdge, EdgeDisplayProperty> knownEdges = null;

  private EdgeDisplayProperty getDisplayProperty(GraphEdge edge) {
    EdgeDisplayProperty result = knownEdges.get(edge);
    if (null != result) {
      return result;
    }
    result = getEditor().getEdgeProperty(edge);
    if (null == result) {
      result = new EdgeDisplayProperty();
    }
    knownEdges.put(edge, result);
    return result;
  }

  private EdgeDisplayProperty getMutableDisplayProperty(GraphEdge edge) {
    EdgeDisplayProperty current = getDisplayProperty(edge);
    EdgeDisplayProperty result = new EdgeDisplayProperty(current);
    knownEdges.put(edge, result);
    return result;
  }

  @Override
  public boolean canModify(Object element, String property) {
    return EditColTableDef.get(TABLE_DEF, property).isEditable();
  }

  @Override
  public Object getValue(Object element, String property) {
    if (element instanceof GraphEdge) {
      GraphEdge edge = (GraphEdge) element;
      if (property.equals(COL_SOURCE)) {
        return edge.getHead().friendlyString();
      }
      if (property.equals(COL_RELATION)) {
        return edge.getRelation().toString();
      }
      if (property.equals(COL_TARGET)) {
        return edge.getTail().friendlyString();
      }

      EdgeDisplayProperty edgeProps = getDisplayProperty(edge);
      if (property.equals(COL_LINE_STYLE)) {
        return edgeProps.getLineStyle().ordinal();
      }
      if (property.equals(COL_ARROWHEAD)) {
        return edgeProps.getArrowhead().ordinal();
      }
      if (property.equals(COL_LINE_COLOR)) {
        Color edgeColor = edgeProps.getColor();
        if (edgeColor != null) {
          return edgeColor.toString();
        }
        return "";
      }
    }
    return null;
  }

  @Override
  public void modify(Object element, String property, Object value) {
    if (!(element instanceof TableItem)) {
      return;
    }
    Object modifiedObject = ((TableItem) element).getData();
    if (!(modifiedObject instanceof GraphEdge)) {
      return;
    }

    GraphEdge edge = (GraphEdge) modifiedObject;
    EdgeDisplayProperty edgeProps = getMutableDisplayProperty(edge);

    if (property.equals(COL_LINE_STYLE) && (value instanceof Integer)) {
      edgeProps.setLineStyle(LineStyle.values()[(Integer) value]);
    } else if (property.equals(COL_ARROWHEAD) && (value instanceof Integer)) {
      edgeProps.setArrowhead(ArrowheadStyle.values()[(Integer) value]);
    } else if (property.equals(COL_LINE_COLOR) && (value instanceof String)) {
      Color newColor = Colors.stringToColor((String) value);
      edgeProps.setColor(newColor);
    }

    // Update listeners and the table
    getEditor().setEdgeProperty(edge, edgeProps);
    edgeTable.update(element, new String[] {property});
  }

  /**
   * Creates the interface for this <code>EdgeEditorTool</code> and places it on
   * the given <code>Composite</code>.
   *
   * @param parent Parent of this control.
   * @return The <code>Control</code> for this editor.
   */
  @Override
  public Control setupComposite(Composite parent) {
    // Create a table viewer and its content provider
    edgeTable = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL);
    edgeTableContent = new TableContentProvider<GraphEdge>();
    edgeTableContent.initViewer(edgeTable);

    // set up label provider
    EdgeEditorLabelProvider pathMatchersLabelProvider =
        new EdgeEditorLabelProvider();
    edgeTable.setLabelProvider(pathMatchersLabelProvider);
    edgeTable.setCellModifier(this);

    // Set up layout properties
    Table edgeTableControl = edgeTable.getTable();
    edgeTableControl.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));
    edgeTableControl.setToolTipText("List of Edges");

    // initialize the table
    edgeTableControl.setHeaderVisible(true);
    EditColTableDef.setupTable(TABLE_DEF, edgeTableControl);

    CellEditor[] cellEditors = new CellEditor[6];
    cellEditors[INDEX_SOURCE] = null;
    cellEditors[INDEX_RELATION] = null;
    cellEditors[INDEX_TARGET] = null;
    cellEditors[INDEX_LINE_STYLE] = new ComboBoxCellEditor(edgeTableControl,
        Tools.toString(LineStyle.values(), true));
    cellEditors[INDEX_ARROWHEAD] = new ComboBoxCellEditor(edgeTableControl,
        Tools.toString(ArrowheadStyle.values(), true));
    cellEditors[INDEX_LINE_COLOR] = new TextCellEditor(edgeTableControl);

    edgeTable.setCellEditors(cellEditors);
    edgeTable.setColumnProperties(EditColTableDef.getProperties(TABLE_DEF));
    return edgeTableControl;
  }

  @Override
  public Image getIcon() {
    return Resources.IMAGE_EDGEEDITOR;
  }

  @Override
  public String getName() {
    return Resources.NAME_EDGEEDITOR;
  }

  /**
   * Set the editor that is currently selected.
   * With a selected editor, the tool operates on the editors content.
   * Display changes may occur separately via notifications or may be
   * initiated directly by the tool
   * <p>
   * This method is called only when a new or different
   * {@link ViewEditor} is selected. Can be called with a {@code null}
   * argument if an editor is closed.
   *
   * @param viewEditor new {@link ViewEditor}.
   */
  @Override
  public void setEditor(ViewEditor viewEditor) {
    if (viewEditor != getEditor()) {
      knownEdges = Maps.newHashMap();
    }
    super.setEditor(viewEditor);
    if (null == getEditor()) {
      return;
    }

    // Reset the table by removing everything and adding all the edges.
    edgeTable.getTable().removeAll();
    edgeTable.add(viewEditor.getViewGraph().getEdges().toArray());
  }

  /**
   * Label Provider for {@link EdgeEditorTerm} objects.
   */
  private class EdgeEditorLabelProvider extends LabelProvider
      implements ITableLabelProvider {

    /**
     * Currently unsupported and returns <code>null</code> for all cases.
     *
     * @param element The object that appears in the list.
     * @param columnIndex The index of the column that the image will be
     * displayed.
     * @return <code>null</code> for all cases.
     */
    @Override
    public Image getColumnImage(Object element, int columnIndex) {
      return null;
    }

    /**
     * Returns a String representation of this element. Not intended for
     * external use.
     *
     * @param element Element whose String representation is requested.
     * @param columnIndex The index of the column that this text will be
     * displayed.
     * @return Text associated with this element for the given index.
     */
    @Override
    public String getColumnText(Object element, int columnIndex) {
      if (element instanceof GraphEdge) {
        GraphEdge edge = (GraphEdge) element;
        EdgeDisplayProperty edgeProps = getDisplayProperty(edge);
        switch (columnIndex) {
        case 0:
          // return the displayName of the path matcher term, trim just in case
          return edge.getHead().friendlyString();
        case 1:
          return edge.getRelation().toString();
        case 2:
          return edge.getTail().friendlyString();
        case 3:
          return edgeProps.getLineStyle().getDisplayName().toLowerCase();
        case 4:
          return edgeProps.getArrowhead().getDisplayName().toLowerCase();
        case 5:
          if (edgeProps.getColor() != null) {
            return Colors.getRgb(edgeProps.getColor());
          }
        }
      }
      return "";
    }
  } // end of class EdgeEditorLabelProvider
}
