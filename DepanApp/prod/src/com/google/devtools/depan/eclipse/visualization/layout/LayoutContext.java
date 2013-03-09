/*
 * Copyright 2013 ServiceNow.
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
import com.google.devtools.depan.graph.api.DirectedRelationFinder;
import com.google.devtools.depan.graph.basic.ForwardIdentityRelationFinder;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;

import com.google.common.collect.Maps;

import java.awt.geom.Point2D;
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
  public final static GLRegion DEFAULT_REGION =
      new GLRegion(0, 0, 1000.0, 1000.0);

  private GraphModel graphModel;

  private DirectedRelationFinder relations= 
      ForwardIdentityRelationFinder.FINDER;

  private GLRegion viewport = DEFAULT_REGION;

  private Collection<GraphNode> movableNodes = GraphNode.EMPTY_NODE_LIST;

  private Collection<GraphNode> fixedNodes = GraphNode.EMPTY_NODE_LIST;

  private Map<GraphNode, Point2D> nodeLocations = Collections.emptyMap();

  public void setGraphModel(GraphModel context) {
    this.graphModel = context;
  }

  public void setMovableNodes(Collection<GraphNode> movableNodes) {
    this.movableNodes = movableNodes;
  }

  public void setFixedNodes(Collection<GraphNode> fixedNodes) {
    this.fixedNodes = fixedNodes;
  }

  public void setRelations(DirectedRelationFinder relations) {
    this.relations = relations;
  }

  public void setViewport(GLRegion viewport) {
    this.viewport = viewport;
  }

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

  public GraphModel getGraphModel() {
    return graphModel;
  }

  public Collection<GraphNode> getMovableNodes() {
    return movableNodes;
  }

  public Collection<GraphNode> getFixedNodes() {
    return fixedNodes;
  }

  public DirectedRelationFinder getRelations() {
    return relations;
  }

  public GLRegion getViewport() {
    return viewport;
  }

  public Map<GraphNode, Point2D> getNodeLocations() {
    return nodeLocations;
  }
}
