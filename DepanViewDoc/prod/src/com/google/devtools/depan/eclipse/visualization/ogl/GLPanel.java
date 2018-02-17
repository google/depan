/*
 * Copyright 2008 The Depan Project Authors
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

import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.nodes.trees.SuccessorEdges;
import com.google.devtools.depan.view_doc.model.NodeColorMode;
import com.google.devtools.depan.view_doc.model.NodeRatioMode;
import com.google.devtools.depan.view_doc.model.NodeShapeMode;
import com.google.devtools.depan.view_doc.model.NodeSizeMode;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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

  private static final int BYTES_PER_INT = (Integer.SIZE / Byte.SIZE);

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
   * Callback instance for OGL gestures.
   */
  private final RendererChangeListener changeListener;

  /**
   * Thread responsible for redrawing the OGL scene.
   */
  private final Refresher refresher;

  /////////////////////////////////////
  // What to draw, and the resources to render it.
  // These must be re-allocated whenever the view graph and its properties
  // are ever changed, including initial construction.

  /**
   * The graph that should be rendered.  This must be configured before
   * rendering is started via {@link #setGraphModel(GraphModel)}.
   */
  private GraphModel viewGraph;

  /**
   * Rendering pipe.
   */
  private RenderingPipe renderer;

  /**
   * Set of {@link NodeRenderingProperty}, one for each {@link GraphNode} to
   * render.
   */
  private NodeRenderingProperty[] nodesProperties;

  /**
   * Set of {@link EdgeRenderingProperty}, one for each {@link GraphEdge} to
   * render.
   */
  private EdgeRenderingProperty[] edgesProperties;

  /**
   * Sufficient space to select every element in this panel.
   */
  private IntBuffer selectBuffer;

  /**
   * Map to retrieve a {@link NodeRenderingProperty} given its
   * {@link GraphNode}.
   */
  private Map<GraphNode, NodeRenderingProperty> nodePropMap = Maps.newHashMap();

  /**
   * Map to retrieve an {@link EdgeRenderingProperty} given its
   * {@link GraphEdge}.
   */
  private Map<GraphEdge, EdgeRenderingProperty> edgePropMap = Maps.newHashMap();

  /////////////////////////////////////
  // Lifecycle management

  public GLPanel(Composite parent,
      RendererChangeListener changeListener, String threadLabel) {
    super(parent);
    this.changeListener = changeListener;

    refresher = new Refresher(this);
    refresher.setName("OGL " + threadLabel);
  }

  /**
   * As part of establishing the {@link GraphModel} for the {@link GLPanel},
   * this method allocates all of the node and edge rendering properties
   * that will be used once the canvas rendering is active.
   * 
   * This method should be called first after the constructor is invoked
   * to ensure that all rendering data structures are in place.
   */
  public void setGraphModel(
      GraphModel viewGraph) {

    this.viewGraph = viewGraph;
    prepareResources();

    selectBuffer = allocSelectBuffer();
    renderer = new RenderingPipe(this);
  }

  /**
   * Labels for nodes and edges need to be created within the
   * OGL context.
   */
  @Override
  protected void allocateResources() {
    // nodes
    GraphNode[] nodes = new GraphNode[0];
    nodes = viewGraph.getNodes().toArray(nodes);
    nodesProperties = new NodeRenderingProperty[nodes.length];
    for (int i = 0; i < nodes.length; ++i) {
      GraphNode n = nodes[i];
      NodeRenderingProperty nodeProp =
          new NodeRenderingProperty(i & ID_MASK_INV | NODE_MASK, n);
      nodesProperties[i] = nodeProp; 
      nodePropMap.put(n, nodeProp);
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
      EdgeRenderingProperty edgesProp =
          new EdgeRenderingProperty(i & ID_MASK_INV | EDGE_MASK, edge, p1, p2);
      edgesProperties[i] = edgesProp;
      edgePropMap.put(edge, edgesProp);
    }
  }

  public void start() {
    dryRun();
    refresher.start();
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
   * Perform a dry run in the rendering pipe, so that every plug-in knows about
   * all nodes and edges.
   */
  private void dryRun() {
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

    // Very small diagrams, e.g. a single collapsed node,
    // consume no drawing space.
    Rectangle2D drawing = renderer.getDrawing().getDrawingBounds();
    if (null == drawing) {
      return;
    }
    Rectangle2D viewport = getOGLViewport();
    getRendererCallback().updateDrawingBounds(drawing, viewport);
    if (isNowStable()) {
      getRendererCallback().sceneChanged();
    }
  }


  /**
   * Draw the pickable elements in the scene.
   * No updates for rendering metrics.
   * Scene management is not required.
   * 
   * [2017] Omit non-pickable edges for faster rendering and better pick
   * resolution on busy diagrams.
   */
  @Override
  protected void drawPickables() {
    super.drawPickables(); // clean up, setup background, camera, etc....
    renderer.preFrame(0.0f);

    // only draw nodes
    for (NodeRenderingProperty p : nodesProperties) {
      renderer.render(p);
    }

    renderer.postFrame();
  }

  @Override
  public void uncaughtKey(KeyEvent event,
      boolean keyCtrlState, boolean keyAltState, boolean keyShiftState) {

    // Does any renderer handle it?
    // Is this obsolete?
    boolean caught = renderer.uncaughtKey(
        event.keyCode, event.character,
        keyCtrlState, keyAltState, keyShiftState);
    if (caught) {
      return;
    }

    if (selectAll(event.keyCode, event.character,
        keyCtrlState, keyAltState, keyShiftState)) {
      return;
    }

    super.uncaughtKey(event, keyCtrlState, keyAltState, keyShiftState);
  }

  private boolean selectAll(int keyCode, char character,
      boolean keyCtrlState, boolean keyAltState, boolean keyShiftState) {
    if (keyAltState || keyShiftState || !keyCtrlState) {
      return false;
    }
    if ('a' != keyCode) {
      return false;
    }

    // Select all directly or indirectly visible nodes.
    // Assume this is all of the known nodes.
    List<GraphNode> selection =
        Lists.newArrayListWithExpectedSize(nodesProperties.length);
    for (NodeRenderingProperty nodeProp : nodesProperties) {
      if (nodeProp.isApparent()) {
        selection.add(nodeProp.node);
      }
    }

    getRendererCallback().selectionChanged(selection);
    return true;
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

  private Collection<GraphNode> getGraphNodes(int[] ids) {
    List<GraphNode> result = Lists.newArrayListWithExpectedSize(ids.length);
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

    @Override
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

  public void unCollapse(GraphNode child, GraphNode master) {
    renderer.getCollapsePlugin().unCollapse(
        node2property(child), node2property(master));
  }

  public void collapseUnder(GraphNode child, GraphNode master) {
    renderer.getCollapsePlugin().collapseUnder(
        node2property(child), node2property(master));
  }

  public void finishSteps() {
    for (NodeRenderingProperty nodeProp : nodesProperties) {
      nodeProp.finishSteps();
    }
  }

  /////////////////////////////////////
  // Node and edge property methods

  public void setEdgeVisible(GraphEdge edge, boolean isVisible) {
    EdgeRenderingProperty edgeProperty = edge2property(edge);
    edgeProperty.isVisible = isVisible;
  }

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
   * Selects the set of colors to use
   */
  public void setNodeColorMode(NodeColorMode mode) {
    renderer.getNodeColors().setNodeColorMode(mode);
  }

  /**
   * Selects the set of colors to use
   */
  public void setRootColorMode(NodeColorMode mode) {
    renderer.getNodeColors().setRootColorMode(mode);
  }

  /**
   * Sets an alternate color for the given node.
   */
  public void setNodeColorByMode(
      GraphNode node, NodeColorMode mode, NodeColorSupplier supplier) {
    NodeRenderingProperty nodeProperty = node2property(node);
    renderer.getNodeColors().setNodeColorByMode(nodeProperty, mode, supplier);
  }

  public void setNodeRatioMode(NodeRatioMode mode) {
    renderer.getNodeRatio().setNodeRatioMode(mode);
  }

  public void setNodeRatioByMode(
      GraphNode node, NodeRatioMode mode, NodeRatioSupplier supplier) {
    NodeRenderingProperty nodeProperty = node2property(node);
    renderer.getNodeRatio().setNodeRatioByMode(nodeProperty, mode, supplier);
  }

  public void setNodeShapeMode(NodeShapeMode mode) {
    renderer.getNodeShape().setNodeShapeMode(mode);
  }

  public void setNodeShapeByMode(
      GraphNode node, NodeShapeMode mode, NodeShapeSupplier supplier) {
    NodeRenderingProperty nodeProperty = node2property(node);
    renderer.getNodeShape().setNodeShapeByMode(nodeProperty, mode, supplier);
  }

  public void setNodeSizeMode(NodeSizeMode mode) {
    renderer.getNodeSize().setNodeSizeMode(mode);
  }

  public void setNodeSizeByMode(
      GraphNode node, NodeSizeMode mode, NodeSizeSupplier supplier) {
    NodeRenderingProperty nodeProperty = node2property(node);
    renderer.getNodeSize().setNodeSizeByMode(nodeProperty, mode, supplier);
  }

  /**
   * Override the size supplier for individual nodes.
   */
  public void setNodeSize(GraphNode node, NodeSizeSupplier supplier) {
    NodeRenderingProperty nodeProperty = node2property(node);
    renderer.getNodeSize().setOverriddenSize(nodeProperty, supplier);
  }

  /**
   * Sets if a node is visible.
   *
   * @param node Node whose visibility is modified.
   * @param isVisible New value for the visibility of this node.
   */
  public void setNodeVisible(GraphNode node, boolean isVisible) {
    node2property(node).isVisible = isVisible;
  }

  public void setNodeNeighbors(
      Map<GraphNode, ? extends SuccessorEdges> edgeMap) {
    renderer.getNodeStroke().setNodeNeighbors(edgeMap);
  }

  public void activateNodeStroke(boolean value) {
    renderer.getNodeStroke().activate(value);
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
    int allocBytes = pickableCount * 6 * BYTES_PER_INT;
    ByteBuffer result = ByteBuffer.allocateDirect(allocBytes);
    result.order(ByteOrder.nativeOrder());
    return result.asIntBuffer();
  }

  @Override
  protected IntBuffer getSelectBuffer() {
    return selectBuffer;
  }

  @Override
  protected boolean isSelected(int id) {
    NodeRenderingProperty prop = getNodeRenderer(id);
    if (null == prop) {
      return false;
    }
    return prop.isSelected();
  }

  private RendererChangeListener getRendererCallback() {
    return changeListener;
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
      NodeRenderingProperty props = node2property(node);
      if (null != props) {
        props.setSelected(false);
      }
    }

    // Select all the chosen nodes.
    for (GraphNode node : selectedNodes) {
      NodeRenderingProperty props = node2property(node);
      if (null != props) {
        props.setSelected(true);
      }
    }
  }

  public void handleEvent(RendererEvent event) {
    getRendererCallback().handleEvent(event);
  }

  public NodeRenderingProperty[] getNodeProperties() {
    return nodesProperties;
  }

  public EdgeRenderingProperty[] getEdgeProperties() {
    return edgesProperties;
  }
}
