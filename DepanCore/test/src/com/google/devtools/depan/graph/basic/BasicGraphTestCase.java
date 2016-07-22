/*
 * Copyright 2006 The Depan Project Authors
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

package com.google.devtools.depan.graph.basic;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

public class BasicGraphTestCase {

  protected static final String HEAD = "head";
  protected static final String TAIL = "tail";

  protected static BasicNode<String> createSimpleNode(String name) {
    return new SimpleNode<String>(name);
  }

  public static abstract class GraphFixture {

    public BasicGraph<String> graph;

    public void create() {
      Map<String, BasicNode<? extends String>> nodes = createNodes();
      Set<BasicEdge<? extends String>> edges = createEdges();

      graph = new BasicGraph<String>(nodes, edges);
    }

    public BasicNode<? extends String> findNode(String id) {
      return graph.findNode(id);
    }

    protected abstract Map<String, BasicNode<? extends String>> createNodes();

    protected abstract Set<BasicEdge<? extends String>> createEdges();
  }

  public static class SimpleGraphFixture extends GraphFixture{

    public BasicNode<String> headNode;
    public BasicNode<String> tailNode;
    public BasicEdge<String> edge;

    @Override
    public Map<String, BasicNode<? extends String>> createNodes() {
      headNode = createSimpleNode(HEAD);
      tailNode = createSimpleNode(TAIL);
      Map<String, BasicNode<? extends String>> nodes = Maps.newHashMap();
      nodes.put(headNode.getId(), headNode);
      nodes.put(tailNode.getId(), tailNode);
      return nodes;
    }

    @Override
    public Set<BasicEdge<? extends String>> createEdges() {
      edge = new BasicEdge<String>(
              MockRelation.SIMPLE_RELATION, headNode, tailNode);
      Set<BasicEdge<? extends String>> edges = Sets.newHashSet();
      edges.add(edge);
      return edges;
    }
  }
}