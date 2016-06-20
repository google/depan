/*
 * Copyright 2009 The Depan Project Authors
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

package com.google.devtools.depan.eclipse.visualization.ogl;

import com.google.devtools.depan.model.GraphNode;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Map;

/**
 * A listener to notify when the renderer changes significant properties of 
 * presentation.
 * 
 * <p>These notifications indicate that the user as requested the indicated
 * changes, but no change of rendering state has been made.  The receiver
 * (e.g. {@code ViewEditor}) must update the state of the rendered objects
 * to complete the request.
 * 
 * <p>This class is defined as a Listener, but works more like a Callback.
 * The renderer allows only a single listener, which is normally provided
 * by the {@code ViewEditor}.  The receiver should convert these callbacks
 * into events and publish them to all interested subscribers.
 *
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public interface RendererChangeListener {

  /**
   * Notify the receiver that the locations for the indicated nodes should
   * be changed.
   */
  public void locationsChanged(Map<GraphNode, Point2D> changes);

  /**
   * Notify the receiver that the given set of nodes is the current selection.
   */
  public void selectionChanged(Collection<GraphNode> pickedNodes);

  /**
   * Notify the receiver that the given set of nodes should be added to
   * the current selection.
   */
  public void selectionExtended(Collection<GraphNode> pickedNodes);

  /**
   * Notify the receiver that the given set of nodes should be removed from
   * the current selection.
   */
  public void selectionReduced(Collection<GraphNode> pickedNodes);

  /**
   * Notify the receiver that the position all of currently selected nodes
   * should be adjusted by the relative amounts.
   */
  public void selectionMoved(double x, double y);

  /**
   * Notify the receiver of the total size for the drawing.
   * 
   * Drawing bounds are updated for each rendering cycle. Listeners should
   * wait for the {@link #sceneChanged()} event to detect stable points in
   * the graph drawing.
   */
  public void updateDrawingBounds(Rectangle2D drawing, Rectangle2D viewport);

  /**
   * Notify the receiver that the scene (viewpoint, camera, etc.) has changed,
   * and this it is currently stable (unlikely to further change without an
   * explicit action).
   * 
   * The receiver should query the renderer for the current state.
   */
  public void sceneChanged();

  /**
   * Notify the receiver that a user action event has been detected on the
   * OGL canvas. Many of these are based on keyboard or mouse gestures.
   */
  public void handleEvent(RendererEvent event);
}
