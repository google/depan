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

package com.google.devtools.depan.model.builder;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.DependenciesListener;
import com.google.devtools.depan.model.interfaces.GraphBuilder;

/**
 * Implements a DependenciesListener, which means that this is the first class
 * that receive all the new dependencies after their creation.
 *
 * This class will first check if the two elements involved in the new
 * dependency pass the filter constructor-provided filter.
 *
 * If yes, the next step is to check if we already seen this element before
 * (this operation is longer than the filter check, so we do it after).
 * For this, it uses an ElementToNodeMapper, which return the correct
 * Node to insert in the graph, and says if we have to insert it into
 * the graph, or if it should already be present in the graph.
 *
 * Finally, insert in the graph the edge, representing the dependency.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class DependenciesDispatcher implements DependenciesListener {

  /**
   * The filter for incoming elements.
   */
  private final ElementFilter filter;

  /**
   * GraphBuilder to finally create the graph.
   */
  private final GraphBuilder graphBuilder;

  /**
   * Construct a DependenciesDispatcher using the given
   * ElementFilter, and GraphBuilder.
   *
   * @param filter ElementFilter to check which Element to insert into the
   *        graph.
   * @param gb Graph builder which create the graph.
   */
  public DependenciesDispatcher(ElementFilter filter, GraphBuilder builder) {
    this.filter = filter;
    this.graphBuilder = builder;
  }

  /**
   * Construct a DependenciesDispatcher without filtering for
   * the given GraphBuilder.
   *
   * @param gb Graph builder which create the graph.
   */
  public DependenciesDispatcher(GraphBuilder builder) {
    this(ElementFilter.ALL_NODES, builder);
  }

  /**
   * {@inheritDoc}
   * @see DependenciesListener#newDep(GraphNode,GraphNode,Relation)
   */
  public void newDep(GraphNode parent, GraphNode child, Relation t) {
    if (!passFilter(parent, child)) return;

    GraphNode p = graphBuilder.mapNode(parent);
    GraphNode c = graphBuilder.mapNode(child);

    graphBuilder.addEdge(new GraphEdge(p, c, t));
  }

  /**
   * {@inheritDoc}
   * @see DependenciesListener#newDeps(GraphNode,GraphNode[],Relation)
   */
  public void newDeps(
      GraphNode parent, GraphNode[] childs, Relation t) {
    for (GraphNode c : childs) {
      newDep(parent, c, t);
    }
  }

  /**
   * check if the given element pass the filter.
   *
   * @param e Element to check
   * @return true if the element passes the filter, or if no filter were
   *         specified (filter is null). false otherwise.
   */
  protected boolean passFilter(GraphNode e) {
    return filter.passFilter(e);
  }

  /**
   * check if both elements passes the filter.
   *
   * @param e1 first Element
   * @param e2 second Element
   * @return true if both elements pass the filter, false if at least one
   *         of them failed.
   */
  private boolean passFilter(GraphNode e1, GraphNode e2) {
    return passFilter(e1) && passFilter(e2);
  }

}
