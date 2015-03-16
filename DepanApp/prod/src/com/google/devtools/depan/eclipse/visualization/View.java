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

import com.google.devtools.depan.eclipse.editors.CameraPosPreference;
import com.google.devtools.depan.eclipse.editors.ScenePreferences;
import com.google.devtools.depan.eclipse.editors.ViewEditor;
import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds;
import com.google.devtools.depan.eclipse.visualization.layout.LayoutGenerator;
import com.google.devtools.depan.eclipse.visualization.ogl.ArrowHead;
import com.google.devtools.depan.eclipse.visualization.ogl.GLPanel;
import com.google.devtools.depan.eclipse.visualization.ogl.GLScene;
import com.google.devtools.depan.eclipse.visualization.ogl.RendererChangeListener;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.CollapsePlugin;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view.CollapseData;
import com.google.devtools.depan.view.EdgeDisplayProperty;
import com.google.devtools.depan.view.EdgeDisplayProperty.LineStyle;
import com.google.devtools.depan.view.NodeDisplayProperty;
import com.google.devtools.depan.view.NodeDisplayProperty.Size;

import edu.uci.ics.jung.graph.Graph;

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
 * {@link ViewEditor}.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class View {

  // TODO: Turn this into an event dispatcher, and have editor
  // register a listener.
  private final ViewEditor editor;

  /** Callbacks from {@link GLPanel}. */
  private final RendererChangeReceiver changeReceiver =
      new RendererChangeReceiver();

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
      Composite parent, int style, ViewEditor editor) {
    this.editor = editor;

    glPanel = new GLPanel(parent, changeReceiver, editor.getPartName());

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
      Graph<GraphNode, GraphEdge> jungGraph,
      Map<GraphNode, Double> nodeRanking) {

    // Creates the rendering pipe
    glPanel.setGraphModel(viewGraph, jungGraph, nodeRanking);

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

    glPanel.setArrowhead(edge,
        ArrowHead.createNewArrowhead(property.getArrowhead()));
    glPanel.setEdgeVisible(edge, property.isVisible());
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
    Size nodeSize = property.getSize();
    if (nodeSize != null) {
      NodePreferencesIds.NodeSize sizeRepresentation =
          NodePreferencesIds.NodeSize.convertSizeRepresentation(nodeSize);
      glPanel.setNodeSize(node, sizeRepresentation);
    }
  }

  /////////////////////////////////////
  // Rendering support

  public void updateCollapseChanges(
      Collection<CollapseData> created,
      Collection<CollapseData> removed) {
    CollapsePlugin collapsePlugin =
        glPanel.getRenderingPipe().getCollapsePlugin();
    for (CollapseData data : created) {
      // collapse each child under the parent
      for (GraphNode child : data.getChildrenNodes()) {
        collapsePlugin.collapseUnder(
            glPanel.node2property(child),
            glPanel.node2property(data.getMasterNode()));
      }
    }

    for (CollapseData data : removed) {
      // uncollapse every children
      for (GraphNode child : data.getChildrenNodes()) {
        collapsePlugin.unCollapse(
            glPanel.node2property(child),
            glPanel.node2property(data.getMasterNode()));
      }
    }
  }

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
   * Move the camera position to the supplied ScenePreferences as a "cut",
   * without animation.
   * 
   * A scene changed event to indicate the newly stable diagram rendering
   * can occur despite the lack of animation.
   */
  public void setCameraPosition(ScenePreferences prefs) {
    if (null == prefs) {
      return;
    }
    CameraPosPreference prefsPos = prefs.getCameraPos();
    if (null == prefsPos) {
      return;
    }

    GLScene scene = glPanel;
    scene.moveToCamera(prefsPos.getX(), prefsPos.getY());
    scene.zoomToCamera(prefsPos.getZ());
    scene.cutCamera();
  }

  /**
   * Initialize the camera position to the supplied ScenePreferences,
   * without animation.  No scene changed event is genereated.
   */
  public void initCameraPosition(ScenePreferences prefs) {
    setCameraPosition(prefs);
    glPanel.clearChanges();
  }

  /////////////////////////////////////
  // Callbacks from the rendering engine.

  private class RendererChangeReceiver
      implements RendererChangeListener {

    @Override
    public void locationsChanged(Map<GraphNode, Point2D> changes) {
      editor.editNodeLocations(changes, this);
    }

    @Override
    public void selectionMoved(double x, double y) {
      editor.moveSelectionDelta(x, y, this);
    }

    @Override
    public void selectionChanged(Collection<GraphNode> pickedNodes) {
      editor.selectNodes(pickedNodes);
    }

    @Override
    public void selectionExtended(Collection<GraphNode> extendNodes) {
      editor.extendSelection(extendNodes, null);
    }

    @Override
    public void selectionReduced(Collection<GraphNode> reduceNodes) {
      editor.reduceSelection(reduceNodes, null);
    }

    @Override
    public void updateDrawingBounds(Rectangle2D drawing, Rectangle2D viewport) {
      scrollHandler.updateDrawingBounds(drawing, viewport);
      editor.updateDrawingBounds(drawing, viewport);
    }

    @Override
    public void sceneChanged() {
      editor.sceneChanged();
    }

    @Override
    public void applyLayout(LayoutGenerator layout) {
      editor.applyLayout(layout);
    }

    @Override
    public void scaleLayout(double scaleX, double scaleY) {
      editor.scaleLayout(scaleX, scaleY);
    }

    @Override
    public void scaleToViewport() {
      editor.scaleToViewport();
    }
  }
}
