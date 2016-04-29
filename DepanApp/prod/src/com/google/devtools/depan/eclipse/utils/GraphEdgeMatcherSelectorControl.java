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
import com.google.devtools.edges.matchers.GraphEdgeMatcherDescriptor;

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
 * A drop-down widget showing a list of named edge matchers
 * ({@link GraphEdgeMatcherDescriptor}.
 *
 * Listener are notified whenever the selected edge matcher is changed.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class GraphEdgeMatcherSelectorControl extends Composite {

  /** Text for standard picker label */
  public static final String EDGE_MATCHER_LABEL = "Edges: ";

  /** The drop-down list itself. */
  private ComboViewer setsViewer = null;

  /////////////////////////////////////
  // Helpers for users.

  /**
   * Provide a standard label for a {@code RelationSetPickerControl}.
   */
  public static Label createEdgeMatcherLabel(Composite parent) {
    Label result = new Label(parent, SWT.NONE);
    result.setText(EDGE_MATCHER_LABEL);
    return result;
  }

  /////////////////////////////////////
  // Listener interface for interested parties

  public static interface SelectorListener {
    public void selectedEdgeMatcherChanged(
        GraphEdgeMatcherDescriptor edgeMatcher);
  }

  /** Listener when the selection change. */
  private List<SelectorListener> listeners = Lists.newArrayList();

  /////////////////////////////////////
  // Edge Matcher Selector itself

  public GraphEdgeMatcherSelectorControl(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new FillLayout());

    setsViewer = new ComboViewer(this, SWT.READ_ONLY | SWT.FLAT);
    setsViewer.setContentProvider(new ArrayContentProvider());
    setsViewer.setLabelProvider(GraphEdgeMatcherLabelProvider.PROVIDER);
    setsViewer.setSorter(new AlphabeticSorter());

    setsViewer.addSelectionChangedListener(new ISelectionChangedListener() {

      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        GraphEdgeMatcherDescriptor edgeMatcher = extractFromSelection(event.getSelection());
        if (null == edgeMatcher) {
          return;
        }

        // Notify interested parties about the change
        fireSelectionChange(edgeMatcher);
      }
    });
  }

  /**
   * Update the picker to show only the provided choices, with the indicated
   * {@code GraphEdgeMatcherDescriptor} as the selected element.  The selected
   * {@code GraphEdgeMatcherDescriptor} must be included in the {@code choices}
   * list, or no item will selected.
   * 
   * @param selectedRelSet {GraphEdgeMatcherDescriptor RelationSet} to select
   *  in control
   * @param choices selectable alternatives
   */
  public void setInput(
      GraphEdgeMatcherDescriptor selectedEdgeMatcher,
      List<GraphEdgeMatcherDescriptor> choices) {
    setsViewer.setInput(choices);
    setSelection(selectedEdgeMatcher);
  }

  @SuppressWarnings("unchecked")
  // TODO(leeca): make private .. needed temporarily to allow
  // RelationshipPicker to add temporary RelSets.
  public List<GraphEdgeMatcherDescriptor> getInput() {
    return (List<GraphEdgeMatcherDescriptor>) setsViewer.getInput();
  }

  /**
   * Select the given {@link RelationSetDescriptor} on the list if it is present.
   * @param instanceSet the {@link RelationSetDescriptor} to select.
   */
  public void setSelection(GraphEdgeMatcherDescriptor edgeMatcher) {
    for (GraphEdgeMatcherDescriptor choice : getInput()) {
      if (choice == edgeMatcher) {
        setsViewer.setSelection(new StructuredSelection(choice));
        fireSelectionChange(edgeMatcher);
        return;
      }
    }
  }

  public void clearSelection() {
    setsViewer.setSelection(StructuredSelection.EMPTY);
    fireSelectionChange(null);
  }

  /**
   * @return the currently selected GraphEdgeMatcherDescriptor,
   *    or {@code null} if nothing is selected.
   */
  public GraphEdgeMatcherDescriptor getSelection() {
    return extractFromSelection(setsViewer.getSelection());
  }

  /**
   * return the {@link GraphEdgeMatcherDescriptor} for the given selection,
   * or {@code null} if an error happens.
   * 
   * @param selection selection object containing a
   *   {@link GraphEdgeMatcherDescriptor}
   * @return the extracted {@link GraphEdgeMatcherDescriptor} or
   *   {@code null} in case of error.
   */
  private GraphEdgeMatcherDescriptor extractFromSelection(ISelection selection) {
    if (!(selection instanceof IStructuredSelection)) {
      return null;
    }
    IStructuredSelection select = (IStructuredSelection) selection;
    if (select.getFirstElement() instanceof GraphEdgeMatcherDescriptor) {
      return (GraphEdgeMatcherDescriptor) select.getFirstElement();
    }
    return null;
  }

  /////////////////////////////////////
  // Listener support

  /**
   * @param listener new listener for this selector
   */
  public void addChangeListener(SelectorListener listener) {
    listeners.add(listener);
  }

  /**
   * @param listener new listener for this selector
   */
  public void removeChangeListener(SelectorListener listener) {
    listeners.remove(listener);
  }

  /**
   * Called when the selection changes to the given {@link ISelection}.
   * @param selection the new selection
   */
  protected void fireSelectionChange(GraphEdgeMatcherDescriptor newEdgeMatcher) {
    for (SelectorListener listener : listeners) {
      listener.selectedEdgeMatcherChanged(newEdgeMatcher);
    }
  }
}
