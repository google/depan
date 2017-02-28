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

import com.google.devtools.depan.resources.PropertyDocument;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
class PropertyDocumentAdapter implements IWorkbenchAdapter {

  @Override
  public Object[] getChildren(Object element) {
    return new Object[] {};
  }

  @Override
  public ImageDescriptor getImageDescriptor(Object element) {
    if (element instanceof PropertyDocument) {
      // TODO: Support plugin contributed resource types
      return null;
    }
    return null;
  }

  @Override
  public String getLabel(Object element) {
    if (element instanceof PropertyDocument) {
      return ((PropertyDocument<?>) element).getName();
    }
    return element.toString();
  }

  @Override
  public Object getParent(Object element) {
    return null;
  }
}
