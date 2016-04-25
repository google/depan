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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

class NodeWrapperRootAdapter<E> implements IWorkbenchAdapter {

  @Override
  @SuppressWarnings("unchecked")
  public Object[] getChildren(Object o) {
    if (o instanceof NodeWrapperRoot) {
      return (((NodeWrapperRoot<E>) o).getChildren());
    }
    return new Object[] {};
  }

  @Override
  public ImageDescriptor getImageDescriptor(Object o) {
    return null;
  }

  @Override
  public String getLabel(Object o) {
    if (o instanceof NodeWrapperRoot) {
      return "";
    }
    return o.toString();
  }

  @Override
  public Object getParent(Object o) {
    return null;
  }
}
