/*
 * Copyright 2008 Yohann R. Coppel
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

package com.google.devtools.depan.eclipse.visualization.ogl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.devtools.depan.eclipse.editors.ViewEditor;
import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;

import com.sun.opengl.util.BufferUtil;

import org.eclipse.swt.widgets.Composite;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A class extending {@link GLScene}, that specialize the {@link GLScene} to
 * be able to draw a graph, with nodes and edges.
 *
 * It also handle the selection events, forwarding the selection changes to
 * a listener.
 *
 * @author Yohann Coppel
 */
public class GLPanel extends GLScene {

  private static final Logger logger =
    Logger.getLogger(GLPanel.class.getName());

  public static final int ID_MASK     = 0xC0000000; // 2 higher bits as tag
  // 2 higher bits are 0
  public static final int ID_MASK_INV = ID_MASK ^ 0xFFFFFFFF;

  /**
   * Mask used to mask edges IDs.
   * 2 higher bits are "10" -> edge
   */
  public static final int EDGE_MASK   = 0x80000000;

  /**
   * Mask used to mask nodes IDs.
   * 2 higher bits are "11" -> node
   */
  public static final int NODE_MASK   = 0xC0000000;

  /**
   * Set of {@link NodeRenderingProperty}, one for each {@link GraphNode} to
   * render.
   */
  NodeRenderingProperty[] nodesProperties;

  /**
   * Set of {@link EdgeRenderingProperty}, one for each {@link GraphEdge} to
   * render.
   */
  EdgeRenderingProperty[] edgesProperties;

  /**
   * Sufficient space to select every element in this panel.
   */
  private IntBuffer selectBuffer;

  /**
   * Rendering pipe.
   */
  RenderingPipe renderer;

  /**
   * Map to retrieve a {@link NodeRenderingProperty} given its
   * {@link GraphNode}.
   */
  Map<GraphNode, NodeRenderingProperty> nodePropMap = Maps.newHashMap();

  /**
   * Map to retrieve an {@link EdgeRenderingProperty} given its
   * {@link GraphEdge}.
   */
  Map<GraphEdge, EdgeRenderingProperty> edgePropMap = Maps.newHashMap();

  /**
   * Source of information about the graph.
   */
  private ViewEditor editor;

  /////////////////////////////////////
  // Lifecycle management

  public GLPanel(Composite parent, ViewEditor editor) {
    super(parent);
    this.editor = editor;

    GraphModel viewGraph = editor.getViewGraph();

    // nodes
    GraphNode[] nodes = new GraphNode[0];
    nodes = viewGraph.getNodes().toArray(nodes);
    nodesProperties = new NodeRenderingProperty[nodes.length];
    for (int i = 0; i < nodes.length; ++i) {
      GraphNode n = nodes[i];
      nodesProperties[i] =
        new NodeRenderingProperty(i & ID_MASK_INV | NODE_MASK, n);
      nodePropMap.put(n, nodesProperties[i]);
    }

    // edges
    GraphEdge[] edges = new GraphEdge[0];
    edges = viewGraph.getEdges().toArray(edges);
    edgesProperties = new EdgeRenderingProperty[edges.length];
    for (int i = 0; i < edges.length; ++i) {
      GraphEdge edge = edges[i];
      GraphNode n1 = edge.getHead();
      GraphNode n2 = edge.getTail();
      NodeRenderingProperty p1 = nodePropMap.get(n1);
      NodeRenderingProperty p2 = nodePropMap.get(n2);
      if (p1 == null || p2 == null) {
        continue;
      }
      edgesProperties[i] =
        new EdgeRenderingProperty(i & ID_MASK_INV | EDGE_MASK, edge, p1, p2);
      edgePropMap.put(edge, edgesProperties[i]);
    }

    // Allocate the select buffer needed for these graphic elements.
    selectBuffer = allocSelectBuffer();

    renderer = new RenderingPipe(gl, glu, this, editor);
    dryRun();

    Refresher r = new Refresher(this);
    r.start();
  }

  @Override
  public void dispose() {
    super.dispose();
  }

  /////////////////////////////////////
  // Accessors

  public RenderingPipe getRenderingPipe() {
    return renderer;
  }

  /////////////////////////////////////
  // Rendering methods.

  /**
   * Perform a dry run in the rendering pipe, so that every plugin knows about
   * all nodes and edges.
   */
  protected void dryRun() {
    for (NodeRenderingProperty p : nodesProperties) {
      renderer.dryRun(p);
    }
    for (EdgeRenderingProperty p : edgesProperties) {
      renderer.dryRun(p);
    }
  }

  /**
   * Draw the scene.
   */
  @Override
  protected void drawScene(float elapsedTime) {
    super.drawScene(elapsedTime); // clean up, setup background, camera, etc....
    renderer.preFrame(elapsedTime);

    // draw nodes first
    for (NodeRenderingProperty p : nodesProperties) {
      renderer.render(p);
    }

    // draw edges then (because edges are linking nodes, that can move...
    for (EdgeRenderingProperty p : edgesProperties) {
      renderer.render(p);
    }

    renderer.postFrame();
  }

  @Override
  public void uncaughtKey(int keyCode, char character, boolean keyCtrlState,
      boolean keyAltState, boolean keyShiftState) {
    boolean caught = renderer.uncaughtKey(keyCode, character, keyCtrlState,
        keyAltState, keyShiftState);
    if (!caught) {
      logger.info("Lost key press: " + keyCode + " (" + character + ")");
    }
  }

  /////////////////////
  // conversions utilities functions between nodes, renderingProperties,
  // and openGL ids

  /**
   * Searches for {@link EdgeRenderingProperty} object mapped by given
   * {@link GraphEdge} object.
   *
   * @param edge The {@link GraphEdge} object whose properties are searched.
   * @return {@link EdgeRenderingProperty} object mapped by given
   * {@link GraphEdge} object iff it exists, null otherwise.
   */
  public EdgeRenderingProperty edge2property(GraphEdge edge) {
    return edgePropMap.get(edge);
  }

  public NodeRenderingProperty node2property(GraphNode node) {
    return nodePropMap.get(node);
  }

  public NodeRenderingProperty[] nodes2properties(Collection<GraphNode> nodes) {
    List<NodeRenderingProperty> list = Lists.newArrayList();
    for (GraphNode node : nodes) {
      NodeRenderingProperty prop = node2property(node);
      if (null != prop) {
        list.add(prop);
      }
    }
    return list.toArray(new NodeRenderingProperty[0]);
  }

  /**
   *  check if the given id matches a node and is correct.
   */
  private boolean isNodeId(int id) {
    if ((id & ID_MASK) != NODE_MASK) {
      return false;
    }
    int n = id & ID_MASK_INV;
    if (n < 0 || n >= nodesProperties.length) {
      return false;
    }
    return true;
  }

  private NodeRenderingProperty getNodeRenderer(int id) {
    if (!isNodeId(id)) {
      return null;
    }
    int n = id & ID_MASK_INV;
    return nodesProperties[n];
  }

  private GraphNode getGraphNode(int id) {
    NodeRenderingProperty prop = getNodeRenderer(id);
    if (null != prop) {
      return prop.node;
    }
    return null;
  }

  private Collection<GraphNode> getGraphNodes(int[] ids) {
    List<GraphNode> result = Lists.newArrayListWithCapacity(ids.length);
    for (int id : ids) {
      NodeRenderingProperty prop = getNodeRenderer(id);
      if (null != prop) {
        result.add(prop.node);
      }
    }
    return result;
  }

  /////////////////////////////////////
  // Update node locations.

  /**
   * Define how node position data is changed.
   * 
   * <p>This allows different derived instances to default for unknown values
   * or configure animation without lots of tests in the logic to assign
   * position data.  This is most useful when the position data for multiple
   * nodes is being updated in the same fashion.
   * @author leeca@google.com (Your Name Here)
   *
   */
  private interface PositionChanger {
    void setPosition(NodeRenderingProperty nodeProp, Point2D position);
  }

  private static enum PositionChangers implements PositionChanger {
    DIRECT() {
      @Override
      public void setPosition(
          NodeRenderingProperty nodeProp, Point2D position) {
        if (null == position) {
          return;
        }
        nodeProp.positionX = (float) position.getX();
        nodeProp.positionY = (float) position.getY();
        nodeProp.targetPositionX = nodeProp.positionX; 
        nodeProp.targetPositionY = nodeProp.positionY;
      }
    },

    INFERS() {
      @Override
      public void setPosition(
          NodeRenderingProperty nodeProp, Point2D position) {
        if (null == position) {
          nodeProp.positionX = 0.0f;
          nodeProp.positionY = 0.0f;
        }
        else {
          nodeProp.positionX = (float) position.getX();
          nodeProp.positionY = (float) position.getY();
        }
        nodeProp.targetPositionX = nodeProp.positionX; 
        nodeProp.targetPositionY = nodeProp.positionY;
      }
    },

    ANIMATE() {
      @Override
      public void setPosition(
          NodeRenderingProperty nodeProp, Point2D position) {
        if (null == position) {
          return;
        }
        nodeProp.targetPositionX = (float) position.getX();
        nodeProp.targetPositionY = (float) position.getY();
      }
    };

    public abstract void setPosition(
        NodeRenderingProperty nodeProp, Point2D position);
  }

  private void changeNodeLocations(
      PositionChanger setter, Map<GraphNode, Point2D> locations) {

    for (NodeRenderingProperty nodeProp : nodesProperties) {
      Point2D pos = locations.get(nodeProp.node);
      setter.setPosition(nodeProp, pos);
    }
  }

  public void initializeNodeLocations(Map<GraphNode, Point2D> locations) {
    changeNodeLocations(PositionChangers.INFERS, locations);
  }

  public void setNodeLocations(Map<GraphNode, Point2D> locations) {
    changeNodeLocations(PositionChangers.INFERS, locations);
  }

  public void editNodeLocations(Map<GraphNode, Point2D> locations) {
    changeNodeLocations(PositionChangers.ANIMATE, locations);
  }

  public void updateNodeLocations(Map<GraphNode, Point2D> locations) {
    changeNodeLocations(PositionChangers.DIRECT, locations);
  }

  /////////////////////////////////////
  // Node and edge property methods

  /**
   * Sets the <code>Color</code> of the given edge.
   *
   * @param edge {@link GraphEdge} object whose line color is modified.
   * @param newEdgeColor New color of this edge.
   */
  public void setEdgeColor(GraphEdge edge, Color newEdgeColor) {
    EdgeRenderingProperty edgeProperty = edge2property(edge);
    edgeProperty.overriddenStrokeColor = newEdgeColor;
  }

  /**
   * Sets the line style of the given edge.
   *
   * @param edge {@link GraphEdge} object whose line style is modified.
   * @param dashed Whether this edge must be drawn dashed (<code>true</code>) or
   * solid (<code>false</code>).
   */
  public void setEdgeLineStyle(GraphEdge edge, boolean dashed) {
    EdgeRenderingProperty edgeProperty = edge2property(edge);
    edgeProperty.getArrow().setDashed(dashed);
  }

  /**
   * Sets the arrow head style of the given edge.
   *
   * @param edge {@link GraphEdge} object whose arrow head style is modified.
   * @param arrowhead New arrow head style of this edge.
   */
  public void setArrowhead(GraphEdge edge, ArrowHead arrowhead) {
    EdgeRenderingProperty edgeProperty = edge2property(edge);
    edgeProperty.getArrow().setArrowhead(arrowhead);
  }

  /**
   * Sets the color of the given node.
   *
   * @param node Node whose color is updated on graph.
   * @param newNodeColor The new color of this node.
   */
  public void setNodeColor(GraphNode node, Color newNodeColor) {
    NodeRenderingProperty nodeProperty = node2property(node);
    nodeProperty.overriddenColor = newNodeColor;
  }

  /**
   * Sets how the size of this node is determined with the given size model.
   *
   * @param node Node whose size model is modified.
   * @param newSizeModel The new size model to be used while computing node
   * size.
   */
  public void setNodeSize(
      GraphNode node, NodePreferencesIds.NodeSize newSizeModel) {
    NodeRenderingProperty nodeProperty = node2property(node);
    nodeProperty.overriddenSize = newSizeModel;
  }

  /**
   * Sets if a node is visible.
   *
   * @param node Node whose visibility is modified.
   * @param isVisible New value for the visibility of this node.
   */
  public void setVisible(GraphNode node, boolean isVisible) {
    node2property(node).isVisible = isVisible;
  }

  ///////////////////////
  // Modifying selection

  // complete selection change

  private int countAllPickable() {
    // Double node properties to account for unpickable text objects
    // used to label the nodes.
    return edgesProperties.length + (nodesProperties.length * 2);
  }

  private IntBuffer allocSelectBuffer() {
    int pickableCount = countAllPickable();
    return BufferUtil.newIntBuffer(pickableCount * 6);
  }

  @Override
  protected IntBuffer getSelectBuffer() {
    return selectBuffer;
  }

  @Override
  protected boolean isSelected(int id) {
    GraphNode node = getGraphNode(id);
    if (null == node) {
      return false;
    }
    return editor.isSelected(node);
  }

  private RendererChangeListener getRendererCallback() {
    return editor.getRendererCallback();
  }

  @Override
  protected void setSelection(int[] picked) {
    getRendererCallback().selectionChanged(getGraphNodes(picked));
  }

  @Override
  protected void extendSelection(int[] extend) {
    getRendererCallback().selectionExtended(getGraphNodes(extend));
  }

  @Override
  protected void reduceSelection(int[] remove) {
    getRendererCallback().selectionReduced(getGraphNodes(remove));
  }

  @Override
  public void moveSelectionDelta(double x, double y) {
    getRendererCallback().selectionMoved(x, y);
  }

  public void updateSelection(
      Collection<GraphNode> clearedNodes,
      Collection<GraphNode> selectedNodes) {

    // Unselect all the cleared nodes.
    for (GraphNode node : clearedNodes) {
      node2property(node).setSelected(false);
    }

    // Select all the chosen nodes.
    for (GraphNode node : selectedNodes) {
      node2property(node).setSelected(true);
    }
  }
}
