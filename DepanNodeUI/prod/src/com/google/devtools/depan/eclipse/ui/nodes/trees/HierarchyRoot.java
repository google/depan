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

import com.google.devtools.depan.nodes.trees.TreeModel;

import org.eclipse.core.runtime.PlatformObject;

/**
 * Define the root of each node hierarchy.
 * 
 * Name is typically the EdgeSet that was used to create
 * the {@link TreeModel}.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 * @param <E>
 */
public class HierarchyRoot<E> extends PlatformObject {

  private final TreeDescr<E> tree;

  private Object[] roots;

  public HierarchyRoot(TreeDescr<E> tree) {
    this.tree = tree;
  }

  public String getName() {
    return tree.builderInfo.getName();
  }

  public Object[] getChildren() {
    if (null == roots) {
      roots = new Object[] {tree.treeInfo.getHierarchyRoots()};
    }
    return roots;
  }
}