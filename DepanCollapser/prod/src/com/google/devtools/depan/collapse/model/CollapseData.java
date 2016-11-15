/*
 * Copyright 2007 The Depan Project Authors
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

import com.google.common.collect.Lists;

import com.google.devtools.depan.model.GraphNode;

import java.util.Collection;
import java.util.Collections;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class CollapseData {

  public static Collection<CollapseData> EMPTY_LIST =
      Collections.<CollapseData>emptyList();

  /**
   * The defined master for this collapsed set of nodes.
   */
  private final GraphNode masterNode;

  /**
   * The defining set of children for this collapsed master node.
   */
  private final Collection<GraphNode> childrenNodes;

  /**
   * A collection of collapsed nodes include within this collapse.
   */
  private final Collection<CollapseData> childrenCollapse;

  /**
   * Construct a {@link CollapseData} with the given children nodes and
   * children collapse data.
   * 
   * @param masterNode master for collapsed nodes
   * @param childrenNodes children nodes to collapse
   * @param childrenCollapse collapse data for any collapsed children
   */
  public CollapseData(
      GraphNode masterNode,
      Collection<GraphNode> childrenNodes,
      Collection<CollapseData> childrenCollapse) {

    this.masterNode = masterNode;
    // Make sure we have our own copy of the lists.
    // Can't have changes in the picked list spontaneously change
    // a collapse group.
    this.childrenNodes = Lists.newArrayList(childrenNodes);
    this.childrenCollapse = Lists.newArrayList(childrenCollapse);
  }

  public GraphNode getMasterNode() {
    return masterNode;
  }

  public Collection<GraphNode> getChildrenNodes() {
    return childrenNodes;
  }

  public Collection<CollapseData> getChildrenCollapse() {
    return childrenCollapse;
  }

  public CollapseData getCollapseData(GraphNode node) {
    // Someday it might be worth using a hash set for the lookup.
    for (CollapseData data : childrenCollapse) {
      if (data.getMasterNode() == node) {
        return data;
      }
    }
    return null;
  }

  /**
   * Add all nodes included in this collapse data, including any
   * nested collapse data.  The master node is included in the member nodes.
   * 
   * @param result destination for collapsed nodes.
   */
  public void addMemberNodes(Collection<GraphNode> result) {
    result.addAll(getChildrenNodes());
    for (CollapseData data : getChildrenCollapse()) {
      data.addMemberNodes(result);
    }
  }

  /////////////////////////////////////
  // Factory methods

  /**
   * Provide a copy of the children nodes with full {@link CollapseData},
   * even for nodes without children.  It is safe to manipulate the result.
   * 
   * @return a new {@link Collection} of {@link CollapseData}.
   */
  public static Collection<CollapseData> buildChildrenData(CollapseData data) {

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
