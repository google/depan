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
import com.google.devtools.depan.platform.ListenerManager;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.resources.PropertyDocumentReference;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * A drop-dowp widget showing a list of named relation sets
 * ({@link RelationSetDescriptor}s).
 *
 * Listener are notified whenever the selected edge matcher is changed.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class RelationSetSelectorControl extends Composite {

  @SuppressWarnings("unused")
  // The sole current application (RelationSetEditorControl) uses a
  // listener to pickup the current selection.
  private PropertyDocumentReference<RelationSetDescriptor> curr;

  private IProject project;

  /////////////////////////////////////
  // UX Elements

  private Label nameViewer;

  private RelationSetLoadControl chooser;

  /////////////////////////////////////
  // Listener interface for interested parties

  public static interface SelectorListener {
    public void selectedRelationSetChanged(
        PropertyDocumentReference<RelationSetDescriptor> relationSet);
  }

  /** Listener when the selection change. */
  private ListenerManager<SelectorListener> selectionListeners =
      new ListenerManager<SelectorListener> ();

  private abstract static class LoggingDispatcher
      implements ListenerManager.Dispatcher<SelectorListener> {

    @Override
    public void captureException(RuntimeException errAny) {
      EdgeUILogger.LOG.error(
          "Exception in selection handler for relation set selector control",
          errAny);
    }
  }

  /////////////////////////////////////
  // Relation set selector itself

  public RelationSetSelectorControl(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(Widgets.buildContainerLayout(2));

    nameViewer = Widgets.buildGridLabel(this, "");
    nameViewer.setLayoutData(Widgets.buildGrabFillData());

    chooser = new RelationSetLoadControl(this) {
      @Override
      protected IProject getProject() {
        return project;
      }

      @Override
      protected void installLoadResource(
          PropertyDocumentReference<RelationSetDescriptor> ref) {
        if (null == ref) {
          return;
        }
        RelationSetSelectorControl.this.handleInstallLoadResource(ref);
      }
    };
    chooser.setLayoutData(Widgets.buildTrailFillData());
  }

  private void handleInstallLoadResource(
      PropertyDocumentReference<RelationSetDescriptor> ref) {
    curr = ref;
    nameViewer.setText(ref.getDocument().getName());
    fireSelectionChange(ref);
  }

  /**
   * Update the selector control to show the supplied relation set as the
   * selected item from the project's choices.
   */
  public void setInput(
      PropertyDocumentReference<RelationSetDescriptor> ref,
      IProject project) {
    this.curr = ref;
    this.project = project;
    nameViewer.setText(ref.getDocument().getName());
  }

  public void clearSelection() {
    this.curr = null;
    nameViewer.setText("");
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
      final PropertyDocumentReference<RelationSetDescriptor> ref) {

    selectionListeners.fireEvent(new LoggingDispatcher() {

      @Override
      public void dispatch(SelectorListener listener) {
        listener.selectedRelationSetChanged(ref);
      }
    });
  }
}
