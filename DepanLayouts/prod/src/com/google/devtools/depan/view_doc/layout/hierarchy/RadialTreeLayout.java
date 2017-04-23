/*
 * Copyright 2008 The Depan Project Authors
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

import com.google.devtools.depan.graph.api.EdgeMatcher;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Set;

/**
 * Assign locations to the graph nodes so they are rendered in a 
 * radial hierarchy.  The root(s) of the tree are in the center of a
 * circle, and their children nodes are arrayed around them in concentric
 * circles.
 * <p>
 * This layout occurs in two steps:
 * 1) dry-run step to determine the number of leaf positions in the graph
 * 2) layout step where the number of leaf positions defines the conceptual
 *    circumference of the presentation.
 * 
 * @author <a href='mailto:leeca@google.com'>Lee Carver </a>
 */
public class RadialTreeLayout extends HierarchicalTreeLayout {

  /**
   * @param viewModel source of nodes (exposed graph) to layout
   * @param edgeMatcher edge matcher that defines the hierarchy
   * @param size available rendering space (ignored)
   */
  protected RadialTreeLayout(
      GraphModel graphModel, EdgeMatcher<String> edgeMatcher,
      Rectangle2D region) {
    super(graphModel, edgeMatcher, region);
  }

  @Override
  protected HierarchicalLayoutTool buildLayoutTool() {
    // Determine number of leafs ("circumference" of the circles).
    DryRunTool dryRun = new DryRunTool(graphModel, edgeMatcher);
    dryRun.layoutTree();

    // Now assign locations
    return new RadialLayoutTool(graphModel, edgeMatcher, dryRun.getLeafCount());
  }

  /**
   * Define how x and y locations are assigned to nodes (but don't
   * actually set those locations).
   */
  private static class DryRunTool extends HierarchicalLayoutTool.Planar {

    protected Set<GraphNode> orphans = Sets.newHashSet();

    /**
     * Create a DryRun tool for radial layouts.
     * 
     * @param layoutGraph source of nodes for layout
     * @param relations relations that define the hierarchy
     */
    public DryRunTool(
        GraphModel layoutGraph, EdgeMatcher<String> edgeMatcher) {
      super(layoutGraph, edgeMatcher);
    }

    @Override
    public int getLeafCount() {
      int layoutLeafs = super.getLeafCount();
      if (orphans.size() > layoutLeafs) {
        return orphans.size();
      }
      return layoutLeafs;
    }

    @Override
    protected Collection<GraphNode> computeLayoutRoots(
        Collection<GraphNode> roots) {
      Collection<GraphNode> result =
          Lists.newArrayListWithExpectedSize(roots.size());
      for (GraphNode root : roots) {
        if (getNodeSuccessors(root).isEmpty()) {
          orphans.add(root);
        } else {
          result.add(root);
        }
      }
      return result;
    }

    /**
     * {@inheritDoc}
     * <p>
     * For radial layouts, the center of the circle should not be occupied
     * unless their is only one root.
     */
    @Override
    protected int getRootLevel(Collection<GraphNode> roots) {

      // Don't occupy the center unless there is only one root
      int rootCount = roots.size();
      if (rootCount <= 1) {
        return 0;
      }
      if (rootCount <= 3) {
        return 1;
      }
      if (rootCount <= 9) {
        return 2;
      }
      return 3;
    }

    /**
     * {@inheritDoc}
     * <p>
     * In this dry-run, don't actually assign locations.
     */
    @Override
    protected void assignNode(GraphNode node, int level, int offset) {
      // logAssignNode(node, level, offset);
      // No node assignments in dry run.
    }

  }

  /**
   * Using exactly the DryRun layout strategy, actually assign locations.
   * <p>
   * The circumference defines the number of positions around the circle
   * that need to exist for nodes.  In each concentric circle, it defines
   * the basic of each node's radial offset.
   */
  private class RadialLayoutTool extends DryRunTool {

    private int maxLevel;

    /** Number of leaf positions required by this layout. */
    private final int circumference;

    /** Pre-computed radians for each unit of offset. */
    private final double radiansPerLeaf;

    /**
     * Create a RadialLayoutTool, capturing the circumference and
     * pre-computing the number radians to use for each unit of offset.
     * 
     * @param layoutGraph source of nodes for layout
     * @param relations relations that define the hierarchy
     * @param circumference number of leaf positions around the circle
     */
    public RadialLayoutTool(
        GraphModel layoutGraph, EdgeMatcher<String> edgeMatcher,
        int circumference) {
      super(layoutGraph, edgeMatcher);
      this.circumference = circumference;
      this.radiansPerLeaf = 2.0 * Math.PI / this.circumference;
    }

    @Override
    public void layoutTree() {
      super.layoutTree();
      assignOrphans(maxLevel);
    }

    private void assignOrphans(int level) {
      int nextLevel = level + 1;
      int orphanSeq = 0;
      for (GraphNode node : orphans) {
        assignNode(node, nextLevel, orphanSeq++);
      }
    }

    @Override
    protected int getCurrOffset(int level) {
      int result = super.getCurrOffset(level);
      if (level > maxLevel) {
        maxLevel = result;
      }
      return result;
    }

    /**
     * {@inheritDoc}
     * <p>
     * In this implementation, each offset is converted into a radial
     * position around the circle.  The corresponding (x,y) location from
     * the unit circle is scaled to the nodes level.
     * <p>
     * With the OpenGL rendering engine, this just scales nicely
     * in the ViewEditor.  When we add better layout options and sub-layouts
     * this naive scaling will need enhancements.
     */
    @Override
    protected void assignNode(GraphNode node, int level, int offset) {
      // TODO(leeca):  use Dimension size to scale layout into the ViewEditor.
      double radians = radiansPerLeaf * offset;
      double xPos = Math.cos(radians) * level;
      double yPos = Math.sin(radians) * level;
      // logAssign(node, level, offset, xPos, yPos);
      Point2D point = new Point2D.Double(xPos, yPos);
      positions.put(node, point);
    }
  }
}
