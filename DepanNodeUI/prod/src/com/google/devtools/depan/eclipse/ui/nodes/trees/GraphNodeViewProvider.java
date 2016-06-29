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

package com.google.devtools.depan.eclipse.ui.nodes.trees;

import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeTreeProvider;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeViewerProvider;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.nodes.trees.TreeModel;

import com.google.common.collect.Lists;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeViewer;

import java.util.List;
import java.util.Set;

/**
 * Basic {@link NodeViewerProvider} for routine graph elements.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 * @param <E>
 */
public class GraphNodeViewProvider<T, E> implements NodeViewerProvider {

  /** Category for any unclassified nodes */
  public static final String SOLITARY_NODES = "Solitary Nodes";

  // Displayed content
  private GraphModel graph;

  private NodeTreeProvider<T> provider;

  private List<TreeDescr<E>> trees = Lists.newArrayList();

  @Override
  public void addMultiActions(IMenuManager manager) {
  }

  @Override
  public void addItemActions(IMenuManager manager, Object menuElement) {
    manager.add(new Action("push up", IAction.AS_PUSH_BUTTON) {
    });
    manager.add(new Action("push down", IAction.AS_PUSH_BUTTON) {
    });
    manager.add(new Action("uncollapse", IAction.AS_PUSH_BUTTON) {
    });
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public ViewerRoot buildViewerRoots() {
    List<Object> result = Lists.newArrayList();
    Set<GraphNode> solitaries = graph.getNodesSet();

    for (TreeDescr tree : trees) {
      result.add(new HierarchyRoot(tree));
      solitaries.removeAll(tree.computeNodes());
    }

    TreeModel.Flat model = new TreeModel.Flat(solitaries);
    GraphData solo = new GraphData(provider, model);
    result.add(new SolitaryRoot(solo, SOLITARY_NODES));
    return new ViewerRoot(result.toArray());
  }

  @Override
  public void updateExpandState(TreeViewer viewer) {
  }
}
