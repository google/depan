/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.view_doc.model;

import com.google.devtools.depan.collapse.model.CollapseData;
import com.google.devtools.depan.collapse.model.Collapser;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view_doc.model.Point2dUtils.DeltaTranslater;

import java.awt.geom.Point2D;
import java.util.Map;

/**
 * Persistent data for collapsed nodes.
 * Instances of this class are expected to be embedded in larger document
 * types (e.g. {@code ViewDocument}).
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class CollapseInfo {

  /**
   * Map (master => {@link CollapseData}) linking a group head to its collapse
   * informations.
   */
  private Map<GraphNode, Point2D> nodeLocations;

  private Collapser collapseData = new Collapser();

  /**
   * Provide the CollapseData for a master node.
   * 
   * @param master master node for a collapsed group
   * @return the master's CollapseData, or {@code null} if it is not 
   * the master for a collapse group.
   */
  public CollapseData getCollapseData(GraphNode master) {
    return collapseData.getCollapseData(master);
  }

  public Point2D getLocationData(GraphNode master) {
    return nodeLocations.get(master);
  }

  public void putLocationData(GraphNode master, Point2D point) {
    nodeLocations.put(master, point);
  }
  
  public void uncollapse(GraphNode master, Point2D curr) {
    Point2D base = nodeLocations.get(master);
    double offsetX = curr.getX() - base.getX();
    double offsetY = curr.getY() - base.getY();
    DeltaTranslater delta = new Point2dUtils.DeltaTranslater(offsetX, offsetY);

    CollapseData info = collapseData.getCollapseData(master);
    for (CollapseData member : info.getChildrenCollapse()) {
      GraphNode memberMaster = member.getMasterNode();
      Point2D source = nodeLocations.get(memberMaster);
      Point2D update = delta.translate(source);
      nodeLocations.put(memberMaster, update);
    }
  }
}
