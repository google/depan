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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.devtools.depan.model.GraphNode;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
public class TreeModel {
  
  private final Map<GraphNode, ? extends SuccessorEdges> index;

  /**
   * Create a TreeModel from a Map of SuccessorEdges.
   * 
   * @param index data to answer hierarchical queries
   */
  public TreeModel(Map<GraphNode, ? extends SuccessorEdges> index) {
    this.index = index;
  }

  /**
   * Provide the Collection of successors for the given node.
   * 
   * @param node node
   * @return Collection of successor nodes
   */
  public Collection<GraphNode> getSuccessors(GraphNode node) {
    if (false == index.containsKey(node)) {
      return Collections.emptyList();
    }
    return index.get(node).computeSuccessorNodes();
  }

  /**
   * Provide the roots of the graph base on the TreeModel's successor map.
   * Only key nodes from the successor map are included.
   * 
   * @return Dependency ordered List of Nodes
   */
  public Collection<GraphNode> computeRoots() {
    return computeRoots(index.keySet());
  }

  /**
   * Return a permutation of the Map's key Nodes in dependency order.
   * The dependency network is defined by the successor map provided
   * during TreeModel construction.
   * Only key nodes from the successor map are included.
   * In effect, this generates a dependency-sorted List
   * of parent (i.e. interior) nodes.
   * 
   * @return Dependency ordered List of Nodes
   */
  public List<GraphNode> topoSort() {
    return topoSort(index.keySet());
  }

  private static class NodeState {
    public int discovered = 0;
    public int explored = 0;
    public GraphNode parent = null;
  }

  /**
   * Based on the depth-first-search discussion in "Introduction to Algorithms"
   * by Corman, Leiserson, and Rivest (CLR) [1990, 17th printing 1996],
   * this manages the tree-walk state as a depth-first-search is conducted
   * over a set of nodes.
   * <p>
   * I assume this will generalize or expand a bit going forward.
   * Perhaps it is just a small tweek to do strongly-connected graphs.
   */
  private class DfsState {
    Map<GraphNode, NodeState> infoMap = Maps.newHashMap();
    private int ticks = 0;

    protected int nextTick() {
      return ++ticks;
    }

    protected NodeState findNodeState(GraphNode node) {
      return infoMap.get(node);
    }

    /**
     * @return the infoMap
     */
    protected NodeState getNodeState(GraphNode node) {
      NodeState result = findNodeState(node);
      if (null == result) {
        result = new NodeState();
        infoMap.put(node, result);
      }
      return result;
    }

    public void setPrecessor(GraphNode child, GraphNode parent) {
      getNodeState(child).parent = parent;
    }

    public void setDiscovered(GraphNode node) {
      getNodeState(node).discovered = nextTick();
    }

    public void setExplored(GraphNode node) {
      getNodeState(node).explored = nextTick();
    }

    /**
     * @param child
     * @return
     */
    public GraphNode getPrecessor(GraphNode node) {
      NodeState result = infoMap.get(node);
      if (null == result) {
        return null;
      }
      return result.parent;
    }

    /**
     * Node is WHITE in CLR terminology.
     * @param node Node to check
     * @return true iff the Node has never been visited
     */
    public boolean isUnvisited(GraphNode node) {
      NodeState result = infoMap.get(node);
      if (null == result) {
        return true;
      }
      return (0 == result.discovered);
    }

    /**
     * Node is GREY in CLR terminology.
     * @param node Node to check
     * @return true iff the Node has been visited but not fully explored
     */
    public boolean isActive(GraphNode node) {
      NodeState result = findNodeState(node);
      if (null == result) {
        return false;
      }
      return ((result.discovered > 0) && (0 == result.explored));
    }

    public boolean isDiscovered(GraphNode node) {
      NodeState result = findNodeState(node);
      if (null == result) {
        return false;
      }
      return (result.discovered > 0);
    }

    /**
     * Node is BLACK in CLR terminology.
     * @param node Node to check
     * @return true iff the Node has been fully explored
     */
    public boolean isExplored(GraphNode node) {
      NodeState result = findNodeState(node);
      if (null == result) {
        return false;
      }
      return (result.explored > 0);
    }

    public void visitNode(GraphNode parent) {
      setDiscovered(parent);
      for(GraphNode child : getSuccessors(parent)) {
        // Even if we have previously visited this node,
        // set it's predecessor if it doesn't have one.
        // This appears to be a bug in the CLR version of DFS-visit(u)
        // from section 23.3.
        if (null == getPrecessor(child)) {
          setPrecessor(child, parent);
        }
        if (isUnvisited(child)) {
          visitNode(child);
        }
      }
      setExplored(parent);
    }

    public Collection<GraphNode> extractRoots() {
      Collection<GraphNode> result =
          Lists.newArrayList();
      for (Entry<GraphNode, NodeState> entry
          : infoMap.entrySet()) {
        if (null == entry.getValue().parent) {
          result.add(entry.getKey());
        }
      }
      return result;
    }
  }

  /**
   * Extend the basic DfsState to also construct a topological ordering for
   * the set of relations.
   */
  private class TopoSortState extends DfsState {
    private List<GraphNode> topoOrder= Lists.newArrayList();

    /**
     * {@inheritDoc}
     * <p>
     * This implementation extends the finish-visit step to add the
     * fully explored node to the topological sequence of nodes.
     */
    @Override
    public void setExplored(GraphNode node) {
      super.setExplored(node);

      // Only keep the interior nodes of the tree.
      // Omit all the leaves.
      if (index.containsKey(node)) {
        topoOrder.add(node);
      }
    }

    /**
     * Compute a topological ordering for the Nodes in the universe.
     * 
     * @param universe Collection of Nodes to order
     * @return a topological permutation of the input Nodes
     */
    public List<GraphNode> topoSort(
        Collection<GraphNode> universe) {
      for (GraphNode node : universe) {
        if (isUnvisited(node)) {
          visitNode(node);
        }
      }
      return topoOrder;
    }
  }

  /**
   * Provide a collection of nodes to serve as roots of a tree.
   * <p>
   * The returned set of nodes guarantees a successor path to
   * every node in the universe.
   * 
   * @param universe set of node for root discovery
   * @return collection of root nodes
   */
  public Collection<GraphNode> computeRoots(
      Collection<GraphNode> universe) {

    DfsState dfsState = new DfsState();

    for (GraphNode node : universe) {
      if (dfsState.isUnvisited(node)) {
        dfsState.visitNode(node);
      }
    }

    return dfsState.extractRoots();
  }

  /**
   * Provide a list of nodes in topological sort order.
   * <p>
   * The output is a permutation of the input universe,
   * such that the least dependent nodes are listed first.
   * In general, the leafs occur first and the roots occur last.
   * The topological sort order is defined by the successor map
   * provided during the TreeModel's construction.
   * 
   * @param universe Collection of Nodes to order
   * @return List of Nodes in dependency order
   */
  public List<GraphNode> topoSort(
      Collection<GraphNode> universe) {

    TopoSortState topoState = new TopoSortState();
    return topoState.topoSort(universe);
  }

  /**
   * Provide the number of interior nodes in this tree.
   *
   * @return number of nodes in this tree
   */
  public int countInteriorNodes() {
    int result = 0;

    for (SuccessorEdges fanout: index.values()) {
      if (fanout.hasSuccessors()) {
        ++result;
      }
    }

    return result;
  }

  /**
   * Provide the set of nodes in this tree.
   *
   * @return nodes in this tree
   */
  public Set<GraphNode> computeTreeNodes() {
    Set<GraphNode> treeNodes = Sets.newHashSet();

    for (Map.Entry<GraphNode, ? extends SuccessorEdges> entry
        : index.entrySet()) {
      treeNodes.add(entry.getKey());
      treeNodes.addAll(entry.getValue().computeSuccessorNodes());
    }

    return treeNodes;
  }

  /**
   * Provide the number of nodes in this tree.
   *
   * @return number of nodes in this tree
   */
  public int countTreeNodes() {
    return computeTreeNodes().size();
  }
}
