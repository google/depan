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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.devtools.depan.eclipse.editors.ViewEditor;
import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds;
import com.google.devtools.depan.eclipse.visualization.ogl.ArrowHead;
import com.google.devtools.depan.eclipse.visualization.ogl.GLPanel;
import com.google.devtools.depan.eclipse.visualization.ogl.RenderingPipe;
import com.google.devtools.depan.eclipse.visualization.ogl.SceneGrip;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.CollapsePlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.EdgeIncludePlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.FactorPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.NodeColorPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.NodeLabelPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.NodeShapePlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.NodeSizePlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.NodeStrokePlugin;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view.CollapseData;
import com.google.devtools.depan.view.EdgeDisplayProperty;
import com.google.devtools.depan.view.NodeDisplayProperty;
import com.google.devtools.depan.view.EdgeDisplayProperty.LineStyle;
import com.google.devtools.depan.view.NodeDisplayProperty.Size;

import edu.uci.ics.jung.algorithms.importance.PageRank;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Map;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class View implements SelectionChangeListener {

  /**
   * The JUNG graph that is use for rendering and presentation.
   * It is derived and rebuild from the ViewEditor on demand,
   * based on ViewPreferences notion of exposed nodes and edges .
   */
  private DirectedGraph<GraphNode, GraphEdge> jungGraph;

  /**
   * The ranking values for each nodes (using a page rank algorithm).
   */
  // TODO(leeca): move this to ViewEditor to provide a) shared resource,
  // b) robust Job (async execution) support.
  private Map<GraphNode, Double> ranking;

  /**
   * Collection of listeners listening for a change in the node selection.
   */
  private Collection<SelectionChangeListener> changeListeners =
      Lists.newArrayList();

  /**
   * Rendering object.
   */
  private GLPanel glPanel;

  /**
   * Rendering pipe.
   */
  RenderingPipe renderer;

  private GraphModel exposedGraph;

  /**
   * Create a new View, with the given model and initialize the layout.
   *
   * @param model underlying model.
   * @param initLayout first layout to apply to the view.
   * @param parent parent SWT control.
   * @param style style to apply to the SWT object containing the view.
   */
  public View(
      Composite parent, int style, ViewEditor editor) {
    this.exposedGraph = editor.getExposedGraph();
    this.jungGraph = editor.getJungGraph();

    rankGraph();
    glPanel = new GLPanel(parent, editor, this, ranking);
    updateNodeLocations(editor.getNodeLocations());
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

  public SceneGrip getGrip() {
    return glPanel.getGrip();
  }

  /////////////////////////////////////

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

    // set if this node is selected
    glPanel.setSelected(node, property.isSelected());

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

  /**
   * @return a map of importance, i.e. a map associating a value to each node,
   *         determined after applying a ranking algorithm to the underlying
   *         graph.
   */
  public Map<GraphNode, Double> getNodeImportanceMap() {
    return ranking;
  }

  public Graph<GraphNode, GraphEdge> getJungGraph() {
    return jungGraph;
  }

  public Control getControl() {
    return glPanel.getContext();
  }

  /**
   * @return a list of currently selected nodes.
   */
  public GraphNode[] getPickedNodes() {
    return glPanel.getSelectedNodes();
  }

  /**
   * Unselect all currently selected nodes, and select only the nodes in the
   * given list.
   *
   * @param pickedNodes set of nodes to select.
   */
  public void setPickedNodes(Collection<GraphNode> pickedNodes) {
    glPanel.setSelection(pickedNodes);
  }

  public BufferedImage takeScreenshot() {
    return glPanel.takeScreenshot();
  }

  /////////////////////////////////////
  // Rendering support

  /**
   * Associate to each node a value based on it's "importance" in the graph.
   * The selected algorithm is a Page Rank algorithm.
   *
   * The result is stored in the map {@link #ranking}.
   */
  private void rankGraph() {
    if (null == ranking) {
      ranking = Maps.newHashMap();
    }
    ranking.clear();

    PageRank<GraphNode, GraphEdge> pageRank =
        new PageRank<GraphNode, GraphEdge>(jungGraph, 0.15);
    pageRank.setRemoveRankScoresOnFinalize(false);
    pageRank.evaluate();

    for (GraphNode node : exposedGraph.getNodes()) {
      ranking.put(node, pageRank.getVertexRankScore(node));
    }
  }

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

  public void updateNodeLocations(Map<GraphNode, Point2D> nodeLocations) {
    glPanel.getRenderingPipe().getLayout().setLayout(nodeLocations);
    glPanel.getRenderingPipe().getFactor().computeBestScalingFactor();
  }

  /////////////////////////////////////
  // OpenGL selection change support
  // The View registers itself with the GLPanel as it's sole listener.
  // It then forwards any updates that it receives to any
  // SelectionChangListener that has register with it.

  public void registerListener(SelectionChangeListener listener) {
    if (!changeListeners.contains(listener)) {
      changeListeners.add(listener);
    }
  }

  public void unRegisterListener(SelectionChangeListener listener) {
    if (changeListeners.contains(listener)) {
      changeListeners.remove(listener);
    }
  }

  /**
   * Notify listeners that there were nodes added to the current selection.
   */
  @Override
  public void notifyAddedToSelection(GraphNode[] selected) {
    for (SelectionChangeListener listener : changeListeners) {
      listener.notifyAddedToSelection(selected);
    }
  }

  /**
   * Notify listeners that there were nodes removed from the current selection.
   */
  @Override
  public void notifyRemovedFromSelection(GraphNode[] unselected) {
    for (SelectionChangeListener listener : changeListeners) {
      listener.notifyRemovedFromSelection(unselected);
    }
  }
}
