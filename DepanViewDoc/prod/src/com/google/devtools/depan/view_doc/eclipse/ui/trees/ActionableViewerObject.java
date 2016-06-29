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

import com.google.devtools.depan.eclipse.ui.nodes.trees.GraphData;
import com.google.devtools.depan.eclipse.ui.nodes.trees.SolitaryRoot;
import com.google.devtools.depan.eclipse.ui.nodes.trees.TreeViewerObject;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.action.IMenuManager;

/**
 * Implemented by {@link org.eclipse.core.runtime.PlatformObject}s that provide specific command sets.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public interface ActionableViewerObject {

  void addMultiActions(IMenuManager manager, ViewEditor editor);

  public void addItemActions(IMenuManager manager, ViewEditor editor);

  /////////////////////////////////////
  // Action-ized TreeeViewerObject

  public static abstract class ActionViewerObject
      extends TreeViewerObject
      implements ActionableViewerObject {

    public ActionViewerObject(String name, PlatformObject[] children) {
      super(name, children);
    }

    public ActionViewerObject(
        String name, PlatformObject parent, PlatformObject[] children) {
      super(name, parent, children);
    }
  }

  /////////////////////////////////////
  // Action-ized TreeeViewerObject

  public static abstract class ActionSolitaryRoot
      extends SolitaryRoot<GraphNode>
      implements ActionableViewerObject {

    public ActionSolitaryRoot(GraphData<GraphNode> nodes, String label) {
      super(nodes, label);
    }
  }
}
