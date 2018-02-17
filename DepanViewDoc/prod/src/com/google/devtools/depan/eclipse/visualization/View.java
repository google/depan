/*
 * Copyright 2007 The Depan Project Authors
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

package com.google.devtools.depan.eclipse.visualization;

import com.google.devtools.depan.eclipse.visualization.ogl.ArrowHead;
import com.google.devtools.depan.eclipse.visualization.ogl.Arrowheads;
import com.google.devtools.depan.eclipse.visualization.ogl.EdgeRenderingProperty;
import com.google.devtools.depan.eclipse.visualization.ogl.GLPanel;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeColorSupplier;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeRatioSupplier;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeRenderingProperty;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeShapeSupplier;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeSizeSupplier;
import com.google.devtools.depan.eclipse.visualization.ogl.RendererChangeListener;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.nodes.trees.SuccessorEdges;
import com.google.devtools.depan.view_doc.model.CameraDirPreference;
import com.google.devtools.depan.view_doc.model.CameraPosPreference;
import com.google.devtools.depan.view_doc.model.EdgeDisplayProperty;
import com.google.devtools.depan.view_doc.model.EdgeDisplayProperty.ArrowheadStyle;
import com.google.devtools.depan.view_doc.model.EdgeDisplayProperty.LineStyle;
import com.google.devtools.depan.view_doc.model.NodeColorMode;
import com.google.devtools.depan.view_doc.model.NodeDisplayProperty;
import com.google.devtools.depan.view_doc.model.NodeRatioMode;
import com.google.devtools.depan.view_doc.model.NodeShapeMode;
import com.google.devtools.depan.view_doc.model.NodeSizeMode;
import com.google.devtools.depan.view_doc.model.ScenePreferences;

import org.eclipse.swt.widgets.Composite;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Map;

/**
 * Widget-like object that encapsulates the OGL rendering on behalf of the
 * ViewEditor.
 * 
 * Hides the {@link GLPanel} and other OGL artifacts from the
 * {@code ViewEditor}.
 * 
 * Wraps the {@link GLPanel} canvas with a horizontal and a vertical
 * scrollbar. Make sure to notify this instance via
 * {@link #updateDrawingBounds(Rectangle2D, Rectangle2D)} for any
 * {@link RendererChangeReceiver#updateDrawingBounds(Rectangle2D, Rectangle2D)}
 * event that are received.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class View {

  /** Run the scroll bar for the OGL canvas. */
  private final ScrollbarHandler scrollHandler;

  // Not final, so they can be released when this view instance is dispose()ed.
  
  /** Rendering object. */
  private GLPanel glPanel;

  /** Update drawing options from Eclipse preferences. */
  private RendererPreferences prefUpdater;

  /**
   * Create a new View, with the given model and initialize the layout.
   *
   * @param parent parent SWT control.
   * @param style style to apply to the SWT object containing the view.
   * @param editor source of rendering properties and target of change actions
   */
  public View(
      Composite parent, String partName, RendererChangeListener changeListener) {

    glPanel = new GLPanel(parent, changeListener, partName);

    scrollHandler = new ScrollbarHandler(parent, glPanel);
    scrollHandler.acquireResources();
  }

  public void setLayoutData(Object layoutData) {
    glPanel.getContext().setLayoutData(layoutData);
  }

  public void dispose() {
    if (null != prefUpdater) {
      prefUpdater.dispose();
      prefUpdater = null;
    }
    if (null != glPanel) {
      glPanel.dispose();
      glPanel = null;
    }
  }

  public void setGraphModel(
      GraphModel viewGraph,
      Map<GraphNode, ? extends SuccessorEdges> edgeMap) {

    // Creates the rendering pipe
    glPanel.setGraphModel(viewGraph);
    glPanel.setNodeNeighbors(edgeMap);

    prefUpdater = RendererPreferences.preparePreferences(glPanel);
  }

  public void start() {
    glPanel.start();
  }

  /////////////////////////////////////
  // High-level actions

  public BufferedImage takeScreenshot() {
    return glPanel.takeScreenshot();
  }

  public void updateDrawingBounds(Rectangle2D drawing, Rectangle2D viewport) {
    scrollHandler.updateDrawingBounds(drawing, viewport);
  }

  /////////////////////////////////////
  // Configurable instances

  public Rectangle2D getOGLViewport() {
    return glPanel.getOGLViewport();
  }

  public void setEdgeVisible(GraphEdge edge, boolean isVisible) {
    glPanel.setEdgeVisible(edge, isVisible);
  }

  /**
   * Updates various properties of the given <code>GraphEdge</code> such as
   * edge color, line style and arrow head using the given
   * {@link EdgeDisplayProperty} object.
   *
   * @param edge {@link GraphEdge} whose properties are updated.
   * @param property {@link EdgeDisplayProperty} object that stores various
   * changes related to the given edge.
   */
  public void updateEdgeProperty(
      GraphEdge edge, EdgeDisplayProperty property) {
    glPanel.setEdgeColor(edge, property.getColor());

    boolean stippled = (property.getLineStyle() == LineStyle.DASHED);
    glPanel.setEdgeLineStyle(edge, stippled);

    glPanel.setArrowhead(edge, createNewArrowhead(property.getArrowhead()));
  }

  /**
   * Transform a {@code ViewDoc} arrowhead style into a concrete
   * {@link ArrowHead}.
   *
   * @param arrowheadStyle The style of the new arrowhead.
   * @return A new {@link ArrowHead} object with the given style.
   */
  private ArrowHead createNewArrowhead(ArrowheadStyle arrowheadStyle) {
    switch (arrowheadStyle) {
      case FILLED:
        return new Arrowheads.FilledArrowhead();
      case OPEN:
        return new Arrowheads.OpenArrowhead();
      case TRIANGLE:
        return new Arrowheads.UnfilledArrowhead();
      default:
        return new Arrowheads.ArtisticArrowhead();
    }
  }

  /////////////////////////////////////

  public void updateSelectedNodes(
      Collection<GraphNode> removeNodes,
      Collection<GraphNode> extendNodes) {
    glPanel.updateSelection(removeNodes, extendNodes);
  }

  /**
   * Updates various properties of the given <code>GraphNode</code> such as
   * color and size model.
   *
   * @param node Node whose properties are modified.
   * @param property Holds various display properties such as color and size
   * model.
   */
  public void updateNodeProperty(
      GraphNode node, NodeDisplayProperty property) {
    // set node color first.
    // if it is set in property object, it will be colored using this color
    // otherwise, default color will be used.
    glPanel.setNodeColor(node, property.getColor());

    // set if this node is visible
    glPanel.setNodeVisible(node, property.isVisible());

    // Set the size of this node
    NodeSizeSupplier nodeSize = property.getSize();
    if (nodeSize != null) {
      glPanel.setNodeSize(node, nodeSize);
    }
  }

  /////////////////////////////////////
  // Rendering support

  public void initializeNodeLocations(Map<GraphNode, Point2D> locations) {
    glPanel.initializeNodeLocations(locations);
  }

  /**
   * Set the target locations for each node.  Target locations for view nodes
   * omitted from the map are set to the origin (0.0, 0.0).  This leads to 
   * animated moves of the nodes to the new location.
   * 
   * @param nodeLocations new locations for nodes
   */
  public void setNodeLocations(Map<GraphNode, Point2D> newLocations) {
    glPanel.setNodeLocations(newLocations);
  }

  /**
   * Edit the target locations for nodes in the location map.  View nodes
   * omitted from the map are not moved.  These leads to animated moves of the
   * nodes.
   * 
   * @param newLocations new locations for nodes
   */
  public void editNodeLocations(Map<GraphNode, Point2D> newLocations) {
    glPanel.editNodeLocations(newLocations);
  }

  /**
   * Update the target locations for nodes in the location map.  View nodes
   * omitted from the map are not moved.  The nodes are moved directly,
   * without animation.
   * 
   * @param newLocations new locations for nodes
   */
  public void updateNodeLocations(Map<GraphNode, Point2D> newLocations) {
    glPanel.updateNodeLocations(newLocations);
  }

  /**
   * Move the camera to the supplied x and y coordinates.
   */
  public void moveToPosition(float camX, float camY) {
    glPanel.moveToPosition(camX, camY);
  }

  /**
   * Zoom by moving camera to supplied z coordinate.
   */
  public void zoomToCamera(float camZ) {
    glPanel.zoomToCamera(camZ);
  }

  /**
   * Set the direction of the camera.
   * 
   * TODO: Make sure these match their behaviors
   * @param xRot - amount to tilt up or down
   * @param yRot - amount to pan left or right
   * @param zRot - amount to turn/twist clockwise or counterclockwise.
   */
  public void rotateToDirection(float xRot, float yRot, float zRot) {
    glPanel.rotateToDirection(xRot, yRot, zRot);
  }

  /**
   * Zoom to supplied scale.
   * A value of 1.0 places the camera at the default location.
   */
  public void setZoom(float scale) {
    glPanel.setZoom(scale);
  }

  /**
   * Save the current camera position into the supplied ScenePreferences
   * instance.
   */
  public void saveCameraPosition(ScenePreferences prefs) {
    CameraPosPreference prefsPos = prefs.getCameraPos();
    if (null == prefsPos) {
      prefsPos = CameraPosPreference.getDefaultCameraPos();
      prefs.setCameraPos(prefsPos);
    }

    float[] scenePos = glPanel.getCameraPosition();
    prefsPos.setX(scenePos[0]);
    prefsPos.setY(scenePos[1]);
    prefsPos.setZ(scenePos[2]);
  }

  /**
   * Save the current camera position into the supplied ScenePreferences
   * instance.
   */
  public void saveCameraDirection(ScenePreferences prefs) {
    CameraDirPreference prefsDir = prefs.getCameraDir();
    if (null == prefsDir) {
      prefsDir = CameraPosPreference.getDefaultCameraDir();
      prefs.setCameraDir(prefsDir);
    }

    float[] scenePos = glPanel.getCameraDirection();
    prefsDir.setX(scenePos[0]);
    prefsDir.setY(scenePos[1]);
    prefsDir.setZ(scenePos[2]);
  }

  /**
   * Move the camera position to the supplied position as a "cut",
   * without animation.
   * 
   * A scene changed event to indicate the newly stable diagram rendering
   * can occur despite the lack of animation.
   */
  private void setCameraPosition(CameraPosPreference posPrefs) {
    if (null == posPrefs) {
      return;
    }

    glPanel.moveToPosition(posPrefs.getX(), posPrefs.getY());
    glPanel.zoomToCamera(posPrefs.getZ());
    glPanel.cutCamera();
  }

  /**
   * Rotate the camera position to the supplied direction as a "cut",
   * without animation.
   * 
   * A scene changed event to indicate the newly stable diagram rendering
   * can occur despite the lack of animation.
   */
  private void setCameraDirection(CameraDirPreference dirPrefs) {
    if (null == dirPrefs) {
      return;
    }

    glPanel.rotateToDirection(dirPrefs.getX(), dirPrefs.getY(), dirPrefs.getZ());
    glPanel.cutCamera();
  }

  /**
   * Initialize the camera position to the supplied ScenePreferences,
   * without animation.  No scene changed event is generated.
   */
  public void initializeScenePrefs(ScenePreferences prefs) {
    if (null == prefs) {
      return;
    }

    setCameraPosition(prefs.getCameraPos());
    setCameraDirection(prefs.getCameraDir());
    glPanel.clearChanges();
  }

  public void setNodeColorMode(NodeColorMode mode) {
    glPanel.setNodeColorMode(mode);
  }

  public void setNodeColorByMode(
      GraphNode node, NodeColorMode mode, NodeColorSupplier supplier) {
    glPanel.setNodeColorByMode(node, mode, supplier);
  }

  public void setRootColorMode(NodeColorMode mode) {
    glPanel.setRootColorMode(mode);
  }

  public void setNodeRatioMode(NodeRatioMode mode) {
    glPanel.setNodeRatioMode(mode);
  }

  public void setNodeRatioByMode(
      GraphNode node, NodeRatioMode mode, NodeRatioSupplier supplier) {
    glPanel.setNodeRatioByMode(node, mode, supplier);
  }

  public void setNodeShapeMode(NodeShapeMode mode) {
    glPanel.setNodeShapeMode(mode);
  }

  public void setNodeShapeByMode(
      GraphNode node, NodeShapeMode mode, NodeShapeSupplier supplier) {
    glPanel.setNodeShapeByMode(node, mode, supplier);
  }

  public void setNodeSizeMode(NodeSizeMode mode) {
    glPanel.setNodeSizeMode(mode);
  }

  public void setNodeSizeByMode(
      GraphNode node, NodeSizeMode mode, NodeSizeSupplier supplier) {
    glPanel.setNodeSizeByMode(node, mode, supplier);
  }

  public void activateNodeStroke(boolean enable) {
    glPanel.activateNodeStroke(enable);
  }

  public void unCollapse(GraphNode child, GraphNode master) {
    glPanel.unCollapse(child, master);
  }

  public void collapseUnder(GraphNode child, GraphNode master) {
    glPanel.collapseUnder(child, master);
  }

  public void finishSteps() {
    glPanel.finishSteps();
  }

  public NodeRenderingProperty[] getNodeProperties() {
    return glPanel.getNodeProperties();
  }

  public EdgeRenderingProperty[] getEdgeProperties() {
    return glPanel.getEdgeProperties();
  }
}
