/*
 * Copyright 2017 The Depan Project Authors
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

package com.google.devtools.depan.nodes.trees;

import com.google.devtools.depan.graph.basic.BasicEdge;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.test.TestUtils;

import com.google.common.collect.Sets;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
public class TreesTest {

  @Test
  public void testComputeSpanningHierarchyForward() {
    GraphNode[] nodeArray = TestUtils.buildNodes(5);
    GraphModel test = TestUtils.buildComplete(nodeArray, TestUtils.RELATION);

    Map<GraphNode, ? extends SuccessorEdges> result =
        Trees.computeSpanningHierarchy(test, TestUtils.FORWARD);
    Assert.assertEquals(5, buildAllNodes(result).size());
  }

  @Test
  public void testComputeSpanningHierarchyReverse() {
    GraphNode[] nodeArray = TestUtils.buildNodes(5);
    GraphModel test = TestUtils.buildComplete(nodeArray, TestUtils.RELATION);

    Map<GraphNode, ? extends SuccessorEdges> result =
        Trees.computeSpanningHierarchy(test, TestUtils.REVERSE);
    Assert.assertEquals(5, buildAllNodes(result).size());
  }

  @Test
  public void testComputeSuccessorHierarchy() {
    // Build a one node self-referent graph to test
    GraphNode[] nodeArray = TestUtils.buildNodes(1);
    GraphNode node = nodeArray[0];
    GraphEdge edge = new GraphEdge(node, node, TestUtils.RELATION);
    // Set<BasicEdge<? extends String>> edges = Sets.newHashSet((BasicEdge<? extends String>) edge);
    // Set<BasicEdge<? extends String>> edges = Collections.singleton((BasicEdge<? extends String>) edge);
    Set<BasicEdge<? extends String>> edges = Sets.newHashSet();
    edges.add((BasicEdge<? extends String>) edge);
    GraphModel test = TestUtils.buildGraphModel(nodeArray, edges);

    Map<GraphNode, ? extends SuccessorEdges> result =
        Trees.computeSpanningHierarchy(test, TestUtils.FORWARD);
    Assert.assertEquals(0, result.size());
  }

  private Set<GraphNode> buildAllNodes(
      Map<GraphNode, ? extends SuccessorEdges> map) {
    Set<GraphNode> result  = Sets.newHashSet();
    for (Entry<GraphNode, ? extends SuccessorEdges> entry : map.entrySet()) {
      result.add(entry.getKey());
      result.addAll(entry.getValue().computeSuccessorNodes());
    }
    return result;
  }
}
