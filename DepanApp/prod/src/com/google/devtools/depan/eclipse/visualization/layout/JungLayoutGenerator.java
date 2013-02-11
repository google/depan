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

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.graph.DirectedGraph;

import java.awt.Dimension;

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
          Layout<GraphNode, GraphEdge> jungLayout);

  @Override
  // Template method, with hook runnerBuilder()
  public final LayoutRunner buildRunner(LayoutContext context) {
    DirectedGraph<GraphNode, GraphEdge> jungGraph =
        LayoutUtil.buildJungGraph(context);
    Dimension size = LayoutUtil.buildJungDimension(context);
    Layout<GraphNode, GraphEdge> jungLayout = buildJungLayout(jungGraph, size);
    return buildLayoutRunner(jungLayout);
  }

  /////////////////////////////////////
  // Implementation classes

  @SuppressWarnings("unused")  // Use when UniversalTree, etc. return
  private static abstract class Direct extends JungLayoutGenerator {

    @Override
    protected final LayoutRunner buildLayoutRunner(
            Layout<GraphNode, GraphEdge> jungLayout) {
      return new JungLayoutRunner.Direct(jungLayout);
    }
  }

  private static abstract class Iterative extends JungLayoutGenerator {

    private final int iterations;

    public Iterative(int iterations) {
      this.iterations = iterations;
    }

    @Override
    protected final LayoutRunner buildLayoutRunner(
            Layout<GraphNode, GraphEdge> jungLayout) {
      return new JungLayoutRunner.Iterative(jungLayout, iterations);
    }
  }

  private static abstract class Counted extends JungLayoutGenerator {

    private final int iterations;

    public Counted(int iterations) {
      this.iterations = iterations;
    }

    @Override
    protected final LayoutRunner buildLayoutRunner(
            Layout<GraphNode, GraphEdge> jungLayout) {
      return new JungLayoutRunner.Counted(jungLayout, iterations);
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
