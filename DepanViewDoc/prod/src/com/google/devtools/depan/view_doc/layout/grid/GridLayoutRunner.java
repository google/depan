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

  public static final double UNIT = 30.0;

  private Map<GraphNode, Point2D> positions;

  private final LayoutContext context;

  private double columns;

  private double rows;

  private double horizontalSpace = UNIT;

  private double verticalSpace = UNIT;

  public GridLayoutRunner(LayoutContext context) {
    this.context = context;
  }

  public void setColumnRows(int columns, int rows) {
    this.columns = columns;
    this.rows = rows;
  }

  public void setHorizontalSpace(double horizontalSpace) {
    this.horizontalSpace = horizontalSpace;
  }

  public void setVerticalSpace(double verticalSpace) {
    this.verticalSpace = verticalSpace;
  }

  @Override
  public int layoutCost() {
    return 1;
  }

  @Override
  public void layoutStep() {
    Collection<GraphNode> layoutNodes = context.getMovableNodes();
    positions = Maps.newHashMapWithExpectedSize(layoutNodes.size());
    double leftPos = - horizontalSpace * ((columns / 2.0) - 0.5);
    double topPos = verticalSpace * ((rows / 2.0) - 0.5);

    double xCurr = leftPos;
    double yCurr = topPos;
    int item = (int) columns;
    for (GraphNode node : layoutNodes) {
      positions.put(node, new Point2D.Double(xCurr, yCurr));
      item--;
      if (item > 0) {
        xCurr += horizontalSpace;
      } else {
        item = (int) columns;
        xCurr = leftPos;
        yCurr -= verticalSpace;
      }
    }
  }

  @Override
  public boolean layoutDone() {
    return (positions != null);
  }

  @Override
  public Map<GraphNode, Point2D> getPositions(Collection<GraphNode> nodes) {
    return positions;
  }

  public static GridLayoutRunner buildByRows(
      LayoutContext context, int buildRows) {
    Collection<GraphNode> layoutNodes = context.getMovableNodes();
    int buildColumns = layoutNodes.size() / buildRows;
    GridLayoutRunner result = new GridLayoutRunner(context);
    result.setColumnRows(buildColumns, buildRows);
    return result;
  }

  public static GridLayoutRunner buildByColumns(
      LayoutContext context, int buildColumns) {
    Collection<GraphNode> layoutNodes = context.getMovableNodes();
    int buildRows = layoutNodes.size() / buildColumns;
    GridLayoutRunner result = new GridLayoutRunner(context);
    result.setColumnRows(buildColumns, buildRows);
    return result;
  }

  public static GridLayoutRunner buildPortrait(LayoutContext context) {
    Collection<GraphNode> layoutNodes = context.getMovableNodes();
    double split = Math.sqrt(layoutNodes.size());
    return buildByRows(context, (int) Math.ceil(split));
  }

  public static GridLayoutRunner buildLandscape(LayoutContext context) {
    Collection<GraphNode> layoutNodes = context.getMovableNodes();
    double split = Math.sqrt(layoutNodes.size());
    return buildByRows(context, (int) Math.floor(split));
  }
}
