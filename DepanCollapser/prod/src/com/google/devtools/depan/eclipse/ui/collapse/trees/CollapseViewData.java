/*
 * Copyright 2016 The Depan Project Authors
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

import com.google.devtools.depan.collapse.model.CollapseData;
import com.google.devtools.depan.collapse.model.CollapseTreeModel;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeTreeProvider;
import com.google.devtools.depan.model.GraphNode;

import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;

/**
 * Provide tree viewer wrappers for collapsed nodes.
 * Maps nodes to wrappers, and provides wrappers as needed.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class CollapseViewData<T> {

  /** Generated hierarchical view */
  private final CollapseTreeModel treeModel;

  /** Convert from {@link GraphNode} to supplied data type. */
  private final NodeTreeProvider<T> provider;

  private Map<GraphNode, CollapseDataWrapper<T>>
      reverseMap = Maps.newHashMap();

  private CollapseDataWrapper<T>[] rootWrappers;

  public CollapseViewData(
      CollapseTreeModel treeModel, NodeTreeProvider<T> provider) {
    this.treeModel = treeModel;
    this.provider = provider;
  }

  public T getContent(GraphNode node) {
    return provider.getObject(node);
  }

  public CollapseDataWrapper<T>[] computeRootWrappers() {
    if (null == rootWrappers) {
      Collection<CollapseData> roots = treeModel.computeRoots();
      rootWrappers = buildWrapperArray(roots, null);
    }
    return rootWrappers;
  }

  public CollapseDataWrapper<T> getCollapseDataWrapper(GraphNode node) {
    return reverseMap.get(node);
  }

  public CollapseDataWrapper<T>[] buildChildren(CollapseDataWrapper<T> parent) {
    Collection<CollapseData> childrenData = pruneChildren(parent);

    CollapseDataWrapper<T>[] result = buildWrapperArray(childrenData, parent);
    return result;
  }

  /**
   * Construct the array of {@link CollapseDataWrapper}s, and ensure they
   * are recorded in the {@link #reverseMap}.
   */
  private CollapseDataWrapper<T>[] buildWrapperArray(
      Collection<CollapseData> childrenData, CollapseDataWrapper<T> parent) {
    CollapseDataWrapper<T>[] result =
        CollapseDataWrapper.buildCollapseDataWrapperArray(
            childrenData, parent, this);
    for (CollapseDataWrapper<T> item : result) {
      reverseMap.put(item.getCollapseData().getMasterNode(), item);
    }
    return result;
  }

  /**
   * Remove the master node if it is present in the children list.
   */
  private Collection<CollapseData> pruneChildren(
      CollapseDataWrapper<T> parent) {
    Collection<CollapseData> childrenData =
        CollapseData.buildChildrenData(parent.getCollapseData());
    CollapseData prune = findParent(childrenData, parent);
    if (null != prune) {
      childrenData.remove(prune);
    }
    return childrenData;
  }

  private CollapseData findParent(
      Collection<CollapseData> childrenData, CollapseDataWrapper<T> parent) {
    for (CollapseData data : childrenData) {
      if (data.getMasterNode() == parent.getCollapseData().getMasterNode()) {
        return data;
      }
    }

    return null;
  }
}
