/*
 * Copyright 2013 The Depan Project Authors
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

package com.google.devtools.depan.view_doc.layout.keep;

import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view_doc.layout.LayoutContext;
import com.google.devtools.depan.view_doc.layout.LayoutRunner;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlan;

import com.google.common.collect.Maps;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Map;

/**
 * A concrete layout generator that leaves all the known nodes in their
 * original locations.  Nodes missing from the {@link LayoutContext} will
 * get a null location.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class KeepPositionsPlan implements LayoutPlan {

  /** Only need one. */
  public static KeepPositionsPlan INSTANCE = new KeepPositionsPlan();

  @Override
  public String buildSummary() {
    return "Keep positions";
  }

  @Override
  public LayoutRunner buildLayout(LayoutContext context) {
    return new KeepPositionsRunner(context.getNodeLocations());
  }

  /**
   * A concrete layout runner that leaves all the nodes in their original
   * positions.  Nodes missing from the {@link LayoutContext} will
   * get a null location.
   */
  private static class KeepPositionsRunner implements LayoutRunner {

    private final Map<GraphNode, Point2D> nodeLocations;

    public KeepPositionsRunner(Map<GraphNode, Point2D> nodeLocations) {
      this.nodeLocations = nodeLocations;
    }

    @Override
    public int layoutCost() {
      return 0;
    }

    @Override
    public void layoutStep() {
    }

    @Override
    public boolean layoutDone() {
      return true;
    }

    @Override
    public Map<GraphNode, Point2D> getPositions(Collection<GraphNode> nodes) {
      // No point in even a little work if we have no locations.
      if (nodeLocations.isEmpty())
        return nodeLocations;

      int size = Math.min(nodeLocations.size(), nodes.size());
      Map<GraphNode, Point2D> result = Maps.newHashMapWithExpectedSize(size);

      for (GraphNode node : nodes) {
        Point2D point = nodeLocations.get(node);
        if (null != point) {
          result.put(node, point);
        }
      }
      return result;
    }
  }
}
