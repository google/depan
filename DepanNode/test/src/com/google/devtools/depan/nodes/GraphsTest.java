/*
 * Copyright 2007 The Depan Project Authors
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

package com.google.devtools.depan.nodes;

import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.RelationSets;

import org.junit.Test;

import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
public class GraphsTest {

  @Test
  public void testBasic() {
    GraphNode[] nodeArray = TestUtils.buildNodes(5);
    GraphModel test = TestUtils.buildComplete(nodeArray, TestRelation.SAMPLE);

    assertEquals(5, test.getNodes().size());
    assertEquals(10, test.getEdges().size());
  }


  @Test
  public void testGetForwardRelationCount() {
    GraphNode[] nodeArray = TestUtils.buildNodes(5);
    GraphModel test = TestUtils.buildComplete(nodeArray, TestRelation.SAMPLE);

    Collection<GraphNode> nodeSet = test.getNodesSet();
    assertEquals(nodeArray.length, nodeSet.size());

    RelationSet forwardSet = RelationSets.createSingle(TestRelation.SAMPLE);

    Map<GraphNode, Integer> forwardMap =
        Graphs.getForwardRelationCount(test, nodeSet, forwardSet );

    assertNotNull(forwardMap);
    assertEquals(4, forwardMap.get(nodeArray[0]).intValue());
    assertEquals(3, forwardMap.get(nodeArray[1]).intValue());
    assertEquals(2, forwardMap.get(nodeArray[2]).intValue());
    assertEquals(1, forwardMap.get(nodeArray[3]).intValue());
    assertEquals(0, forwardMap.get(nodeArray[4]).intValue());
  }

  @Test
  public void testGetReverseRelationCount() {
    GraphNode[] nodeArray = TestUtils.buildNodes(5);
    GraphModel test = TestUtils.buildComplete(nodeArray, TestRelation.SAMPLE);

    Collection<GraphNode> nodeSet = test.getNodesSet();
    assertEquals(nodeArray.length, nodeSet.size());

    RelationSet reverseSet = RelationSets.createSingle(TestRelation.SAMPLE);

    Map<GraphNode, Integer> reverseMap =
        Graphs.getReverseRelationCount(test, nodeSet, reverseSet);
    assertNotNull(reverseMap);
    assertEquals(0, reverseMap.get(nodeArray[0]).intValue());
    assertEquals(1, reverseMap.get(nodeArray[1]).intValue());
    assertEquals(2, reverseMap.get(nodeArray[2]).intValue());
    assertEquals(3, reverseMap.get(nodeArray[3]).intValue());
    assertEquals(4, reverseMap.get(nodeArray[4]).intValue());
  }
}
