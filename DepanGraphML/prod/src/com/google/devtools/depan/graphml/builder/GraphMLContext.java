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

package com.google.devtools.depan.graphml.builder;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.chain.DependenciesListener;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Common shared context for the analysis of a GraphML files.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class GraphMLContext {

  /**
   * Pair the GraphML node id with the node label.
   * 
   * A rich GraphML import mechanism probably moves this id map into
   * the {@link GraphFactory}, since it would know how to interpret any
   * nested elements.  This works for Maven integration.
   */
  private static class GraphMLNode {
    public final String id;
    public final String nodeLabel;

    public GraphMLNode(String id, String nodeLabel) {
      this.id = id;
      this.nodeLabel = nodeLabel;
    }

    public static GraphMLNode build(String id, String nodeLabel) {
      return new GraphMLNode(id, nodeLabel);
    }
  }

  /**
   * Destination for the discovered graph.
   * 
   * Normally, the context constructor retains a references to the
   * {@link DependenciesListener} instance for output through a separate
   * process.
   */
  private final DependenciesListener builder;

  /**
   * Defines the translation from GraphML nodes and edges to DepAn's
   * notions of these graph entities.
   */
  private final GraphFactory graphFactory;

  private final Map<String, GraphMLNode> graphMLNodes =
      Maps.newHashMap();

  private final Map<String, GraphNode> depanNodes =
      Maps.newHashMap();

  public GraphMLContext(
      DependenciesListener builder, GraphFactory graphFactory) {
    this.builder = builder;
    this.graphFactory = graphFactory;
  }

  /**
   * Register a GraphML node.  The GraphML node id is the primary
   * lookup key.
   */
  public void addGraphMLNode(String id, String nodeLabel) {
    GraphMLNode node = GraphMLNode.build(id, nodeLabel);
    graphMLNodes.put(node.id,  node);
  }

  /**
   * Create a DepAn relationship between the GraphML.  The supplied text
   * names are mapped to DepAn nodes and a relation, and the resulting
   * instances create a DepAn dependency.
   * 
   * @param source GraphML id for the source node
   * @param target GraphML id for the source node
   * @param edgeLabel GraphML/Maven edge label for the relation
   */
  public void addRelation(String source, String target, String edgeLabel) {
    GraphNode head = getGraphNode(source);
    GraphNode tail = getGraphNode(target);
    Relation relation = getRelation(edgeLabel);
    if (null != relation ) {
      builder.newDep(head, tail, relation);
      return;
    }

    logBadEdge(source, target, edgeLabel);
  }

  private GraphNode getGraphNode(String id) {
    GraphNode result = depanNodes.get(id);
    if (null != result) {
      return result;
    }

    GraphMLNode label = graphMLNodes.get(id);
    if (null != label) {
      result = graphFactory.buildNode(label.nodeLabel);
      depanNodes.put(id, result);
      return result;
    }
    return null;
  }

  private Relation getRelation(String id) {
    Relation result = graphFactory.buildRelation(id);
    return result;
  }

  /////////////////////////////////////
  // Error reporting helpers

  private void logBadEdge(String source, String target, String edgeLabel) {
    String dbgSource = getDbgLabel(source);
    String dbgTarget = getDbgLabel(target);
    GraphMLLogger.LOG.warn("Unable to build edge for {} from {} to {}.",
        edgeLabel, dbgSource, dbgTarget);
  }

  private String getDbgLabel(String key) {
    if (null == key) {
      return "<null>";
    }
    if (key.isEmpty()) {
      return "<empty>";
    }
    GraphMLNode result = graphMLNodes.get(key);
    if (null != result) {
      return result.nodeLabel;
    }
    return key;
  }
}
