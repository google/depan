/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.eclipse.utils;

import com.google.devtools.depan.eclipse.plugins.SourcePlugin;
import com.google.devtools.depan.eclipse.plugins.SourcePluginRegistry;
import com.google.devtools.depan.eclipse.utils.relsets.RelSetDescriptor;
import com.google.devtools.depan.eclipse.utils.relsets.RelSetDescriptors;
import com.google.devtools.depan.eclipse.wizards.NewRelationshipSetWizard;
import com.google.devtools.depan.filters.PathExpression;
import com.google.devtools.depan.filters.PathMatcher;
import com.google.devtools.depan.filters.PathMatcherTerm;
import com.google.devtools.depan.graph.api.DirectedRelation;
import com.google.devtools.depan.graph.api.DirectedRelationFinder;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.basic.BasicDirectedRelation;
import com.google.devtools.depan.graph.basic.MultipleDirectedRelationFinder;
import com.google.devtools.depan.model.RelationshipSet;
import com.google.devtools.depan.model.RelationshipSetAdapter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A GUI tool to display a list of relationships, with a selector for forward
 * and backward directions. This is a editable table (directions are editable,
 * content and names are not).
 *
 * To use it, call {@link #getControl(Composite)} to retrieve the widget.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class RelationshipPicker
    implements ModificationListener<DirectedRelation, Boolean>,
    RelationshipSelectorListener, ViewerObjectToString {

  /**
   * The actual table.
   */
  private TableViewer relationPicker = null;

  /**
   * Content provider for the table.
   */
  private TableContentProvider<DirectedRelation>
      relationPickerContent = null;

  /**
   * A mapping from a Relation to a DirectedRelation. This makes easy to
   * retrieve a {@link DirectedRelation} in the middle of the
   * {@link #relationPicker}.
   */
  private Map<Relation, DirectedRelation> contentMap;

  /**
   * Listeners for changes in the model.
   */
  private Collection<ModificationListener<DirectedRelation, Boolean>>
      listeners = Lists.newArrayList();

  /**
   * The instance set. Can be modified, but can't be saved.
   */
  private RelationshipSet instanceSet = new RelationshipSetAdapter("");

  /**
   * the currently selected {@link RelationshipSet}.
   */
  private RelationshipSet selectedSet = null;

  /**
   * The quick selector on top of this widget applying a selection to the list
   * of relationship.
   */
  private RelationshipSetPickerControl relSetPicker = null;

  /** RelSets received from parent, without any temporary RelSets. */
  List<RelSetDescriptor> baseChoices;

  /**
   * A shell necessary to open dialogs.
   */
  private Shell shell = null;

  /**
   * The Path Matcher Model used for cumulative filtering.
   */
  private PathMatcher pathMatcherModel;

  /**
   * return a {@link Control} for this widget, containing every useful buttons,
   * labels, table... necessary to use this component.
   *
   * @param parent the parent.
   * @return a {@link Control} containing this widget.
   */
  public Control getControl(Composite parent) {
    this.shell = parent.getShell();

    // component
    Composite panel = new Composite(parent, SWT.BORDER);
    panel.setLayout(new GridLayout());

    // components inside the panel
    Composite pickerRegion = setupRelationPicker(panel);
    pickerRegion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Composite allRels = setupAllRelsButtons(panel);
    allRels.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    relationPicker = new TableViewer(
        panel, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);

    Composite toggles = setupRelationToggles(panel);
    toggles.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    // initialize the table
    Table relationTable = relationPicker.getTable();
    relationTable.setHeaderVisible(true);
    relationTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    EditColTableDef.setupTable(
        RelationshipPickerHelper.TABLE_DEF, relationTable);

    CellEditor[] cellEditors = new CellEditor[3];
    cellEditors[0] = null;
    cellEditors[1] = new CheckboxCellEditor(relationTable);
    cellEditors[2] = new CheckboxCellEditor(relationTable);

    // cell content
    relationPicker.setCellEditors(cellEditors);
    relationPicker.setColumnProperties(EditColTableDef.getProperties(
        RelationshipPickerHelper.TABLE_DEF));
    SelectionEditorTableEditor tableLabelProvider =
        new SelectionEditorTableEditor(relationPicker, this);
    relationPicker.setLabelProvider(tableLabelProvider);
    relationPicker.setCellModifier(tableLabelProvider);
    relationPicker.setSorter(new AlphabeticSorter(this));

    // content provider
    relationPickerContent =
        new TableContentProvider<DirectedRelation>();
    relationPickerContent.initViewer(relationPicker);

    return panel;
  }

  private Composite setupAllRelsButtons(Composite parent) {
    Composite result = new Composite(parent, SWT.NONE);
    result.setLayout(new GridLayout(2, false));

    Button reverseAll = new Button(result, SWT.PUSH);
    reverseAll.setText("Reverse all lines");
    reverseAll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Button invertAll = new Button(result, SWT.PUSH);
    invertAll.setText("Invert all lines");
    invertAll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    reverseAll.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        reverseRels(contentMap.values());
      }
    });

    invertAll.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        invertRels(contentMap.values());
      }
    });

    return result;
  }

  private Composite setupRelationPicker(Composite parent) {
    Composite region = new Composite(parent, SWT.NONE);
    region.setLayout(new GridLayout(3, false));

    Label pickerLabel = RelationshipSetPickerControl.createPickerLabel(region);
    pickerLabel.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, false, false));

    relSetPicker = new RelationshipSetPickerControl(region);
    relSetPicker.addChangeListener(this);
    relSetPicker.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, true, false));

    Button save = new Button(region, SWT.PUSH);
    save.setText("Save selection as");
    save.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, false, false));

    save.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        saveAsAction();
      }
    });

    return region;
  }

  private Composite setupRelationToggles(Composite parent) {
    Composite result = new Composite(parent, SWT.NONE);
    GridLayout togglesLayout = new GridLayout(1, false);
    togglesLayout.verticalSpacing = 0;
    result.setLayout(togglesLayout);

    Label optionsLabel = new Label(result, SWT.NONE);
    optionsLabel.setText("For selected lines:");
    optionsLabel.setLayoutData(
        new GridData(SWT.LEFT, SWT.FILL, false, false));

    // Relation operations
    Composite group = new Composite(result, SWT.NONE);
    group.setLayout(new GridLayout(2, false));
    group.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    Button groupReverse = new Button(group, SWT.PUSH);
    groupReverse.setText("Reverse");
    groupReverse.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Button groupInvert = new Button(group, SWT.PUSH);
    groupInvert.setText("Invert");
    groupInvert.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    groupReverse.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        reverseRels(getSelectedRels());
      }
    });

    groupInvert.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        invertRels(getSelectedRels());
      }
    });

    // Toggle operations
    Composite toggles = new Composite(result, SWT.NONE);
    toggles.setLayout(new GridLayout(8, false));
    toggles.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    Label forward = new Label(toggles, SWT.NONE);
    forward.setText("Forward");
    forward.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

    Button forwardAll = new Button(toggles, SWT.PUSH);
    forwardAll.setImage(Resources.IMAGE_ON);
    forwardAll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Button forwardNone = new Button(toggles, SWT.PUSH);
    forwardNone.setImage(Resources.IMAGE_OFF);
    forwardNone.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Button forwardInvert = new Button(toggles, SWT.PUSH);
    forwardInvert.setText("Invert");
    forwardInvert.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Label backward = new Label(toggles, SWT.NONE);
    backward.setText("Backward");
    backward.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

    Button backwardAll = new Button(toggles, SWT.PUSH);
    backwardAll.setImage(Resources.IMAGE_ON);
    backwardAll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Button backwardNone = new Button(toggles, SWT.PUSH);
    backwardNone.setImage(Resources.IMAGE_OFF);
    backwardNone.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Button backwardInvert = new Button(toggles, SWT.PUSH);
    backwardInvert.setText("Invert");
    backwardInvert.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    // actions
    forwardAll.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        forwardSelectAll(true);
      }
    });

    forwardNone.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        forwardSelectAll(false);
      }
    });

    forwardInvert.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        forwardInvertSelection();
      }
    });

    backwardAll.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        backwardSelectAll(true);
      }
    });

    backwardNone.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        backwardSelectAll(false);
      }
    });

    backwardInvert.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        backwardInvertSelection();
      }
    });

    return result;
  }

  /**
   * (un)Select the forward direction for each selected relations types.
   *
   * @param select true to select, false to unselect.
   */
  protected void forwardSelectAll(boolean select) {
    for (DirectedRelation rel : getSelectedRels()) {
      setForward(rel.getRelation(), select, true);
    }
    copyToAndSelectInstanceSet();
  }

  /**
   * (un)Select the backward direction for each selected relations types.
   *
   * @param select true to select, false to unselect.
   */
  protected void backwardSelectAll(boolean select) {
    for (DirectedRelation rel : getSelectedRels()) {
      setBackward(rel.getRelation(), select, true);
    }
    copyToAndSelectInstanceSet();
  }


  /**
   * Invert the forward setting for all selected lines.
   */
  protected void forwardInvertSelection() {
    for (DirectedRelation rel : getSelectedRels()) {
      setForward(rel.getRelation(), !rel.matchForward(), true);
    }
    copyToAndSelectInstanceSet();
  }

  /**
   * Invert the backward setting for all selected lines.
   */
  protected void backwardInvertSelection() {
    for (DirectedRelation rel : getSelectedRels()) {
      setBackward(rel.getRelation(), !rel.matchBackward(), true);
    }
    copyToAndSelectInstanceSet();
  }

  private List<DirectedRelation> getSelectedRels() {
     List<?> selection = ((IStructuredSelection) relationPicker.getSelection()).toList();
     List<DirectedRelation> result =
         Lists.newArrayListWithExpectedSize(selection.size());
     for (Object item : selection) {
       if (item instanceof DirectedRelation) {
         result.add((DirectedRelation) item);
       }
     }
     return result;
  }

  /**
   * Invert the direction choices for all supplied directions.
   */
  protected void invertRels(Collection<DirectedRelation> rels) {
    for (DirectedRelation direct :  rels) {
      boolean forward = direct.matchForward();
      boolean backward = direct.matchBackward();
      setForward(direct.getRelation(), !forward, true);
      setBackward(direct.getRelation(), !backward, true);
    }
    copyToAndSelectInstanceSet();
  }

  /**
   * Reverse direction for all supplied relations.
   */
  protected void reverseRels(Collection<DirectedRelation> rels) {
    for (DirectedRelation direct : rels) {
      boolean forward = direct.matchForward();
      boolean backward = direct.matchBackward();
      setForward(direct.getRelation(), backward, true);
      setBackward(direct.getRelation(), forward, true);
    }
    copyToAndSelectInstanceSet();
  }

  /**
   * Fill the list with {@link Relation}s.
   */
  public void updateTable(List<SourcePlugin> plugins) {
    relationPickerContent.clear();
    contentMap = Maps.newHashMap();
    for (SourcePlugin p : plugins) {
      for (Relation r : p.getRelations()) {
        DirectedRelation directedRelation = new BasicDirectedRelation(r);
        // add to the content provider
        relationPickerContent.add(directedRelation);
        // and to the map for fast retrieval
        contentMap.put(r, directedRelation);
      }
    }
    relationPicker.refresh(false);
  }

  /**
   * Update the RelSetPicker with the current set of choices.
   */
  public void updateRelSetPicker(
      RelationshipSet selectedRelSet, List<RelSetDescriptor> choices) {
    baseChoices = choices;
    relSetPicker.setInput(selectedRelSet, choices);
  }

  /**
   * @return a {@link MultipleDirectedRelationFinder} representing the selected
   *         relationships and their direction.
   */
  public MultipleDirectedRelationFinder getRelationShips() {
    // get a set of relations
    MultipleDirectedRelationFinder finder =
        new MultipleDirectedRelationFinder();

    for (DirectedRelation relation : relationPickerContent.getObjects()) {
      finder.addRelation(relation.getRelation(),
          relation.matchForward(), relation.matchBackward());
    }
    return finder;
  }

  /**
   * deselect all the relations in both forward and backward direction.
   * @param notify true if we want to notify listeners that the object changed.
   */
  private void unselectAll(boolean notify) {
    for (DirectedRelation relation : relationPickerContent.getObjects()) {
      boolean toUpdate = relation.matchBackward() || relation.matchForward();
      relation.setMatchBackward(false);
      relation.setMatchForward(false);
      if (toUpdate) {
        relationPicker.update(relation, RelationshipPickerHelper.CHANGING_COLS);
        if (notify) {
          notifyListeners(
              relation, RelationshipPickerHelper.COL_FORWARD, false);
          notifyListeners(
              relation, RelationshipPickerHelper.COL_BACKWARD, false);
        }
      }
    }
  }

  /**
   * (un)select the forward direction for the {@link Relation} relation.
   * @param relation the relation
   * @param on true to select, false to unselect
   * @param notify true to notify the listeners of the changes
   */
  private void setForward(Relation relation, boolean on, boolean notify) {
    DirectedRelation directedRelation = contentMap.get(relation);
    if (null == directedRelation) {
      return;
    }
    if (on != directedRelation.matchForward()) {
      directedRelation.setMatchForward(on);
      // update the table
      relationPicker.update(directedRelation,
          new String[] {RelationshipPickerHelper.COL_FORWARD});
      if (notify) {
        // notify the listeners that this column has changed
        notifyListeners(contentMap.get(relation),
            RelationshipPickerHelper.COL_FORWARD, on);
      }
    }
  }

  /**
   * (un)select the backward direction for the {@link Relation} relation.
   * @param relation the relation
   * @param on true to select, false to unselect
   * @param notify true to notify the listeners of the changes
   */
  private void setBackward(Relation relation, boolean on, boolean notify) {
    DirectedRelation directedRelation = contentMap.get(relation);
    if (null == directedRelation) {
      return;
    }
    if (on != directedRelation.matchBackward()) {
      directedRelation.setMatchBackward(on);
      // update the table
      relationPicker.update(directedRelation,
          new String[] {RelationshipPickerHelper.COL_BACKWARD});
      if (notify) {
        // notify listeners
        notifyListeners(contentMap.get(relation),
            RelationshipPickerHelper.COL_BACKWARD, on);
      }
    }
  }

  /**
   * register a {@link ModificationListener}.
   * @param listener the new listener
   */
  public void registerListener(
      ModificationListener<DirectedRelation, Boolean> listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  /**
   * Unregister the listener.
   * @param listener to un-register
   */
  public void unRegisterListener(
      ModificationListener<DirectedRelation, Boolean> listener) {
    if (listeners.contains(listener)) {
      listeners.remove(listener);
    }
  }

  /**
   * Select the given {@link RelationshipSet}. Its definition will be reflected
   * on the view: only its enabled directions will be selected.
   * @param set the new set.
   */
  public void selectRelationshipSet(RelationshipSet set) {
    unselectAll(false);
    for (Relation relation : set.getBackwardRelations()) {
      setBackward(relation, true, false);
    }
    for (Relation relation : set.getForwardRelations()) {
      setForward(relation, true, false);
    }
    this.selectedSet = set;
  }

  /**
   * {@inheritDoc}
   * 
   * If the selected set is the instanceSet, this method doesn't notify
   * of the changes.
   */
  @Override
  public void selectedSetChanged(RelationshipSet set) {
    this.selectedSet = set;
    unselectAll(set != instanceSet);
    for (Relation relation : set.getBackwardRelations()) {
      setBackward(relation, true, set != instanceSet);
    }
    for (Relation relation : set.getForwardRelations()) {
      setForward(relation, true, set != instanceSet);
    }
  }

  /**
   * Select the directions described by the {@link DirectedRelationFinder}
   *
   * @param finder finder describing each direction.
   */
  public void selectFinder(DirectedRelationFinder finder) {
    for (SourcePlugin p : SourcePluginRegistry.getInstances()) {
      for (Relation relation : p.getRelations()) {
        setForward(relation, finder.matchForward(relation), true);
        setBackward(relation, finder.matchBackward(relation), true);
      }
    }
    copyToAndSelectInstanceSet();
  }

  @Override
  public void modify(DirectedRelation element, String property, Boolean value) {
    notifyListeners(element, property, value);

    if (selectedSet != instanceSet) {
      copyToAndSelectInstanceSet();
    }

    // after the change, if we are currently editing the instance set,
    // we modify it to get the changes back if we leave/come back to the
    // temporary set.
    if (selectedSet == instanceSet) {
      if (property.equals(RelationshipPickerHelper.COL_FORWARD)) {
        instanceSet.setMatchForward(element.getRelation(), value);
      } else if (property.equals(RelationshipPickerHelper.COL_BACKWARD)) {
        instanceSet.setMatchBackward(element.getRelation(), value);
      }
    }
  }

  /**
   * Notify the listener when a change is made in the selection.
   *
   * @param element the element which changed
   * @param property the property involved
   * @param value the new value
   */
  private void notifyListeners(
      DirectedRelation element, String property, Boolean value) {
    for (ModificationListener<DirectedRelation, Boolean> listener : listeners) {
      listener.modify(element, property, value);
    }
  }

  /**
   * Copy the selected directions to the instanceSet, then select it. This
   * happens when an existing set is selected, and we modify it. So the new
   * modified set is copied to the temporary set, that we can use freely (even
   * save it under a new name...)
   */
  private void copyToAndSelectInstanceSet() {
    for (DirectedRelation relation : contentMap.values()) {
      instanceSet.setMatchForward(
          relation.getRelation(), relation.matchForward());
      instanceSet.setMatchBackward(
          relation.getRelation(), relation.matchBackward());
    }
    this.selectedSet = instanceSet;
    List<RelSetDescriptor> tempRelSets =  RelSetDescriptors.addTemporaryRelSet(
        baseChoices, "Temporary set", instanceSet);
    relSetPicker.setInput(instanceSet, tempRelSets);
  }

  @Override
  public String getString(Object object) {
    if (!(object instanceof DirectedRelation)) {
      return object.toString();
    }
    DirectedRelation relation = (DirectedRelation) object;
    return relation.getRelation().toString();
  }

  /**
   * Because this GUI class is a maintain informations about directions for
   * relations, it can be viewed as a DirectedRelationFider. This method return
   * a DirectedRelationFinder representing this class at a precise moment.
   *
   * @return a representation of this selector as a DirectedRelationFinder.
   */
  public DirectedRelationFinder getSelectedRelations() {
    MultipleDirectedRelationFinder finder =
        new MultipleDirectedRelationFinder();
    for (DirectedRelation relation : contentMap.values()) {
      finder.addOrReplaceRelation(relation.getRelation(),
          relation.matchForward(), relation.matchBackward());
    }
    return finder;
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.eclipse.utils.RelationshipSelectorListener
   *      #saveAsAction()
   */
  public void saveAsAction() {
    NewRelationshipSetWizard wizard =
        new NewRelationshipSetWizard(getSelectedRelations());
    WizardDialog dialog = new WizardDialog(shell, wizard);
    dialog.open();
  }

  /**
   * Creates a Path Matcher Model that will be used in conjunction to this tool.
   *
   * @param isRecursive Shows whether the selected relations should be applied
   * recursively.
   */
  public void createPathMatcherModel(boolean isRecursive) {
    String setName = getSelectedRelationshipSet().getName();
    PathExpression pathExpressionModel = new PathExpression();
    RelationshipSetAdapter setAdapterFromPicker =
        new RelationshipSetAdapter(setName, getRelationShips(),
            SourcePluginRegistry.getRelations());
    pathExpressionModel.addPathMatcher(
        new PathMatcherTerm(setAdapterFromPicker, isRecursive, false));
    pathMatcherModel = pathExpressionModel;
  }

  /**
   * Accessor for the <code>PathMatcher</code>.
   *
   * @return The <code>PathMatcher</code> model associated with this tool.
   */
  public PathMatcher getPathMatcherModel() {
    return pathMatcherModel;
  }

  /**
   * Returns the <code>RelationshipSet</code> that contains the selected
   * relations in this <code>RelationshipPicker</code>.
   *
   * @return Set of relations selected in this picker. Returns an empty object
   * if a valid {@link RelationshipSetSelector} object is not found.
   */
  public RelationshipSet getSelectedRelationshipSet() {
    if (relSetPicker == null) {
      return RelationshipSetAdapter.EMTPY;
    }
    return relSetPicker.getSelection();
  }
}
