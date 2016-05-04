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

package com.google.devtools.depan.platform.eclipse.ui.tables;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * Useful class for making multi columns trees or tables.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class EditColTableDef {

  private final String property;
  private final boolean editable;
  private String label;
  private int width;

  public EditColTableDef(final String property, final boolean editable,
      String label, int width) {
    this.property = property;
    this.editable = editable;
    this.label = label;
    this.width = width;
  }

  public String getProperty() {
    return property;
  }

  public boolean isEditable() {
    return editable;
  }

  public String getLabel() {
    return label;
  }

  public int getWidth() {
    return width;
  }

  /**
   * Find in the list of {@link EditColTableDef} the one corresponding to the
   * given property.
   *
   * @param tableData list of {@link EditColTableDef}.
   * @param property property to look for.
   * @return the {@link EditColTableDef} corresponding to the given property.
   */
  public static EditColTableDef get(
      EditColTableDef[] tableData, String property) {
    for (EditColTableDef d : tableData) {
      if (d.property == property) {
        return d;
      }
    }
    return null;
  }

  /**
   * Extract an array of Strings of properties for each {@link EditColTableDef}
   * in the given array. Useful for the setColumnProperties method.
   *
   * @param tableData array of {@link EditColTableDef} to extract.
   * @return each property of <code>tableData</code> as an array of String.
   */
  public static String[] getProperties(EditColTableDef[] tableData) {
    String[] s = new String[tableData.length];
    int i = 0;
    for (EditColTableDef d : tableData) {
      s[i++] = d.property;
    }
    return s;
  }

  public static void setupTable(EditColTableDef[] tableDef, Table tableData) {
    for (EditColTableDef d : tableDef) {
      TableColumn col = new TableColumn(tableData, SWT.LEFT);
      col.setText(d.getLabel());
      col.setWidth(d.getWidth());
    }
  }

  public static void setupTree(EditColTableDef[] tableDef, Tree tree) {
    for (EditColTableDef d : tableDef) {
      TreeColumn col = new TreeColumn(tree, SWT.LEFT);
      col.setText(d.getLabel());
      col.setWidth(d.getWidth());
    }
  }
}
