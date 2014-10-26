/**
 * 
 */
package com.google.devtools.depan.eclipse.trees;

import com.google.devtools.depan.eclipse.editors.NodeWrapperTreeSorter;
import com.google.devtools.depan.eclipse.utils.EditColTableDef;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import java.util.List;

/**
 * @author Standard Lee
 *
 */
public class NodeTreeViews {

  /**
   * Caller to identifier Cell Modifier??
   * viewer.setCellModifier(cellModifier);
   * @param viewer
   * @param columns
   * @param cellModifier
   */
  public static void configCols(TreeViewer viewer, List<EditColTableDef> columns, ICellModifier cellModifier) {

    Tree tree = viewer.getTree();
    tree.setHeaderVisible(true);

    for (EditColTableDef d : columns) {
      TreeColumn col = new TreeColumn(tree, SWT.LEFT);
      col.setText(d.getLabel());
      col.setWidth(d.getWidth());
    }

    CellEditor[] cellA = new CellEditor[columns.size()];
    for(int cellIndex = 0; cellIndex < cellA.length; cellIndex++) {
      // cellA[cellIndex] = columns.get(cellIndex).createEditor();
    }

    viewer.setCellEditors(cellA);
    // viewer.setColumnProperties(
    //    EditColTableDef.getProperties(columns));
    viewer.setCellModifier(cellModifier);
  }

  public static void configure(TreeViewer viewer) {
    viewer.setSorter(new NodeWrapperTreeSorter());
  }
}
