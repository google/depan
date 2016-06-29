/*
 * Copyright 2014 The Depan Project Authors
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

package com.google.devtools.depan.eclipse.ui.collapse.trees;

import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeSorter;
import com.google.devtools.depan.model.GraphNode;

import org.eclipse.jface.viewers.Viewer;

/**
 * TODO - Collapse adds this capability to the NodeTrees
 *
 * Sort collapse tree entries based on the master node.
 */
public class NodeTreeSorter<E> extends NodeSorter {

  @SuppressWarnings("unchecked")
  @Override
  public int compare(Viewer viewer, Object e1, Object e2) {
    if ((e1 instanceof CollapseDataWrapper) && (e2 instanceof CollapseDataWrapper)) {
      return compare(viewer,
          getGraphNode((CollapseDataWrapper<E>) e1),
          getGraphNode((CollapseDataWrapper<E>) e2));
    }
    return super.compare(viewer, e1, e2);
  }

  @SuppressWarnings("unchecked")
  @Override
  public int category(Object element) {
    if (element instanceof CollapseDataWrapper<?>) {
      return category(getGraphNode((CollapseDataWrapper<E>) element));
    }
    return super.category(element);
  }

  public GraphNode getGraphNode(CollapseDataWrapper<E> wrapper) {
    return wrapper.getCollapseData().getMasterNode();
  }
}
