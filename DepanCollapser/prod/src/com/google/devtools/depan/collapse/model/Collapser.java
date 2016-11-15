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

package com.google.devtools.depan.collapse.model;

import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.api.GraphBuilder;
import com.google.devtools.depan.model.builder.api.GraphBuilders;
import com.google.devtools.depan.nodes.trees.TopoSortState;
import com.google.devtools.depan.nodes.trees.TreeModel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.LinkedList;
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
  public void uncollapse(GraphNode master) {
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

  /**
   * Collapse all Nodes in the exposed graph using the hierarchy implied
   * by the given set of relations.
   * <p>
   * The algorithm works by computing a topological sort over the imputed
   * hierarchy, and then collapsing the nodes in order from bottom to top.
   * This allows a user to later uncollapse individual masters,
   * and to incrementally expose their internal details.
   *
   * @param graph source of nodes to collapse
   * @param finder set of relations that define the hierarchy
   * @param author interface component that initiated the action
   */
  public Collection<CollapseData> collapseTree(
      GraphModel graph, TreeModel treeData) {

    TopoSortState sorter = new TopoSortState(treeData);
    List<GraphNode> inOrder = sorter.topoSort(graph.getNodes());
    Collection<CollapseData> collapseChanges = Lists.newArrayList();

    for (GraphNode top : inOrder) {
      addCollapseData(graph, collapseChanges, treeData, top);
    }

    return collapseChanges;
  }

  /**
   * Collapse a single node based on its ancestors in the tree model.
   * The given node becomes the master for the collapse group.
   * All exposed children (and their exposed ancestors) become members
   * of the collapse group, and the collapse group stops for (but includes)
   * top-level master nodes in the ancestor set.
   *
   * @param graph source of nodes to collapse
   * @param collapseChange destination of any added collapseData
   * @param parent master node for collapse group
   * @param treeModel source of successor/ancestor relations
   */
  private void addCollapseData(
      GraphModel graph,
      Collection<CollapseData> collapseChanges,
      TreeModel treeModel,
      GraphNode parent) {

    // Nothing to do if the node has no successors
    if (!treeModel.hasSuccessorNodes(parent)) {
      return;
    }

    // Only include successor nodes that are exposed
    Map<GraphNode, GraphNode> hiddenNodeMap = buildHiddenNodeMap();
    HiddenNodesGizmo gizmo = new HiddenNodesGizmo(hiddenNodeMap);
    Set<GraphNode> exposedNodes = getExposedNodeSet(graph, gizmo);

    Collection<GraphNode> result = Lists.newArrayList();
    result.add(parent);
    addExposedAncestors(result, treeModel, exposedNodes, parent);

    CollapseData collapseData = collapse(parent, result, false);
    collapseChanges.add(collapseData);
  }

  private void addExposedAncestors(
      Collection<GraphNode> result,
      TreeModel treeModel,
      Set<GraphNode> exposedNodes,
      GraphNode parent) {

    for (GraphNode child : treeModel.getSuccessorNodes(parent)) {
      // Only include exposed children
      if (exposedNodes.contains(child)) {
        result.add(child);

        // Recursively add any exposed ancestors
        addExposedAncestors(
            result, treeModel, exposedNodes, child);
      }
    }
  }

  /**
   * Provide (a snapshot) of the {@link CollapseData} for the root
   * nodes.
   */
  public Collection<CollapseData> computeRoots() {
    return Sets.newHashSet(collapsedData.values());
  }

  /**
   * Provide a complete set of nodes in this model.
   */
  public Collection<GraphNode> computeNodes() {
    Collection<GraphNode> result = Sets.newHashSet();
    Collection<GraphNode> seen = Sets.newHashSet();

    LinkedList<CollapseData> queue = Lists.newLinkedList();
    for (CollapseData master : collapsedData.values()) {
      queue.add(master);
    }

    while (!queue.isEmpty()) {
      CollapseData data = queue.removeFirst();
      GraphNode node = data.getMasterNode();
      seen.add(node);

      result.add(node);
      result.addAll(data.getChildrenNodes());

      // Enqueue the masters for all children collapse groups.
      for (CollapseData nest : data.getChildrenCollapse()) {
        GraphNode child = nest.getMasterNode();
        if (!seen.contains(child)) {
          queue.add(nest);
        }
      }
    }
    return result;
  }

  /**
   * Based on the collapsing preferences, compute the set of nodes and
   * edges that are exposed in a graph.
   * 
   * @param graph source of nodes to collapse
   * @return graph containing only uncollapsed nodes
   */
  public GraphModel buildExposedGraph(GraphModel graph) {
    Map<GraphNode, GraphNode> hiddenNodeMap = buildHiddenNodeMap();

    // quick exit if nothing is collapsed
    if (hiddenNodeMap.isEmpty()) {
      return graph;
    }

    HiddenNodesGizmo gizmo = new HiddenNodesGizmo(hiddenNodeMap);

    // Determine the exposed nodes and edges
    Collection<GraphNode> nodes = getExposedNodeSet(graph, gizmo);

    Collection<GraphEdge> edges = Lists.newArrayList();
    gizmo.addExposedEdges(edges, graph.getEdges());

    // Add the exposed components to the generated result
    GraphBuilder builder = GraphBuilders.createGraphModelBuilder();
    for (GraphNode node : nodes) {
      builder.newNode(node);
    }
    for (GraphEdge edge : edges) {
      builder.addEdge(edge);
    }
    return builder.createGraphModel();
  }

  /**
   * Provide the current Set of exposed Nodes.
   *
   * @param graph source of nodes to collapse
   * @param gizmo source of exposed Node information
   * @return Set of exposed Nodes, including exposed master Nodes.
   */
  private Set<GraphNode> getExposedNodeSet(
      GraphModel graph, HiddenNodesGizmo gizmo) {
    Set<GraphNode> nodeSet = Sets.newHashSet();
    addMasterNodes(nodeSet);
    gizmo.addExposedNodes(nodeSet, graph.getNodes());
    return nodeSet;
  }
}
