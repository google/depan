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

import com.google.devtools.depan.model.GraphNode;

import org.eclipse.core.runtime.PlatformObject;

import java.util.Collection;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 * @param <E> Type of data associated to each Node<Element>.
 */
public class NodeWrapper<E> extends PlatformObject {
  @SuppressWarnings("rawtypes")
  public static final NodeWrapper[] LEAF_KIDS = new NodeWrapper[0];

  private final GraphNode node;
  private final NodeWrapper<E> parent;
  private final GraphData<E> data;

  // Populated lazy by GraphData.getChildren();
  public NodeWrapper<E>[] childs;

  public NodeWrapper(
      GraphNode node,
      NodeWrapper<E> parent,
      GraphData<E> data) {
    this.node = node;
    this.parent= parent;
    this.data = data;
  }

  public GraphNode getNode() {
    return node;
  }

  public E getContent() {
    return data.getContent(node);
  }

  @Override
  public String toString() {
    return node.toString();
  }

  public Object[] getChildren() {

    // Cache the children if we don't have them already
    if (null == childs) {
      childs = data.getChildren(this);
    }

    return childs;
  }

  public NodeWrapper<E> getParent() {
    return parent;
  }

  @SuppressWarnings("unchecked")
  public static <F> NodeWrapper<F>[] buildNodeWrapperArray(
      Collection<GraphNode> nodes,
      NodeWrapper<F> parent, GraphData<F> data) {

    // All empty children lists look the same,
    // so early exit with the singleton
    if (0 == nodes.size()) {
      return LEAF_KIDS;
    }

    NodeWrapper<F>[] children = new NodeWrapper[nodes.size()];
    int index = 0;
    for (GraphNode node : nodes) {
      NodeWrapper<F> nodeWrapper = createNodeWrapper(node, parent, data);
      children[index] = nodeWrapper;
      index++;
    }
    return children;
  }

  private static <F> NodeWrapper<F> createNodeWrapper(
      GraphNode node, NodeWrapper<F> parent, GraphData<F> data) {
    return new NodeWrapper<F>(node, parent, data);
  }
}
