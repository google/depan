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
import com.google.devtools.depan.view_doc.model.Point2dUtils;

import com.google.common.collect.Maps;

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
public abstract class HierarchicalTreeLayout {

  /** The source of nodes for layout. */
  protected final GraphModel graphModel;

  /** Edge matcher that defines the tree hierarchy. */
  protected final EdgeMatcher<String> edgeMatcher;

  /** The region to fill with the layout. */
  protected final Rectangle2D region;

  /**
   * The computed positions for the nodes.  Allocated just before the
   * positions are computed.
   */
  protected Map<GraphNode, Point2D> positions;

  /**
   * @param graphModel nodes and edges to layout
   * @param edgeMatcher edge matcher that defines the hierarchy
   * @param size available rendering space (ignored)
   */
  protected HierarchicalTreeLayout(
      GraphModel graphModel,
      EdgeMatcher<String> edgeMatcher,
      Rectangle2D region) {
    this.graphModel = graphModel;
    this.edgeMatcher = edgeMatcher;
    this.region = region;
  }

  protected abstract HierarchicalLayoutTool buildLayoutTool();

  /**
   * Does the complete left-to-right planar layout.
   */
  public void initialize() {
    HierarchicalLayoutTool layoutTool = buildLayoutTool();

    positions = Maps.newHashMapWithExpectedSize(graphModel.getNodes().size());
    layoutTool.layoutTree();
    Point2dUtils.translatePos(region, graphModel.getNodes(), positions);
  }

  public Point2D getPosition(GraphNode node) {
    return positions.get(node);
  }
}
