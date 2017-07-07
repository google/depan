/*
 * Copyright 2013 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.devtools.depan.nodes.trees;

import com.google.devtools.depan.model.GraphNode;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Based on the depth-first-search discussion in "Introduction to Algorithms"
 * by Corman, Leiserson, and Rivest (CLR) [1990, 17th printing 1996],
 * this manages the tree-walk state as a depth-first-search is conducted
 * over a set of nodes.
 * <p>
 * I assume this will generalize or expand a bit going forward.
 * Perhaps it is just a small tweek to do strongly-connected graphs.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class DfsState {

  private static class NodeState {
    public int discovered = 0;
    public int explored = 0;
    public GraphNode parent = null;
  }

  private final TreeModel treeData;

  private final Map<GraphNode, NodeState> infoMap = Maps.newHashMap();

  private int ticks = 0;

  public DfsState(TreeModel treeData) {
    this.treeData = treeData;
  }

  protected int nextTick() {
    return ++ticks;
  }

  protected boolean hasSuccessorNodes(GraphNode node) {
    return treeData.hasSuccessorNodes(node);
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
    for(GraphNode child : treeData.getSuccessorNodes(parent)) {
      // Ignore self loops, too.
      if (child == parent) {
        continue;
      }
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
    Collection<GraphNode> result = Lists.newArrayList();
    for (Entry<GraphNode, NodeState> entry : infoMap.entrySet()) {
      if (null == entry.getValue().parent) {
        result.add(entry.getKey());
      }
    }
    return result;
  }
}
