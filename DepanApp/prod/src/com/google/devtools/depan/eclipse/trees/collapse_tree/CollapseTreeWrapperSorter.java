/*
 * Copyright 2014 Pnambic Computing.
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

package com.google.devtools.depan.eclipse.trees.collapse_tree;

import com.google.devtools.depan.eclipse.trees.NodeTreeView.NodeWrapper;
import com.google.devtools.depan.eclipse.trees.collapse_tree.CollapseTreeView.CollapseDataWrapper;
import com.google.devtools.depan.eclipse.utils.NodeSorter;
import com.google.devtools.depan.model.GraphNode;

import org.eclipse.jface.viewers.Viewer;

/**
 * Sort collapse tree entries based on the master node.
 */
public class CollapseTreeWrapperSorter<E> extends NodeSorter {

  @Override
  public int compare(Viewer viewer, Object e1, Object e2) {
    if ((e1 instanceof CollapseDataWrapper) && (e2 instanceof CollapseDataWrapper)) {
      return compare(viewer, getGraphNode(e1), getGraphNode(e2));
    }
    return super.compare(viewer, e1, e2);
  }

  @Override
  public int category(Object element) {
    if (element instanceof NodeWrapper) {
      return category(getGraphNode(element));
    }
    return super.category(element);
  }

  @SuppressWarnings("unchecked")
  public GraphNode getGraphNode(Object obj) {
    return ((CollapseDataWrapper<E>) obj).getCollapseData().getMasterNode();
  }
}
