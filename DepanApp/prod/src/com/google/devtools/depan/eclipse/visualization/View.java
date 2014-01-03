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

package com.google.devtools.depan.eclipse.visualization;

import com.google.devtools.depan.eclipse.editors.ViewEditor;
import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds;
import com.google.devtools.depan.eclipse.visualization.ogl.ArrowHead;
import com.google.devtools.depan.eclipse.visualization.ogl.GLPanel;
import com.google.devtools.depan.eclipse.visualization.ogl.GLScene;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.CollapsePlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.EdgeIncludePlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.FactorPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.NodeColorPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.NodeLabelPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.NodeShapePlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.NodeSizePlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.NodeStrokePlugin;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view.CollapseData;
import com.google.devtools.depan.view.EdgeDisplayProperty;
import com.google.devtools.depan.view.EdgeDisplayProperty.LineStyle;
import com.google.devtools.depan.view.NodeDisplayProperty;
import com.google.devtools.depan.view.NodeDisplayProperty.Size;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Map;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class View {

  private final ViewEditor editor;

  /**
   * Rendering object.
   */
  private GLPanel glPanel;

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

    glPanel = new GLPanel(parent, editor);
    glPanel.start();
  }

  public void dispose() {
    if (null != glPanel) {
      glPanel.dispose();
      glPanel = null;
    }
  }

  /////////////////////////////////////
  // Configurable instances

  public void setColors(Color background, Color foreground) {
    glPanel.setColors(background, foreground);
  }

  public NodeSizePlugin<GraphEdge> getNodeSize() {
    return glPanel.getRenderingPipe().getNodeSize();
  }

  public NodeColorPlugin<GraphEdge> getNodeColor() {
    return glPanel.getRenderingPipe().getNodeColors();
  }

  public NodeShapePlugin<GraphEdge> getNodeShape() {
    return glPanel.getRenderingPipe().getNodeShape();
  }

  public NodeLabelPlugin getNodeLabel() {
    return glPanel.getRenderingPipe().getNodeLabel();
  }

  public NodeStrokePlugin<GraphEdge> getNodeStroke() {
    return glPanel.getRenderingPipe().getNodeStroke();
  }

  public EdgeIncludePlugin getEdgeInclude() {
    return glPanel.getRenderingPipe().getEdgeInclude();
  }

  public FactorPlugin getFactor() {
    return glPanel.getRenderingPipe().getFactor();
  }

  public GLScene getScene() {
    return glPanel;
  }

  public Rectangle2D getOGLViewport() {
    return glPanel.getOGLViewport();
  }

  /////////////////////////////////////

  public void updateSelectedNodes(
      Collection<GraphNode> removeNodes,
      Collection<GraphNode> extendNodes) {
    glPanel.updateSelection(removeNodes, extendNodes);
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
    glPanel.setVisible(node, property.isVisible());

    // Set the size of this node
    Size nodeSize = property.getSize();
    if (nodeSize != null) {
      NodePreferencesIds.NodeSize sizeRepresentation =
          NodePreferencesIds.NodeSize.convertSizeRepresentation(nodeSize);
      glPanel.setNodeSize(node, sizeRepresentation);
    }
  }

  /////////////////////////////////////
  // Basic Getters and Setters

  public Control getControl() {
    return glPanel.getContext();
  }

  public BufferedImage takeScreenshot() {
    return glPanel.takeScreenshot();
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
}
