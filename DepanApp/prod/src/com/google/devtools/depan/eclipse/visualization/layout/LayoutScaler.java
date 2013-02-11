/*
 * Copyright 2010 Google Inc.
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

package com.google.devtools.depan.eclipse.visualization.layout;

import com.google.devtools.depan.eclipse.visualization.ogl.GLRegion;
import com.google.devtools.depan.model.GraphNode;

import java.awt.geom.Point2D;
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
  public static final Point2D ZERO_POINT = new Point2D.Double(0.0, 0.0);

  public final int nodeCnt;
  public final int emptyCnt;
  public final GLRegion graphArea;

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

    this.graphArea = new GLRegion(minX, maxY, maxX, minY);
  }

  private static double scaleRange(double range, double view) {
    if (view == 0) {
      return 1.0;
    }
    return view / range;
  }

  public double getCenterX() {
    return graphArea.getCenterX();
  }

  public double getCenterY() {
    return graphArea.getCenterY();
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
  public double getFullViewScale(GLRegion viewport, double zeroThreshold) {
    // Don't move the origin, 
    double rangeX = graphArea.getRangeX();
    double rangeY = graphArea.getRangeY();

    if ((rangeX < zeroThreshold) && (rangeY < zeroThreshold)) {
      return 1.0;
    }
    if (rangeX < zeroThreshold) {
      return scaleRange(rangeY, viewport.getRangeY());
    }
    if (rangeY < zeroThreshold) {
      return scaleRange(rangeX, viewport.getRangeX());
    }

    double scaleX = scaleRange(rangeX, viewport.getRangeX());
    double scaleY = scaleRange(rangeY, viewport.getRangeY());

    return Math.min(scaleX, scaleY);
  }
}
