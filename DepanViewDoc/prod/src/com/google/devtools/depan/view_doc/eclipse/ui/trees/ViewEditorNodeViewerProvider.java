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

package com.google.devtools.depan.view_doc.eclipse.ui.trees;

import com.google.devtools.depan.eclipse.ui.collapse.trees.CollapseDataWrapper;
import com.google.devtools.depan.eclipse.ui.nodes.trees.ViewerRoot;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeViewerProvider;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * {@link NodeViewerProvider} for the {@link ViewEditor}.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class ViewEditorNodeViewerProvider implements NodeViewerProvider {

  private final ViewEditor editor;

  public ViewEditorNodeViewerProvider(ViewEditor editor) {
    this.editor = editor;
  }

  @Override
  public void addMultiActions(IMenuManager manager) {
    // if (menuElement instanceof ActionableViewerObject) {
    //   ((ActionableViewerObject) menuElement).addMultiActions(manager, editor);
    // }
  }

  @Override
  public void addItemActions(IMenuManager manager, Object menuElement) {
    if (menuElement instanceof ActionableViewerObject) {
      ((ActionableViewerObject) menuElement).addItemActions(manager, editor);
    }

    if (menuElement instanceof CollapseDataWrapper<?>) {
      CollapseDataWrapper<?> root = (CollapseDataWrapper<?>) menuElement;
      final GraphNode master = root.getCollapseData().getMasterNode();
      manager.add(new Action("Uncollapse", IAction.AS_PUSH_BUTTON) {
        @Override
        public void run() {
          editor.uncollapseMasterNode(master);
        }
      });
    }
  }

  @Override
  public ViewerRoot buildViewerRoots() {
    return editor.buildViewerRoot();
  }

  @Override
  public Object findNodeObject(GraphNode node) {
    return editor.findViewerNodeObject(node);
  }

  @Override
  public void updateExpandState(TreeViewer viewer) {
    int nodeCnt = editor.getViewGraph().getNodes().size();
    if (nodeCnt < NodeViewerProvider.AUTO_EXPAND_LIMIT) {
      viewer.expandAll();
    } else {
      viewer.expandToLevel(1);
    }
  }
}
