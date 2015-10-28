/*
 * Copyright 2007 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.devtools.depan.eclipse.trees;

import com.google.devtools.depan.eclipse.plugins.SourcePluginRegistry;
import com.google.devtools.depan.model.GraphNode;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

class NodeTreeViewAdapter<E> implements IWorkbenchAdapter {

  @SuppressWarnings("unchecked")
  private GraphNode getGraphNode(Object obj) {
    return ((NodeTreeView.NodeWrapper<E>) obj).getNode();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Object[] getChildren(Object o) {
    if (o instanceof NodeTreeView.NodeWrapper) {
      return getChildren((NodeTreeView.NodeWrapper<E>) o);
    }
    if (o instanceof NodeTreeView.NodeWrapperRoot) {
      return (((NodeTreeView.NodeWrapperRoot<E>) o).roots);
    }
    return new Object[] {};
  }

  private Object[] getChildren(NodeTreeView.NodeWrapper<E> nodeWrapper) {

    // Cache the children if we don't have them already
    if (null == nodeWrapper.childs) {
      nodeWrapper.childs = nodeWrapper.data.getChildren(nodeWrapper);
    }

    return nodeWrapper.childs;
  }

  @Override
  public ImageDescriptor getImageDescriptor(Object o) {
    if (o instanceof NodeTreeView.NodeWrapper) {
      return SourcePluginRegistry.getImageDescriptor(getGraphNode(o));
    } else {
      return null;
    }
  }

  @Override
  public String getLabel(Object o) {
    if (o instanceof NodeTreeView.NodeWrapper) {
      return getGraphNode(o).friendlyString();
    }
    if (o instanceof NodeTreeView.NodeWrapperRoot) {
      return "";
    }
    return o.toString();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Object getParent(Object o) {
    if (o instanceof NodeTreeView.NodeWrapper) {
      return getParent((NodeTreeView.NodeWrapper<E>) o);
    } else {
      return null;
    }
  }

  public NodeTreeView.NodeWrapper<E> getParent(NodeTreeView.NodeWrapper<E> n) {
    // can return null or not...
    return n.parent;
  }
}
