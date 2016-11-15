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

package com.google.devtools.depan.eclipse.ui.collapse.trees;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * Adapt {@link CollapseTreeRoot}s for tree display.
 */
class CollapseTreeRootAdapter<T> implements IWorkbenchAdapter {

  @SuppressWarnings("unchecked")
  @Override
  public Object[] getChildren(Object o) {
    if (o instanceof CollapseTreeRoot) {
      return (((CollapseTreeRoot<T>) o).getChildren());
    }
    return new Object[] {};
  }

  @Override
  public ImageDescriptor getImageDescriptor(Object o) {
    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public String getLabel(Object o) {
    if (o instanceof CollapseTreeRoot) {
      return (((CollapseTreeRoot<T>) o).getLabel());
    }
    return o.toString();
  }

  @Override
  public Object getParent(Object o) {
    return null;
  }
}
