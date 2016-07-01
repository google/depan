/*
 * Copyright 2014 The Depan Project Authors
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
package com.google.devtools.depan.collapse.model;

import com.google.devtools.depan.model.GraphNode;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

/**
 * Provide read-only access to the {@link Collapser}.  Any collapse changes
 * should use the ViewPreferences accessor to ensure listeners are notified
 * properly.
 * 
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
public class CollapseTreeModel {

  /** Source of data about collapsed nodes. */
  private final Collapser collapser;

  public CollapseTreeModel(Collapser collapser) {
    this.collapser = collapser;
  }

  public Collection<CollapseData> computeRoots() {
    return collapser.computeRoots();
  }

  public Collection<GraphNode> getMasterNodeSet() {
    return collapser.getMasterNodeSet();
  }

  /**
   * Provide complete set of nodes in this model.
   */
  public Collection<GraphNode> computeNodes() {
    return collapser.computeNodes();
  }

  /**
   * Provide all {@link CollapseData} in depth first order.
   */
  public Collection<CollapseData> computeDepthFirst() {
    List<CollapseData> result = Lists.newArrayList();
    for (CollapseData root : computeRoots()) {
      addChildCollapse(result, root);
    }
    return result;
  }

  private void addChildCollapse(List<CollapseData> result, CollapseData data) {
    for (CollapseData nest : data.getChildrenCollapse()) {
       addChildCollapse(result, nest);
     }
    result.add(data);
  }
}
