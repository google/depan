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
package com.google.devtools.depan.view;

import com.google.devtools.depan.model.GraphNode;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Set;

/**
 * Use collapser start to represent a hierarchical tree of GraphNodes.
 * 
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
public class CollapseTreeModel {

  /**
   * Index of interior nodes to their successors.
   */
  private final Collapser collapser;

  public CollapseTreeModel(Collapser collapser) {
    this.collapser = collapser;
  }


  public Collection<CollapseData> computeRoots() {
    Set<GraphNode> masterNodes = collapser.getMasterNodeSet();
    Collection<CollapseData> result =
        Lists.newArrayListWithExpectedSize(masterNodes.size());

    for (GraphNode node : masterNodes) {
      result.add(collapser.getCollapseData(node));
    }
    return result ;
  }

  /////////////////////////////////////
  // Factory methods

  public static Collection<CollapseData> getChildrenData(CollapseData data) {

    if (null == data) {
      return CollapseData.EMPTY_LIST;
    }

    Collection<GraphNode> childrenNodes = data.getChildrenNodes();
    int size = childrenNodes.size();
    Collection<CollapseData> result =
        Lists.newArrayListWithExpectedSize(size);
    for (GraphNode node : childrenNodes) {
      result.add(loadCollapseData(data, node));
    }

    return result;
  }

  private static CollapseData loadCollapseData(
      CollapseData data, GraphNode node) {
    CollapseData collapseNode = data.getCollapseData(node);
    if (null != collapseNode) {
      return collapseNode;
    }
    if (data.getChildrenNodes().contains(node)) {
      return new CollapseData(
          node, GraphNode.EMPTY_NODE_LIST, CollapseData.EMPTY_LIST);
    }
    return null;
  }
}
