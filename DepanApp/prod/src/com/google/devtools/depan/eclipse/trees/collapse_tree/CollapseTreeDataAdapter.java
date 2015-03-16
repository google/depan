/*
 * Copyright 2014 The Depan Project Authors
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
package com.google.devtools.depan.eclipse.trees.collapse_tree;

import com.google.devtools.depan.eclipse.plugins.SourcePluginRegistry;
import com.google.devtools.depan.eclipse.trees.collapse_tree.CollapseTreeView.CollapseDataWrapper;
import com.google.devtools.depan.view.CollapseData;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * Adapt {@link CollapseDataWrapper}s for tree display.
 */
class CollapseTreeDataAdapter<E> implements IWorkbenchAdapter {

  @SuppressWarnings("unchecked")
  @Override
  public Object[] getChildren(Object o) {
    if (o instanceof CollapseDataWrapper) {
      return ((CollapseDataWrapper<E>) o).getChildren();
    }
    return new Object[] {};
  }

  @SuppressWarnings("unchecked")
  @Override
  public ImageDescriptor getImageDescriptor(Object o) {
    if (o instanceof CollapseDataWrapper) {
      CollapseData collapseData = ((CollapseDataWrapper<E>) o).getCollapseData();
      return SourcePluginRegistry.getImageDescriptor(
          collapseData.getMasterNode());
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public String getLabel(Object o) {
    if (o instanceof CollapseDataWrapper) {
      return getLabel((CollapseDataWrapper<E>) o);
    }
    return o.toString();
  }

  private String getLabel(CollapseDataWrapper<E> wrapper) {
    CollapseData data = wrapper.getCollapseData();
    return data.getMasterNode().friendlyString();
  }

  @Override
  public Object getParent(Object o) {
    if (o instanceof CollapseDataWrapper<?>) {
      return ((CollapseDataWrapper<?>) o).getParent();
    }

    return null;
  }
}
