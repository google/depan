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

package com.google.devtools.depan.view;

import com.google.devtools.depan.model.GraphNode;

import java.util.Collection;

/**
 * A listener for events that can occurs in ViewModels
 * 
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
public interface ViewModelListener {

  /**
   * Callback when nodes change location.
   * 
   * @param collection of nodes at new locations.
   * @param author the initiator of the change.
   */
  public void locationsChanged(
      Collection<GraphNode> movedNodes, Object author);

  /**
   * Callback when nodes collapsing changes.
   * 
   * @param created Collection of new collapsed nodes.
   * @param removed Collection of now un-collapsed nodes.
   * @param author the initiator of the change.
   */
  public void collapseChanged(
      Collection<CollapseData> created,
      Collection<CollapseData> removed,
      Object author);
}
