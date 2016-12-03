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
package com.google.devtools.depan.view_doc.layout;

import com.google.devtools.depan.model.GraphNode;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Map;

/**
 * Define an implementation independent model for computing the layout
 * of a set of nodes.
 * 
 * @author <a href='mailto:lee.carver@servicenow.com'>Lee Carver</a>
 */
public interface LayoutRunner {

  /**
   * Provide a cost estimate for the layout.
   * 
   * A cost of zero indicates that the layout positions have already be
   * computed.  This can happen if the position are part of the constructor.
   * A cost of one indicates that the layout positions can be computed in
   * a single call to layoutStep().  Larger costs indicate that repeated calls
   * to layoutStep() are necessary to achieve a good set of positions.
   */
  int layoutCost();

  /**
   * Execute one iteration of the layout algorithm.  If the layout is complete,
   * repeated calls should not change the layout positions.
   */
  void layoutStep();

  /**
   * Indicate if the layout process is complete.  The may return {@code true}
   * before any calls to {@link #layoutStep()} if the new positions are
   * computed during the initialization of the instance.
   * 
   * @return {@code true} if layout is complete
   */
  boolean layoutDone();

  /**
   * Provide the most recent positions for the supplied collection of nodes.
   */
  Map<GraphNode, Point2D> getPositions(Collection<GraphNode> nodes);
}
