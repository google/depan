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
package com.google.devtools.depan.view_doc.layout.hierarchy;

import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view_doc.layout.LayoutContext;
import com.google.devtools.depan.view_doc.layout.LayoutGenerator;
import com.google.devtools.depan.view_doc.layout.LayoutRunner;
import com.google.devtools.depan.view_doc.layout.LayoutUtil;

import com.google.common.collect.Maps;

import edu.uci.ics.jung.graph.DirectedGraph;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Map;

/**
 * An adapter from @c{@code TreeLayout} classes to the {@link LayoutGenerator}
 * class.
 * 
 * @author <a href='mailto:lee.carver@servicenow.com'>Lee Carver</a>
 */
public abstract class TreeLayoutGenerator implements LayoutGenerator {

  /**
   * Hook method for layout construction.
   * from {@link #buildRunner(LayoutContext)}.
   */
  protected abstract NewTreeLayout buildTreeLayout(LayoutContext context);

  @Override
  public LayoutRunner buildRunner(LayoutContext context) {
    NewTreeLayout layout = buildTreeLayout(context);
    LayoutRunnerAdapter runner = new LayoutRunnerAdapter(layout);
    return runner;
  }

  private static class LayoutRunnerAdapter implements LayoutRunner {

    private final NewTreeLayout layout;
    private boolean done;

    public LayoutRunnerAdapter(NewTreeLayout layout) {
      this.layout = layout;
    }

    @Override
    public int layoutCost() {
      return 1;
    }

    @Override
    public void layoutStep() {
      if (done) {
        return;
      }

      layout.initialize();
      done = true;
    }

    @Override
    public boolean layoutDone() {
      return done;
    }

    @Override
    public Map<GraphNode, Point2D> getPositions(Collection<GraphNode> nodes) {
      // TODO: Simply wrapper jungLayout with a Map Adapter?
      Map<GraphNode, Point2D> result = Maps.newHashMap();
      for (GraphNode node : nodes) {
        Point2D position = layout.getPosition(node);
        result.put(node, position);
      }
      return result;
    }
  }

  /////////////////////////////////////
  // Canonical instances

  public static TreeLayoutGenerator NewTreeLayoutBuilder =
      new TreeLayoutGenerator() {

    @Override
    protected NewTreeLayout buildTreeLayout(LayoutContext context) {
      // TODO:  Use GraphModel more directly
      DirectedGraph<GraphNode, GraphEdge> jungGraph =
              LayoutUtil.buildJungGraph(context);

      return new NewTreeLayout(
              jungGraph,
              context.getGraphModel(),
              context.getEdgeMatcher().getEdgeMatcher(),
              context.getViewport());
    }
  };

  public static TreeLayoutGenerator NewRadialLayoutBuilder =
          new TreeLayoutGenerator() {

    @Override
    protected NewTreeLayout buildTreeLayout(LayoutContext context) {
      // TODO:  Use GraphModel more directly
      DirectedGraph<GraphNode, GraphEdge> jungGraph =
              LayoutUtil.buildJungGraph(context);

      return new NewRadialLayout(
              jungGraph,
              context.getGraphModel(),
              context.getEdgeMatcher().getEdgeMatcher(),
              context.getViewport());
    }
  };
}