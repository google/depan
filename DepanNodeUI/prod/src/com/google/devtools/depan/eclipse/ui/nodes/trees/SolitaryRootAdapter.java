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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class SolitaryRootAdapter implements IWorkbenchAdapter {
  private static final Object[] NO_CHILDREN = new Object[0];

  @Override
  public Object[] getChildren(Object o) {
    if (o instanceof SolitaryRoot) {
      return ((SolitaryRoot<?>) o).getChildren();
    }
    return NO_CHILDREN;
  }

  @Override
  public ImageDescriptor getImageDescriptor(Object object) {
    return null;
  }

  @Override
  public String getLabel(Object o) {
    if (o instanceof SolitaryRoot) {
      return ((SolitaryRoot<?>) o).getLabel();
    }

    return o.toString();
  }

  @Override
  public Object getParent(Object o) {
    return null;
  }
}