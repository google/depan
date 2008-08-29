/*
 * Copyright 2008 Google Inc.
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

package com.google.devtools.depan.eclipse.visualization.layout;

import com.google.devtools.depan.graph.api.DirectedRelationFinder;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view.ViewModel;

import edu.uci.ics.jung.graph.Graph;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Collection;


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
public class NewRadialLayout extends NewTreeLayout {

  /**
   * Create a JUNG Layout object from the available data.
   * 
   * @param graph JUNG graph for layout (ignored)
   * @param viewModel source of nodes (exposed graph) to layout
   * @param relations set of relations that define the hierarchy
   * @param size available rendering space (ignored)
   */
  protected NewRadialLayout(Graph<GraphNode, GraphEdge> graph,
      ViewModel viewModel, DirectedRelationFinder relations, Dimension size) {
    super(graph, viewModel, relations, size);
  }

  /**
   * Does the complete radial layout.
   */
  @Override
  public void initialize() {
    GraphModel layoutGraph = viewModel.getExposedGraph();

    // Determine number of leafs ("circumference" of the circles).
    DryRunTool dryRun = new DryRunTool(layoutGraph, relations);
    dryRun.layoutTree();

    // Now assign locations
    HierarchicalLayoutTool layoutTool =
        new RadialLayoutTool(layoutGraph, relations, dryRun.getLeafCount());
    layoutTool.layoutTree();
  }

  /**
   * Define how x and y locations are assigned to nodes (but don't
   * actually set those locations).
   */
  private class DryRunTool extends HierarchicalLayoutTool.Planar {

    /**
     * Create a DryRun tool for radial layouts.
     * 
     * @param layoutGraph source of nodes for layout
     * @param relations relations that define the hierarchy
     */
    public DryRunTool(
        GraphModel layoutGraph, DirectedRelationFinder relations) {
      super(layoutGraph, relations);
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
      return (roots.size() > 1) ? 1 : 0;
    }

    /**
     * {@inheritDoc}
     * <p>
     * In this dry-run, don't actually assign locations.
     */
    @Override
    protected void assignNode(GraphNode node, int level, int offset) {
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
        GraphModel layoutGraph, DirectedRelationFinder relations,
        int circumference) {
      super(layoutGraph, relations);
      this.circumference = circumference;
      this.radiansPerLeaf = 2.0 * Math.PI / this.circumference;
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
      locations.get(node).setLocation(point);
    }

    /**
     * Debugging support to display assignments for nodes.
     */
    @SuppressWarnings("unused")
    private void logAssign(
        GraphNode node, int level, int offset, double xPos, double yPos) {
      System.out.println(node
          + ": [" + level + ", " + offset + "]"
          + " @(" + xPos + ", " + yPos + ")");
    }
  }
}
