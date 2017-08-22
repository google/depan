/*
 * Copyright 2007 The Depan Project Authors
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

package com.google.devtools.depan.view_doc.layout.hierarchy;

import static com.google.devtools.depan.view_doc.layout.LayoutLogger.LOG;

import com.google.devtools.depan.graph.api.EdgeMatcher;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.nodes.trees.HierarchicalTreeModel;
import com.google.devtools.depan.nodes.trees.TreeModel;
import com.google.devtools.depan.nodes.trees.Trees;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author <a href='mailto:leeca@google.com'>Lee Carver </a>
 */
public abstract class HierarchicalLayoutTool {

  /** Protection from loops */
  protected transient Set<GraphNode> allreadyDone = Sets.newHashSet();

  /** Computer hierarchical data for the nodes. */
  private TreeModel treeData;

  /**
   * Build the layout tool from a TreeModel.
   * 
   * @param treeData source of hierarchy data.
   */
  public HierarchicalLayoutTool(TreeModel treeData) {
    this.treeData = treeData;
  }

  /**
   * Build the layout tool from GraphModel and a set of relations that
   * define the hierarchy.
   * 
   * @param layouGraph set of nodes to layout
   * @param relations set of relations that define the hierarchy
   */
  public HierarchicalLayoutTool(
      GraphModel layoutGraph, EdgeMatcher<String> edgeMatcher) {
    this(createTreeModel(layoutGraph, edgeMatcher));
  }

  public static TreeModel createTreeModel(
      GraphModel layoutGraph, EdgeMatcher<String> edgeMatcher) {
    return new HierarchicalTreeModel(
        Trees.computeSuccessorHierarchy(layoutGraph, edgeMatcher));
  }

  /**
   * Assign positions to all nodes in the Tool's graph.
   * This method determines the roots of the current tree data,
   * and then assigns positions in a depth first traversal of the tree data. 
   */
  public void layoutTree() {
    Collection<GraphNode> treeRoots = treeData.computeRoots();
    Collection<GraphNode> layoutRoots = computeLayoutRoots(treeRoots);

    // If there are multiple roots, make room for an implicit root above them
    int level = getRootLevel(layoutRoots);
    for (GraphNode node : layoutRoots) {
      assignChildren(node, level);
    }
  }

  protected Collection<GraphNode> computeLayoutRoots(
      Collection<GraphNode> roots) {
    return roots;
  }

  /**
   * Provide the starting level to use for the given set of roots.
   * In radial displays, it is best to start at level 1 if there are more
   * then one root.
   * 
   * @param roots collection of nodes that define the roots of the
   *     current hierarchy.
   * @return starting level to use for these roots.
   */
  protected abstract int getRootLevel(Collection<GraphNode> roots);

  /**
   * Recursively assign the position for the given node and all of
   * it's descendants.  Through the use of an alreadyDone lookup set,
   * loops and joins in the tree data are prevented.
   * 
   * @param node GraphNode to position, along with its descendents.
   * @param level hierarchical level ("depth") to place node.
   */
  private void assignChildren(GraphNode root, int level) {
    // Don't try to place an already located node.
    if (allreadyDone.contains(root)) {
      return;
    }
    allreadyDone.add(root);

    int nextLevel = level + 1;
    int childLeft = getCurrOffset(nextLevel);
    for (GraphNode node : orderChildren(root)) {
      assignChildren(node, nextLevel);
    }

    // If there were any children, try to center this node above them
    int childRight = getCurrOffset(nextLevel);
    if (childLeft != childRight) {
      assignNode(root, level, (childLeft + childRight) / 2);
    }

    // With no children, assign to next leaf location, and bump it.
    else {
      assignNode(root, level, getCurrOffset(level));
      incrCurrOffset(level);
    }
  }

  /**
   * Provide the current offset to use for a node at the indicated level.
   * 
   * @param level hierarchical level (depth) for the current offset
   * @return offset for placement of node.
   */
  protected abstract int getCurrOffset(int level);

  /**
   * Increment the current offset at the provide level, indicating that
   * the current offset has been consumed by some node placement.
   * 
   * @param level hierarchical level (depth) for the current offset
   */
  protected abstract void incrCurrOffset(int level);

  /**
   * Assign the node to the provided level and offset
   * @param node GraphNode to place
   * @param level hierarchical level (depth) for the current offset
   * @param offset "horizontal" position of node in graph
   */
  protected abstract void assignNode(GraphNode node, int level, int offset);

  /**
   * Debugging support to display assignments for nodes.
   */
  protected void logAssignNode(GraphNode node, int level, int offset) {
    LOG.info("[{}, {}]: {}", level, offset, node);
  }

  protected Collection<GraphNode> getNodeSuccessors(GraphNode node) {
    return treeData.getSuccessorNodes(node);
  }

  /**
   * Provide the current nodes set of children in a principled order.
   * <p>
   * The organic ordering of TreeModel.getSuccessors() has little meaning,
   * and can appear to be pretty random.  Various sorting strategies are
   * feasible: alphabetic, length of label, etc.
   * <p>
   * In this implementation, all leaf children are grouped at the front of
   * the list, with the remaining nodes at the end.  This roughly approximates
   * the view that many Filers (e.g. Package Explorer) use, with leaf files
   * and directories partitioned from each other.
   * 
   * @param root node with children
   * @return Collection of children in desired processing order
   */
  private Collection<GraphNode> orderChildren(GraphNode root) {
    List<GraphNode> leafs = Lists.newArrayList();
    List<GraphNode> inners = Lists.newArrayList();
    for (GraphNode node : treeData.getSuccessorNodes(root)) {

      // Don't include nodes that are already placed.
      if (allreadyDone.contains(node)) {
        continue;
      }

      if (treeData.hasSuccessorNodes(node)) {
        inners.add(node);
      }
      else {
        leafs.add(node);
      }
    }

    leafs.addAll(inners);
    return leafs;
  }

  /**
   * Utility function to create a valid Point2D for Jung placement from
   * integer level and offset values.
   * 
   * @param level level for node
   * @param offset offset for node
   * @return Point2D taht corresponds to level and offset
   */
  protected static Point2D makePoint2D(int level, int offset) {
    Point2D result = new Point2D.Float(level, offset);
    return result;
  }

  /**
   * Provide a planar implementation of the basic hierarchical layout tool.
   * In an early version, that was an alternative that assigned offsets for
   * each level separately.  While that was abandoned (radial did not need it),
   * other strategies for offset generation are feasible.
   */
  public static class Planar extends HierarchicalLayoutTool {

    /** Each leaf node is assigned a new position. */
    private int leafOffset = 0;

    /**
     * Create a planar layout tool for the given graph and hierarchy.
     * 
     * @param layouGraph set of nodes to layout
     * @param edgeMatcher edge matcher that defines the hierarchy
     */
    public Planar(GraphModel layoutGraph, EdgeMatcher<String> edgeMatcher) {
      super(layoutGraph, edgeMatcher);
    }

    /**
     * Expose the total leaf count.
     * 
     * @return the leafOffset
     */
    public int getLeafCount() {
      return leafOffset;
    }

    /**
     * {@inheritDoc}
     * <p>
     * For the planar hierarchical layout, all roots are at level zero.
     */
    @Override
    protected int getRootLevel(Collection<GraphNode> roots) {
      // All planar roots are at the top of the region
      return 0;
    }

    @Override
    protected void assignNode(GraphNode node, int level, int offset) {
      // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     * <p>
     * In this implementation, the offset is always the next leaf position,
     * regardless of the level.
     */
    @Override
    protected int getCurrOffset(int level) {
      return getLeafCount();
    }

    /**
     * {@inheritDoc}
     * <p>
     * In this implementation, simply bump the leaf count by one.
     * This indicates that that leaf position is no longer available.
     */
    @Override
    protected void incrCurrOffset(int level) {
      leafOffset++;
    }
  }
}
