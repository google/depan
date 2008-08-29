/*
 * Copyright 2006 Google Inc.
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

/**
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
public class BasicGraphTest extends BasicGraphTestCase {

  public void testBasic() {
    BasicGraph<String> graph = new BasicGraph<String>();
    assertNotNull(graph);

    BasicNode<? extends String> head = addSimpleNode(graph, "head");
    assertNotNull(head);
    assertSame(head, graph.findNode("head"));

    BasicNode<? extends String> tail = addSimpleNode(graph, "tail");
    assertNotNull(tail);
    assertNotSame(head, tail);
    assertSame(tail, graph.findNode("tail"));

    BasicEdge<? extends String> simpleEdge =
        graph.addEdge(MockRelation.SIMPLE_RELATION, head, tail);
    assertNotNull(simpleEdge);

    assertSame(MockRelation.SIMPLE_RELATION, simpleEdge.getRelation());
    assertSame(head, simpleEdge.getHead());
    assertSame(tail, simpleEdge.getTail());
  }

  public void test2Relations() {
    BasicGraph<String> graph = new BasicGraph<String>();
    assertNotNull(graph);

    BasicNode<? extends String> head = addSimpleNode(graph, "head");
    assertNotNull(head);

    BasicNode<? extends String> tail = addSimpleNode(graph, "tail");
    assertNotNull(tail);

    BasicEdge<? extends String> simpleEdge =
        graph.addEdge(MockRelation.SIMPLE_RELATION, head, tail);
    assertNotNull(simpleEdge);

    BasicEdge<? extends String> memberEdge =
        graph.addEdge(MockRelation.MEMBER_RELATION, head, tail);
    assertNotNull(memberEdge);
    assertNotSame(simpleEdge, memberEdge);

    BasicEdge<? extends String> foundEdge =
        graph.findEdge(MockRelation.MEMBER_RELATION, head, tail);
    assertNotNull(foundEdge);
    assertSame(memberEdge, foundEdge);
  }

  public void testDuplicateNodes() {
    BasicGraph<String> graph = new BasicGraph<String>();
    assertNotNull(graph);

    BasicNode<? extends String> head = addSimpleNode(graph, "head");
    assertNotNull(head);

    try {
      graph.addNode(head);
      fail("should not be able to create two 'head' nodes");
    } catch (IllegalArgumentException errArg) {
      // expected
    }
  }

  public void testDuplicateEdges() {
    BasicGraph<String> graph = new BasicGraph<String>();
    assertNotNull(graph);

    BasicNode<String> head = addSimpleNode(graph, "head");
    assertNotNull(head);

    BasicNode<String> tail = addSimpleNode(graph, "tail");
    assertNotNull(tail);

    BasicEdge<? extends String> simpleEdge =
        graph.addEdge(MockRelation.SIMPLE_RELATION, head, tail);
    assertNotNull(simpleEdge);

    try {
      @SuppressWarnings("unused")
      BasicEdge<? extends String> badEdge =
          graph.addEdge(MockRelation.SIMPLE_RELATION, head, tail);
      fail("should not be able to create two simple edges");
    } catch (IllegalArgumentException errArg) {
      // expected
    }
  }
}
