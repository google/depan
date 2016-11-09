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

package com.google.devtools.depan.eclipse.ui.nodes.viewers;

import com.google.devtools.depan.eclipse.ui.nodes.trees.GraphData;
import com.google.devtools.depan.eclipse.ui.nodes.trees.SolitaryRoot;
import com.google.devtools.depan.eclipse.ui.nodes.trees.ViewerRoot;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.nodes.trees.TreeModel;

import com.google.common.collect.Lists;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeViewer;

import java.util.Collection;
import java.util.List;

/**
 * {@link NodeViewerProvider} for node lists (non-hierarchical).
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 * @param <E>
 */
public class NodeListViewProvider<T> implements NodeViewerProvider {

  private final String listLabel;

  // Displayed content
  private final Collection<GraphNode> nodes;

  private NodeTreeProvider<T> provider;

  private GraphData<T> solo;

  public NodeListViewProvider(String listLabel, Collection<GraphNode> nodes) {
    this.listLabel = listLabel;
    this.nodes = nodes;
  }

  public void setProvider(NodeTreeProvider<T> provider) {
    this.provider = provider;
  }

  @Override
  public void addMultiActions(IMenuManager manager) {
  }

  @Override
  public void addItemActions(IMenuManager manager, Object menuElement) {
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public PlatformObject buildViewerRoots() {
    List<PlatformObject> result = Lists.newArrayList();

    TreeModel.Flat model = new TreeModel.Flat(nodes);
    solo = new GraphData(provider, model);
    result.add(new SolitaryRoot(solo, listLabel));
    return new ViewerRoot(result.toArray());
  }

  @Override
  public void updateExpandState(TreeViewer viewer) {
    if (nodes.size() < NodeViewerProvider.AUTO_EXPAND_LIMIT) {
      viewer.expandAll();
    }
  }

  @Override
  public Object findNodeObject(GraphNode node) {
    return solo.getNodeWrapper(node);
  }
}
