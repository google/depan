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
package com.google.devtools.depan.view_doc.layout.jung;

import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view_doc.layout.LayoutContext;
import com.google.devtools.depan.view_doc.layout.LayoutRunner;
import com.google.devtools.depan.view_doc.layout.model.DoubleOption;
import com.google.devtools.depan.view_doc.layout.model.IntegerOption;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlan;
import com.google.devtools.depan.view_doc.layout.model.Options;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.graph.DirectedGraph;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

/**
 * Define a group of LayoutGenerator that use the JUNG layout algorithms.
 * 
 * Basic components derived from the earlier Layouts class.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public abstract class JungLayoutPlan implements LayoutPlan {

  /**
   * Hook method for layout construction.
   * from {@link #buildRunner(LayoutContext)}.
   */
  protected abstract Layout<GraphNode,GraphEdge> buildJungLayout(
          DirectedGraph<GraphNode, GraphEdge> jungGraph,
          Dimension layoutSize);

  /**
   * Hook method for runner construction
   * from {@link #buildRunner(LayoutContext)}.
   */
  protected abstract LayoutRunner buildLayoutRunner(
      Rectangle2D region,
      Layout<GraphNode, GraphEdge> jungLayout);

  @Override
  public String buildSummary() {
    return "-- no summary --";
  }

  @Override
  public final LayoutRunner buildLayout(LayoutContext context) {
    DirectedGraph<GraphNode, GraphEdge> jungGraph =
        JungBuilder.buildJungGraph(context);

    Rectangle2D region = context.getViewport();
    Dimension size = new Dimension((int) region.getWidth(), (int) region.getHeight());

    Layout<GraphNode, GraphEdge> jungLayout = buildJungLayout(jungGraph, size);
    return buildLayoutRunner(region, jungLayout);
  }

  /////////////////////////////////////
  // Implementation classes

  @SuppressWarnings("unused")  // Use when UniversalTree, etc. return
  private static abstract class DirectLayoutPlan extends JungLayoutPlan {

    @Override
    protected final LayoutRunner buildLayoutRunner(
        Rectangle2D region,
        Layout<GraphNode, GraphEdge> jungLayout) {
      return new JungLayoutRunner.Direct(region, jungLayout);
    }
  }

  private static abstract class IterativeLayoutPlan
      extends JungLayoutPlan {

    private final IntegerOption maxIterations;

    private final int hardLimit;

    public IterativeLayoutPlan(IntegerOption maxIterations, int hardLimit) {
      this.maxIterations = maxIterations;
      this.hardLimit = hardLimit;
    }

    protected LayoutRunner buildLayoutRunner(
        Rectangle2D region,
        Layout<GraphNode, GraphEdge> jungLayout) {
      int iterations = Options.getValue(maxIterations, hardLimit);
      return new JungLayoutRunner.Iterative(region, jungLayout, iterations);
    }
  }

  private static abstract class CountedLayoutPlan extends JungLayoutPlan {

    private final IntegerOption maxIterations;

    private final int hardLimit;

    public CountedLayoutPlan(IntegerOption maxIterations, int hardLimit) {
      this.maxIterations = maxIterations;
      this.hardLimit = hardLimit;
    }

    @Override
    protected final LayoutRunner buildLayoutRunner(
        Rectangle2D region,
        Layout<GraphNode, GraphEdge> jungLayout) {
      int iterations = Options.getValue(maxIterations, hardLimit);
      return new JungLayoutRunner.Counted(region, jungLayout, iterations);
    }
  }

  /////////////////////////////////////
  // In alphabetical order
  // Iteration counts discovered by inspection of layout implementations.

  public static class FRLayoutPlan extends IterativeLayoutPlan {
    private final DoubleOption attractMultiplier;
    private final DoubleOption repulsionMultiplier;

    public FRLayoutPlan(
        IntegerOption maxIterations,
        DoubleOption attractMultiplier,
        DoubleOption repulsionMultiplier) {
      super(maxIterations, 700);
      this.attractMultiplier = attractMultiplier;
      this.repulsionMultiplier = repulsionMultiplier;
    }

    @Override
    protected Layout<GraphNode, GraphEdge> buildJungLayout(
        DirectedGraph<GraphNode, GraphEdge> jungGraph, Dimension layoutSize) {

      FRLayout<GraphNode, GraphEdge> result =
          new FRLayout<GraphNode, GraphEdge>(jungGraph, layoutSize);
      if (attractMultiplier.isSet()) {
        result.setAttractionMultiplier(attractMultiplier.getValue());
      }
      if (repulsionMultiplier.isSet()) {
        result.setAttractionMultiplier(repulsionMultiplier.getValue());
      }
      return result;
    }
  }

  public static FRLayoutPlan BASE_FR_LAYOUT =
      new FRLayoutPlan(
          IntegerOption.UNSET_INT,
          DoubleOption.UNSET_DOUBLE,
          DoubleOption.UNSET_DOUBLE);


  public static class FR2LayoutPlan extends IterativeLayoutPlan {
    private final DoubleOption attractMultiplier;
    private final DoubleOption repulsionMultiplier;

    public FR2LayoutPlan(
        IntegerOption maxIterations,
        DoubleOption attractMultiplier,
        DoubleOption repulsionMultiplier) {
      super(maxIterations, 2000);
      this.attractMultiplier = attractMultiplier;
      this.repulsionMultiplier = repulsionMultiplier;
    }

    @Override
    protected Layout<GraphNode,GraphEdge> buildJungLayout(
            DirectedGraph<GraphNode, GraphEdge> jungGraph,
            Dimension layoutSize) {
      FRLayout2<GraphNode, GraphEdge> result =
          new FRLayout2<GraphNode, GraphEdge>(jungGraph, layoutSize);
      if (attractMultiplier.isSet()) {
        result.setAttractionMultiplier(attractMultiplier.getValue());
      }
      if (repulsionMultiplier.isSet()) {
        result.setAttractionMultiplier(repulsionMultiplier.getValue());
      }

      return result;
    }
  };

  public static FR2LayoutPlan BASE_FR2_LAYOUT =
      new FR2LayoutPlan(
          IntegerOption.UNSET_INT,
          DoubleOption.UNSET_DOUBLE,
          DoubleOption.UNSET_DOUBLE);


  public static class ISOMLayoutPlan extends IterativeLayoutPlan {

    public ISOMLayoutPlan(IntegerOption maxIterations) {
      super(maxIterations, 2000);
    }

    @Override
    protected Layout<GraphNode,GraphEdge> buildJungLayout(
        DirectedGraph<GraphNode, GraphEdge> jungGraph,
        Dimension layoutSize) {
      ISOMLayout<GraphNode, GraphEdge> result =
          new ISOMLayout<GraphNode, GraphEdge>(jungGraph);
      result.setSize(layoutSize);
      return result;
    }
  };

  public static ISOMLayoutPlan BASE_ISOM_LAYOUT =
      new ISOMLayoutPlan(IntegerOption.UNSET_INT);


  public static class KKLayoutPlan extends IterativeLayoutPlan {

    public KKLayoutPlan(IntegerOption maxIterations) {
      super(maxIterations, 2000);
    }

    @Override
    protected Layout<GraphNode,GraphEdge> buildJungLayout(
            DirectedGraph<GraphNode, GraphEdge> jungGraph,
            Dimension layoutSize) {
      KKLayout<GraphNode, GraphEdge> jungLayout =
              new KKLayout<GraphNode, GraphEdge>(jungGraph);
      jungLayout.setSize(layoutSize);
      return jungLayout;
    }
  };

  public static KKLayoutPlan BASE_KK_LAYOUT =
      new KKLayoutPlan(IntegerOption.UNSET_INT);


  public static class SpringLayoutPlan extends CountedLayoutPlan {
    private DoubleOption force;
    private IntegerOption range;
    private DoubleOption stretch;

    public SpringLayoutPlan(
        IntegerOption maxIterations,
        DoubleOption force,
        IntegerOption range,
        DoubleOption stretch) {
      super(maxIterations, 500);
      this.force = force;
      this.range = range;
      this.stretch = stretch;
    }

    @Override
    protected Layout<GraphNode,GraphEdge> buildJungLayout(
            DirectedGraph<GraphNode, GraphEdge> jungGraph,
            Dimension layoutSize) {
      SpringLayout<GraphNode, GraphEdge> result =
              new SpringLayout<GraphNode, GraphEdge>(jungGraph);
      result.setSize(layoutSize);
      if (force.isSet()) {
        result.setForceMultiplier(force.getValue());
      }
      if (range.isSet()) {
        result.setRepulsionRange(range.getValue());
      }
      if (stretch.isSet()) {
        result.setStretch(stretch.getValue());
      }
      return result;
    }
  };

  public static SpringLayoutPlan BASE_SPRING_LAYOUT =
      new SpringLayoutPlan(
          IntegerOption.UNSET_INT,
          DoubleOption.UNSET_DOUBLE,
          IntegerOption.UNSET_INT,
          DoubleOption.UNSET_DOUBLE);


  public static class Spring2LayoutPlan extends CountedLayoutPlan {
    private DoubleOption force;
    private IntegerOption range;
    private DoubleOption stretch;

    public Spring2LayoutPlan(
        IntegerOption maxIterations,
        DoubleOption force,
        IntegerOption range,
        DoubleOption stretch) {
      super(maxIterations, 500);
      this.force = force;
      this.range = range;
      this.stretch = stretch;
    }

    @Override
    protected Layout<GraphNode,GraphEdge> buildJungLayout(
            DirectedGraph<GraphNode, GraphEdge> jungGraph,
            Dimension layoutSize) {
      SpringLayout2<GraphNode, GraphEdge> result =
              new SpringLayout2<GraphNode, GraphEdge>(jungGraph);
      result.setSize(layoutSize);
      if (force.isSet()) {
        result.setForceMultiplier(force.getValue());
      }
      if (range.isSet()) {
        result.setRepulsionRange(range.getValue());
      }
      if (stretch.isSet()) {
        result.setStretch(stretch.getValue());
      }
      return result;
    }
  };

  public static Spring2LayoutPlan BASE_SPRING2_LAYOUT =
      new Spring2LayoutPlan(
          IntegerOption.UNSET_INT,
          DoubleOption.UNSET_DOUBLE,
          IntegerOption.UNSET_INT,
          DoubleOption.UNSET_DOUBLE);
}
