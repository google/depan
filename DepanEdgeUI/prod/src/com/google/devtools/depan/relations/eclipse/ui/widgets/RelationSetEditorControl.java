/*
 * Copyright 2015 The Depan Project Authors
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

package com.google.devtools.depan.relations.eclipse.ui.widgets;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.graph.registry.RelationRegistry;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.models.RelationSetRepository;
import com.google.devtools.depan.resources.PropertyDocumentReference;

import com.google.common.collect.Lists;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import java.util.Collection;

/**
 * Editor control for {@link RelationSet}.  This {@link Control} is
 * intended for wide-spread use.  It can be an element of an editor for
 * any container that has embedded {@link RelationSet}s.
 * 
 * Although the caller sets the range of displayed relations via the
 * {@link #setInput(Collection)} method, this {@link Control} hides this
 * notion of a fixed "universe" from normal use.  This facilitates the use
 * of "intentional" {@link RelationSet}s (e.g. {@code ALL}, {@code EMPTY},
 * {@code EVEN}) in concert with the commonly used "extensional"
 * {@link RelationSet}s (e.g. enumerated {@link Relation}s). The method
 * {@link #selectedInvertRelations()} 
 *
 * This avoids exposing the universe of elements, which helps with
 * predicate based {@link RelationSet}s (e.g. {@code ALL}, {@code EMPTY},
 * {@code EVEN}).
 *
 * Based heavily on the legacy RelationshipPicker and the RelationPickerTool
 * types.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class RelationSetEditorControl extends Composite {

  /////////////////////////////////////
  // UX Elements

  /**
   * Manages the relation set data.
   */
  private RelationSetTableControl viewer;

  /**
   * The {@link RelationshipSetSelector} to choose a named set.
   */
  private RelationSetSelectorControl relationSetSelector;

  private Collection<Relation> relSetChange;

  /////////////////////////////////////
  // Public methods

  public RelationSetEditorControl(Composite parent) {
    super(parent, SWT.NONE);

    GridLayout gridLayout = Widgets.buildContainerLayout(1);
    gridLayout.verticalSpacing = 10;
    setLayout(gridLayout);

    Composite commands = setupCommandButtons(this);
    commands.setLayoutData(Widgets.buildHorzFillData());

    viewer = new RelationSetTableControl(this);
    viewer.setLayoutData(Widgets.buildGrabFillData());
    viewer.addSelectionChangedListener(new ISelectionChangedListener() {

        @Override
        public void selectionChanged(SelectionChangedEvent event) {
          handleTableSelectionChange();
        }
      });
  }

  /**
   * Provide the list of {@link Relation}s to display in the table.
   * Typically, this is the universe of {@link Relation}s for the current
   * document.
   * 
   * The underlying table view is also {@code refresh()}ed.
   * 
   * In normal use, {@link #setRelationSetRepository(RelationSetRepository)}
   * is a better choice.  See {@link RelationSetRepository.ProvidesUniverse}
   * for integration details.
   * 
   * @param relations
   */
  public void setInput(Collection<Relation> relations) {
    viewer.setInput(relations);
    viewer.refresh();
  }

  /**
   * Updates control with {@link #setInput(Collection)} based on repo content.
   */
  public void setRelationSetRepository(RelationSetRepository relSetRepo) {
    viewer.setVisibiltyRepository(relSetRepo);
    setInput(getRepositoryUniverse(relSetRepo));
  }

  private Collection<Relation> getRepositoryUniverse(
      RelationSetRepository relSetRepo) {
    if (relSetRepo instanceof RelationSetRepository.ProvidesUniverse) {
      return((RelationSetRepository.ProvidesUniverse) relSetRepo).getUniverse();
    }
    return RelationRegistry.getRegistryRelations();
  }

  public void removeRelationSetRepository(RelationSetRepository relSetRepo) {
    viewer.removeRelSetRepository(relSetRepo);
  }

  /**
   * Select the lines described by the supplied collection of
   * {@link Relation}s.
   */
  public void selectRelations(Collection<Relation> relations) {
    viewer.setSelectedRelations(relations, false);
  }

  public void setRelationSetSelectorInput(
      PropertyDocumentReference<RelationSetDescriptor> selectedRelSet,
      IProject project) {

    relationSetSelector.setInput(selectedRelSet, project);
    handleRelSetPickerChange(selectedRelSet);
  }

  /////////////////////////////////////
  // UX Actions

  private void clearAll() {
    viewer.clearAll();
  }

  private void checkAll() {
    viewer.checkAll();
  }

  private void invertAll() {
    viewer.invertAll();
  }

  private void checkSelected() {
    viewer.checkRelations(viewer.getSelectedRelations());
  }

  private void clearSelected() {
    viewer.clearRelations(viewer.getSelectedRelations());
  }

  private void invertSelected() {
    viewer.invertRelations(viewer.getSelectedRelations());
  }

  /**
   * Select the inverse of elements from the supplied collection of
   * {@link Relation}s.
   */
  private void selectedInvertRelations() {
    Collection<Relation> relations = viewer.getSelectedRelations();
    viewer.selectInverseRelations(relations);
  }

  /////////////////////////////////////
  // UX Setup

  private Composite setupCommandButtons(Composite parent) {
    Composite result = Widgets.buildGridContainer(parent, 1);

    Composite pickerRegion = setupRelationSetSelector(result);
    pickerRegion.setLayoutData(Widgets.buildHorzFillData());

    Composite selectOps =  setupSelectOps(parent);
    selectOps.setLayoutData(Widgets.buildHorzFillData());

    return result;
  }

  @SuppressWarnings("unused")
  private Composite setupRelationSetSelector(Composite parent) {
    Composite result = Widgets.buildGridContainer(parent, 3);

    Label pickerLabel = Widgets.buildCompactLabel(result, "Select Relations: ");

    relationSetSelector = new RelationSetSelectorControl(result);
    relationSetSelector.setLayoutData(Widgets.buildHorzFillData());
    relationSetSelector.addChangeListener(
        new RelationSetSelectorControl.SelectorListener() {

      @Override
      public void selectedRelationSetChanged(
          PropertyDocumentReference<RelationSetDescriptor> relationSet) {
        handleRelSetPickerChange(relationSet);
      }
    });

    Button reverse = Widgets.buildTrailPushButton(result, "Reverse selection");
    reverse.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        selectedInvertRelations();
      }
    });

    return result;
  }

  private Composite setupSelectOps(Composite parent) {
    Composite result = Widgets.buildGridContainer(parent, 3);

    // First row for selected relations
    Button checkSel = Widgets.buildGridPushButton(result, "check selected");
    checkSel.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        checkSelected();
      }
    });

    Button clearSel = Widgets.buildGridPushButton(result, "clear selected");
    clearSel.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        clearSelected();
      }
    });

    Button invertSel = Widgets.buildGridPushButton(result, "invert selected");
    invertSel.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        invertSelected();
      }
    });

    // Second row for selected relations
    Button checkAll = Widgets.buildGridPushButton(result, "check all");
    checkAll.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        checkAll();
      }
    });

    Button clearAll = Widgets.buildGridPushButton(result, "clear all");
    clearAll.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        clearAll();
      }
    });

    Button invertAll = Widgets.buildGridPushButton(result, "invert all");
    invertAll.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        invertAll();
      }
    });

    return result;
  }

  /////////////////////////////////////
  // RelSetPicker integration

  private void handleTableSelectionChange() {
    if (null != relSetChange) {
      relSetChange = null;
      return;
    }
    // Invalidate relation set on manual relation selection
    relationSetSelector.clearSelection();
  }

  /**
   * Change listener for RelationSetPickerControl.
   */
  private void handleRelSetPickerChange(
      PropertyDocumentReference<RelationSetDescriptor> relationSet) {
    if (null != relationSet) {
      relSetChange = buildRelations(relationSet.getDocument().getInfo());
      selectRelations(relSetChange);
    }
  }

  private Collection<Relation> buildRelations(RelationSet relationSet) {
    Collection<Relation> result = Lists.newArrayList();
    for (Relation relation : RelationRegistry.getRegistryRelations()) {
      if (relationSet.contains(relation)) {
        result.add(relation);
      }
    }
    return result;
  }
}
