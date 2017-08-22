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

package com.google.devtools.depan.matchers.eclipse.ui.widgets;

import com.google.devtools.depan.edge_ui.EdgeUILogger;
import com.google.devtools.depan.edges.matchers.GraphEdgeMatchers;
import com.google.devtools.depan.graph.api.EdgeMatcher;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.GraphEdgeMatcher;
import com.google.devtools.depan.model.RelationSets;
import com.google.devtools.depan.platform.AlphabeticSorter;
import com.google.devtools.depan.platform.ListenerManager;
import com.google.devtools.depan.platform.PlatformResources;
import com.google.devtools.depan.platform.ViewerObjectToString;
import com.google.devtools.depan.platform.eclipse.ui.tables.EditColTableDef;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Selections;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;

import com.google.common.collect.Sets;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import java.util.Collection;
import java.util.Set;

/**
 * Show (and edit) a EdgeMatcher for a table of Relations.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class EdgeMatcherTableControl extends Composite {

  public static final String COL_RELATION = "Relation";
  public static final String COL_FORWARD = "Forward";
  public static final String COL_BACKWARD = "Backward";

  public static final int INDEX_RELATION = 0;
  public static final int INDEX_FORWARD = 1;
  public static final int INDEX_BACKWARD = 2;

  public static final EditColTableDef[] TABLE_DEF = new EditColTableDef[] {
    new EditColTableDef(COL_RELATION, false, COL_RELATION, 320),
    new EditColTableDef(COL_FORWARD, true, COL_FORWARD, 180),
    new EditColTableDef(COL_BACKWARD, true, COL_BACKWARD, 180)
  };

  private static final String[] BOTH_MATCHERS =
      new String [] { COL_FORWARD, COL_BACKWARD };
  private static final String[] FORWARD_MATCHERS =
      new String [] { COL_FORWARD };
  private static final String[] REVERSE_MATCHERS =
      new String [] { COL_BACKWARD };

  /////////////////////////////////////
  // Edge matcher integration

  /**
   * Data being managed
   */
  private final Set<Relation> forwardMatchers = Sets.newHashSet();

  private final Set<Relation> reverseMatchers = Sets.newHashSet();

  private ListenerManager<ModificationListener<Relation, Boolean>>
      changeListener =
          new ListenerManager<ModificationListener<Relation, Boolean>>();

  private static class ControlDispatcher
      implements ListenerManager.Dispatcher<ModificationListener<Relation, Boolean>> {

    // Retain these values just long enough to dispatch the event
    private final Relation relation;
    private final String property;
    private final boolean change;

    public ControlDispatcher(Relation relation, String property, boolean change) {
      this.relation = relation;
      this.property = property;
      this.change = change;
    }

    @Override
    public void dispatch(ModificationListener<Relation, Boolean> listener) {
      listener.modify(relation, property, change);
    }

    @Override
    public void captureException(RuntimeException errAny) {
      EdgeUILogger.LOG.error("Listener dispatch failure", errAny);
    }
  };

  /////////////////////////////////////
  // UX Elements

  private TableViewer viewer;

  // private TableContentProvider<Relation> content;

  /////////////////////////////////////
  // Public methods

  public EdgeMatcherTableControl(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(Widgets.buildContainerLayout(1));

    viewer = new TableViewer(this,
        SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);

    // Layout embedded table
    Table relationTable = viewer.getTable();
    relationTable.setLayoutData(Widgets.buildGrabFillData());

    // initialize the table
    relationTable.setHeaderVisible(true);
    relationTable.setToolTipText("Edge Matcher Definition");
    EditColTableDef.setupTable(TABLE_DEF, relationTable);

    CellEditor[] cellEditors = new CellEditor[TABLE_DEF.length];
    cellEditors[INDEX_RELATION] = null;
    cellEditors[INDEX_FORWARD] = new CheckboxCellEditor(relationTable);
    cellEditors[INDEX_BACKWARD] = new CheckboxCellEditor(relationTable);

    // cell content
    viewer.setCellEditors(cellEditors);
    viewer.setLabelProvider(new CellLabelProvider());
    viewer.setColumnProperties(EditColTableDef.getProperties(TABLE_DEF));
    viewer.setCellModifier(new CellModifier());
    viewer.setContentProvider(ArrayContentProvider.getInstance());

    viewer.setComparator(new AlphabeticSorter(new ViewerObjectToString() {

      @Override
      public String getString(Object object) {
          if (object instanceof Relation) {
            return ((Relation) object).toString();
          }
          return object.toString();
        }
      }));
  }

  /////////////////////////////////////
  // Provide modification listener API

  public void registerModificationListener(ModificationListener<Relation, Boolean> listener ) {
    changeListener.addListener(listener);
  }

  public void unregisterModificationListener(ModificationListener<Relation, Boolean> listener ) {
    changeListener.addListener(listener);
  }

  /////////////////////////////////////
  // Respond to external actions

  /**
   * Fill the list with {@link Relation}s.
   */
  public void updateTableRows(Collection<Relation> rowRelations) {
    forwardMatchers.clear();
    reverseMatchers.clear();

    viewer.setInput(rowRelations);
    viewer.refresh(false);
  }

  /**
   * Update the rows to show the relation directions for the
   * supplied edge matcher.
   */
  public void updateEdgeMatcher(EdgeMatcher<String> edgeMatcher) {
    forwardMatchers.clear();
    reverseMatchers.clear();

    for (Relation relation : getInput()) {
      if (edgeMatcher.relationForward(relation)) {
        forwardMatchers.add(relation);
      }
      if (edgeMatcher.relationReverse(relation)) {
        reverseMatchers.add(relation);
      }
      viewer.update(relation, BOTH_MATCHERS);
    }
  }

  public GraphEdgeMatcher buildEdgeMatcher() {
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

  public void clearRelations() {
    clearRelations(getInput());
  }

  public void reverseRelations() {
    reverseRelations(getInput());
  }

  public void invertRelations() {
    invertRelations(getInput());
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

  private Collection<Relation> getSelectedRelations() {
    ISelection selection = viewer.getSelection();
    return Selections.getSelection(selection, Relation.class);
  }

  @SuppressWarnings("unchecked")
  private Collection<Relation> getInput() {
    return (Collection<Relation>) viewer.getInput();
  }

  /////////////////////////////////////
  // Change values for collected relations

  public void clearRelations(Collection<Relation> relations) {
    for (Relation relation : relations) {
      setForward(relation, false);
      setReverse(relation, false);
      viewer.update(relation, BOTH_MATCHERS);
    }
  }

  public void reverseRelations(Collection<Relation> relations) {
    for (Relation relation : relations) {
      boolean wasForward = forwardMatchers.contains(relation);
      boolean wasReverse = reverseMatchers.contains(relation);
      setForward(relation, wasReverse);
      setReverse(relation, wasForward);
      viewer.update(relation, BOTH_MATCHERS);
    }
  }

  public void invertRelations(Collection<Relation> relations) {
    for (Relation relation : relations) {
      boolean wasForward = forwardMatchers.contains(relation);
      boolean wasReverse = reverseMatchers.contains(relation);
      setForward(relation, !wasForward);
      setReverse(relation, !wasReverse);
      viewer.update(relation, BOTH_MATCHERS);
    }
  }

  public void setForwardRelations(
      Collection<Relation> relations, boolean select) {
    for (Relation relation : relations) {
      setForward(relation, select);
      viewer.update(relation, FORWARD_MATCHERS);
    }
  }

  public void invertForwardRelations(Collection<Relation> relations) {
    for (Relation relation : relations) {
      boolean wasForward = forwardMatchers.contains(relation);
      setForward(relation, !wasForward);
      viewer.update(relation, FORWARD_MATCHERS);
    }
  }

  public void setReverseRelations(
      Collection<Relation> relations, boolean select) {
    for (Relation relation : relations) {
      setReverse(relation, select);
      viewer.update(relation, REVERSE_MATCHERS);
    }
  }

  public void invertReverseRelations(Collection<Relation> relations) {
    for (Relation relation : relations) {
      boolean wasForward = forwardMatchers.contains(relation);
      setReverse(relation, !wasForward);
      viewer.update(relation, REVERSE_MATCHERS);
    }
  }

  private void setForward(Relation relation, boolean included) {
    setMatcher(forwardMatchers, relation, included);
    fireChangeEvent(relation, COL_FORWARD, included);
  }

  private void setReverse(Relation relation, boolean included) {
    setMatcher(reverseMatchers, relation, included);
    fireChangeEvent(relation, COL_BACKWARD, included);
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
    // fireChangeEvent(relation, property, isOn);
  }

  private void fireChangeEvent(
      Relation relation, String direction, Boolean include) {
    changeListener.fireEvent(new ControlDispatcher(
        relation, direction, include));
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
      return PlatformResources.getOnOff(isOn);
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
      if (element instanceof Relation) {
        Relation relation = ((Relation) element);
        switch (columnIndex) {
        case INDEX_RELATION:
          return relation.toString();
        case INDEX_FORWARD:
          return relation.getForwardName();
        case INDEX_BACKWARD:
          return relation.getReverseName();
        }
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
      boolean include = ((Boolean) value).booleanValue();

      if (property.equals(COL_BACKWARD)) {
        setMatcher(reverseMatchers, relation, include);
      } else if (property.equals(COL_FORWARD)) {
        setMatcher(forwardMatchers, relation, include);
      }

      // update the column / line we just modified
      viewer.update(o, new String[] {property});
      fireChangeEvent(relation, property, include);
    }
  }
}
