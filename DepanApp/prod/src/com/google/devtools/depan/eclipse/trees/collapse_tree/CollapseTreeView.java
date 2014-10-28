/*
 * Copyright 2014 Pnambic Computing.
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

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * Provide a tree view of the graph nodes.
 *
 * @param <E> Type of data associated to each Node<Element>.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class CollapseTreeView<E> {

  /**
   * Define a generous limit for the number of tree elements to automatically
   * open for display.  A more refined implementation might offer a user
   * configured choice, but this prevents the most egregious problems from
   * over-zealous expansion of tree elements.
   * 
   * Approx 8000 nodes takes ~8sec to expand and display.  So a limit of 1000
   * should keep the initial open time down to ~1sec.
   */
  public static final int AUTO_EXPAND_LIMIT = 1000;

  private TreeViewer tree;
  private CollapseTreeData<E> data;

  public CollapseTreeView(Composite parent, int style) {
    initWidget(parent, style);
  }

  protected void initWidget(Composite parent, int style) {
    tree = new TreeViewer(parent, style);
    tree.setLabelProvider(new WorkbenchLabelProvider());
    tree.setContentProvider(new BaseWorkbenchContentProvider());
  }

  public void updateData(CollapseTreeData<E> fresh) {
    data = fresh;

    updateTree();
    updateExpandState();
    //$ NEEDED??
    tree.refresh();
  }

  private void updateTree() {
    CollapseDataWrapperRoot<E> roots = data.computeRoots();
    tree.setInput(roots);
  }

  public void collapseAll() {
    tree.collapseAll();
  }

  public void expandAll() {
    tree.expandAll();
  }

  private void updateExpandState() {
    TreePath[] expandState = data.getExpandState();
    if (expandState.length > 0) {
      getTreeViewer().setExpandedTreePaths(expandState);
      return;
    } else {
      getTreeViewer().expandToLevel(1);
    }
    data.saveExpandState(getTreeViewer().getExpandedTreePaths());
  }

  public TreeViewer getTreeViewer() {
    return tree;
  }

  /**
   * Gives the NodeWrapper containing the given node. Useful for update
   * methods when we just have a node, but need the object actually contained in
   * a tree for example.
   *
   * @param node the node
   * @return the NodeWrapper<F> containing the given node.
   */
  public CollapseDataWrapper<E> getCollapseDataWrapper(
      CollapseData collapseData) {
    return data.getCollapseDataWrapper(collapseData);
  }

  /**
   * @author ycoppel@google.com (Yohann Coppel)
   *
   * @param <E> Type of data associated to each Node<Element>.
   */
  public static class CollapseDataWrapper<E> extends PlatformObject {
    private final CollapseData collapseData;
    private final E content;
    public final CollapseDataWrapper<E> parent;

    public CollapseDataWrapper(
        CollapseData collapseData,
        E content,
        CollapseDataWrapper<E> parent) {
      this.collapseData = collapseData;
      this.content = content;
      this.parent= parent;
    }

    public CollapseData getCollapseData() {
      return collapseData;
    }

    public E getContent() {
      return content;
    }

    public CollapseDataWrapper<E> getParent() {
      return parent;
    }

    @Override
    public String toString() {
      return getCollapseData().toString();
    }
  }

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
}
