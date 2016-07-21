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

package com.google.devtools.depan.relations.eclipse.ui.widgets;

import com.google.devtools.depan.edge_ui.EdgeUILogger;
import com.google.devtools.depan.platform.AlphabeticSorter;
import com.google.devtools.depan.platform.ListenerManager;
import com.google.devtools.depan.platform.ViewerObjectToString;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;

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

import java.util.Collection;
import java.util.List;

/**
 * A drop-dowp widget showing a list of named relation sets
 * ({@link RelationSetDescriptor}s).
 *
 * Listener are notified whenever the selected edge matcher is changed.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class RelationSetSelectorControl extends Composite {

  /** The drop-down list itself. */
  private ComboViewer setsViewer;

  /////////////////////////////////////
  // Listener interface for interested parties

  public static interface SelectorListener {
    public void selectedRelationSetChanged(
        RelationSetDescriptor relationSet);
  }

  /** Listener when the selection change. */
  private ListenerManager<SelectorListener> selectionListeners =
      new ListenerManager<SelectorListener> ();

  private abstract static class LoggingDispatcher
      implements ListenerManager.Dispatcher<SelectorListener> {

    @Override
    public void captureException(RuntimeException errAny) {
      EdgeUILogger.logException(
          "Exception in selection handler for edge selector control", errAny);
    }
  }

  /////////////////////////////////////
  // Relation set selector itself

  public RelationSetSelectorControl(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new FillLayout());

    setsViewer = new ComboViewer(this, SWT.READ_ONLY | SWT.FLAT);
    setsViewer.setContentProvider(new ArrayContentProvider());
    setsViewer.setLabelProvider(RelationSetLabelProvider.PROVIDER);

    setsViewer.setSorter(new AlphabeticSorter(new ViewerObjectToString() {

        @Override
        public String getString(Object object) {
          return RelationSetLabelProvider.PROVIDER.getText(object);
        }
      }));

    setsViewer.addSelectionChangedListener(new ISelectionChangedListener() {

      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        RelationSetDescriptor relationSet =
            extractFromSelection(event.getSelection());
        if (null == relationSet) {
          return;
        }

        // Notify interested parties about the change
        fireSelectionChange(relationSet);
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
   * @param choices selectable alternatives.  Since the viewer controls the
   *   presentation order, no ordering is expected.
   */
  public void setInput(
      RelationSetDescriptor relationSet,
      Collection<RelationSetDescriptor> choices) {
    setsViewer.setInput(choices);
    setSelection(relationSet);
  }

  @SuppressWarnings("unchecked")
  private List<RelationSetDescriptor> getInput() {
    return (List<RelationSetDescriptor>) setsViewer.getInput();
  }

  /**
   * Select the given {@link RelationSetDescriptor} on the list if it is present.
   * @param instanceSet the {@link RelationSetDescriptor} to select.
   */
  public void setSelection(RelationSetDescriptor relationSet) {
    for (RelationSetDescriptor choice : getInput()) {
      if (choice == relationSet) {
        setsViewer.setSelection(new StructuredSelection(choice));
        fireSelectionChange(relationSet);
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
  public RelationSetDescriptor getSelection() {
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
  public void addChangeListener(SelectorListener listener) {
    selectionListeners.addListener(listener);
  }

  /**
   * @param listener new listener for this selector
   */
  public void removeChangeListener(SelectorListener listener) {
    selectionListeners.removeListener(listener);
  }

  /**
   * Called when the selection changes to the given {@link ISelection}.
   * @param selection the new selection
   */
  protected void fireSelectionChange(
      final RelationSetDescriptor relationSet) {

    selectionListeners.fireEvent(new LoggingDispatcher() {

      @Override
      public void dispatch(SelectorListener listener) {
        listener.selectedRelationSetChanged(relationSet);
      }
    });
  }
}
