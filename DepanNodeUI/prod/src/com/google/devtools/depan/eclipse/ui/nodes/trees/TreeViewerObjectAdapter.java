/*
 * Copyright 2015 The Depan Project Authors
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

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * Delegates most requests to matching methods in the supplied
 * {@link TreeViewerObject}}.  The {@link #getImageDescriptor(Object)}
 * needs to be implemented for derived types.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class TreeViewerObjectAdapter implements IWorkbenchAdapter {
  private static final Object[] NO_CHILDREN = new Object[] {};


  @Override
  public String getLabel(Object o) {
    if (o instanceof TreeViewerObject) {
      return getLabel((TreeViewerObject) o);
    }

    return o.toString();
  }

  @Override
  public Object getParent(Object o) {
    if (o instanceof TreeViewerObject) {
      return getParent((TreeViewerObject) o);
    }

    return null;
  }

  @Override
  public Object[] getChildren(Object o) {
    if (o instanceof TreeViewerObject) {
      return getChildren((TreeViewerObject) o);
    }
    return NO_CHILDREN;
  }

  @Override
  public ImageDescriptor getImageDescriptor(Object o) {
    if (o instanceof TreeViewerObject) {
      return getImageDescriptor((TreeViewerObject) o);
    }
    return null;
  }

  /////////////////////////////////////
  // Type clean accessors

  private PlatformObject getParent(TreeViewerObject root) {
    return root.getParent();
  }

  private String getLabel(TreeViewerObject root) {
    return root.getName();
  }

  private Object[] getChildren(TreeViewerObject root) {
    return root.getChildren();
  }

  private ImageDescriptor getImageDescriptor(TreeViewerObject root) {
    return root.getImageDescriptor();
  }
}