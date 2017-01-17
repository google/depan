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

package com.google.devtools.depan.eclipse;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class NavigatorRoot implements IAdaptable, IPersistableElement,
    IElementFactory {
  public NavigatorRoot() {
  }

  // suppress the warning for the Class, which should be parameterized.
  // We can't here: getAdapter is not declared with a parameterized Class.
  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Object getAdapter(Class adapter) {
    if (adapter == IPersistableElement.class) {
      return this;
    }
    if (adapter == IWorkbenchAdapter.class) {
      return ResourcesPlugin.getWorkspace().getRoot().getAdapter(adapter);
    }
    return null;
  }

  @Override
  public String getFactoryId() {
    return this.getClass().getCanonicalName();
  }

  @Override
  public void saveState(IMemento memento) {
    // TODO Auto-generated method stub
    return;
  }

  @Override
  public IAdaptable createElement(IMemento memento) {
    return ResourcesPlugin.getWorkspace().getRoot();
  }
}
