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

import java.util.Collection;

import com.google.devtools.depan.model.GraphNode;

/**
 * React to all ViewModelListener events the same way.
 * This is useful for setting a dirty bit, or similar behavior.
 *
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
public class SimpleViewModelListener implements ViewModelListener {

  protected void simpleChange() {
    // no-op to be filled in by derived classes
    // typical use case is to simply set the dirty bit for any change
  }

  public void locationsChanged(
      Collection<GraphNode> movedNodes, Object author) {
    simpleChange();
  }

  public void collapseChanged(
      Collection<CollapseData> created,
      Collection<CollapseData> removed,
      Object author) {
    simpleChange();
    
  }
}
