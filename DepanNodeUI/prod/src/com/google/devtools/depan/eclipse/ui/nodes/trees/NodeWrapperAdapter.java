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

package com.google.devtools.depan.eclipse.ui.nodes.trees;

import com.google.devtools.depan.eclipse.ui.nodes.plugins.NodeElementPluginRegistry;
import com.google.devtools.depan.model.GraphNode;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

class NodeWrapperAdapter<E> implements IWorkbenchAdapter {

  @SuppressWarnings("unchecked")
  private GraphNode getGraphNode(Object obj) {
    return ((NodeWrapper<E>) obj).getNode();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Object[] getChildren(Object o) {
    if (o instanceof NodeWrapper) {
      return getChildren((NodeWrapper<E>) o);
    }
    return new Object[] {};
  }

  private Object[] getChildren(NodeWrapper<E> nodeWrapper) {
    return nodeWrapper.getChildren();
  }

  @Override
  public ImageDescriptor getImageDescriptor(Object o) {
    if (o instanceof NodeWrapper) {
      return NodeElementPluginRegistry.getImageDescriptor(getGraphNode(o));
    }
    return null;
  }

  @Override
  public String getLabel(Object o) {
    if (o instanceof NodeWrapper) {
      return getGraphNode(o).friendlyString();
    }
    return o.toString();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Object getParent(Object o) {
    if (o instanceof NodeWrapper) {
      return getParent((NodeWrapper<E>) o);
    }
    return null;
  }

  public NodeWrapper<E> getParent(NodeWrapper<E> wrapper) {
    return wrapper.getParent();
  }
}
