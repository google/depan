/*
 * Copyright 2017 The Depan Project Authors
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
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.platform.ListenerManager;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.resources.PropertyDocumentReference;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * A drop-down widget showing a list of named edge matchers
 * ({@link GraphEdgeMatcherDescriptor}.
 *
 * Listener are notified whenever the selected edge matcher is changed.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class EdgeMatcherSelectorControl extends Composite {

  private PropertyDocumentReference<GraphEdgeMatcherDescriptor> curr;

  private IProject project;

  /////////////////////////////////////
  // UX Elements

  private Label nameViewer;

  private EdgeMatcherLoadControl chooser;

  /////////////////////////////////////
  // Listener interface for interested parties

  public static interface SelectorListener {
    public void selectedEdgeMatcherChanged(
        PropertyDocumentReference<GraphEdgeMatcherDescriptor> edgeMatcher);
  }

  /** Listener when the selection change. */
  private ListenerManager<SelectorListener> selectionListeners =
      new ListenerManager<SelectorListener> ();

  private abstract static class LoggingDispatcher
      implements ListenerManager.Dispatcher<SelectorListener> {

    @Override
    public void captureException(RuntimeException errAny) {
      EdgeUILogger.LOG.error(
          "Exception in selection handler for edge selector control", errAny);
    }
  }

  /////////////////////////////////////
  // Edge matcher selector itself

  public EdgeMatcherSelectorControl(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(Widgets.buildContainerLayout(2));

    nameViewer = Widgets.buildGridLabel(this, "");
    nameViewer.setLayoutData(Widgets.buildGrabFillData());

    chooser = new EdgeMatcherLoadControl(this) {

      @Override
      protected IProject getProject() {
        return project;
      }

      @Override
      protected void installLoadResource(
          PropertyDocumentReference<GraphEdgeMatcherDescriptor> ref) {
        EdgeMatcherSelectorControl.this.curr = ref;
        nameViewer.setText(ref.getDocument().getName());
        fireSelectionChange(ref);
      }
    };
    chooser.setLayoutData(Widgets.buildTrailFillData());
  }

  /**
   * Update the selector control to show the supplied matcher as the
   * selected item from the project's choices.
   */
  public void setInput(
      PropertyDocumentReference<GraphEdgeMatcherDescriptor> ref,
      IProject project) {
    this.curr = ref;
    this.project = project;
    nameViewer.setText(ref.getDocument().getName());
  }

  public PropertyDocumentReference<GraphEdgeMatcherDescriptor> getSelection() {
    return curr;
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
      final PropertyDocumentReference<GraphEdgeMatcherDescriptor> ref) {

    selectionListeners.fireEvent(new LoggingDispatcher() {

      @Override
      public void dispatch(SelectorListener listener) {
        listener.selectedEdgeMatcherChanged(ref);
      }
    });
  }
}
