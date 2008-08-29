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

import com.google.devtools.depan.eclipse.trees.NodeTreeView.NodeWrapper;
import com.google.devtools.depan.eclipse.utils.NodeSorter;
import com.google.devtools.depan.model.GraphNode;

import org.eclipse.jface.viewers.Viewer;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class GraphNodeSorter extends NodeSorter {

  @SuppressWarnings("unchecked")
  @Override
  public int compare(Viewer viewer, Object e1, Object e2) {
    if ((e1 instanceof GraphNode) && (e2 instanceof GraphNode)) {
      return compare(viewer, ((GraphNode) e1),
          ((NodeWrapper) e2).getNode());
    }
    return super.compare(viewer, e1, e2);
  }

  @SuppressWarnings("unchecked")
  @Override
  public int category(Object element) {
    if (element instanceof NodeWrapper) {
      return category(((NodeWrapper) element).getNode());
    }
    return super.category(element);
  }

}
