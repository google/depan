/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.nodelist_doc.model;

import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.graph_doc.model.GraphModelReference;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.api.GraphBuilders;

import com.google.common.collect.ImmutableList;

import org.eclipse.core.resources.IFile;

import java.util.Collection;

/**
 * Similar to a ViewDoc, but none of the rendering preferences.
 * 
 * This document type is available without UX components.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class NodeListDocument {

  /**
   * Standard extension to use when loading or saving {@code NodeListDocument}s.
   * The characters represent "DepAn View Info".
   */
  public static final String EXTENSION = "dni";

  private final GraphModelReference parentGraph;

  private final Collection<GraphNode> nodes;

  public NodeListDocument(
      GraphModelReference parentGraph, Collection<GraphNode> nodes) {
    this.parentGraph = parentGraph;
    this.nodes = nodes;
  }

  /**
   * Provide a copy of the collection of nodes in this document.
   */
  public Collection<GraphNode> getNodes() {
    return ImmutableList.copyOf(nodes);
  }

  /**
   * Provide the {@link GraphModelReference} for this {@link NodeListDocument}.
   * Intended primarily for the peristence layers.
   */
  public GraphModelReference getReferenceGraph() {
    return parentGraph;
  }

  public GraphDocument getGraphDocument() {
    return parentGraph.getGraph();
  }

  /**
   * Provide the location where the parent {@link GraphModelReference} is
   * stored.  Typically, the {@link NodeListDocument} is stored near by.
   */
  public IFile getReferenceLocation() {
    return parentGraph.getLocation();
  }

  /**
   * Provide the complete graph from the {@link GraphModelReference}.
   */
  public GraphModel getGraphModel() {
    return getGraphDocument().getGraph();
  }

  /**
   * Provide the {@link DependencyModel} from the {@link GraphModelReference}.
   */
  public DependencyModel getDependencyModel() {
    return getGraphDocument().getDependencyModel();
  }

  /**
   * Provide the imputed graph based on the edges from the
   * {@link GraphModelReference} and these {@link #nodes}.
   */
  public GraphModel getNodeListGraph() {
    GraphModel master = parentGraph.getGraph().getGraph();
    return GraphBuilders.buildFromNodes(master, nodes);
  }
}
