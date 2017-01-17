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

package com.google.devtools.depan.eclipse.ui.collapse.trees;

import com.google.devtools.depan.platform.TypeAdapter;

import com.google.common.collect.Lists;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

import java.util.List;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 * @param <E> Type of data associated to each Node<Element>.
 */
public class CollapserAdapterFactory<E> implements IAdapterFactory {

  private static List<TypeAdapter> knownAdapters = buildKnownAdapters();

  /**
   * Build the list of know adapter types at static initialization time.
   * Registration with platform is deferred until an instance can be created.
   */
  private static <E> List<TypeAdapter> buildKnownAdapters() {
    List<TypeAdapter> result = Lists.newArrayList();
    result.add(new TypeAdapter(
        CollapseDataWrapper.class, new CollapseDataWrapperAdapter<E>()));
    result.add(new TypeAdapter(
        CollapseTreeRoot.class, new CollapseTreeRootAdapter<E>()));
    return result;
  }

  // suppressWarning, because getAdapter have a Class as parameter, but
  // Class should be parameterized. To update if the IAdapterFactory is updated.
  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Object getAdapter(Object adaptableObject, Class adapterType) {
    if (adapterType != IWorkbenchAdapter.class) {
      return null;
    }

    for (TypeAdapter asso : knownAdapters) {
      IWorkbenchAdapter result = asso.getAdapter(adaptableObject);
      if (null != result) {
        return result;
      }
    }
    return null;
  }

  @Override
  public Class<?>[] getAdapterList() {
    return new Class[] {IWorkbenchAdapter.class};
  }
}
