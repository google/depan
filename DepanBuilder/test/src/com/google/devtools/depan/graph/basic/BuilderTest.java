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

import static org.junit.Assert.*;

import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.simple.GraphModelBuilder;

import org.junit.Test;

import java.util.Set;

/**
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
public class BuilderTest { // extends BasicGraphTestCase {

  protected static final String HEAD = "head";
  protected static final String TAIL = "tail";

  private static MockElement createNode(String name) {
    return new MockElement(name);
  }

  @Test
  public void test2Relations() {
    GraphModelBuilder builder = new GraphModelBuilder();

    GraphNode headNode = builder.newNode(createNode(HEAD));
    GraphNode tailNode = builder.newNode(createNode(TAIL));
    assertNotNull(headNode);
    assertNotNull(tailNode);

    GraphEdge simpleEdge = new GraphEdge(
        headNode, tailNode, MockRelation.SIMPLE_RELATION);
    GraphEdge memberEdge = new GraphEdge(
        headNode, tailNode, MockRelation.MEMBER_RELATION);
    GraphEdge foundEdge = new GraphEdge(
        headNode, tailNode, MockRelation.MEMBER_RELATION);

    assertNotNull(simpleEdge);
    assertNotNull(memberEdge);
    assertNotNull(foundEdge);
    assertNotSame(simpleEdge, memberEdge);
    assertNotSame(simpleEdge, foundEdge);
    assertNotSame(memberEdge, foundEdge);

    builder.addEdge(simpleEdge);
    builder.addEdge(memberEdge);
    builder.addEdge(foundEdge);

    GraphModel graph = builder.createGraphModel();

    assertSame(headNode, graph.findNode(HEAD));
    assertSame(tailNode, graph.findNode(TAIL));

    Set<GraphEdge> edges = graph.getEdgesSet();
    assertEquals(2, edges.size());
    assertTrue(edges.contains(simpleEdge));
    assertTrue(edges.contains(memberEdge));
    assertTrue(edges.contains(foundEdge));

    BasicEdge<? extends String> simpleFind = graph.findEdge(
        simpleEdge.getRelation(), simpleEdge.getHead(), simpleEdge.getTail());
    BasicEdge<? extends String> memberFind = graph.findEdge(
        memberEdge.getRelation(), memberEdge.getHead(), memberEdge.getTail());
    BasicEdge<? extends String> foundFind = graph.findEdge(
        foundEdge.getRelation(), foundEdge.getHead(), foundEdge.getTail());

    assertNotNull(simpleFind);
    assertNotNull(memberFind);
    assertNotNull(foundFind);
    assertSame(simpleEdge, simpleFind);
    assertSame(memberEdge, memberFind);
    assertSame(memberEdge, foundFind);
    assertNotSame(foundEdge, foundFind);
  }

  @SuppressWarnings("unused")
  @Test
  public void testDuplicateNodes() {
    GraphModelBuilder builder = new GraphModelBuilder();

    GraphNode headOne = createNode(HEAD);
    GraphNode headTwo = createNode(HEAD);
    assertNotNull(headOne);
    assertNotNull(headTwo);
    assertNotSame(headOne, headTwo);

    GraphNode fromOne = builder.newNode(headOne);
    assertSame(headOne, fromOne);

    // Try twice with same node.
    try {
      GraphNode fromOneA = builder.newNode(headOne);
      fail("should not be able to create two 'head' nodes");
    } catch (IllegalArgumentException errArg) {
      // expected
    }

    // Try with differnt node, same id.
    try {
      GraphNode fromTwo = builder.newNode(headTwo);
      fail("should not be able to create two 'head' nodes");
    } catch (IllegalArgumentException errArg) {
      // expected
    }
  }

  @Test
  public void testDuplicateEdges() {
    GraphModelBuilder builder = new GraphModelBuilder();

    GraphNode headNode = builder.newNode(createNode(HEAD));
    GraphNode tailNode = builder.newNode(createNode(TAIL));
    assertNotNull(headNode);
    assertNotNull(tailNode);

    GraphEdge simpleOne = new GraphEdge(
        headNode, tailNode, MockRelation.SIMPLE_RELATION);
    GraphEdge simpleTwo = new GraphEdge(
        headNode, tailNode, MockRelation.SIMPLE_RELATION);
    assertNotNull(simpleOne);
    assertNotNull(simpleTwo);
    assertNotSame(simpleOne, simpleTwo);

    // Collapsing adds multiple edges with the same relation
    // as relations are propogated to the parent node.
    // Analysis builders should check for duplicates.
    GraphEdge fromOne = builder.addEdge(simpleOne);
    GraphEdge fromTwo = builder.addEdge(simpleTwo);

    assertNotNull(fromOne);
    assertNotNull(fromTwo);
    assertSame(simpleOne, fromOne);
    assertNotSame(simpleOne, fromTwo);
    assertSame(simpleTwo, fromTwo);
  }
}
