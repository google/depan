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

package com.google.devtools.depan.eclipse.ui.collapse.trees;

import com.google.devtools.depan.collapse.model.CollapseTreeModel;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeTreeProvider;
import com.google.devtools.depan.model.GraphNode;

import org.eclipse.core.runtime.PlatformObject;

/**
 * @param <T> Type of data associated to each node.
 */
public class CollapseTreeRoot<T>
    extends PlatformObject {

  private final String label;
  private final CollapseViewData<T> data;

  private CollapseDataWrapper<T>[] children;

  CollapseTreeRoot(String label, CollapseViewData<T> data) {
    this.label = label;
    this.data = data;
  }

  public String getLabel() {
    return label;
  }

  public CollapseDataWrapper<T>[] getChildren() {
    if (null == children) {
      children = data.computeRootWrappers();
    }
    return children;
  }

  public CollapseDataWrapper<T> getCollapseNodeWrapper(GraphNode node) {
    return data.getCollapseDataWrapper(node);
  }

  public static <T> CollapseTreeRoot<T> build(
      String label,
      CollapseTreeModel treeModel,
      NodeTreeProvider<T> provider) {
    CollapseViewData<T> data = new CollapseViewData<T>(treeModel, provider);
    return new CollapseTreeRoot<T>(label, data);
  }
}
