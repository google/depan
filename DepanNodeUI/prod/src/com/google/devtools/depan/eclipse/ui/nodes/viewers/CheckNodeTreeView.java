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

package com.google.devtools.depan.eclipse.ui.nodes.viewers;

import com.google.devtools.depan.eclipse.ui.nodes.trees.NodeWrapper;
import com.google.devtools.depan.eclipse.ui.nodes.trees.NodeWrapperTreeSorter;
import com.google.devtools.depan.model.GraphNode;

import com.google.common.collect.Sets;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import java.util.Collection;
import java.util.Set;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class CheckNodeTreeView extends GraphNodeViewer {

  private CheckboxTreeViewer tree;

  private boolean recursiveTreeSelect;

  public CheckNodeTreeView(Composite parent) {
    super(parent);
  }

  @Override
  protected CheckboxTreeViewer createTreeViewer(Composite parent) {
    int style = SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.BORDER
        | SWT.H_SCROLL | SWT.V_SCROLL;
    CheckboxTreeViewer result = new CheckboxTreeViewer(parent, style);
    result.setLabelProvider(new WorkbenchLabelProvider());
    result.setContentProvider(new BaseWorkbenchContentProvider());
    result.setComparator(new NodeWrapperTreeSorter());

    result.addCheckStateListener(new ICheckStateListener() {
      @Override
      public void checkStateChanged(CheckStateChangedEvent event) {
        if (recursiveTreeSelect) {
          tree.setSubtreeChecked(event.getElement(), event.getChecked());
        }
      }
    });

    tree = result;
    return result;
  }

  public void setRecursive(boolean recursiveTreeSelect) {
    this.recursiveTreeSelect = recursiveTreeSelect;
  }

  public GraphNode getFirstNode() {
    for (Object item : getCheckedElements()) {
      if (item instanceof NodeWrapper) {
        return ((NodeWrapper<?>) item).getNode();
      }
    }
    return null;
  }

  public Collection<GraphNode> getSelectedNodes() {
    Set<GraphNode> result = Sets.newHashSet();
    for (Object item : getCheckedElements()) {
      if (item instanceof NodeWrapper) {
        GraphNode node = ((NodeWrapper<?>) item).getNode();
        result.add(node);
      }
    }
    return result;
  };

  private Object[] getCheckedElements() {
    return tree.getCheckedElements();
  }
}
