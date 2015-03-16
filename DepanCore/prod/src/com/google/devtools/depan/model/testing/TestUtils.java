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

package com.google.devtools.depan.model.testing;

import com.google.common.collect.Sets;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.ElementVisitor;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.interfaces.GraphBuilder;

import java.util.Set;

/**
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
public class TestUtils {

  public static String getNodeId(int count) {
    return "node " + Integer.toString(count);
  }
  
  public static class MethodElement extends GraphNode {
    private final String name;
    private final String args;
    private final String type;

    MethodElement(String name, String args, String type) {
      this.name = name;
      this.args = args;
      this.type = type;
    }

    @Override
    public String friendlyString() {
      return type + " " + name + "(" + args + ")";
    }

    @Override
    public void accept(ElementVisitor visitor) {
    }

    @Override
    public String getId() {
      return name + "(" + args + ")" + type;
    }
  }

  public static GraphNode[] buildComplete(
      GraphModel graph, int degree, Relation relation) {
    GraphNode nodes[] = new GraphNode[degree];
    GraphBuilder builder = graph.getBuilder();
    for (int nodeCnt = 0; nodeCnt < degree; nodeCnt++) {
      GraphNode node = new MethodElement(
          "FakeSig", nameGen("complete", nodeCnt), "boolean");
      nodes[nodeCnt] = builder.newNode(node);
    }

    for (int head = 0; head < (degree - 1); head++) {
      for (int tail = head + 1; tail < degree; tail++) {
        graph.addEdge(relation, nodes[head], nodes[tail]);
      }
    }
    
    return nodes;
  }

  public static Set<GraphNode> toSet(GraphNode[] nodes) {
    Set<GraphNode> result = Sets.newHashSet();
    for (GraphNode node : nodes) {
      result.add(node);
    }

    return result;
  }

  /**
   * @param prefix
   * @param nodeCnt
   * @return
   */
  private static String nameGen(String prefix, int nodeCnt) {
    return prefix + Integer.toString(nodeCnt);
  }
}
