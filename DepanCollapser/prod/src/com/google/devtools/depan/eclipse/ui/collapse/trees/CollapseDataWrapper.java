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
import com.google.devtools.depan.model.GraphNode;

import org.eclipse.core.runtime.PlatformObject;

import java.util.Collection;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 * @param <T> Type of data associated to each CollapseData.
 */
public class CollapseDataWrapper<T> extends PlatformObject {

  @SuppressWarnings("rawtypes")
  private static final CollapseDataWrapper[] LEAF_KIDS =
      new CollapseDataWrapper[0];

  private final CollapseData collapseData;
  private final CollapseDataWrapper<T> parent;
  private final CollapseViewData<T> data;
  
  private CollapseDataWrapper<T>[] children;

  CollapseDataWrapper(
      CollapseData collapseData,
      CollapseDataWrapper<T> parent,
      CollapseViewData<T> data) {
    this.collapseData = collapseData;
    this.parent= parent;
    this.data = data;
  }

  public CollapseData getCollapseData() {
    return collapseData;
  }

  public T getContent() {
    GraphNode node = collapseData.getMasterNode();
    return data.getContent(node);
  }

  public CollapseDataWrapper<T> getParent() {
    return parent;
  }

  public CollapseDataWrapper<T>[] getChildren() {
    if (null == children) {
      children = data.buildChildren(this);
    }

    return children;
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
  public static <T> CollapseDataWrapper<T>[] buildCollapseDataWrapperArray(
      Collection<CollapseData> collapseData,
      CollapseDataWrapper<T> parent,
      CollapseViewData<T> data) {

    // All empty children lists look the same,
    // so early exit with the singleton
    if (0 == collapseData.size()) {
      return LEAF_KIDS;
    }

    CollapseDataWrapper<T>[] result =
        new CollapseDataWrapper[collapseData.size()];
    int index = 0;
    for (CollapseData node : collapseData) {
      CollapseDataWrapper<T> nodeWrapper =
          new CollapseDataWrapper<T>(node, parent, data);
      result[index] = nodeWrapper;
      index++;
    }
    return result;
  }
}
