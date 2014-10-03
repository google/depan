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

package com.google.devtools.depan.eclipse.visualization.ogl;

import com.google.devtools.depan.eclipse.editors.ViewEditor;
import com.google.devtools.depan.eclipse.visualization.layout.LayoutGenerator;
import com.google.devtools.depan.model.GraphNode;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Map;

/**
 * A listener to notify when the renderer changes significant properties of 
 * presentation.  This currently handles location and location changes.
 * It should also support zoom and scale changes.
 * 
 * <p>These notifications indicate that the user as requested the indicated
 * changes, but no change of rendering state has been made.  The receiver
 * (e.g. {@link ViewEditor}) must update the state of the rendered objects
 * to complete the request.
 * 
 * <p>This class is defined as a Listener, but works more like a Callback.
 * The renderer allows only a single listener, which is normally provided
 * by the {@code ViewEditor}.  The receiver should convert these callbacks
 * into events and publish them to all interested subscribers.
 *
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
// TODO(leeca):  See Javadoc above
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
   */
  public void updateDrawingBounds(Rectangle2D drawing, Rectangle2D viewport);

  /**
   * Notify the receiver that the scene (viewpoint, camera, etc.) has changed.
   * The receiver should query the renderer for the current state.
   */
  public void sceneChanged();

  /**
   * Notify the receiver that the supplied layout should be used.
   */
  public void applyLayout(LayoutGenerator layout);

  /**
   * Notify the receiver that node positions should be scaled with the
   * provided factors.
   */
  public void scaleLayout(double scaleX, double scaley);

  /**
   * Notify the receiver that nodes positions should be scaled to fit
   * entirely within the current viewport.
   */
  public void scaleToViewport();
}
