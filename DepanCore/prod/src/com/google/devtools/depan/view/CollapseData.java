/*
 * Copyright 2007 Google Inc.
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

import com.google.devtools.depan.collect.Lists;
import com.google.devtools.depan.model.GraphNode;

import java.util.Collection;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class CollapseData {

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
   * It would be better if CollapseData was a generic kind of GroupElement,
   * but that's a bigger change.
   */
  // TODO(leeca): Make CollapseData a form of GroupElement.
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
}
