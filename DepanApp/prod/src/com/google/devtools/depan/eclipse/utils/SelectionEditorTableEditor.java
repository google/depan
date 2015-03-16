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

package com.google.devtools.depan.eclipse.utils;

import com.google.devtools.depan.graph.api.DirectedRelation;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TableItem;

/**
 * A class providing editing and labeling for the table used to show and select
 * relationship directions in the SelectionEditorTool.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class SelectionEditorTableEditor extends LabelProvider
  implements ITableLabelProvider, ICellModifier {

  /**
   * the table.
   */
  private final TableViewer table;

  private ModificationListener<DirectedRelation, Boolean> changeListener;

  protected SelectionEditorTableEditor(TableViewer table,
      ModificationListener<DirectedRelation, Boolean> changeListener) {
    this.table = table;
    this.changeListener = changeListener;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.ICellModifier #canModify(java.lang.Object,
   *      java.lang.String)
   */
  public boolean canModify(Object element, String property) {
    return EditColTableDef.get(
        RelationshipPickerHelper.TABLE_DEF, property).isEditable();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.ICellModifier #getValue(java.lang.Object,
   *      java.lang.String)
   */
  public Object getValue(Object element, String property) {
    if (element instanceof DirectedRelation) {
      DirectedRelation relation = ((DirectedRelation) element);
      if (property.equals(RelationshipPickerHelper.COL_BACKWARD)) {
        return relation.matchBackward();
      } else if (property.equals(RelationshipPickerHelper.COL_FORWARD)) {
        return relation.matchForward();
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.ICellModifier #modify(java.lang.Object,
   *      java.lang.String, java.lang.Object)
   */
  public void modify(Object element, String property, Object value) {
    if (!(value instanceof Boolean)) {
      return;
    }
    if (!(element instanceof TableItem)) {
      return;
    }
    Object o = ((TableItem) element).getData();

    if (!(o instanceof DirectedRelation)) {
      return;
    }

    DirectedRelation relation = ((DirectedRelation) o);

    if (null != changeListener) {
      changeListener.modify(relation, property, (Boolean) value);
    }

    if (property.equals(RelationshipPickerHelper.COL_BACKWARD)) {
      relation.setMatchBackward((Boolean) value);
    } else if (property.equals(RelationshipPickerHelper.COL_FORWARD)) {
      relation.setMatchForward((Boolean) value);
    }

    // update the column / line we just modified
    table.update(o, new String[] {property});
  }

  /*
   * (non-Javadoc)
   * return null, since we don't have icons (yet) for relationships.
   *
   * @see org.eclipse.jface.viewers.ITableLabelProvider
   *      #getColumnImage(java.lang.Object, int)
   */
  public Image getColumnImage(Object element, int columnIndex) {
    if (element instanceof DirectedRelation) {
      DirectedRelation relation = ((DirectedRelation) element);
      switch (columnIndex) {
      case 0: return null;
      case 1:
        return Resources.getOnOff(relation.matchForward());
      case 2:
        return Resources.getOnOff(relation.matchBackward());
      default:
        break;
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.ITableLabelProvider
   *      #getColumnText(java.lang.Object, int)
   */
  public String getColumnText(Object element, int columnIndex) {
    if (element instanceof DirectedRelation) {
      DirectedRelation relation = ((DirectedRelation) element);
      switch (columnIndex) {
      case 0:
        return relation.getRelation().toString().toLowerCase();
      case 1:
        if (relation.matchForward()) {
          return relation.getRelation().getForwardName();
        }
        return relation.getRelation().getForwardName();
      case 2:
        if (relation.matchBackward()) {
          return relation.getRelation().getReverseName();
        }
        return relation.getRelation().getReverseName();
      default:
        break;
      }
    }
    return "";
  }

}

