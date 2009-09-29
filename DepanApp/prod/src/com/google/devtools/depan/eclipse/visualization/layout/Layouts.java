/*
 * Copyright 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.devtools.depan.eclipse.visualization.layout;

import com.google.devtools.depan.eclipse.editors.ViewEditor;
import com.google.devtools.depan.graph.api.DirectedRelationFinder;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphNode;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;

import java.awt.Dimension;

/**
 * Different layout provided by the Jung package. See Jung documentation for
 * more informations about each Layout. Two layouts:
 * {@link UniversalRadialLayout} and {@link UniversalTreeLayout} have been taken
 * from examples in the yung package.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public enum Layouts {
  FRLayout {
    @Override
    public AbstractLayout<GraphNode, GraphEdge> getLayout(
        ViewEditor editor, DirectedRelationFinder relations) {
      return new FRLayout<GraphNode, GraphEdge>(
          editor.getJungGraph(), LAYOUT_SIZE);
    }
  },
  FRLayout2 {
    @Override
    public AbstractLayout<GraphNode, GraphEdge> getLayout(
        ViewEditor editor, DirectedRelationFinder relations) {
      AbstractLayout<GraphNode, GraphEdge> result =
          new FRLayout2<GraphNode, GraphEdge>(
              editor.getJungGraph(), LAYOUT_SIZE);
      result.setSize(LAYOUT_SIZE);
      return result;
    }
  },
  SpringLayout {
    @Override
    public AbstractLayout<GraphNode, GraphEdge> getLayout(
        ViewEditor editor, DirectedRelationFinder relations) {
      AbstractLayout<GraphNode, GraphEdge> result =
          new SpringLayout<GraphNode, GraphEdge>(editor.getJungGraph());
      result.setSize(LAYOUT_SIZE);
      return result;
    }
  },
  SpringLayout2 {
    @Override
    public AbstractLayout<GraphNode, GraphEdge> getLayout(
        ViewEditor editor, DirectedRelationFinder relations) {
      AbstractLayout<GraphNode, GraphEdge> result =
            new SpringLayout2<GraphNode, GraphEdge>(editor.getJungGraph());
      result.setSize(LAYOUT_SIZE);
      return result;
    }
  },
  KKLayout {
    @Override
    public AbstractLayout<GraphNode, GraphEdge> getLayout(
        ViewEditor editor, DirectedRelationFinder relations) {
      KKLayout<GraphNode, GraphEdge> result =
          new KKLayout<GraphNode, GraphEdge>(editor.getJungGraph());
      result.setSize(LAYOUT_SIZE);
      return result;
    }
  },
  CircleLayout {
    @Override
    public AbstractLayout<GraphNode, GraphEdge> getLayout(
        ViewEditor editor, DirectedRelationFinder relations) {
      AbstractLayout<GraphNode, GraphEdge> result =
          new CircleLayout<GraphNode, GraphEdge>(editor.getJungGraph());
      result.setSize(LAYOUT_SIZE);
      return result;
    }
  },
  ISOMLayout {
    @Override
    public AbstractLayout<GraphNode, GraphEdge> getLayout(
        ViewEditor editor, DirectedRelationFinder relations) {
      ISOMLayout<GraphNode, GraphEdge> result =
          new ISOMLayout<GraphNode, GraphEdge>(editor.getJungGraph());
      result.setSize(LAYOUT_SIZE);
      return result;
    }
  },
  UniversalTreeLayout {
    @Override
    public AbstractLayout<GraphNode, GraphEdge> getLayout(
        ViewEditor editor, DirectedRelationFinder relations) {
      AbstractLayout<GraphNode, GraphEdge> result =
          new UniversalTreeLayout(
              editor.getJungGraph(), editor.getViewGraph(),
              relations, LAYOUT_SIZE);
      result.initialize();
      return result;
    }
  },
  UniversalRadialLayout {
    @Override
    public AbstractLayout<GraphNode, GraphEdge> getLayout(
        ViewEditor editor, DirectedRelationFinder relations) {
      AbstractLayout<GraphNode, GraphEdge> result =
          new UniversalRadialLayout(
              editor.getJungGraph(), editor.getViewGraph(),
              relations, LAYOUT_SIZE);
      result.initialize();
      return result;
    }
  },
  NewTreeLayout {
    @Override
    public AbstractLayout<GraphNode, GraphEdge> getLayout(
        ViewEditor editor, DirectedRelationFinder relations) {
      AbstractLayout<GraphNode, GraphEdge> result =
          new NewTreeLayout(
            editor.getJungGraph(), editor.getViewGraph(),
          relations, LAYOUT_SIZE);
      result.initialize();
      return result;
    }
  },
  NewRadialLayout {
    @Override
    public AbstractLayout<GraphNode, GraphEdge> getLayout(
        ViewEditor editor, DirectedRelationFinder relations) {
      AbstractLayout<GraphNode, GraphEdge> result =
          new NewRadialLayout(
            editor.getJungGraph(), editor.getViewGraph(),
          relations, LAYOUT_SIZE);
      result.initialize();
      return result;
    }
  };

  /**
   * Return a new layout.
   *
   * @param g Jung Graph
   * @param viewModel the {@link ViewModel}
   * @param relations a {@link DirectedRelationFinder}, used when we need to
   *        find a hierarchy.
   * @return a new Layout.
   */
  public abstract AbstractLayout<GraphNode, GraphEdge> getLayout(
      ViewEditor editor, DirectedRelationFinder relations);

  public static final Dimension LAYOUT_SIZE = new Dimension(1000, 1000);
}
