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

package com.google.devtools.depan.eclipse.visualization.layout;

import com.google.devtools.depan.graph.api.DirectedRelationFinder;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;

import edu.uci.ics.jung.algorithms.layout.PolarPoint;
import edu.uci.ics.jung.graph.Graph;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.map.LazyMap;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;


/**
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class UniversalRadialLayout extends UniversalTreeLayout {

  protected Map<GraphNode, PolarPoint> polarLocations =
      LazyMap.decorate(
          new HashMap<GraphNode, PolarPoint>(),
          new Transformer<GraphNode, PolarPoint>() {
            public PolarPoint transform(GraphNode arg0) {
              return new PolarPoint();
            }
      });

  protected UniversalRadialLayout(
      Graph<GraphNode, GraphEdge> graph,
      GraphModel graphModel, DirectedRelationFinder relations,
      Dimension size) {
    super(graph, graphModel, relations, size);
  }

  @Override
  void buildTree() {
    super.buildTree();
    setRadialLocations();
  }

  private Point2D getMaxXY() {
    double maxx = 0;
    double maxy = 0;
    for (Point2D p : locations.values()) {
      maxx = Math.max(maxx, p.getX());
      maxy = Math.max(maxy, p.getY());
    }
    return new Point2D.Double(maxx, maxy);
  }

  private void setRadialLocations() {
    Point2D max = getMaxXY();
    double maxx = max.getX();
    double maxy = max.getY();
    maxx = Math.max(maxx, getSize().width);
    double theta = 2 * Math.PI / maxx;

    double deltaRadius = getSize().width / 2 / maxy;
    for (Map.Entry<GraphNode, Point2D> entry
        : locations.entrySet()) {
      GraphNode v = entry.getKey();
      Point2D p = entry.getValue();
      PolarPoint polarPoint =
          new PolarPoint(p.getX() * theta, (p.getY() - 50) * deltaRadius);
      polarLocations.put(v, polarPoint);
    }
  }

  public Point2D getCenter() {
    return new Point2D.Double(
        getSize().getWidth() / 2, getSize().getHeight() / 2);
  }

  public Map<GraphNode, PolarPoint> getPolarLocations() {
    return polarLocations;
  }

  @Override
  public void setLocation(GraphNode v, Point2D location) {
    Point2D c = getCenter();
    Point2D pv = new Point2D.Double(location.getX() - c.getX(), location.getY()
        - c.getY());
    PolarPoint newLocation = PolarPoint.cartesianToPolar(pv);
    polarLocations.get(v).setLocation(newLocation);
  }

  @Override
  public void setSize(Dimension size) {
    this.setSize(size);
    buildTree();
  }

  @Override
  public double getX(GraphNode v) {
    return transform(v).getX();
  }

  @Override
  public double getY(GraphNode v) {
    return transform(v).getY();
  }

  @Override
  public Point2D transform(GraphNode v) {
    PolarPoint pp = polarLocations.get(v);
    double centerX = getSize().getWidth() / 2;
    double centerY = getSize().getHeight() / 2;
    Point2D cartesian = PolarPoint.polarToCartesian(pp);
    cartesian.setLocation(cartesian.getX() + centerX, cartesian.getY()
        + centerY);
    return cartesian;
  }
}
