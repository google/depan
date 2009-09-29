/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.eclipse.views;

import com.google.devtools.depan.eclipse.visualization.SelectionChangeListener;
import com.google.devtools.depan.model.GraphNode;

/**
 * Provide an abstract Tool implementation for tools that operates
 * on ViewEditor contents and tracks the currently selected Nodes.
 * <p>
 * Interested tools need only implement {@link #emptySelection()} and
 * {@link #update(Map)} to integrate their Node selection control.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public abstract class ViewSelectionListenerTool extends ViewEditorTool
    implements SelectionChangeListener {

  @Override
  protected void acquireResources() {
    getEditor().addSelectionChangeListener(this);
  }

  @Override
  protected void releaseResources() {
    emptySelection();
    if (hasEditor()) {
      getEditor().removeSelectionChangeListener(this);
    }
  }

  @Override
  protected void clearControls() {
    emptySelection();
  }

  @Override
  protected void updateControls() {
    updateSelectionTo(getEditor().getSelectedNodeArray());
  }

  /**
   * Forward selection changed notifications to the selection control.
   * Implemented for {@link SelectionChangeListener}.
   *
   * @see com.google.devtools.depan.eclipse.visualization.PaintListener
   *      #notifyEndOfRepaint()
   */
  @Override
  public void notifyAddedToSelection(GraphNode[] selected) {
    updateSelectedAdd(selected);
  }

  /**
   * Forward selection changed notifications to the selection control.
   * Implemented for {@link SelectionChangeListener}.
   *
   * @see com.google.devtools.depan.eclipse.visualization.PaintListener
   *      #notifyEndOfRepaint()
   */
  @Override
  public void notifyRemovedFromSelection(GraphNode[] unselected) {
    updateSelectedRemove(unselected);
  }

  /**
   * Extending classes implement this to update their node selection control.
   *
   * @param selection Map of nodes and selection states
   */
  public abstract void updateSelectedAdd(GraphNode[] selection);
  public abstract void updateSelectedRemove(GraphNode[] selection);
  public abstract void updateSelectionTo(GraphNode[] selection);

  /**
   * Extending classes implement this to empty their node selection control.
   */
  public abstract void emptySelection();
}
