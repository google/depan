/*
 * Copyright 2017 The Depan Project Authors
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

package com.google.devtools.depan.resource_doc.eclipse.ui.widgets;

import com.google.devtools.depan.platform.PlatformResources;
import com.google.devtools.depan.resources.ResourceContainer;

import com.google.common.collect.Lists;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

import java.util.Collection;
import java.util.List;

/**
 * Adapts a {@link ResourceContainer()} to a {@link IWorkbenchAdapter}.
 * 
 * Orders children as other containers first, then documents.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
class ResourceContainerAdapter implements IWorkbenchAdapter {

  @Override
  public Object[] getChildren(Object element) {
    if (element instanceof ResourceContainer) {
      return buildDescendants((ResourceContainer) element);
    }
    return new Object[] {};
  }

  @Override
  public ImageDescriptor getImageDescriptor(Object element) {
    if (element instanceof ResourceContainer) {
      return PlatformResources.IMAGE_DESC_LIBRARY_OBJ;
    }
    return null;
  }

  @Override
  public String getLabel(Object element) {
    if (element instanceof ResourceContainer) {
      return ((ResourceContainer) element).getLabel();
    }
    return element.toString();
  }

  @Override
  public Object getParent(Object element) {
    if (element instanceof ResourceContainer) {
      return ((ResourceContainer) element).getParent();
    }
    return null;
  }

  /**
   * Build the list of descendants from both children and resources.
   */
  private Object[] buildDescendants(ResourceContainer rsrcCntr) {
    Collection<ResourceContainer> children = rsrcCntr.getChildren();
    Collection<Object> resources = rsrcCntr.getResources();
    int size = children.size() + resources.size();
    List<Object> result = Lists.newArrayListWithExpectedSize(size);
    result.addAll(children);
    result.addAll(resources);
    return result.toArray();
  }
}
