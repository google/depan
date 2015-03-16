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

import com.google.devtools.depan.eclipse.utils.relsets.RelSetDescriptor;
import com.google.devtools.depan.model.RelationshipSet;

import com.google.common.collect.Lists;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import java.util.List;

/**
 * A drop-down widget showing a list of named set of relationships.
 *
 * Listener are notified whenever the selected relation set is changed.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class RelationshipSetPickerControl extends Composite {

  /** Text for standard picker label */
  public static final String RELATION_SET_LABEL = "Relation Set: ";

  /** Listener when the selection change. */
  private List<RelationshipSelectorListener> listeners = Lists.newArrayList();

  /** The drop-down list itself. */
  private ComboViewer setsViewer = null;

  /**
   * Return the proper string label from {@link RelSetDescriptor}s.
   */
  private static class RelSetLabelProvider extends BaseLabelProvider
      implements ILabelProvider {

    @Override
    public Image getImage(Object element) {
      return null;
    }

    @Override
    public String getText(Object element) {
      return ((RelSetDescriptor) element).getName();
    }
  }

  /////////////////////////////////////
  // Helpers for users.

  /**
   * Provide a standard label for a {@code RelationSetPickerControl}.
   */
  public static Label createPickerLabel(Composite parent) {
    Label result = new Label(parent, SWT.NONE);
    result.setText(RELATION_SET_LABEL);
    return result;
  }

  /////////////////////////////////////
  // Relationship Set Selector itself

  public RelationshipSetPickerControl(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new FillLayout());

    setsViewer = new ComboViewer(this, SWT.READ_ONLY | SWT.FLAT);
    setsViewer.setContentProvider(new ArrayContentProvider());
    setsViewer.setLabelProvider(new RelSetLabelProvider());
    setsViewer.setSorter(new AlphabeticSorter());

    setsViewer.addSelectionChangedListener(new ISelectionChangedListener() {

      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        RelationshipSet set = extractFromSelection(event.getSelection());
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
   * {@code RelationSet} as the selected element.  The selected
   * {@code RelationshipSet} must be included in the {@code choices} list,
   * or no item will selected.
   * 
   * @param selectedRelSet {@code RelationSet} to select in control
   * @param choices selectable alternatives
   */
  public void setInput(
      RelationshipSet selectedRelSet, List<RelSetDescriptor> choices) {
    setsViewer.setInput(choices);
    setSelection(selectedRelSet);
  }

  @SuppressWarnings("unchecked")
  // TODO(leeca): make private .. needed temporarily to allow
  // RelationshipPicker to add temporary RelSets.
  public List<RelSetDescriptor> getInput() {
    return (List<RelSetDescriptor>) setsViewer.getInput();
  }

  /**
   * Select the given {@link RelationshipSet} on the list if it is present.
   * @param instanceSet the {@link RelationshipSet} to select.
   */
  public void setSelection(RelationshipSet instanceSet) {
    for (RelSetDescriptor choice : getInput()) {
      if (choice.getRelSet() == instanceSet) {
        setsViewer.setSelection(new StructuredSelection(choice));
        fireSelectionChange(instanceSet);
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
  public RelationshipSet getSelection() {
    return extractFromSelection(setsViewer.getSelection());
  }

  /**
   * return the {@link RelationshipSet} for the given selection, or
   * {@code null} if an error happens.
   * 
   * @param selection the selection to extract the {@link RelationshipSet} from.
   * @return the extracted {@link RelationshipSet} or {@code null} in
   *         case of error.
   */
  private RelationshipSet extractFromSelection(ISelection selection) {
    if (!(selection instanceof IStructuredSelection)) {
      return null;
    }
    IStructuredSelection select = (IStructuredSelection) selection;
    if (select.getFirstElement() instanceof RelSetDescriptor) {
      return ((RelSetDescriptor) select.getFirstElement()).getRelSet();
    }
    return null;
  }

  /////////////////////////////////////
  // Listener support

  /**
   * @param listener new listener for this selector
   */
  public void addChangeListener(RelationshipSelectorListener listener) {
    listeners.add(listener);
  }

  /**
   * @param listener new listener for this selector
   */
  public void removeChangeListener(RelationshipSelectorListener listener) {
    listeners.remove(listener);
  }

  /**
   * Called when the selection changes to the given {@link ISelection}.
   * @param selection the new selection
   */
  protected void fireSelectionChange(RelationshipSet newSet) {
    for (RelationshipSelectorListener listener : listeners) {
      listener.selectedSetChanged(newSet);
    }
  }
}
