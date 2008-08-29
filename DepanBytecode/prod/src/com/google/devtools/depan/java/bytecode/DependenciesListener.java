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

package com.google.devtools.depan.java.bytecode;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.GraphNode;

/**
 * Listener used when running into the class file, field, or method; when
 * dependencies are found, the appropriate callback is called...
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public interface DependenciesListener {

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
