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

import com.google.devtools.depan.graph.api.DirectedRelationFinder;
import com.google.devtools.depan.graph.basic.ForwardIdentityRelationFinder;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;

import edu.uci.ics.jung.graph.DirectedGraph;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Map;

/**
 * Isolate some static utilities useful for manipulating layout processes.
 * 
 * @author <a href='mailto:lee.carver@servicenow.com'>Lee Carver</a>
 */
public class LayoutUtil {

  private LayoutUtil() {
    // Prevent instantiation.
  }

  /**
   * @param layout
   * @param layoutOptions
   * @param layoutNodes
   * @return
   */
  public static Map<GraphNode, Point2D> calcPositions(
          LayoutGenerator layout, LayoutContext context,
          Collection<GraphNode> layoutNodes) {

    LayoutRunner runner = layout.buildRunner(context);
    runLayout(runner);
    return runner.getPositions(layoutNodes);
  }

  public static void runLayout(LayoutRunner runner) {
    // Some layout tools do all the work during their construction.
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
    builder.setRelations(context.getRelations());

    DirectedGraph<GraphNode, GraphEdge> result = builder.build();
    return result;
  }

  public static LayoutContext newLayoutContext(
      GraphModel parentGraph,
      Collection<GraphNode> viewNodes,
      DirectedRelationFinder viewRelations) {
    LayoutContext layoutContext = new LayoutContext();
    layoutContext.setGraphModel(parentGraph);
    layoutContext.setMovableNodes(viewNodes);

    if (null == viewRelations) {
      layoutContext.setRelations(ForwardIdentityRelationFinder.FINDER);
    } else {
      layoutContext.setRelations(viewRelations);
    }
    return layoutContext;
  }
}
