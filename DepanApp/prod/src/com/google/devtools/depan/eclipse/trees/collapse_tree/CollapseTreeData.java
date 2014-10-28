/*
 * Copyright 2014 Pnambic Computing.
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

package com.google.devtools.depan.eclipse.trees.collapse_tree;

import com.google.devtools.depan.eclipse.trees.collapse_tree.CollapseTreeView.CollapseDataWrapper;
import com.google.devtools.depan.eclipse.trees.collapse_tree.CollapseTreeView.CollapseDataWrapperRoot;
import com.google.devtools.depan.view.CollapseData;
import com.google.devtools.depan.view.CollapseTreeModel;

import com.google.common.collect.Maps;

import org.eclipse.jface.viewers.TreePath;

import java.util.Collection;
import java.util.Map;

/**
 * Provide child and root information for a hierarchical set of relations.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 *
 * @param <F> Type for node wrapper objects
 */
public class CollapseTreeData<F> {

  public static final TreePath[] EMPTY_PATHS =
    new TreePath[0];

  @SuppressWarnings("rawtypes")
  private static final CollapseDataWrapper[] LEAF_KIDS =
      new CollapseDataWrapper[0];

  private final CollapseTreeProvider<F> provider;

  /** Generated hierarchical view */
  private CollapseTreeModel treeData;

  private Map<CollapseData, CollapseDataWrapper<F>> collapseMap =
      Maps.newHashMap();

  private TreePath[] expandState = EMPTY_PATHS;

  /**
   * Comprehensive constructor for GraphData.
   * 
   * @param graph table of relationship data
   * @param relationFinder relations to include in children
   * @param provider source for rendering information
   */
  public CollapseTreeData(
      CollapseTreeProvider<F> provider, CollapseTreeModel treeData) {
    this.provider = provider;
    this.treeData = treeData;

    CollapseTreeViewAdapter<F> adapter =
        new CollapseTreeViewAdapter<F>(this);
    CollapseTreeViewAdapterFactory.register(adapter);
  }

  public CollapseDataWrapper<F> getCollapseDataWrapper(
      CollapseData collapseData) {
    CollapseDataWrapper<F> result = collapseMap.get(collapseData);
    return result ;
  }

  /**
   * Provide the children of a parent node.
   * 
   * @param parent Parent node for children
   * @return Array of children NodeWrappers.
   */
  public CollapseDataWrapper<F>[] getChildren(
      CollapseDataWrapper<F> parent) {

    Collection<CollapseData> childrenData = CollapseTreeModel.getChildrenData(parent.getCollapseData());
    CollapseDataWrapper<F>[] result = buildCollapseDataWrapperArray(childrenData, parent);
    return result;
  }

  /**
   * Compute the roots for the relationship for this graph.
   * This should only be called once - lazily, or by the constructor.
   * 
   * @return node wrapper with all roots
   */
  public CollapseDataWrapperRoot<F> computeRoots() {
    Collection<CollapseData> roots = treeData.computeRoots();

    CollapseDataWrapper<F>[] rootArray = buildCollapseDataWrapperArray(roots, null);
    CollapseDataWrapperRoot<F> wrapper = new CollapseDataWrapperRoot<F>(rootArray);
    return wrapper;
  }

  @SuppressWarnings("unchecked")
  private CollapseDataWrapper<F>[] buildCollapseDataWrapperArray(
      Collection<CollapseData> collapseData,
      CollapseDataWrapper<F> parent) {

    // All empty children lists look the same,
    // so early exit with the singleton
    if (0 == collapseData.size()) {
      return LEAF_KIDS;
    }

    CollapseDataWrapper<F>[] children
        = new CollapseDataWrapper[collapseData.size()];
    int index = 0;
    for (CollapseData node : collapseData) {
      CollapseDataWrapper<F> nodeWrapper = buildWrapper(node, parent);
      children[index] = nodeWrapper;
      index++;
    }
    return children;
  }

  private CollapseDataWrapper<F> buildWrapper(
      CollapseData collapseData, CollapseDataWrapper<F> parent) {
    F content = provider.getObject(collapseData);
    CollapseTreeView.CollapseDataWrapper<F> result
        = new CollapseTreeView.CollapseDataWrapper<F>(
            collapseData, content, parent);
    collapseMap.put(collapseData, result);
     return result;
    
  }

  public TreePath[] getExpandState() {
    return expandState;
  }

  public void saveExpandState(TreePath[] expandState) {
    this.expandState = expandState;
  }
}
