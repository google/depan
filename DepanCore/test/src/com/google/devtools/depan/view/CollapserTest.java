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

package com.google.devtools.depan.view;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.google.devtools.depan.graph.basic.MultipleDirectedRelationFinder;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.testing.TestUtils;

/**
 * At least some tests for the ViewModel class.
 *
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
public class CollapserTest {

  /**
   * Assert that the ViewModel provides the expected number of
   * exposed nodes and edges.
   *
   * @param testView ViewModel to test
   * @param nodeCnt expected number of exposed nodes
   * @param edgeCnt expected number of exposed edges
   */
  private static void assertGraphNodesEdges(
      GraphModel testGraph, int nodeCnt, int edgeCnt) {
    assertEquals(nodeCnt, testGraph.getNodes().size());
    assertEquals(edgeCnt, testGraph.getEdges().size());
  }

  /**
   * Create a simple complete-5 graph, and verify that everything arrives
   * in the complete view.
   */
  @Test
  public void testBasic() {
    GraphModel testGraph = new GraphModel();
    @SuppressWarnings("unused")
    GraphNode srcNodes[] =
        TestUtils.buildComplete(testGraph, 5, SampleRelation.sampleRelation);
  }

  /**
   * Create a simple complete-5 graph with a view, and then collapse
   * the least 2 significant nodes into a collapsed node.
   */
  @Test
  public void testCollapse() {
    Collapser collapser = new Collapser();
    GraphModel testGraph = new GraphModel();
    GraphNode srcNodes[] =
        TestUtils.buildComplete(testGraph, 5, SampleRelation.sampleRelation);

    assertGraphNodesEdges(testGraph, 5, 10);

    // Do a simple collapse
    GraphNode master = srcNodes[3];
    Collection<GraphNode> collapsed = Lists.newArrayList();
    collapsed.add(master);
    collapsed.add(srcNodes[4]);
    collapser.collapse(master, collapsed, true);

    assertGraphNodesEdges(collapser.buildExposedGraph(testGraph), 4, 6);
  }

  /**
   * Create a simple complete-5 graph with a view, and then:
   * 1) collapse the least 2 significant nodes into a collapsed node.
   * 2) collapse the next least significant node into the collapsed master
   * from step 1
   */
  @Test
  public void testNestedCollapse() {
    Collapser collapser = new Collapser();
    GraphModel testGraph = new GraphModel();
    GraphNode srcNodes[] =
        TestUtils.buildComplete(testGraph, 5, SampleRelation.sampleRelation);

    assertGraphNodesEdges(testGraph, 5, 10);

    // Do a simple collapse
    GraphNode masterOne = srcNodes[3];
    Collection<GraphNode> collapseOne = Lists.newArrayList();
    collapseOne.add(masterOne);
    collapseOne.add(srcNodes[4]);
    collapser.collapse(masterOne, collapseOne, true);
    assertGraphNodesEdges(collapser.buildExposedGraph(testGraph), 4, 6);

    // Collapse this master into a new master
    GraphNode masterTwo = srcNodes[2];
    Collection<GraphNode> collapseTwo = Lists.newArrayList();
    collapseTwo.add(masterOne);
    collapseTwo.add(masterTwo);
    collapser.collapse(masterTwo, collapseTwo, false);
    assertGraphNodesEdges(collapser.buildExposedGraph(testGraph), 3, 3);
  }


  /**
   * Create a simple complete-5 graph with a view, and then:
   * 1) collapse the least 2 significant nodes into a collapsed node.
   * 2) collapse the next 2 least significant nodes into a separate
   * collapse group.
   * <p>
   * Note that reusing the master and picked list demonstrates that the
   * collapse group does not change if the input variables are altered.
   */
  @Test
  public void testDoubleCollapse() {
    Collapser collapser = new Collapser();
    GraphModel testGraph = new GraphModel();
    GraphNode srcNodes[] =
        TestUtils.buildComplete(testGraph, 5, SampleRelation.sampleRelation);

    assertGraphNodesEdges(testGraph, 5, 10);

    // Allocate a re-usable master and picked lists
    GraphNode master;
    Collection<GraphNode> picked = Lists.newArrayList();

    // Do a simple collapse
    master = srcNodes[3];
    picked.add(master);
    picked.add(srcNodes[4]);
    collapser.collapse(master, picked, false);
    assertGraphNodesEdges(collapser.buildExposedGraph(testGraph), 4, 6);

    // Reuse the collapse variables for a new operation
    master = srcNodes[1];
    picked.clear();
    picked.add(master);
    picked.add(srcNodes[2]);
    collapser.collapse(master, picked, false);
    assertGraphNodesEdges(collapser.buildExposedGraph(testGraph), 3, 3);
  }

  @Test
  public void testAutoCollapse() {
    Collapser collapser = new Collapser();
    GraphModel testGraph = new GraphModel();
    GraphNode srcNodes[] =
        TestUtils.buildComplete(testGraph, 5, SampleRelation.sampleRelation);

    assertGraphNodesEdges(testGraph, 5, 10);

    MultipleDirectedRelationFinder finder =
      new MultipleDirectedRelationFinder();
    finder.addRelation(SampleRelation.sampleRelation, true, false);
    TreeModel treeData = new HierarchicalTreeModel(
        testGraph.computeSuccessorHierarchy(finder));

    @SuppressWarnings("unused")
    Collection<CollapseData> collapseChanges =
        collapser.collapseTree(testGraph, treeData);

    assertGraphNodesEdges(collapser.buildExposedGraph(testGraph), 1, 0);

    // Uncollapse each, and check nodes and edges
    collapser.uncollapse(srcNodes[0], false);
    assertGraphNodesEdges(collapser.buildExposedGraph(testGraph), 2, 1);

    collapser.uncollapse(srcNodes[1], false);
    assertGraphNodesEdges(collapser.buildExposedGraph(testGraph), 3, 3);

    collapser.uncollapse(srcNodes[2], false);
    assertGraphNodesEdges(collapser.buildExposedGraph(testGraph), 4, 6);

    collapser.uncollapse(srcNodes[3], false);
    assertGraphNodesEdges(collapser.buildExposedGraph(testGraph), 5, 10);
  }
}
