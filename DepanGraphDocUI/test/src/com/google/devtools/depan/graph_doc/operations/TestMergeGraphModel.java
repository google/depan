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

package com.google.devtools.depan.graph_doc.operations;

import static org.junit.Assert.*;

import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.test.TestUtils;

import org.junit.Test;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class TestMergeGraphModel {

  @Test
  public void testBasic() {
    GraphNode[] testNodes = TestUtils.buildNodes(3);
    GraphModel testGraph = TestUtils.buildComplete(
        testNodes, TestUtils.RELATION);

    MergeGraphModel testMerger = new MergeGraphModel();
    testMerger.merge(testGraph);
    GraphModel result = testMerger.getGraphModel();

    assertEquals(testGraph.getNodes().size(), result.getNodes().size());
    assertEquals(testGraph.getEdges().size(), result.getEdges().size());
  }

  @Test
  public void testOverlap() {
    GraphNode[] nodesThree = TestUtils.buildNodes(3);
    GraphNode[] nodesFive = TestUtils.buildNodes(3);
    
    GraphModel graphThree = TestUtils.buildComplete(
        nodesThree, TestUtils.RELATION);
    GraphModel graphFive = TestUtils.buildComplete(
        nodesFive, TestUtils.RELATION);

    MergeGraphModel testMerger = new MergeGraphModel();
    testMerger.merge(graphThree);
    testMerger.merge(graphFive);
    GraphModel result = testMerger.getGraphModel();

    assertEquals(graphFive.getNodes().size(), result.getNodes().size());
    assertEquals(graphFive.getEdges().size(), result.getEdges().size());
  }
}
