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
import com.google.common.collect.Sets;
import com.google.devtools.depan.eclipse.editors.ViewEditor;
import com.google.devtools.depan.eclipse.visualization.plugins.core.EdgeRenderingPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.core.NodeRenderingPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.core.Plugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.CollapsePlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.EdgeColorPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.EdgeIncludePlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.EdgeLabelPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.FactorPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.LayoutPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.LayoutShortcutsPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.NodeColorPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.NodeLabelPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.NodeShapePlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.NodeSizePlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.NodeStrokePlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.SteperPlugin;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphNode;

import edu.uci.ics.jung.graph.Graph;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

/**
 * A class that take care of every rendering plugins.
 * Every element to render is passed through each adapted plugin. Each
 * plugin can modify the properties for the element to render. They
 * also have the ability to get the element out of the pipe, if they don't want
 * it to be rendered.
 *
 * The order in which each plugin is used is important, since some plugins
 * may modify, or use values previously set by another plugin.
 *
 * @see Plugin
 * @see NodeRenderingPlugin
 * @see EdgeRenderingPlugin
 *
 * @author Yohann Coppel
 *
 */
public class RenderingPipe {

  private NodeRenderingPlugin[] nodePlugins;
  private EdgeRenderingPlugin[] edgePlugins;
  private Plugin[] plugins;

  // plugins
  private LayoutShortcutsPlugin shortcuts;
  private LayoutPlugin layout;
  private CollapsePlugin collapse;
  private NodeColorPlugin<GraphEdge> nodeColors;
  private EdgeColorPlugin edgeColors;
  private SteperPlugin stepper;
  private DrawingPlugin drawing;
  private FactorPlugin factor;
  private NodeSizePlugin<GraphEdge> nodeSize;
  private NodeStrokePlugin<GraphEdge> nodeStroke;
  private NodeShapePlugin<GraphEdge> nodeShape;
  private NodeLabelPlugin nodeLabel;
  private EdgeLabelPlugin edgeLabel;
  private EdgeIncludePlugin edgeInclude;


  public RenderingPipe(GL gl, GLU glu, GLPanel panel,
      ViewEditor editor, Map<GraphNode, Double> nodeRanking) {
    Graph<GraphNode, GraphEdge> graph = editor.getJungGraph();
    shortcuts = new LayoutShortcutsPlugin(editor);
    layout = new LayoutPlugin(editor.getNodeLocations());
    nodeColors = new NodeColorPlugin<GraphEdge>(graph, nodeRanking);
    edgeColors = new EdgeColorPlugin();
    stepper = new SteperPlugin();
    drawing = new DrawingPlugin(gl, panel);
    factor = new FactorPlugin(gl, glu, panel.getGrip());
    nodeSize = new NodeSizePlugin<GraphEdge>(graph, nodeRanking);
    nodeStroke = new NodeStrokePlugin<GraphEdge>(panel, graph);
    nodeShape = new NodeShapePlugin<GraphEdge>(graph);
    nodeLabel = new NodeLabelPlugin();
    edgeLabel = new EdgeLabelPlugin();
    edgeInclude = new EdgeIncludePlugin();
    collapse = new CollapsePlugin();

    List<NodeRenderingPlugin> nodesP = Lists.newArrayList();
    List<EdgeRenderingPlugin> edgesP = Lists.newArrayList();
    Set<Plugin> pluginsSet = Sets.newHashSet();

    ////////// Node plugins
    // --- logical plugins
    nodesP.add(layout);
    nodesP.add(collapse);
    nodesP.add(nodeColors);
    nodesP.add(nodeSize);
    nodesP.add(nodeStroke);
    nodesP.add(nodeShape);
    nodesP.add(nodeLabel);
    // --- effects plugins
    // --- rendering plugins
    nodesP.add(factor);
    nodesP.add(stepper);
    nodesP.add(drawing);

    ////////// Edge plugins
    // --- logical plugins
    edgesP.add(edgeInclude);
    edgesP.add(layout);
    edgesP.add(collapse);
    edgesP.add(edgeColors);
    edgesP.add(edgeLabel);
    // --- effects plugins
    // --- rendering plugins
    edgesP.add(stepper);
    edgesP.add(drawing);

    ////////// all plugins.
    pluginsSet.add(shortcuts);
    pluginsSet.addAll(nodesP);
    pluginsSet.addAll(edgesP);

    // transform the lists and set into arrays, which are faster
    // to go through. (we do that at each frame...)
    nodePlugins = new NodeRenderingPlugin[nodesP.size()];
    edgePlugins = new EdgeRenderingPlugin[edgesP.size()];
    plugins = new Plugin[pluginsSet.size()];
    nodePlugins = nodesP.toArray(nodePlugins);
    edgePlugins = edgesP.toArray(edgePlugins);
    plugins = pluginsSet.toArray(plugins);
  }

  public void dryRun(NodeRenderingProperty nrp) {
    for (NodeRenderingPlugin p : nodePlugins) {
      p.dryRun(nrp);
    }
  }

  public void dryRun(EdgeRenderingProperty erp) {
    for (EdgeRenderingPlugin p : edgePlugins) {
      p.dryRun(erp);
    }
  }

  public void preFrame(float elapsedTime) {
    for (int i = 0; i < plugins.length; ++i) {
      plugins[i].preFrame(elapsedTime);
    }
  }

  public void render(NodeRenderingProperty nrp) {
    boolean mustBeRendered = true;
    for (int i = 0; mustBeRendered && i < nodePlugins.length; ++i) {
      mustBeRendered = nodePlugins[i].apply(nrp);
    }
  }

  public void render(EdgeRenderingProperty nrp) {
    boolean mustBeRendered = true;
    for (int i = 0; mustBeRendered && i < edgePlugins.length; ++i) {
      mustBeRendered = edgePlugins[i].apply(nrp);
    }
  }

  public void postFrame() {
    for (int i = 0; i < plugins.length; ++i) {
      plugins[i].postFrame();
    }
  }

  public boolean uncaughtKey(int keycode, char character, boolean keyCtrlState,
      boolean keyAltState, boolean keyShiftState) {
    boolean isCaught = false;
    for (int i = 0; i < plugins.length && !isCaught; ++i) {
      isCaught = plugins[i].keyPressed(keycode, character, keyCtrlState,
          keyAltState, keyShiftState);
    }
    return isCaught;
  }

  // getters for each plugin.

  public NodeRenderingPlugin[] getNodePlugins() {
    return nodePlugins;
  }

  public EdgeRenderingPlugin[] getEdgePlugins() {
    return edgePlugins;
  }

  public Plugin[] getPlugins() {
    return plugins;
  }

  public LayoutPlugin getLayout() {
    return layout;
  }

  public NodeColorPlugin<GraphEdge> getNodeColors() {
    return nodeColors;
  }

  public EdgeColorPlugin getEdgeColors() {
    return edgeColors;
  }

  public SteperPlugin getStepper() {
    return stepper;
  }

  public DrawingPlugin getDrawing() {
    return drawing;
  }

  public FactorPlugin getFactor() {
    return factor;
  }

  public NodeSizePlugin<GraphEdge> getNodeSize() {
    return nodeSize;
  }

  public NodeStrokePlugin<GraphEdge> getNodeStroke() {
    return nodeStroke;
  }

  public NodeShapePlugin<GraphEdge> getNodeShape() {
    return nodeShape;
  }

  public NodeLabelPlugin getNodeLabel() {
    return nodeLabel;
  }

  public EdgeIncludePlugin getEdgeInclude() {
    return edgeInclude;
  }

  public CollapsePlugin getCollapsePlugin() {
    return collapse;
  }
}
