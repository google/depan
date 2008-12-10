/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.model;

import junit.framework.TestCase;

import java.util.Map;
import java.util.Set;

import com.google.devtools.depan.model.testing.TestUtils;
import com.google.devtools.depan.view.SampleRelation;

/**
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
public class GraphModelTest extends TestCase {

  @Override
  public void setUp() throws Exception {
  }

  @Override
  public void tearDown() throws Exception {
  }

  public void testBasic() {
    GraphModel test = new GraphModel();
    TestUtils.buildComplete(test, 5, SampleRelation.sampleRelation);

    assertEquals(5, test.getNodes().size());
    assertEquals(10, test.getEdges().size());
  }

  public void testGetForwardRelationCount() {
    GraphModel test = new GraphModel();
    GraphNode[] nodeArray =
          TestUtils.buildComplete(test, 5, SampleRelation.sampleRelation);
    Set<GraphNode> nodeSet = TestUtils.toSet(nodeArray);

    RelationshipSetAdapter forwardSet = new RelationshipSetAdapter("forward");
    forwardSet.addRelation(SampleRelation.sampleRelation, true, false);
    Map<GraphNode, Integer> forwardMap =
        test.getForwardRelationCount(nodeSet, forwardSet);
    assertNotNull(forwardMap);
    assertEquals(4, forwardMap.get(nodeArray[0]).intValue());
    assertEquals(3, forwardMap.get(nodeArray[1]).intValue());
    assertEquals(2, forwardMap.get(nodeArray[2]).intValue());
    assertEquals(1, forwardMap.get(nodeArray[3]).intValue());
    assertEquals(0, forwardMap.get(nodeArray[4]).intValue());
  }

  public void testGetReverseRelationCount() {
    GraphModel test = new GraphModel();
    GraphNode[] nodeArray =
          TestUtils.buildComplete(test, 5, SampleRelation.sampleRelation);
    Set<GraphNode> nodeSet = TestUtils.toSet(nodeArray);

    RelationshipSetAdapter reverseSet = new RelationshipSetAdapter("reverse");
    reverseSet.addRelation(SampleRelation.sampleRelation, false, true);
    Map<GraphNode, Integer> reverseMap =
        test.getReverseRelationCount(nodeSet, reverseSet);
    assertNotNull(reverseMap);
    assertEquals(0, reverseMap.get(nodeArray[0]).intValue());
    assertEquals(1, reverseMap.get(nodeArray[1]).intValue());
    assertEquals(2, reverseMap.get(nodeArray[2]).intValue());
    assertEquals(3, reverseMap.get(nodeArray[3]).intValue());
    assertEquals(4, reverseMap.get(nodeArray[4]).intValue());
  }
}
