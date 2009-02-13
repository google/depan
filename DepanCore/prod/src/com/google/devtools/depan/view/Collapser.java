/*
Copyright 2007 Google Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.google.devtools.depan.view;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.devtools.depan.model.GraphNode;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The collection of all collapsed nodes for a ViewModel.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class Collapser {

  /**
   * Map (master => {@link CollapseData}) linking a group head to its collapse
   * informations.
   */
  private Map<GraphNode, CollapseData> collapsedData =
      Maps.newHashMap();

  /**
   * Provide a collapser to manage a set of collapsed nodes.
   */
  public Collapser() {
  }

  /**
   * Provide the CollapseData for a master node.
   * 
   * @param master master node for a collapsed group
   * @return the master's CollapseData, or {@code null} if it is not 
   * the master for a collapse group.
   */
  public CollapseData getCollapseData(GraphNode master) {
    return collapsedData.get(master);
  }

  /**
   * Collapse the selected elements, under a <code>master</code>.
   * <p>
   * This seems to be broken:  If master
   * is <code>null</code>, cluster under a new vertex.
   * 
   * @param master collapse selected nodes under this node.
   * @param picked list of nodes to collapse.
   * @param erase if true, if any collapsed group have already been made under
   *        the given <code>master</code>, it will be uncollapsed and then
   *        erased.
   * @return CollapseData for new collapse group
   */
  public CollapseData collapse(
      GraphNode master,
      Collection<GraphNode> picked,
      boolean erase) {

    // Move any included collapsed nodes into the CollapseData for this master
    List<CollapseData> childrenCollapse = Lists.newArrayList();
    for (GraphNode child : picked) {
      CollapseData collapseInfo = getCollapseData(child);
      if (null != collapseInfo) {
        collapsedData.remove(child);
        childrenCollapse.add(collapseInfo);
      }
    }

    CollapseData data = new CollapseData(master, picked, childrenCollapse);

    collapsedData.put(master, data);
    return data;
    }

  /**
   * Uncollapse the nodes contained under this master.
   * 
   * @param master master node for the group to uncollapse
   * @param deleteGroup if true, the collapsed group will be deleted, otherwise
   *        it will be kept, allowing to call collapse() on the root node only.
   */
  public void uncollapse(GraphNode master, boolean deleteGroup) {
    CollapseData data = getCollapseData(master);
    if (null == data) {
      return;
    }
    // First, remove previous map entry so that we do not remove what we just
    // put in
    collapsedData.remove(master);
    
    // Now add new entries that existed in master's collapse data
    for (CollapseData info : data.getChildrenCollapse()) {
      collapsedData.put(info.getMasterNode(), info);
    }
  }

  /**
   * Add the master nodes to a collections of nodes.
   * This is often one step in building the set of visible nodes
   * for an entire ViewModel.
   * 
   * @param result destination of master nodes.
   */
  public void addMasterNodes(Collection<GraphNode> result) {
    for (GraphNode master : collapsedData.keySet()) {
      result.add(master);
    }
  }

  /**
   * Provide a copy of the current set of master Nodes.
   * @return Set of master Nodes
   */
  public Set<GraphNode> getMasterNodeSet() {
    return Sets.newHashSet(collapsedData.keySet());
  }

  /**
   * Build a map of hidden nodes to their top-level master nodes.
   * This is often used to filter exposed nodes and edges.
   * 
   * @return map of hidden nodes to their top-level master node
   */
  public Map<GraphNode, GraphNode> buildHiddenNodeMap() {

    Map<GraphNode, GraphNode> result = Maps.newHashMap();
    for (CollapseData masterData : collapsedData.values()) {
      Collection<GraphNode> masterNodes = Lists.newArrayList();
      masterData.addMemberNodes(masterNodes);

      for (GraphNode childNode : masterNodes) {
        result.put(childNode, masterData.getMasterNode());
      }
    }
    return result;
  }
}
