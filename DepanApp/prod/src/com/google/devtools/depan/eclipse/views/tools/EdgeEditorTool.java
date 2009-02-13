/*
 * Copyright 2008 Google Inc.
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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.devtools.depan.eclipse.editors.ViewEditor;
import com.google.devtools.depan.eclipse.utils.EditColTableDef;
import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.utils.TableContentProvider;
import com.google.devtools.depan.eclipse.utils.Tools;
import com.google.devtools.depan.eclipse.views.ViewEditorTool;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.util.StringUtils;
import com.google.devtools.depan.view.EdgeDisplayProperty;
import com.google.devtools.depan.view.ViewModel;
import com.google.devtools.depan.view.EdgeDisplayProperty.ArrowheadStyle;
import com.google.devtools.depan.view.EdgeDisplayProperty.LineStyle;

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
import java.util.Collection;
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
  private TableContentProvider<EdgeEditorTerm> edgeTableContent;

  /**
   * Hash map that contains a list of {@link EdgeEditorTerm} objects for each
   * {@link ViewModel}.
   */
  private Map<ViewModel, Collection<EdgeEditorTerm>> lists =
      Maps.newHashMap();

  @Override
  public boolean canModify(Object element, String property) {
    return EditColTableDef.get(TABLE_DEF, property).isEditable();
  }

  @Override
  public Object getValue(Object element, String property) {
    if (element instanceof EdgeEditorTerm) {
      EdgeEditorTerm term = (EdgeEditorTerm) element;
      if (property.equals(COL_SOURCE)) {
        return term.getSourceName();
      }
      if (property.equals(COL_RELATION)) {
        return term.getRelationName();
      }
      if (property.equals(COL_TARGET)) {
        return term.getTargetName();
      }
      if (property.equals(COL_LINE_STYLE)) {
        return term.getDisplayProperty().getLineStyle().ordinal();
      }
      if (property.equals(COL_ARROWHEAD)) {
        return term.getDisplayProperty().getArrowhead().ordinal();
      }
      if (property.equals(COL_LINE_COLOR)) {
        Color edgeColor = term.getDisplayProperty().getColor();
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
    if (!(modifiedObject instanceof EdgeEditorTerm)) {
      return;
    }

    EdgeEditorTerm term = (EdgeEditorTerm) modifiedObject;
    GraphEdge edge = term.edge;

    if (property.equals(COL_LINE_STYLE) && (value instanceof Integer)) {
      term.getDisplayProperty().setLineStyle(
          LineStyle.values()[(Integer) value]);
    } else if (property.equals(COL_ARROWHEAD) && (value instanceof Integer)) {
      term.getDisplayProperty().setArrowhead(
          ArrowheadStyle.values()[(Integer) value]);
    } else if (property.equals(COL_LINE_COLOR) && (value instanceof String)) {
      Color newColor = StringUtils.stringToColor((String) value);
      term.getDisplayProperty().setColor(newColor);
    }

    // Update listeners and the table
    getViewModel().getGraph().fireEdgePropertyChange(
        edge, term.getDisplayProperty());
    edgeTable.update(term, new String[] {property});
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
    edgeTableContent = new TableContentProvider<EdgeEditorTerm>();
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
    super.setEditor(viewEditor);
    if (null == getViewModel()) {
      return;
    }

    Collection<EdgeEditorTerm> edgeTerms;
    if (!lists.containsKey(getViewModel())) {
      Collection<GraphEdge> edges = viewEditor.getViewModel().getEdges();
      edgeTerms = Sets.newHashSet();
      for (GraphEdge edge : edges) {
        EdgeEditorTerm term = new EdgeEditorTerm(edge);
        edgeTerms.add(term);
      }
      // the first time we open this editor, get/save the root nodes:
      lists.put(getViewModel(), edgeTerms);
    } else {
      edgeTerms = lists.get(getViewModel());
    }

    // remove all existing entries from the table
    edgeTable.getTable().removeAll();
    // add all edges that exist on this graph
    edgeTable.add(edgeTerms.toArray());
  }

  /**
   * Stores a {@link GraphEdge} and an {@link EdgeDisplayProperty} object
   * associated with this edge.
   */
  private static class EdgeEditorTerm {

    /**
     * {@link GraphEdge} object this term uses.
     */
    private GraphEdge edge;

    /**
     * {@link EdgeDisplayProperty} object associated with the edge stored this
     * object.
     */
    private EdgeDisplayProperty displayProperty;

    /**
     * Constructs an <code>EdgeEditorTerm</code> with the given
     * {@link GraphEdge} and display properties.
     *
     * @param edge {@link GraphEdge} object that is associated with this term.
     * @param lineStyle {@link LineStyle} of the given edge.
     * @param arrowhead {@link ArrowheadStyle} of the given edge.
     * @param lineColor <code>Color</code> of the given edge.
     */
    public EdgeEditorTerm(GraphEdge edge, LineStyle lineStyle,
        ArrowheadStyle arrowhead, Color lineColor) {
      this.edge = edge;
      this.displayProperty =
          new EdgeDisplayProperty(lineStyle, arrowhead, lineColor);
    }

    /**
     * Constructs an <code>EdgeEditorTerm</code> with the given
     * {@link GraphEdge} and default display properties.
     *
     * @param edge {@link GraphEdge} object that is associated with this term.
     */
    public EdgeEditorTerm(GraphEdge edge) {
      this.edge = edge;
      this.displayProperty = new EdgeDisplayProperty();
    }

    /**
     * Returns the <code>String</code> representation of the source node of this
     * edge.
     *
     * @return <code>String</code> representation of the source node of this
     * edge.
     */
    public String getSourceName() {
      return edge.getHead().friendlyString();
    }

    /**
     * Returns the <code>String</code> representation of the relation of this
     * edge.
     *
     * @return <code>String</code> representation of the relation of this edge.
     */
    public String getRelationName() {
      return edge.getRelation().toString();
    }

    /**
     * Returns the <code>String</code> representation of the target node of this
     * edge.
     *
     * @return <code>String</code> representation of the target node of this
     * edge.
     */
    public String getTargetName() {
      return edge.getTail().friendlyString();
    }

    /**
     * Returns the display property of the associated edge.
     *
     * @return Display property of the associated edge.
     */
    public EdgeDisplayProperty getDisplayProperty() {
      return displayProperty;
    }
  } // end class EdgeEditorTerm

  /**
   * Label Provider for {@link EdgeEditorTerm} objects.
   */
  private static class EdgeEditorLabelProvider extends LabelProvider
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
      if (element instanceof EdgeEditorTerm) {
        EdgeEditorTerm item = (EdgeEditorTerm) element;
        switch (columnIndex) {
        case 0:
          // return the displayName of the path matcher term, trim just in case
          return item.getSourceName();
        case 1:
          return item.getRelationName();
        case 2:
          return item.getTargetName();
        case 3:
          return item.getDisplayProperty().getLineStyle().getDisplayName()
              .toLowerCase();
        case 4:
          return item.getDisplayProperty().getArrowhead().getDisplayName()
              .toLowerCase();
        case 5:
          if (item.getDisplayProperty().getColor() != null) {
            return Tools.getRgb(item.getDisplayProperty().getColor());
          }
        }
      }
      return "";
    }
  } // end of class EdgeEditorLabelProvider
}
