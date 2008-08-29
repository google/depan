/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.model.interfaces;

import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view.EdgeDisplayProperty;
import com.google.devtools.depan.view.NodeDisplayProperty;
import com.google.devtools.depan.view.ViewModel;

/**
 * A listener for standard events that can occurs in the graph.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public interface GraphListener {

  /**
   * Callback when a new view is created.
   *
   * @param view the new {@link ViewModel}.
   */
  public void newView(ViewModel view);

  /**
   * Callback when display property of a node is modified.
   *
   * @param node The node whose display property is modified.
   * @param property The display property that holds the modifications.
   */
  public void nodePropertyChanged(GraphNode node, NodeDisplayProperty property);

  /**
   * Callback when display property of a graph edge is modified.
   *
   * @param edge Graph edge whose display property is modified.
   * @param property The display property that holds the modifications.
   */
  public void edgePropertyChanged(GraphEdge edge, EdgeDisplayProperty property);
}
