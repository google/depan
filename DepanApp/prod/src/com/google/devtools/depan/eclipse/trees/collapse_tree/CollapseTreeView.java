/*
 * Copyright 2014 The Depan Project Authors
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

import com.google.devtools.depan.view.CollapseData;
import com.google.devtools.depan.view.CollapseTreeModel;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import java.util.Collection;

/**
 * Provide a tree view of the graph nodes.
 *
 * @param <E> Type of data associated to each Node<Element>.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class CollapseTreeView<E> {

  public static final TreePath[] EMPTY_PATHS = new TreePath[0];

  private final CollapseTreeProvider<E> provider;

  private final TreeViewer treeViewer;

  private CollapseTreeModel treeModel;

  private TreePath[] expandState = EMPTY_PATHS;

  static {
    CollapseTreeViewAdapterFactory.register();
  }

  /**
   * Acquire {@code CollapseTreeView} instances via the factory methods
   * {@link #buildCollapseTreeView(Composite, int, CollapseTreeProvider)}.
   */
  private CollapseTreeView(
      TreeViewer treeViewer, CollapseTreeProvider<E> provider) {
    this.treeViewer = treeViewer;
    this.provider = provider;
  }

  public static <F> CollapseTreeView<F> buildCollapseTreeView(
      Composite parent, int style, CollapseTreeProvider<F> provider) {
    TreeViewer result = new TreeViewer(parent, style);
    result.setLabelProvider(new WorkbenchLabelProvider());
    result.setContentProvider(new BaseWorkbenchContentProvider());
    return new CollapseTreeView<F>(result, provider);
  }

  public TreeViewer getTreeViewer() {
    return treeViewer;
  }

  public void updateData(CollapseTreeModel collapseTreeModel) {
    treeModel = collapseTreeModel;

    updateTree();
    updateExpandState();
    //$ NEEDED??
    treeViewer.refresh();
  }

  private void updateTree() {
    Collection<CollapseData> roots = treeModel.computeRoots();
    CollapseDataWrapper<E>[] rootArray =
        buildCollapseDataWrapperArray(roots, provider, null);
    CollapseDataWrapperRoot<E> wrapper =
        new CollapseDataWrapperRoot<E>(rootArray);
    treeViewer.setInput(wrapper);
  }

  /////////////////////////////////////
  // Expansion state

  public void collapseAll() {
    treeViewer.collapseAll();
  }

  public void expandAll() {
    treeViewer.expandAll();
  }

  private void updateExpandState() {
    if (expandState.length > 0) {
      getTreeViewer().setExpandedTreePaths(expandState);
      return;
    } 

    getTreeViewer().expandToLevel(1);
    saveExpandState(getTreeViewer().getExpandedTreePaths());
  }

  public TreePath[] getExpandState() {
    return expandState;
  }

  public void saveExpandState(TreePath[] expandState) {
    this.expandState = expandState;
  }

  /////////////////////////////////////
  // Data wrapper type

  /**
   * @author ycoppel@google.com (Yohann Coppel)
   *
   * @param <E> Type of data associated to each CollapseData<Element>.
   */
  public static class CollapseDataWrapper<E> extends PlatformObject {
    private final CollapseData collapseData;
    private final CollapseTreeProvider<E> provider;
    public final CollapseDataWrapper<E> parent;

    private CollapseDataWrapper(
        CollapseData collapseData,
        CollapseTreeProvider<E> provider,
        CollapseDataWrapper<E> parent) {
      this.collapseData = collapseData;
      this.provider = provider;
      this.parent= parent;
    }

    public CollapseData getCollapseData() {
      return collapseData;
    }

    public E getContent() {
      return provider.getObject(collapseData);
    }

    public CollapseDataWrapper<E> getParent() {
      return parent;
    }

    public CollapseDataWrapper<E>[] getChildren() {
      Collection<CollapseData> childrenData =
          CollapseTreeModel.getChildrenData(collapseData);

      CollapseDataWrapper<E>[] result =
          buildCollapseDataWrapperArray(childrenData, provider, this);
      return result;
    }

    @Override
    public String toString() {
      return getCollapseData().toString();
    }
  }

  @SuppressWarnings("rawtypes")
  private static final CollapseDataWrapper[] LEAF_KIDS =
      new CollapseDataWrapper[0];

  /**
   * @param <E> Type of data associated to each Node<Element>.
   */
  public static class CollapseDataWrapperRoot<E>
      extends PlatformObject {

    private final CollapseDataWrapper<E>[] roots;

    CollapseDataWrapperRoot(CollapseDataWrapper<E>[] roots) {
      this.roots = roots;
    }

    public CollapseDataWrapper<E>[] getRoots() {
      return roots;
    }
  }

  /**
   * Create an array of wrapper objects for a collection of data instances.
   * 
   * Called from two places: within {@link #getRoots()} when constructing the
   * roots, and within {@link CollapseDataWrapper#getChildren()} when building
   * the children.
   */
  private static <F> CollapseDataWrapper<F>[] buildCollapseDataWrapperArray(
      Collection<CollapseData> collapseData,
      CollapseTreeProvider<F> provider,
      CollapseDataWrapper<F> parent) {

    // All empty children lists look the same,
    // so early exit with the singleton
    if (0 == collapseData.size()) {
      return LEAF_KIDS;
    }

    @SuppressWarnings("unchecked")
    CollapseDataWrapper<F>[] result
        = new CollapseDataWrapper[collapseData.size()];
    int index = 0;
    for (CollapseData node : collapseData) {
      CollapseDataWrapper<F> nodeWrapper =
          new CollapseDataWrapper<F>(node, provider, parent);
      result[index] = nodeWrapper;
      index++;
    }
    return result;
  }
}
