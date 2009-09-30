/*
 * Copyright 2009 Google Inc.
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

package com.google.devtools.depan.eclipse.visualization;

import com.google.devtools.depan.model.GraphNode;

import java.awt.geom.Point2D;
import java.util.Map;

/**
 * A listener to notify when the renderer changes significant properties of 
 * presentation.  The currently handles location changes, should support
 * zoom and scale changes, and should also absorb the existing
 * {@code SelectionChangeListener}.  Later
 *
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
// TODO(leeca):  See Javadoc above
public interface RendererChangeListener {

  /**
   * Notify that the given set of node was selected.
   */
  public void locationsChanged(Map<GraphNode, Point2D> changes);
}
