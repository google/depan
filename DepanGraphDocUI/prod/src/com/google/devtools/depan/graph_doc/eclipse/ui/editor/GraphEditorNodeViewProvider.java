/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.graph_doc.eclipse.ui.editor;

import com.google.devtools.depan.eclipse.ui.nodes.trees.GraphData;
import com.google.devtools.depan.eclipse.ui.nodes.trees.ViewerRoot;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeViewerProvider;
import com.google.devtools.depan.model.GraphNode;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * {@link NodeViewerProvider} for {@link GraphData} packaged information.
 * The {{@link #buildViewerRoots()} method returns the 
 * {@link GraphData#getHierarchyRoots()} results for the constructor
 * supplied {@link GraphData}.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class GraphEditorNodeViewProvider<T> implements NodeViewerProvider {

  private final GraphData<GraphNode> graphData;

  public GraphEditorNodeViewProvider(GraphData<GraphNode> graphData) {
    this.graphData = graphData;
  }

  @Override
  public void addMultiActions(IMenuManager manager) {
  }

  @Override
  public void addItemActions(IMenuManager manager, Object menuElement) {
  }

  @Override
  public ViewerRoot buildViewerRoots() {
    return graphData.getHierarchyRoots();
  }

  @Override
  public void updateExpandState(TreeViewer viewer) {
    TreePath[] expandState = graphData.getExpandState();
    if (expandState.length > 0) {
      viewer.setExpandedTreePaths(expandState);
      return;
    }
    if (graphData.countTreeNodes() < NodeViewerProvider.AUTO_EXPAND_LIMIT) {
      viewer.expandAll();
    }
    else {
      viewer.expandToLevel(1);
    }
    graphData.saveExpandState(viewer.getExpandedTreePaths());
  }

  @Override
  public Object findNodeObject(GraphNode node) {
    return graphData.getNodeWrapper(node);
  }
}
