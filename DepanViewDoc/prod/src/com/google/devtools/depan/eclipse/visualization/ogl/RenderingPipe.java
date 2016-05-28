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

import com.google.devtools.depan.eclipse.visualization.plugins.core.EdgeRenderingPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.core.NodeRenderingPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.core.Plugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.CollapsePlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.EdgeColorPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.EdgeLabelPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.FactorPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.LayoutPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.LayoutShortcutsPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.NodeColorPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.NodeLabelPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.NodeRatioPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.NodeShapePlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.NodeSizePlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.NodeStrokePlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.SteperPlugin;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

/**
 * A class that takes care of every rendering plugins.
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
  private NodeColorPlugin nodeColors;
  private EdgeColorPlugin edgeColors;
  private SteperPlugin stepper;
  private DrawingPlugin drawing;
  private FactorPlugin factor;
  private NodeRatioPlugin nodeRatio;
  private NodeSizePlugin nodeSize;
  private NodeStrokePlugin nodeStroke;
  private NodeShapePlugin nodeShape;
  private NodeLabelPlugin nodeLabel;
  private EdgeLabelPlugin edgeLabel;

  public RenderingPipe(GLPanel panel) {

    shortcuts = new LayoutShortcutsPlugin(panel);
    layout = new LayoutPlugin();
    nodeColors = new NodeColorPlugin();
    edgeColors = new EdgeColorPlugin();
    stepper = new SteperPlugin();
    drawing = new DrawingPlugin(panel);
    factor = new FactorPlugin(panel);
    nodeRatio = new NodeRatioPlugin();
    nodeSize = new NodeSizePlugin();
    nodeStroke = new NodeStrokePlugin(panel);
    nodeShape = new NodeShapePlugin();
    nodeLabel = new NodeLabelPlugin();
    edgeLabel = new EdgeLabelPlugin();
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
    nodesP.add(nodeRatio);
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
    for (NodeRenderingPlugin plugin : nodePlugins) {
      plugin.dryRun(nrp);
    }
  }

  public void dryRun(EdgeRenderingProperty erp) {
    for (EdgeRenderingPlugin plugin : edgePlugins) {
      plugin.dryRun(erp);
    }
  }

  public void render(NodeRenderingProperty nrp) {
    for (NodeRenderingPlugin plugin : nodePlugins) {
      if (!plugin.apply(nrp)) {
        return;
      }
    }
  }

  public void render(EdgeRenderingProperty erp) {
    for (EdgeRenderingPlugin plugin : edgePlugins) {
      if (!plugin.apply(erp)) {
        return;
      }
    }
  }

  public void preFrame(float elapsedTime) {
    for (Plugin plugin : plugins) {
      plugin.preFrame(elapsedTime);
    }
  }

  public void postFrame() {
    for (Plugin plugin : plugins) {
      plugin.postFrame();
    }
  }

  public boolean uncaughtKey(int keycode, char character,
      boolean keyCtrlState, boolean keyAltState, boolean keyShiftState) {
    for (Plugin plugin : plugins) {
      if (plugin.keyPressed(keycode, character,
          keyCtrlState, keyAltState, keyShiftState))
        return true;
    }
    return false;
  }

  // getters for each plugin.

  public LayoutPlugin getLayout() {
    return layout;
  }

  public NodeColorPlugin getNodeColors() {
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

  public NodeSizePlugin getNodeSize() {
    return nodeSize;
  }

  public NodeRatioPlugin getNodeRatio() {
    return nodeRatio;
  }

  public NodeStrokePlugin getNodeStroke() {
    return nodeStroke;
  }

  public NodeShapePlugin getNodeShape() {
    return nodeShape;
  }

  public NodeLabelPlugin getNodeLabel() {
    return nodeLabel;
  }

  public CollapsePlugin getCollapsePlugin() {
    return collapse;
  }
}
