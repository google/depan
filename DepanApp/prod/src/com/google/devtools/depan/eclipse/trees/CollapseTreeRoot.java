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

package com.google.devtools.depan.eclipse.trees;

import com.google.devtools.depan.view.CollapseData;
import com.google.devtools.depan.view.CollapseTreeModel;

import org.eclipse.core.runtime.PlatformObject;

import java.util.Collection;

/**
 * @param <E> Type of data associated to each Node<Element>.
 */
public class CollapseTreeRoot<E>
    extends PlatformObject {

  private final CollapseDataWrapper<E>[] roots;
  private final String label;

  CollapseTreeRoot(CollapseDataWrapper<E>[] roots, String label) {
    this.roots = roots;
    this.label = label;
  }

  public CollapseDataWrapper<E>[] getRoots() {
    return roots;
  }

  public String getLabel() {
    return label;
  }

  public static <E> CollapseTreeRoot<E> build(
      CollapseTreeModel treeModel,
      NodeTreeProvider<E> provider,
      String label) {
    Collection<CollapseData> roots = treeModel.computeRoots();
    CollapseDataWrapper<E>[] rootArray =
        CollapseDataWrapper.buildCollapseDataWrapperArray(roots, provider, null);
    return new CollapseTreeRoot<E>(rootArray, label);
  }
}
