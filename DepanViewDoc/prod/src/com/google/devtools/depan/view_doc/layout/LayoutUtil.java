package com.google.devtools.depan.view_doc.layout;

import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphNode;

import edu.uci.ics.jung.graph.DirectedGraph;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Map;

public class LayoutUtil {

  public static Map<GraphNode, Point2D> calcPositions(LayoutGenerator layout,
      LayoutContext context, Collection<GraphNode> layoutNodes) {
    LayoutRunner runner = layout.buildRunner(context);
    runLayout(runner);
    return runner.getPositions(layoutNodes);
  }

  private static void runLayout(LayoutRunner runner) {
    if (runner.layoutDone())
      return;

    while (!runner.layoutDone()) {
      runner.layoutStep();
    }
  }

  public static DirectedGraph<GraphNode, GraphEdge> buildJungGraph(
      LayoutContext context) {
    JungBuilder builder = new JungBuilder(context.getGraphModel());

    builder.setMovableNodes(context.getMovableNodes());
    builder.setFixedNodes(context.getFixedNodes());
    builder.setEdgeMatcher(context.getEdgeMatcher());

    DirectedGraph<GraphNode, GraphEdge> result = builder.build();
    return result;
  }
}
