/*
 * Copyright 2016 The Depan Project Authors
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

import com.google.devtools.depan.resources.ResourceContainer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * [Aug-2016] Based on {@code NodeWrapperAdapter}.
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
      // TODO: Support plugin contributed resource types
      return null;
    }
    return null;
  }

  @Override
  public String getLabel(Object element) {
    if (element instanceof ResourceContainer) {
      ((ResourceContainer) element).getLabel();
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
    Object[] result = new Object[children.size() + resources.size()];
    List<Object> builder = Arrays.asList(result);
    builder.addAll(children);
    builder.addAll(resources);
    return result;
  }
}
