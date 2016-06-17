/*
 * Copyright 2010 The Depan Project Authors
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

package com.google.devtools.depan.view_doc.layout;

import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view_doc.model.Point2dUtils;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Map;

/**
 * Provides machinery for computing the size of a graph, and for deriving
 * scaling factors for the graph.  This objects serves mostly as a data
 * records, so it's fields are public final.
 * 
 * <p>Note that the constructor computes the graph area from the set of layout
 * nodes and positions that it receives.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class LayoutScaler {
  @SuppressWarnings("unused") // for now
  private final int nodeCnt;

  @SuppressWarnings("unused") // for now
  private final int emptyCnt;

  /** Destination region for diagram, in OGL coordinates. */
  private final Rectangle2D graphArea;

  /**
   * Initialize the graph area from the set of layout nodes and positions
   * that are provided.  Only nodes listed in the layout nodes collection
   * contribute to the final graph area.
   * 
   * If a layoutNode is not listed in the node locations map, it is ignored for
   * the purposes of computing the layout area.  It is not assumed to lie at
   * the origin (0.0, 0.0).  A count of these empty nodes is available after the
   * constructor completes.
   * 
   * @param layoutNodes set of nodes that define the graph area
   * @param nodeLocations (x, y) locations for nodes
   */
  public LayoutScaler(
      Collection<GraphNode> layoutNodes,
      Map<GraphNode, Point2D> nodeLocations) {

    int emptyCnt = 0;
    int nodeCnt = 0;

    // This both initializes the minimum and maximum values to values that
    // are appropriate for a collection where no node has a location and
    // it avoids Java thinking these variables are not initialized.
    double minX = 0.0;
    double maxX = 0.0;
    double minY = 0.0;
    double maxY = 0.0;

    for (GraphNode eachNode : layoutNodes) {
      Point2D eachPos = nodeLocations.get(eachNode);
      if (null == eachPos) {
        emptyCnt++;
      }
      else if (0 == nodeCnt) {
        nodeCnt = 1;
        minX = eachPos.getX();
        maxX = eachPos.getX();
        minY = eachPos.getY();
        maxY = eachPos.getY();
      }
      else {
        nodeCnt++;
        minX = Math.min(minX, eachPos.getX());
        maxX = Math.max(maxX, eachPos.getX());
        minY = Math.min(minY, eachPos.getY());
        maxY = Math.max(maxY, eachPos.getY());
      }
    }

    // Save the construction counts
    this.nodeCnt = nodeCnt;
    this.emptyCnt = emptyCnt;

    this.graphArea = new Rectangle2D.Double(
        minX, minY, maxX - minX, maxY - minY);
  }

  /**
   * Provide a translator that maps the graph area to the region.  Nodes in the
   * center (and corners) go to the same location in the into regions.  Other
   *  nodes are shifted proportionately.
   */
  public Point2dUtils.Translater intoRegion(Rectangle2D region) {
    Rectangle2D graphOrtho = Point2dUtils.newOrthoRegion(graphArea);
    return Point2dUtils.newIntoRegion(region, graphOrtho);
  }

  /**
   * Determine the maximum scaling factor that will allow this graph area to
   * be contained within an area the size of the provided viewport.  Note that
   * performing this scaling will not guarantee that all nodes are visible in
   * the viewport.  A separate step to position the camera over the center of
   * the resulting graph area may be required.
   * 
   * @param viewport maximum dimensions for the graph area
   * @param zeroThreshold minimum value that can be distinguished from zero.
   * @return
   */
  public double getFullViewScale(Rectangle2D viewport, double zeroThreshold) {
    // Don't move the origin, 
    double rangeY = graphArea.getHeight();
    double rangeX = graphArea.getWidth();

    if ((rangeX < zeroThreshold) && (rangeY < zeroThreshold)) {
      return 1.0;
    }

    if (rangeY < zeroThreshold) {
      return scaleRange(rangeX, viewport.getWidth());
    }
    if (rangeX < zeroThreshold) {
      return scaleRange(rangeY, viewport.getHeight());
    }

    double scaleY = scaleRange(rangeY, viewport.getHeight());
    double scaleX = scaleRange(rangeX, viewport.getWidth());

    return Math.min(scaleX, scaleY);
  }

  private static double scaleRange(double range, double view) {
    if (view == 0) {
      return 1.0;
    }
    return view / range;
  }
}
