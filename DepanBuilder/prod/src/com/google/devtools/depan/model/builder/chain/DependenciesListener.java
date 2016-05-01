/*
 * Copyright 2007 The Depan Project Authors
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

package com.google.devtools.depan.model.builder.chain;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.GraphNode;

/**
 * Listener used to add dependencies to a {@link GraphBuilder}, typically used
 * while analyzing a dependency source.  DependenciesListener implementation
 * chain graph construction behaviors via derived types.
 * 
 * The standard hierarchy of Dependencies listeners includes filtering of
 * dependencies that refer to entities outside the scope of the analysis,
 * detection of duplicate nodes, and avoidance of duplicate dependencies.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public interface DependenciesListener {
  /**
   * Find a node in the current graph.
   * 
   * @param target node to find in graph
   * @return node in graph, or {@code null} if not found.
   */
  public GraphNode lookup(GraphNode target);

  /**
   * Insert a Node with no dependencies.  Sometimes you just have a node
   * with no direct connections
   *
   * @param orphan un-attached node to add to graph
   */
  public GraphNode newNode(GraphNode orphan);

  /**
   * Create a dependency of type t between a child and his parent
   *
   * @param parent his parent
   * @param child a child
   * @param t dependency's type
   */
  public void newDep(GraphNode parent, GraphNode child, Relation t);

  /**
   * Create a dependency of type t between some children and his parent.
   *
   * @param parent parent
   * @param childs array child
   * @param t dependency's type
   */
  public void newDeps(GraphNode parent, GraphNode[] childs, Relation t);
}
