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

import com.google.devtools.depan.eclipse.editors.Point2dUtils;
import com.google.devtools.depan.graph.api.DirectedRelationFinder;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;

import com.google.common.collect.Maps;

import edu.uci.ics.jung.graph.Graph;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

/**
 * Assign locations to the graph nodes so they are rendered in a 
 * left-to-right hierarchy.  With suitable options top-to-bottom and other
 * layout variations are readily accommodated.
 * 
 * @author <a href='mailto:leeca@google.com'>Lee Carver </a>
 */
public class NewTreeLayout {

  /** The source of nodes for layout. */
  protected final GraphModel graphModel;

  /** The set of relations that define the hierarchy. */
  protected final DirectedRelationFinder relations;

  /** The region to fill with the layout. */
  protected final Rectangle2D region;

  /**
   * The computed positions for the nodes.  Allocated just before the
   * positions are computed.
   */
  protected Map<GraphNode, Point2D> positions;

  /**
   * Create a JUNG Layout object from the available data.
   * 
   * @param graph JUNG graph for layout (ignored)
   * @param viewModel source of nodes (exposed graph) to layout
   * @param relations set of relations that define the hierarchy
   * @param size available rendering space (ignored)
   */
  protected NewTreeLayout(Graph<GraphNode, GraphEdge> graph,
      GraphModel graphModel, DirectedRelationFinder relations,
      Rectangle2D region) {
    this.graphModel = graphModel;
    this.relations = relations;
    this.region = region;
  }

  /**
   * Does the complete left-to-right planar layout.
   */
  public void initialize() {
    LayoutTool layoutTool = new LayoutTool(graphModel, relations);

    positions = Maps.newHashMapWithExpectedSize(graphModel.getNodes().size());
    layoutTool.layoutTree();
    Point2dUtils.translatePos(region, graphModel.getNodes(), positions);
  }

  public Point2D getPosition(GraphNode node) {
    return positions.get(node);
  }

  /**
   * Define how x and y locations are assigned to nodes.
   */
  private class LayoutTool extends HierarchicalLayoutTool.Planar {

    /**
     * Scaling factor for X dimension.  Ideally, this should be parameter
     * that is discovered from the geometry of the layout space and the
     * overall properties of the nodes that are being placed.
     * <p>
     * BUT, x12 seems to work well in practice to keep the levels far
     * enough apart that the text label mostly do not overlap.
     */
    static final int EXPAND_X = 12;

    /**
     * Create a LayoutTool for left-to-right planar hierarchies.
     * @param layoutGraph
     * @param relations
     */
    public LayoutTool(
        GraphModel layoutGraph, DirectedRelationFinder relations) {
      super(layoutGraph, relations);
    }

    @Override
    protected void assignNode(GraphNode node, int level, int offset) {
      // TODO(): Come up with a better heuristic for non-overlapping placement
      // of the nodes.
      Point2D point = makePoint2D(level * EXPAND_X, offset);
      positions.put(node, point);
    }
  }
}
