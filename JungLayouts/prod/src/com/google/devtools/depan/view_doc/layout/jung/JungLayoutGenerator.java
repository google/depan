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
import com.google.devtools.depan.view_doc.layout.LayoutGenerator;
import com.google.devtools.depan.view_doc.layout.LayoutRunner;

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
 * @author <a href='mailto:lee.carver@servicenow.com'>Lee Carver</a>
 */
public abstract class JungLayoutGenerator implements LayoutGenerator {

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
  // Template method, with hook runnerBuilder()
  public final LayoutRunner buildRunner(LayoutContext context) {
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
  private static abstract class Direct extends JungLayoutGenerator {

    @Override
    protected final LayoutRunner buildLayoutRunner(
        Rectangle2D region,
        Layout<GraphNode, GraphEdge> jungLayout) {
      return new JungLayoutRunner.Direct(region, jungLayout);
    }
  }

  private static abstract class Iterative extends JungLayoutGenerator {

    private final int iterations;

    public Iterative(int iterations) {
      this.iterations = iterations;
    }

    @Override
    protected final LayoutRunner buildLayoutRunner(
        Rectangle2D region,
        Layout<GraphNode, GraphEdge> jungLayout) {
      return new JungLayoutRunner.Iterative(region, jungLayout, iterations);
    }
  }

  private static abstract class Counted extends JungLayoutGenerator {

    private final int iterations;

    public Counted(int iterations) {
      this.iterations = iterations;
    }

    @Override
    protected final LayoutRunner buildLayoutRunner(
        Rectangle2D region,
        Layout<GraphNode, GraphEdge> jungLayout) {
      return new JungLayoutRunner.Counted(region, jungLayout, iterations);
    }
  }

  /////////////////////////////////////
  // In alphabetical order
  // Iteration counts discovered by inspection of layout implementations.
 
  public static Iterative FRLayoutBuilder =
      new Iterative(700) {

    @Override
    protected Layout<GraphNode,GraphEdge> buildJungLayout(
            DirectedGraph<GraphNode, GraphEdge> jungGraph,
            Dimension layoutSize) {
      return new FRLayout<GraphNode, GraphEdge>(jungGraph, layoutSize);
    }
  };

  public static Iterative FR2LayoutBuilder =
      new Iterative(700) {

    @Override
    protected Layout<GraphNode,GraphEdge> buildJungLayout(
            DirectedGraph<GraphNode, GraphEdge> jungGraph,
            Dimension layoutSize) {
      return new FRLayout2<GraphNode, GraphEdge>(jungGraph, layoutSize);
    }
  };

  public static Iterative ISOMLayoutBuilder =
      new Iterative(2000) {

    @Override
    protected Layout<GraphNode,GraphEdge> buildJungLayout(
            DirectedGraph<GraphNode, GraphEdge> jungGraph,
            Dimension layoutSize) {
      ISOMLayout<GraphNode, GraphEdge> jungLayout =
              new ISOMLayout<GraphNode, GraphEdge>(jungGraph);
      jungLayout.setSize(layoutSize);
      return jungLayout;
    }
  };

  public static Iterative KKLayoutBuilder =
      new Iterative(2000) {

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

  public static Counted SpringLayoutBuilder =
      new Counted(500) {

    @Override
    protected Layout<GraphNode,GraphEdge> buildJungLayout(
            DirectedGraph<GraphNode, GraphEdge> jungGraph,
            Dimension layoutSize) {
      SpringLayout<GraphNode, GraphEdge> jungLayout =
              new SpringLayout<GraphNode, GraphEdge>(jungGraph);
      jungLayout.setSize(layoutSize);
      return jungLayout;
    }
  };

  public static Counted Spring2LayoutBuilder =
      new Counted(500) {

    @Override
    protected Layout<GraphNode,GraphEdge> buildJungLayout(
            DirectedGraph<GraphNode, GraphEdge> jungGraph,
            Dimension layoutSize) {
      SpringLayout2<GraphNode, GraphEdge> jungLayout =
              new SpringLayout2<GraphNode, GraphEdge>(jungGraph);
      jungLayout.setSize(layoutSize);
      return jungLayout;
    }
  };
}
