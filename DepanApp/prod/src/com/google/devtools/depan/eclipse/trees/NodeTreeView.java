/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.eclipse.trees;

import com.google.devtools.depan.eclipse.utils.RelationshipSelectorListener;
import com.google.devtools.depan.graph.api.DirectedRelationFinder;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.RelationshipSet;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import java.util.Calendar;

/**
 * Provide a tree view of the graph nodes.
 *
 * @param <E> Type of data associated to each Node<Element>.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class NodeTreeView<E>
    implements RelationshipSelectorListener{

  protected TreeViewer tree;
  private GraphData<E> data;

  static {
    NodeViewAdapterFactory.register();
  }

  public NodeTreeView(
      Composite parent, int style, NodeTreeProvider<E> provider) {
    this.data = new GraphData<E>(provider);
    initWidget(parent, style);
  }

  protected void initWidget(Composite parent, int style) {
    tree = new TreeViewer(parent, style);
    tree.setLabelProvider(new WorkbenchLabelProvider());
    tree.setContentProvider(new BaseWorkbenchContentProvider());
  }

  public NodeWrapperRoot<E> init(
      GraphModel graph, DirectedRelationFinder relationFinder) {
    data.initTreeData(graph, relationFinder);
    System.out.println("Compute roots...");
    NodeWrapperRoot<E> roots = computeRoots();
    System.out.println("  DONE");
    System.out.println("Set Input...");
    long l = Calendar.getInstance().getTimeInMillis();
    tree.setInput(roots);
    long l2 = Calendar.getInstance().getTimeInMillis() - l;
    System.out.println("  DONE " + (l2 / 1000.0));
    return roots;
  }

  public void reset(
      NodeWrapperRoot<E> root,
      GraphModel graph, DirectedRelationFinder relationFinder) {
    data.initTreeData(graph, relationFinder);
    tree.setInput(root);
  }

  public TreeViewer getTreeViewer() {
    return tree;
  }

  private NodeWrapperRoot<E> computeRoots() {
    return data.computeRoots();
  }

  public void setInput(NodeWrapperRoot<E> input) {
    tree.setInput(input);
    tree.refresh();
  }

  /**
   * Gives the NodeWrapper containing the given node. Useful for update
   * methods when we just have a node, but need the object actually contained in
   * a tree for example.
   *
   * @param node the node
   * @return the NodeWrapper<F> containing the given node.
   */
  public NodeWrapper<E> getNodeWrapper(GraphNode node) {
    return data.getNodeWrapper(node);
  }

  /**
   * @author ycoppel@google.com (Yohann Coppel)
   *
   * @param <E> Type of data associated to each Node<Element>.
   */
  public static class NodeWrapper<E> extends PlatformObject {
    private final GraphNode node;
    private final E content;
    public NodeWrapper<E>[] childs;
    public final NodeWrapper<E> parent;
    public final GraphData<E> data;

    public NodeWrapper(
        GraphNode node,
        E content,
        NodeWrapper<E> parent,
        GraphData<E> data) {
      this.node = node;
      this.content = content;
      this.parent= parent;
      this.data = data;
    }

    public GraphNode getNode() {
      return node;
    }

    public E getContent() {
      return content;
    }

    @Override
    public String toString() {
      return node.toString();
    }
  }

  /**
   * @param <E> Type of data associated to each Node<Element>.
   */
  public static class NodeWrapperRoot<E> extends PlatformObject {
    public NodeWrapper<E>[] roots = null;
  }

  /* (non-Javadoc)
   * @see com.google.devtools.depan.eclipse.utils.RelationshipSelectorListener#selectedSetChanged(com.google.devtools.depan.relationship.RelationshipSet)
   */
  public void selectedSetChanged(RelationshipSet set) {
    data.updateTreeData(set);
    setInput(computeRoots());
  }

}
