/*
 * Copyright 2013 The Depan Project Authors
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

import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptors;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;

import com.google.common.collect.Maps;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;


/**
 * Provide a consistent means to access layout parameters without direct
 * dependencies on any UI feature.
 * 
 * @author <a href='mailto:lee.carver@servicenow.com'>Lee Carver</a>
 */
public class LayoutContext {
  public final static Rectangle2D DEFAULT_REGION =
      new Rectangle2D.Double(0, 0, 1000.0, 1000.0);

  private GraphModel graphModel;

  /**
   * Assume forward traversal on all edges present in the {@code #graphModel}.
   */
  private GraphEdgeMatcherDescriptor edgeMatcher =
      GraphEdgeMatcherDescriptors.FORWARD;

  private Rectangle2D viewport = DEFAULT_REGION;

  private Collection<GraphNode> movableNodes = GraphNode.EMPTY_NODE_LIST;

  private Collection<GraphNode> fixedNodes = GraphNode.EMPTY_NODE_LIST;

  private Map<GraphNode, Point2D> nodeLocations = Collections.emptyMap();

  public GraphModel getGraphModel() {
    return graphModel;
  }

  public void setGraphModel(GraphModel graphModel) {
    this.graphModel = graphModel;
  }

  public Collection<GraphNode> getFixedNodes() {
    return fixedNodes;
  }

  public void setFixedNodes(Collection<GraphNode> fixedNodes) {
    this.fixedNodes = fixedNodes;
  }

  public Collection<GraphNode> getMovableNodes() {
    return movableNodes;
  }

  public void setMovableNodes(Collection<GraphNode> movableNodes) {
    this.movableNodes = movableNodes;
  }

  public GraphEdgeMatcherDescriptor getEdgeMatcher() {
    return edgeMatcher;
  }

  public void setEdgeMatcher(GraphEdgeMatcherDescriptor edgeMatcher) {
    this.edgeMatcher = edgeMatcher;
  }

  public Rectangle2D getViewport() {
    return viewport;
  }

  public void setViewport(Rectangle2D viewport) {
    this.viewport = viewport;
  }

  /**
   * Populate internal table of node locations from supplied positions.
   * Only positions for moveable and fixed nodes are used.
   */
  public void setNodeLocations(Map<GraphNode, Point2D> currPositions) {
    nodeLocations = Maps.newHashMapWithExpectedSize(
        movableNodes.size() + fixedNodes.size());

    for (GraphNode node : movableNodes) {
      Point2D point = currPositions.get(node);
      if (null != point) {
        nodeLocations.put(node, point);
      }
    }

    for (GraphNode node : fixedNodes) {
      Point2D point = currPositions.get(node);
      if (null != point) {
        nodeLocations.put(node, point);
      }
    }
  }

  /**
   * Provide a snapshot of the original positions.
   */
  public Map<GraphNode, Point2D> getNodeLocations() {
    return Maps.newHashMap(nodeLocations);
  }
}
