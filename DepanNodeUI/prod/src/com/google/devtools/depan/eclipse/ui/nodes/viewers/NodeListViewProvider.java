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
import com.google.devtools.depan.eclipse.ui.nodes.trees.TreeDescr;
import com.google.devtools.depan.eclipse.ui.nodes.trees.ViewerRoot;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeSorter;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeTreeProvider;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeViewerProvider;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.nodes.trees.TreeModel;

import com.google.common.collect.Lists;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ViewerSorter;

import java.util.Collection;
import java.util.List;
import java.util.Set;

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

  public NodeListViewProvider(String listLabel, Collection<GraphNode> nodes) {
    this.listLabel = listLabel;
    this.nodes = nodes;
  }

  public void setProvider(NodeTreeProvider<T> provider) {
    this.provider = provider;
  }

  @Override
  public ViewerSorter getViewSorter() {
    return new NodeSorter();
  }

  @Override
  public void addMultiActions(IMenuManager manager) {
  }

  @Override
  public void addItemActions(IMenuManager manager, Object menuElement) {
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public ViewerRoot buildViewerRoots() {
    List<Object> result = Lists.newArrayList();

    TreeModel.Flat model = new TreeModel.Flat(nodes);
    GraphData solo = new GraphData(provider, model);
    result.add(new SolitaryRoot(solo, listLabel));
    return new ViewerRoot(result.toArray());
  }
}
