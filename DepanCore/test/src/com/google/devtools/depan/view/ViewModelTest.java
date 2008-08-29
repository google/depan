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

import com.google.devtools.depan.collect.Lists;
import com.google.devtools.depan.graph.basic.MultipleDirectedRelationFinder;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.testing.TestUtils;

import junit.framework.TestCase;

import java.util.Collection;

/**
 * At least some tests for the ViewModel class.
 *
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
public class ViewModelTest extends TestCase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  /**
   * Assert that the ViewModel provides the expected number of
   * exposed nodes and edges.
   *
   * @param testView ViewModel to test
   * @param nodeCnt expected number of exposed nodes
   * @param edgeCnt expected number of exposed edges
   */
  private static void assertExposedGraph(
      ViewModel testView, int nodeCnt, int edgeCnt) {
    GraphModel exposedGraph = testView.getExposedGraph();
    assertEquals(nodeCnt, exposedGraph.getNodes().size());
    assertEquals(edgeCnt, exposedGraph.getEdges().size());
  }

  /**
   * Create a simple complete-5 graph, and verify that everything arrives
   * in the complete view.
   */
  public void testBasic() {
    GraphModel testGraph = new GraphModel();
    GraphNode srcNodes[] =
        TestUtils.buildComplete(testGraph, 5, SampleRelation.sampleRelation);

    ViewModel testView = new ViewModel("test", testGraph);
    testView.setNodes(srcNodes);  // This also populates all edges

    // Semantic properties of a new ViewModel
    assertTrue(testView.getDirty());
    assertEquals("test", testView.getName());

    assertEquals(5, testView.getNodes().size());
    assertEquals(10, testView.getEdges().size());

    assertExposedGraph(testView, 5, 10);
  }

  /**
   * Create a simple complete-5 graph with a view, and then collapse
   * the least 2 significant nodes into a collapsed node.
   */
  public void testCollapse() {
    GraphModel testGraph = new GraphModel();
    GraphNode srcNodes[] =
        TestUtils.buildComplete(testGraph, 5, SampleRelation.sampleRelation);

    ViewModel testView = new ViewModel("test", testGraph);
    testView.setNodes(srcNodes);  // This also populates all edges

    assertExposedGraph(testView, 5, 10);

    // Do a simple collapse
    GraphNode master = srcNodes[3];
    Collection<GraphNode> collapsed = Lists.newArrayList();
    collapsed.add(master);
    collapsed.add(srcNodes[4]);
    testView.collapse(master, collapsed, false, null);

    assertExposedGraph(testView, 4, 9);
  }

  /**
   * Create a simple complete-5 graph with a view, and then:
   * 1) collapse the least 2 significant nodes into a collapsed node.
   * 2) collapse the next least significant node into the collapsed master
   * from step 1
   */
  public void testNestedCollapse() {
    GraphModel testGraph = new GraphModel();
    GraphNode srcNodes[] =
        TestUtils.buildComplete(testGraph, 5, SampleRelation.sampleRelation);

    ViewModel testView = new ViewModel("test", testGraph);
    testView.setNodes(srcNodes);  // This also populates all edges

    assertExposedGraph(testView, 5, 10);

    // Do a simple collapse
    GraphNode masterOne = srcNodes[3];
    Collection<GraphNode> collapseOne = Lists.newArrayList();
    collapseOne.add(masterOne);
    collapseOne.add(srcNodes[4]);
    testView.collapse(masterOne, collapseOne, false, null);

    assertExposedGraph(testView, 4, 9);

    // Collapse this master into a new master
    GraphNode masterTwo = srcNodes[2];
    Collection<GraphNode> collapseTwo = Lists.newArrayList();
    collapseTwo.add(masterOne);
    collapseTwo.add(masterTwo);
    testView.collapse(masterTwo, collapseTwo, false, null);

    assertExposedGraph(testView, 3, 7);
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
  public void testDoubleCollapse() {
    GraphModel testGraph = new GraphModel();
    GraphNode srcNodes[] =
        TestUtils.buildComplete(testGraph, 5, SampleRelation.sampleRelation);

    ViewModel testView = new ViewModel("test", testGraph);
    testView.setNodes(srcNodes);  // This also populates all edges

    assertExposedGraph(testView, 5, 10);

    // Allocate a re-usable master and picked lists
    GraphNode master;
    Collection<GraphNode> picked = Lists.newArrayList();

    // Do a simple collapse
    master = srcNodes[3];
    picked.add(master);
    picked.add(srcNodes[4]);
    testView.collapse(master, picked, false, null);

    assertExposedGraph(testView, 4, 9);

    // Reuse the collapse variables for a new operation
    master = srcNodes[1];
    picked.clear();
    picked.add(master);
    picked.add(srcNodes[2]);
    testView.collapse(master, picked, false, null);

    assertExposedGraph(testView, 3, 8);
  }

  public void testAutoCollapse() {
    GraphModel testGraph = new GraphModel();
    GraphNode srcNodes[] =
        TestUtils.buildComplete(testGraph, 5, SampleRelation.sampleRelation);

    ViewModel testView = new ViewModel("test", testGraph);
    testView.setNodes(srcNodes);  // This also populates all edges

    assertExposedGraph(testView, 5, 10);

    MultipleDirectedRelationFinder finder =
      new MultipleDirectedRelationFinder();
    finder.addRelation(SampleRelation.sampleRelation, true, false);
    testView.autoCollapse(finder, null);
    assertExposedGraph(testView, 1, 0);

    // Uncollapse each, and check nodes and edges
    testView.uncollapse(srcNodes[0], false, null);
    assertExposedGraph(testView, 2, 4);

    testView.uncollapse(srcNodes[1], false, null);
    assertExposedGraph(testView, 3, 7);

    testView.uncollapse(srcNodes[2], false, null);
    assertExposedGraph(testView, 4, 9);

    testView.uncollapse(srcNodes[3], false, null);
    assertExposedGraph(testView, 5, 10);
  }
}
