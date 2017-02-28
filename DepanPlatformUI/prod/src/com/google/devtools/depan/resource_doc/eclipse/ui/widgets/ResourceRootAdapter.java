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

import com.google.devtools.depan.resources.ResourceContainer;

import com.google.common.collect.Lists;

import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

import java.util.List;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
class ResourceRootAdapter implements IWorkbenchAdapter {

  @Override
  public Object[] getChildren(Object element) {
    if (element instanceof ResourceRoot) {
      return prepareInput((ResourceRoot) element);
    }
    return new Object[] {};
  }

  private Object[] prepareInput(ResourceRoot element) {
    List<Object> result = Lists.newArrayListWithExpectedSize(2);
    ResourceContainer container = element.getContainer();
    if (null != container) {
      result.add(container);
    }
    IContainer folder = element.getFolder();
    if (null != folder) {
      result.add(folder);
    }
    return result.toArray();
  }

  @Override
  public ImageDescriptor getImageDescriptor(Object element) {
    return null;
  }

  @Override
  public String getLabel(Object element) {
    if (element instanceof ResourceRoot) {
      return ((ResourceRoot) element).getLabel();
    }
    return element.toString();
  }

  @Override
  public Object getParent(Object element) {
    return null;
  }
}
