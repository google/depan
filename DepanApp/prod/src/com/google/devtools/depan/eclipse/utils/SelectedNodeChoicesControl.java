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

package com.google.devtools.depan.eclipse.utils;

import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.platform.TableContentProvider;

import com.google.common.collect.Lists;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Package the chosing of one node from the set of selected nodes as
 * as reusable control.
 * 
 * @author <a href='mailto:leeca@pnambic.com'>Lee Carver</a>
 */
public class SelectedNodeChoicesControl extends ComboViewer {
  /**
   * Content provider for selected nodes. Fill the list of selected nodes used
   * to choose the master node for a new collapse group.
   */
  private TableContentProvider<GraphNode> selection =
      new TableContentProvider<GraphNode>();

  public SelectedNodeChoicesControl(Composite parent) {
    super(parent, SWT.READ_ONLY | SWT.FLAT);
    selection.initViewer(this);
  }

  public Collection<GraphNode> getSelectedNodes() {
    int count = getCombo().getItemCount();
    if (count <= 0) {
      return Collections.emptyList();
    }

    List<GraphNode> result = Lists.newArrayListWithExpectedSize(count);
    for (int i = 0; i < count; i++) {
      result.add(selection.getElementAtIndex(i));
    }

    return result;
  }

  // How is this different from getSelectedNodes()??
  public Collection<GraphNode> getSelectionNodes() {
    return selection.getObjects();
  }

  public GraphNode getChosenNode() {
    return selection.getElementAtIndex(getCombo().getSelectionIndex());
  }

  public void emptySelection() {
    selection.clear();
    refresh();
  }

  public void extendSelection(Collection<GraphNode> extension) {
    for (GraphNode node : extension) {
      selection.add(node);
    }

    refresh(false);
    getCombo().select(0);
  }

  public void reduceSelection(Collection<GraphNode> reduction) {
    for (GraphNode node : reduction) {
      selection.remove(node);
    }

    refresh(false);
    getCombo().select(0);
  }
}
