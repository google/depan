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

import com.google.devtools.depan.eclipse.plugins.SourcePlugin;
import com.google.devtools.depan.eclipse.plugins.SourcePlugins;
import com.google.devtools.depan.graph.api.EdgeMatcher;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.GraphEdgeMatcher;
import com.google.devtools.depan.model.GraphEdgeMatchers;
import com.google.devtools.depan.model.RelationSets;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * A class providing editing and labeling for the table used to show and select
 * relationship directions in the SelectionEditorTool.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class GraphEdgeMatcherRelationTableEditor {

  public static final String COL_RELATION = "Relation";
  public static final String COL_FORWARD = "Forward";
  public static final String COL_BACKWARD = "Backward";

  public static final EditColTableDef[] TABLE_DEF = new EditColTableDef[] {
    new EditColTableDef(COL_RELATION, false, COL_RELATION, 150),
    new EditColTableDef(COL_FORWARD, true, COL_FORWARD, 140),
    new EditColTableDef(COL_BACKWARD, true, COL_BACKWARD, 80)
  };

  private static final String[] BOTH_MATCHERS =
      new String [] { COL_FORWARD, COL_BACKWARD };
  private static final String[] FORWARD_MATCHERS =
      new String [] { COL_FORWARD };
  private static final String[] REVERSE_MATCHERS =
      new String [] { COL_BACKWARD };

  /**
   * UX Elements
   */
  private TableViewer viewer;

  private TableContentProvider<Relation> content;

  /**
   * Data being managed
   */
  private final Set<Relation> forwardMatchers = Sets.newHashSet();
  private final Set<Relation> reverseMatchers = Sets.newHashSet();

  private ModificationListener<Relation, Boolean> changeListener;

  protected GraphEdgeMatcherRelationTableEditor(
      ModificationListener<Relation, Boolean> changeListener) {
    this.changeListener = changeListener;
  }

  public TableViewer setupTableViewer(Composite parent) {
    viewer = new TableViewer(
        parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);

    // initialize the table
    Table relationTable = viewer.getTable();
    relationTable.setHeaderVisible(true);
    EditColTableDef.setupTable(
        GraphEdgeMatcherRelationTableEditor.TABLE_DEF, relationTable);

    CellEditor[] cellEditors = new CellEditor[3];
    cellEditors[0] = null;
    cellEditors[1] = new CheckboxCellEditor(relationTable);
    cellEditors[2] = new CheckboxCellEditor(relationTable);

    // cell content
    viewer.setCellEditors(cellEditors);
    viewer.setColumnProperties(EditColTableDef.getProperties(
        GraphEdgeMatcherRelationTableEditor.TABLE_DEF));

    viewer.setLabelProvider(new CellLabelProvider());
    viewer.setCellModifier(new CellModifier());
    viewer.setSorter(new AlphabeticSorter(new ViewerObjectToString() {
      @Override
      public String getString(Object object) {
          if (!(object instanceof Relation)) {
            return object.toString();
          }
          Relation relation = (Relation) object;
          return relation.toString();
        }
      }));

    content = new TableContentProvider<Relation>();
    content.initViewer(viewer);

    return viewer;
  }

  /////////////////////////////////////
  // Respond to external actions

  /**
   * Fill the list with {@link Relation}s.
   */
  public void updateTable(List<SourcePlugin> plugins) {
    forwardMatchers.clear();
    reverseMatchers.clear();

    content.clear();
    for (Relation relation : SourcePlugins.getRelations(plugins)) {
      content.add(relation);
    }
    viewer.refresh(false);
  }

  /**
   * Update the rows to show the relation directions for the
   * supplied edge matcher.
   */
  public void updateEdgeMatcher(EdgeMatcher<String> edgeMatcher) {
    forwardMatchers.clear();
    reverseMatchers.clear();

    for (Relation relation : content.getObjects()) {
      if (edgeMatcher.relationForward(relation)) {
        forwardMatchers.add(relation);
      }
      if (edgeMatcher.relationReverse(relation)) {
        reverseMatchers.add(relation);
      }
      viewer.update(relation, BOTH_MATCHERS);
    }
  }

  public GraphEdgeMatcher createEdgeMatcher() {
    // Build result from defensive snapshots of current matchers
    Set<Relation> onForward = Sets.newHashSet(forwardMatchers);
    Set<Relation> onReverse = Sets.newHashSet(reverseMatchers);

    return GraphEdgeMatchers.createBinaryEdgeMatcher(
        RelationSets.createSimple(onForward),
        RelationSets.createSimple(onReverse));
  }

  /////////////////////////////////////
  // Change values for all relations

  public void unselectAll() {
    viewer.setSelection(null);
  }

  public void reverseRelations() {
    reverseRelations(content.getObjects());
  }

  public void invertRelations() {
    invertRelations(content.getObjects());
  }

  /////////////////////////////////////
  // Change values for the selected relations

  public void reverseSelectedRelations() {
    reverseRelations(getSelectedRelations());
  }

  public void invertSelectedRelations() {
    invertRelations(getSelectedRelations());
  }

  public void setForwardSelectedRelations(boolean select) {
    setForwardRelations(getSelectedRelations(), select);
  }

  public void invertForwardSelectedRelations() {
    invertForwardRelations(getSelectedRelations());
  }

  public void setReverseSelectedRelations(boolean select) {
    setReverseRelations(getSelectedRelations(), select);
  }

  public void invertReverseSelectedRelations() {
    invertReverseRelations(getSelectedRelations());
  }

  private List<Relation> getSelectedRelations() {
    List<?> selection = ((IStructuredSelection) viewer.getSelection()).toList();
    List<Relation> result =
        Lists.newArrayListWithExpectedSize(selection.size());
    for (Object item : selection) {
      if (item instanceof Relation) {
        result.add((Relation) item);
      }
    }
    return result;
  }

  /////////////////////////////////////
  // Change values for collected relations

  public void reverseRelations(Collection<Relation> relations) {
    for (Relation relation : relations) {
      boolean wasForward = forwardMatchers.contains(relation);
      boolean wasReverse = reverseMatchers.contains(relation);
      setMatcher(forwardMatchers, relation, wasReverse);
      setMatcher(reverseMatchers, relation, wasForward);
      viewer.update(relation, BOTH_MATCHERS);
    }
  }

  public void invertRelations(Collection<Relation> relations) {
    for (Relation relation : relations) {
      boolean wasForward = forwardMatchers.contains(relation);
      boolean wasReverse = reverseMatchers.contains(relation);
      setMatcher(forwardMatchers, relation, !wasForward);
      setMatcher(reverseMatchers, relation, !wasReverse);
      viewer.update(relation, BOTH_MATCHERS);
    }
  }

  public void setForwardRelations(
      Collection<Relation> relations, boolean select) {
    for (Relation relation : relations) {
      setMatcher(forwardMatchers, relation, select);
      viewer.update(relation, FORWARD_MATCHERS);
    }
  }

  public void invertForwardRelations(Collection<Relation> relations) {
    for (Relation relation : relations) {
      boolean wasForward = forwardMatchers.contains(relation);
      setMatcher(forwardMatchers, relation, !wasForward);
      viewer.update(relation, FORWARD_MATCHERS);
    }
  }

  public void setReverseRelations(
      Collection<Relation> relations, boolean select) {
    for (Relation relation : relations) {
      setMatcher(reverseMatchers, relation, select);
      viewer.update(relation, REVERSE_MATCHERS);
    }
  }

  public void invertReverseRelations(Collection<Relation> relations) {
    for (Relation relation : relations) {
      boolean wasForward = forwardMatchers.contains(relation);
      setMatcher(reverseMatchers, relation, !wasForward);
      viewer.update(relation, REVERSE_MATCHERS);
    }
  }

  private void setMatcher(
      Set<Relation> matchers, Relation relation, boolean isOn) {
    boolean curr = matchers.contains(relation);
    if (curr == isOn) {
      return;
    }
    if (isOn)
      matchers.add(relation);
    else {
      matchers.remove(relation);
    }
  }

  /////////////////////////////////////
  // Label provider for table cell text

  private class CellLabelProvider extends LabelProvider
      implements ITableLabelProvider {

    /**
     * return null, since we don't have icons (yet) for relationships.
     */
    @Override
    public Image getColumnImage(Object element, int columnIndex) {
      if (!(element instanceof Relation)) {
        return null;
      }
      Relation relation = ((Relation) element);
      switch (columnIndex) {
      case 0: return null;
      case 1:
        return getOnOff(forwardMatchers.contains(relation));
      case 2:
        return getOnOff(reverseMatchers.contains(relation));
      }
      return null;
    }

    private final Image getOnOff(boolean isOn) {
      return Resources.getOnOff(isOn);
      
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
      if (!(element instanceof Relation)) {
        return "";
      }
      Relation relation = ((Relation) element);
      switch (columnIndex) {
      case 0:
        return relation.toString().toLowerCase();
      case 1:
        return relation.getForwardName();
      case 2:
        return relation.getReverseName();
      }
      return "";
    }
  }

  /////////////////////////////////////
  // Value provider/modifier for edit cells

  private class CellModifier implements ICellModifier {

    @Override
    public boolean canModify(Object element, String property) {
      return EditColTableDef.get(TABLE_DEF, property).isEditable();
    }

    @Override
    public Object getValue(Object element, String property) {
      if (!(element instanceof Relation)) {
        return null;
      }
      Relation relation = ((Relation) element);
      if (property.equals(COL_FORWARD)) {
        return forwardMatchers.contains(relation);
      } else if (property.equals(COL_BACKWARD)) {
        return reverseMatchers.contains(relation);
      }
      return null;
    }

    @Override
    public void modify(Object element, String property, Object value) {
      if (!(value instanceof Boolean)) {
        return;
      }
      if (!(element instanceof TableItem)) {
        return;
      }
      Object o = ((TableItem) element).getData();

      if (!(o instanceof Relation)) {
        return;
      }

      Relation relation = ((Relation) o);

      if (null != changeListener) {
        changeListener.modify(relation, property, (Boolean) value);
      }

      if (property.equals(COL_BACKWARD)) {
        setMatcher(reverseMatchers, relation, (Boolean) value);
      } else if (property.equals(COL_FORWARD)) {
        setMatcher(forwardMatchers, relation, (Boolean) value);
      }

      // update the column / line we just modified
      viewer.update(o, new String[] {property});
    }
  }
}
