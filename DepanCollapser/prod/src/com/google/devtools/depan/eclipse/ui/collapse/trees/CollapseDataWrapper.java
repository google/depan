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

import com.google.devtools.depan.collapse.model.CollapseData;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeTreeProvider;
import com.google.devtools.depan.model.GraphNode;

import org.eclipse.core.runtime.PlatformObject;

import java.util.Collection;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 * @param <E> Type of data associated to each CollapseData<Element>.
 */
public class CollapseDataWrapper<E> extends PlatformObject {
  @SuppressWarnings("rawtypes")
  private static final CollapseDataWrapper[] LEAF_KIDS =
      new CollapseDataWrapper[0];

  private final CollapseData collapseData;
  private final NodeTreeProvider<E> provider;
  public final CollapseDataWrapper<E> parent;

  CollapseDataWrapper(
      CollapseData collapseData,
      NodeTreeProvider<E> provider,
      CollapseDataWrapper<E> parent) {
    this.collapseData = collapseData;
    this.provider = provider;
    this.parent= parent;
  }

  public CollapseData getCollapseData() {
    return collapseData;
  }

  public E getContent() {
    GraphNode node = collapseData.getMasterNode();
    return provider.getObject(node);
  }

  public CollapseDataWrapper<E> getParent() {
    return parent;
  }

  public CollapseDataWrapper<E>[] getChildren() {
    Collection<CollapseData> childrenData =
        CollapseData.getChildrenData(collapseData);

    CollapseDataWrapper<E>[] result =
        buildCollapseDataWrapperArray(childrenData, provider, this);
    return result;
  }

  @Override
  public String toString() {
    return getCollapseData().toString();
  }

  /**
   * Create an array of wrapper objects for a collection of data instances.
   * 
   * Called from two places: within {@link #getRoots()} when constructing the
   * roots, and within {@link CollapseDataWrapper#getChildren()} when building
   * the children.
   */
  @SuppressWarnings("unchecked")
  static <F> CollapseDataWrapper<F>[] buildCollapseDataWrapperArray(
      Collection<CollapseData> collapseData,
      NodeTreeProvider<F> provider,
      CollapseDataWrapper<F> parent) {

    // All empty children lists look the same,
    // so early exit with the singleton
    if (0 == collapseData.size()) {
      return LEAF_KIDS;
    }

    CollapseDataWrapper<F>[] result
        = new CollapseDataWrapper[collapseData.size()];
    int index = 0;
    for (CollapseData node : collapseData) {
      CollapseDataWrapper<F> nodeWrapper =
          new CollapseDataWrapper<F>(node, provider, parent);
      result[index] = nodeWrapper;
      index++;
    }
    return result;
  }
}