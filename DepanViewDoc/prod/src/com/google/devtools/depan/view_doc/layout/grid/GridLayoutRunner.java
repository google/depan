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

package com.google.devtools.depan.view_doc.layout.grid;

import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view_doc.layout.LayoutContext;
import com.google.devtools.depan.view_doc.layout.LayoutRunner;

import com.google.common.collect.Maps;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Map;

/**
 * Part of ViewDoc plug to ensure that one layout is always available.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class GridLayoutRunner implements LayoutRunner {

  private static final double UNIT = 30.0;

  private Map<GraphNode, Point2D> result;
  private final LayoutContext context;

  public GridLayoutRunner(LayoutContext context) {
    this.context = context;
  }

  @Override
  public int layoutCost() {
    return 1;
  }

  @Override
  public void layoutStep() {
    Collection<GraphNode> layoutNodes = context.getMovableNodes();
    result = Maps.newHashMapWithExpectedSize(layoutNodes.size());
    double columns = Math.ceil(Math.sqrt(layoutNodes.size()));
    double rows = Math.ceil(layoutNodes.size() / columns);
    double leftPos = - UNIT * ((columns / 2.0) - 0.5);
    double topPos = UNIT * ((rows / 2.0) - 0.5);

    double xCurr = leftPos;
    double yCurr = topPos;
    int item = (int) columns;
    for (GraphNode node : layoutNodes) {
      result.put(node, new Point2D.Double(xCurr, yCurr));
      item--;
      if (item > 0) {
        xCurr += UNIT;
      } else {
        item = (int) columns;
        xCurr = leftPos;
        yCurr -= UNIT;
      }
    }
  }

  @Override
  public boolean layoutDone() {
    return (result != null);
  }

  @Override
  public Map<GraphNode, Point2D> getPositions(Collection<GraphNode> nodes) {
    return result;
  }
}
