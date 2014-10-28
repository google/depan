/*
 * Copyright 2014 Google Inc.
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

import com.google.devtools.depan.eclipse.trees.collapse_tree.CollapseTreeView.CollapseDataWrapper;
import com.google.devtools.depan.eclipse.trees.collapse_tree.CollapseTreeView.CollapseDataWrapperRoot;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 * 
 * Cloned from NovdeViewAdapterFactory
 * @author leeca@pnambic.com
 *
 * @param <E> Type of data associated to each Node<Element>.
 */
public class CollapseTreeViewAdapterFactory<E> implements IAdapterFactory {

  private final CollapseTreeViewAdapter<E> treeAdapter;

  public CollapseTreeViewAdapterFactory(CollapseTreeViewAdapter<E> treeAdapter) {
    this.treeAdapter = treeAdapter;
    
  }

  // NodeViewAdapterFactory should be parameterized, but cannot make static
  // reference to the non-static type E
  @SuppressWarnings("unchecked")
  private static CollapseTreeViewAdapterFactory instance = null;

  // suppressWarning, because getAdapter have a Class as parameter, but
  // Class should be parameterized. To update if the IAdapterFactory is updated.
  @SuppressWarnings("unchecked")
  public Object getAdapter(Object adaptableObject, Class adapterType) {
    if (adapterType != IWorkbenchAdapter.class) {
      return null;
    }
    if (adaptableObject instanceof CollapseDataWrapper
        || adaptableObject instanceof CollapseDataWrapperRoot) {
      return treeAdapter;
    }
    return null;
  }

  public Class<?>[] getAdapterList() {
    return new Class[] {IWorkbenchAdapter.class};
  }

  protected static <E> void register(CollapseTreeViewAdapter<E> treeAdapter) {
    if (null == instance) {
      instance = new CollapseTreeViewAdapterFactory<E>(treeAdapter);
    }
    Platform.getAdapterManager()
        .registerAdapters(instance, CollapseDataWrapper.class);
    Platform.getAdapterManager()
        .registerAdapters(instance, CollapseDataWrapperRoot.class);
  }
}
