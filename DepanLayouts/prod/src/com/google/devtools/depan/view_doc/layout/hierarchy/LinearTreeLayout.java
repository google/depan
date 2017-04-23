/*
 * Copyright 2017 The Depan Project Authors
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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class LinearTreeLayout extends HierarchicalTreeLayout {

  private int horizontalSpace;

  private int verticalSpace;

  protected LinearTreeLayout(GraphModel graphModel,
      EdgeMatcher<String> edgeMatcher, Rectangle2D region,
      int horizontalSpace, int verticalSpace) {
    super(graphModel, edgeMatcher, region);
    this.horizontalSpace = horizontalSpace;
    this.verticalSpace = verticalSpace;
  }

  @Override
  protected HierarchicalLayoutTool buildLayoutTool() {
    return new ConfigurableLayoutTool(
        graphModel, edgeMatcher, horizontalSpace, verticalSpace);
  }

  /**
   * Define how x and y locations are assigned to nodes.
   */
  private class ConfigurableLayoutTool extends HierarchicalLayoutTool.Planar {

    private final int horizontalSpace;
    private final int verticalSpace;

    /**
     * Create a LayoutTool for left-to-right planar hierarchies.
     * 
     * @param layoutGraph source of nodes (exposed graph) to layout
     * @param edgeMatcher edge matcher that defines the hierarchy
     */
    public ConfigurableLayoutTool(
        GraphModel layoutGraph,
        EdgeMatcher<String> edgeMatcher,
        int horizontalSpace,
        int verticalSpace) {
      super(layoutGraph, edgeMatcher);
      this.horizontalSpace = horizontalSpace;
      this.verticalSpace = verticalSpace;
    }

    @Override
    protected void assignNode(GraphNode node, int level, int offset) {
      // TODO(): Come up with a better heuristic for non-overlapping placement
      // of the nodes.
      Point2D point =
            makePoint2D(level * horizontalSpace, offset * verticalSpace);
      positions.put(node, point);
    }
  }
}
