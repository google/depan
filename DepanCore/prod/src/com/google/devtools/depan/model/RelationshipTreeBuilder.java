/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.model;

import com.google.devtools.depan.collect.Lists;
import com.google.devtools.depan.collect.Sets;
import com.google.devtools.depan.graph.api.DirectedRelationFinder;
import com.google.devtools.depan.util.Tree;

import java.util.Collection;
import java.util.Set;

/**
 * Build a tree over any set of relationships.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class RelationshipTreeBuilder {

  /**
   * Collection of nodes in the tree.
   */
  private final Collection<GraphNode> nodes;
  
  /**
   * {@link DirectedRelationFinder} to match the relations creating the tree.
   */
  private final DirectedRelationFinder relations;
  
  /**
   * Collection of edges in this graph.
   */
  private final Collection<GraphEdge> edges;
  
  /**
   * Nodes already processed
   */
  private Set<GraphNode> done = Sets.newHashSet();
  
  /**
   * Create a new Graph analysis to extract a tree over a given set of
   * relationships.
   * 
   * @param edges a collection of edges to follow.
   * @param nodes collection of nodes in the tree
   * @param relations relation matcher: says which relationships to follow.
   */
  public RelationshipTreeBuilder(
      Collection<GraphEdge> edges,
      Collection<GraphNode> nodes,
      DirectedRelationFinder relations) {
    this.nodes = nodes;
    this.relations = relations;
    this.edges = edges;
  }
  
  /**
   * Build the tree, and return a list of root trees.
   * 
   * @return a list of root trees. 
   */
  public Collection<Tree<GraphNode>> buildTree() {
    Collection<Tree<GraphNode>> roots =
        Lists.newArrayList();
    for (GraphNode node : nodes) {
      if (!done.contains(node)) {
        done.add(node);
        // for each node, if not already processed, try to compute its childs
        // and parents.
        Tree<GraphNode> rootTree = new Tree<GraphNode>(node);
        Tree<GraphNode> t = computeChilds(rootTree, null);
        roots.add(t);
      }
    }
    return roots;
  }

  /**
   * Compute the childs from the given tree, given its parent.
   * <code>parent</code> can be <code>null</code>, if this node has no
   * parent yet.
   * 
   * @param from the tree we want to build around (parent and children)
   * @param parent this tree parent.
   * @return a tree containing <code>from</code> as a branch or root.
   */
  // suppressWarnings for the Node<Element> unchecked cast (4x).
  private Tree<GraphNode> computeChilds(
      Tree<GraphNode> from, Tree<GraphNode> parent) {
    for (GraphEdge edge : edges) {
      if ((edge.getHead() == from.getHead())
          && relations.matchForward(edge.getRelation())
          && !done.contains(edge.getTail())) {
        // we get a child.
        done.add(edge.getTail());
        // add it to the list of childs
        Tree<GraphNode> child =
          from.addLeaf(edge.getTail());
        // try to build around this child.
        computeChilds(child, from);
      } else if ((edge.getTail() == from.getHead())
          && relations.matchBackward(edge.getRelation())
          && !done.contains(edge.getHead())
          && null == parent) {
        // we get a parent
        done.add(edge.getHead());
        // make from a child of this new parent.
        parent = from.setAsParent(edge.getHead()); 
        // try to build around this parent.
        parent = computeChilds(parent, null);
      }
    }
    // return a parent if we found one (it contains from as a child anyway),
    // otherwise return the tree.
    return null == parent ? from : parent;
  }
}
