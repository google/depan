/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.view_doc.model;

import com.google.devtools.depan.model.GraphNode;

import java.awt.geom.Point2D;

/**
 * Abstract repository that provides access to node locations.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public interface NodeLocationRepository {

  public static interface ChangeListener {
    void nodeLocationChanged(GraphNode node, Point2D location);
  }

  Point2D getLocation(GraphNode node);

  /**
   * Set the {@link EdgeDisplayProperty} for the supplied {@link GraphNode}.
   */
  void setLocation(GraphNode node, Point2D location);

  void addChangeListener(ChangeListener listener);

  void removeChangeListener(ChangeListener listener);
}
