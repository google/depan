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

package com.google.devtools.depan.eclipse.visualization.plugins.impl;

import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds.NodeShape;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeRenderingProperty;
import com.google.devtools.depan.eclipse.visualization.plugins.core.NodeRenderingPlugin;
import com.google.devtools.depan.model.GraphNode;

import edu.uci.ics.jung.graph.Graph;

/**
 * A plugin that setup the shape for nodes.
 *
 * @author Yohann Coppel
 *
 */
public class NodeShapePlugin<E> implements NodeRenderingPlugin {

  private NodeShape shapeMode = NodeShape.getDefault();

  private boolean hasChanged = true;
  private boolean shapesEnabled = true;

  private Graph<GraphNode, E> graph;

  public NodeShapePlugin(Graph<GraphNode, E> graph) {
    this.graph = graph;
  }

  private void setShape(NodeRenderingProperty p, int degree) {
    if (!shapesEnabled) {
      p.shape = NodeShape.getDefaultShape();
    } else {
      p.shape = shapeMode.getShape(degree, p.node);
    }
  }

  @Override
  public void dryRun(NodeRenderingProperty p) {
    // during the dry run, store with each NodeRenderingProperty the degree
    // of the node: because this is a hard operation, we don't want to do it
    // at every frame.
    int degree = graph.getPredecessorCount(p.node);
    setShape(p, degree);
    p.pluginStore.put(this, degree);
  }

  @Override
  public boolean apply(NodeRenderingProperty p) {
    if (!hasChanged) {
      return true;
    }
    // retrieve the degree, and apply it.
    int degree = (Integer) p.pluginStore.get(this);
    setShape(p, degree);
    return true;
  }

  @Override
  public boolean keyPressed(int keycode, char character, boolean ctrl,
      boolean alt, boolean shift) {
    return false;
  }

  @Override
  public void postFrame() {
    this.hasChanged = false;
  }

  @Override
  public void preFrame(float elapsedTime) {
  }

  public NodeShape getShapeMode() {
    return shapeMode;
  }

  public void setShapeMode(NodeShape shapeMode) {
    this.shapeMode = shapeMode;
  }

  // shapes

  public void setShapes(boolean use) {
    this.hasChanged = true;
    this.shapesEnabled = use;
  }

  public boolean toggleShapes() {
    this.shapesEnabled = !this.shapesEnabled;
    this.hasChanged = true;
    return shapesEnabled;
  }

  public boolean getShapes() {
    return shapesEnabled;
  }

}
