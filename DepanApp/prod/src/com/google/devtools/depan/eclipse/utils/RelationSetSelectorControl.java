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

import com.google.devtools.depan.relations.RelationSetDescriptor;

import com.google.common.collect.Lists;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import java.util.List;

/**
 * A drop-down widget showing a list of named set of relations.
 *
 * Listener are notified whenever the selected relation set is changed.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class RelationSetSelectorControl extends Composite {

  /** Text for standard picker label */
  public static final String RELATION_SET_LABEL = "Relation Set: ";

  /** Listener when the selection change. */
  private List<RelationSetSelectorListener> listeners = Lists.newArrayList();

  /** The drop-down list itself. */
  private ComboViewer setsViewer = null;

  /////////////////////////////////////
  // Helpers for users.

  /**
   * Provide a standard label for a {@code RelationSetPickerControl}.
   */
  public static Label createRelationSetLabel(Composite parent) {
    Label result = new Label(parent, SWT.NONE);
    result.setText(RELATION_SET_LABEL);
    return result;
  }

  /////////////////////////////////////
  // Relation Set Selector itself

  public RelationSetSelectorControl(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new FillLayout());

    setsViewer = new ComboViewer(this, SWT.READ_ONLY | SWT.FLAT);
    setsViewer.setContentProvider(new ArrayContentProvider());
    setsViewer.setLabelProvider(RelationSetLabelProvider.PROVIDER);
    setsViewer.setSorter(new AlphabeticSorter());

    setsViewer.addSelectionChangedListener(new ISelectionChangedListener() {

      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        RelationSetDescriptor set = extractFromSelection(event.getSelection());
        if (null == set) {
          return;
        }

        // Notify interested parties about the change
        fireSelectionChange(set);
      }
    });
  }

  /**
   * Update the picker to show only the provided choices, with the indicated
   * {@code RelationSetDescriptor} as the selected element.  The selected
   * {@code RelationSetDescriptor} must be included in the {@code choices} list,
   * or no item will selected.
   * 
   * @param selectedRelSet {@code RelationSetDescriptor} to select in control
   * @param choices selectable alternatives
   */
  public void setInput(
      RelationSetDescriptor selectedRelSet, List<RelationSetDescriptor> choices) {
    setsViewer.setInput(choices);
    setSelection(selectedRelSet);
  }

  @SuppressWarnings("unchecked")
  // TODO(leeca): make private .. needed temporarily to allow
  // RelationshipPicker to add temporary RelSets.
  public List<RelationSetDescriptor> getInput() {
    return (List<RelationSetDescriptor>) setsViewer.getInput();
  }

  /**
   * Select the given {@link RelationSetDescriptor} on the list if it is present.
   * @param instanceSet the {@link RelationSetDescriptor} to select.
   */
  public void setSelection(RelationSetDescriptor relationSetDescriptor) {
    for (RelationSetDescriptor choice : getInput()) {
      if (choice == relationSetDescriptor) {
        setsViewer.setSelection(new StructuredSelection(choice));
        fireSelectionChange(relationSetDescriptor);
        return;
      }
    }
  }

  public void clearSelection() {
    setsViewer.setSelection(StructuredSelection.EMPTY);
    fireSelectionChange(null);
  }

  /**
   * @return the currently selected RelationshipSet, or {@code null} if
   *         nothing is selected.
   */
  public RelationSetDescriptor getSelection() {
    return extractFromSelection(setsViewer.getSelection());
  }

  /**
   * return the {@link RelationSetDescriptor} for the given selection, or
   * {@code null} if an error happens.
   * 
   * @param selection the selection to extract the {@link RelationshipSet} from.
   * @return the extracted {@link RelationshipSet} or {@code null} in
   *         case of error.
   */
  private RelationSetDescriptor extractFromSelection(ISelection selection) {
    if (!(selection instanceof IStructuredSelection)) {
      return null;
    }
    IStructuredSelection select = (IStructuredSelection) selection;
    if (select.getFirstElement() instanceof RelationSetDescriptor) {
      return (RelationSetDescriptor) select.getFirstElement();
    }
    return null;
  }

  /////////////////////////////////////
  // Listener support

  /**
   * @param listener new listener for this selector
   */
  public void addChangeListener(RelationSetSelectorListener listener) {
    listeners.add(listener);
  }

  /**
   * @param listener new listener for this selector
   */
  public void removeChangeListener(RelationSetSelectorListener listener) {
    listeners.remove(listener);
  }

  /**
   * Called when the selection changes to the given {@link ISelection}.
   * @param selection the new selection
   */
  protected void fireSelectionChange(RelationSetDescriptor newSet) {
    for (RelationSetSelectorListener listener : listeners) {
      listener.selectedSetChanged(newSet);
    }
  }
}
