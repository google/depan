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

package com.google.devtools.depan.test;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.graph.basic.BasicEdge;
import com.google.devtools.depan.graph.basic.BasicNode;
import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.model.ElementVisitor;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphEdgeMatcher;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.RelationSets;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
public class TestUtils {

  public static Relation RELATION = new Relation() {

    @Override
    public String getForwardName() {
      return "forward";
    }

    @Override
    public String getReverseName() {
      return "reverse";
    }
  };

  public static RelationSet RELATION_SET =
      RelationSets.createSingle(RELATION);

  public static GraphEdgeMatcher FORWARD = new GraphEdgeMatcher() {

    @Override
    public boolean relationForward(Relation relation) {
      return RELATION == relation;
    }
  }; 

  public static GraphEdgeMatcher REVERSE = new GraphEdgeMatcher() {

    @Override
    public boolean relationReverse(Relation relation) {
      return RELATION == relation;
    }
  };

  public static class TestNode extends GraphNode {
    private final String label;

    public TestNode(String label) {
      this.label = label;
    }

    @Override
    public String friendlyString() {
      return "TestNode - " + label;
    }

    @Override
    public void accept(ElementVisitor visitor) {
    }

    @Override
    public String getId() {
      return label;
    }
  }

  /////////////////////////////////////
  // Methods

  private TestUtils() {
    // Prevent instantiation.
  }

  public static String getNodeId(int count) {
    return nameGen("node ", count);
  }

  public static GraphNode[] buildNodes(int degree) {
    GraphNode nodes[] = new GraphNode[degree];
    for (int nodeCnt = 0; nodeCnt < degree; nodeCnt++) {
      nodes[nodeCnt] = new TestNode(getNodeId(nodeCnt));;
    }
    return nodes;
  }

  public static GraphModel buildComplete(GraphNode[] nodes, Relation relation) {

    int degree = nodes.length;
    Set<BasicEdge<? extends String>> edges = Sets.newHashSet();
    for (int head = 0; head < (degree - 1); head++) {
      for (int tail = head + 1; tail < degree; tail++) {
        GraphEdge edge = new GraphEdge(nodes[head], nodes[tail], relation);
        edges.add((BasicEdge<? extends String>) edge);
      }
    }

    return buildGraphModel(nodes, edges);
  }

  public static GraphModel buildGraphModel(
      GraphNode nodes[], Set<BasicEdge<? extends String>> edges) {
    Map<String, BasicNode<? extends String>> graphNodes =
        Maps.newHashMapWithExpectedSize(nodes.length);
 
    for (GraphNode node : nodes) {
      graphNodes.put(node.getId(), node);
    }
    return new GraphModel(graphNodes, edges);
  }

  public static Set<GraphNode> toSet(GraphNode[] nodes) {
    Set<GraphNode> result = Sets.newHashSet();
    for (GraphNode node : nodes) {
      result.add(node);
    }

    return result;
  }

  public static GraphDocument buildTestDoc(int order) {
    GraphNode[] nodes = TestUtils.buildNodes(order);
    Set<BasicEdge<? extends String>> edges = Collections.emptySet();
    List<String> nodeContribIds = Collections.emptyList();
    List<String> relationContribId = Collections.emptyList();

    GraphModel graph = TestUtils.buildGraphModel(nodes, edges);
    DependencyModel model =
        new DependencyModel(nodeContribIds, relationContribId);
    return new GraphDocument(model , graph);
  }

  private static String nameGen(String prefix, int nodeCnt) {
    return prefix + Integer.toString(nodeCnt);
  }
}
