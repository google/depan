package com.google.devtools.depan.view_doc.layout;

import com.google.devtools.depan.model.GraphNode;

import com.google.common.collect.Maps;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Map;

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
