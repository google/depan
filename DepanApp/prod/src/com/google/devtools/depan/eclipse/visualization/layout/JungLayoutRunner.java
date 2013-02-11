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

import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphNode;

import com.google.common.collect.Maps;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.util.IterativeContext;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Map;

/**
 * Define a few standard variations of the {@link LayoutRunner} that are
 * useful for JUNG-based layout algorithms.
 * 
 * @author <a href='mailto:lee.carver@servicenow.com'>Lee Carver</a>
 */
public abstract class JungLayoutRunner implements LayoutRunner {

  private final Layout<GraphNode, GraphEdge> jungLayout;

  protected JungLayoutRunner(Layout<GraphNode, GraphEdge> jungLayout) {
    this.jungLayout = jungLayout;
  }

  @Override
  public Map<GraphNode, Point2D> getPositions(Collection<GraphNode> nodes) {
    // TODO: Simply wrapper jungLayout with a Map Adapter?
    Map<GraphNode, Point2D> result = Maps.newHashMap();
    for (GraphNode node : nodes) {
      Point2D position = jungLayout.transform(node);
      result.put(node, position);
    }
    return result;
  }

  public static class Direct extends JungLayoutRunner {

    protected Direct(Layout<GraphNode, GraphEdge> jungLayout) {
      super(jungLayout);
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
  }

  public static class Iterative extends JungLayoutRunner {
    private final IterativeContext runner;
    private final int layoutCost;

    protected Iterative(
            Layout<GraphNode, GraphEdge> jungLayout,
            int layoutCost) {
      super(jungLayout);
      this.layoutCost = layoutCost; 
      this.runner = (IterativeContext) jungLayout;
    }

    @Override
    public int layoutCost() {
      return layoutCost;
    }

    @Override
    public void layoutStep() {
      runner.step();
    }

    @Override
    public boolean layoutDone() {
      return runner.done();
    }
  }

  /**
   * The Spring layout algorithms never claims completion.
   */
  public static class Counted extends Iterative {
    private int stepsRemaining;

    protected Counted(
            Layout<GraphNode, GraphEdge> jungLayout,
            int layoutCost) {
      super(jungLayout, layoutCost);
      stepsRemaining = layoutCost;
    }

    @Override
    public void layoutStep() {
      super.layoutStep();

      if (stepsRemaining > 0)
        stepsRemaining++;
    }

    @Override
    public boolean layoutDone() {
      if (stepsRemaining <= 0)
        return true;

      return super.layoutDone();
    }
  }
}
