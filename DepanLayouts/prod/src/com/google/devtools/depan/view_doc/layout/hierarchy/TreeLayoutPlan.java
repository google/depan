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

import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view_doc.layout.LayoutContext;
import com.google.devtools.depan.view_doc.layout.LayoutRunner;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlan;

import com.google.common.collect.Maps;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Map;

/**
 * An adapter from @c{@code TreeLayout} classes to the {@link LayoutPlan}
 * class.
 * 
 * @author <a href='mailto:lee.carver@servicenow.com'>Lee Carver</a>
 */
public abstract class TreeLayoutPlan implements LayoutPlan {

  /**
   * Hook method for layout construction.
   * from {@link #buildRunner(LayoutContext)}.
   */
  protected abstract HierarchicalTreeLayout buildTreeLayout(LayoutContext context);

  @Override
  public LayoutRunner buildLayout(LayoutContext context) {
    HierarchicalTreeLayout layout = buildTreeLayout(context);
    LayoutRunnerAdapter runner = new LayoutRunnerAdapter(layout);
    return runner;
  }

  private static class LayoutRunnerAdapter implements LayoutRunner {

    private final HierarchicalTreeLayout layout;
    private boolean done;

    public LayoutRunnerAdapter(HierarchicalTreeLayout layout) {
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
}
