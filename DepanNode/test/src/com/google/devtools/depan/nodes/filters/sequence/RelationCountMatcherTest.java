/*
 * Copyright 2008 The Depan Project Authors
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

package com.google.devtools.depan.nodes.filters.sequence;

import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.RelationSets;
import com.google.devtools.depan.nodes.filters.context.MapContext;
import com.google.devtools.depan.nodes.filters.model.ContextKey;
import com.google.devtools.depan.nodes.filters.model.ContextKey.Base;
import com.google.devtools.depan.nodes.filters.model.ContextualFilter;
import com.google.devtools.depan.nodes.filters.sequence.CountPredicates.IncludeAbove;
import com.google.devtools.depan.nodes.filters.sequence.CountPredicates.IncludeBelow;
import com.google.devtools.depan.nodes.filters.sequence.CountPredicates.IncludeEquals;
import com.google.devtools.depan.nodes.filters.sequence.CountPredicates.IncludeInRange;
import com.google.devtools.depan.nodes.filters.sequence.CountPredicates.IncludeOutside;
import com.google.devtools.depan.test.TestUtils;

import com.google.common.collect.Maps;

import junit.framework.TestCase;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class RelationCountMatcherTest extends TestCase {

  public void testNextMatch_ignore() {
    TestData testData = new TestData(5, true, true);

    RelationCountFilter matcher =
        new RelationCountFilter(testData.countSet, null, null);
    assertTrue(testData.computeNodes(matcher).isEmpty());
  }

  public void testNextMatch_above() {
    TestData testData = new TestData(5, true, true);

    CountPredicate countTest = new IncludeAbove(3);
    RelationCountFilter forwardMatcher =
        new RelationCountFilter(testData.countSet, countTest, null);

    Collection<GraphNode> forwardMatch = testData.computeNodes(forwardMatcher);
    assertEquals(1, forwardMatch.size());
    assertTrue(forwardMatch.contains(testData.nodeArray[0]));

    RelationCountFilter reverseMatcher =
        new RelationCountFilter(testData.countSet, null, countTest);

    Collection<GraphNode> reverseMatch = testData.computeNodes(reverseMatcher);
    assertEquals(1, reverseMatch.size());
    assertTrue(reverseMatch.contains(testData.nodeArray[4]));
  }

  public void testNextMatch_below() {
    TestData testData = new TestData(5, true, true);

    CountPredicate countTest = new IncludeBelow(3);
    RelationCountFilter forwardMatcher =
        new RelationCountFilter(testData.countSet, countTest, null);

    Collection<GraphNode> forwardMatch = testData.computeNodes(forwardMatcher);
    assertEquals(3, forwardMatch.size());
    assertTrue(forwardMatch.contains(testData.nodeArray[2]));
    assertTrue(forwardMatch.contains(testData.nodeArray[3]));
    assertTrue(forwardMatch.contains(testData.nodeArray[4]));

    RelationCountFilter reverseMatcher =
        new RelationCountFilter(testData.countSet, null, countTest);

    Collection<GraphNode> reverseMatch = testData.computeNodes(reverseMatcher);
    assertEquals(3, reverseMatch.size());
    assertTrue(reverseMatch.contains(testData.nodeArray[0]));
    assertTrue(reverseMatch.contains(testData.nodeArray[1]));
    assertTrue(reverseMatch.contains(testData.nodeArray[2]));
  }

  // Zero is somewhat a special case, so ensure that we get this one right
  public void testNextMatch_zero() {
    TestData testData = new TestData(5, true, true);

    CountPredicate countTest = new IncludeEquals(0);
    RelationCountFilter forwardMatcher =
        new RelationCountFilter(testData.countSet, countTest, null);

    Collection<GraphNode> forwardMatch = testData.computeNodes(forwardMatcher);
    assertEquals(1, forwardMatch.size());
    assertTrue(forwardMatch.contains(testData.nodeArray[4]));

    RelationCountFilter reverseMatcher =
        new RelationCountFilter(testData.countSet, null, countTest);

    Collection<GraphNode> reverseMatch = testData.computeNodes(reverseMatcher);
    assertEquals(1, reverseMatch.size());
    assertTrue(reverseMatch.contains(testData.nodeArray[0]));
  }

  public void testNextMatch_equals() {
    TestData testData = new TestData(5, true, true);

    CountPredicate countTest = new IncludeEquals(2);
    RelationCountFilter forwardMatcher =
        new RelationCountFilter(testData.countSet, countTest, null);

    Collection<GraphNode> forwardMatch = testData.computeNodes(forwardMatcher);
    assertEquals(1, forwardMatch.size());
    assertTrue(forwardMatch.contains(testData.nodeArray[2]));

    RelationCountFilter reverseMatcher =
        new RelationCountFilter(testData.countSet, null, countTest);

    Collection<GraphNode> reverseMatch = testData.computeNodes(reverseMatcher);
    assertEquals(1, reverseMatch.size());
    assertTrue(reverseMatch.contains(testData.nodeArray[2]));
  }

  public void testNextMatch_range() {
    TestData testData = new TestData(5, true, true);

    CountPredicate countTest = new IncludeInRange(3, 10);
    RelationCountFilter forwardMatcher =
        new RelationCountFilter(testData.countSet, countTest, null);

    Collection<GraphNode> forwardMatch = testData.computeNodes(forwardMatcher);
    assertEquals(2, forwardMatch.size());
    assertTrue(forwardMatch.contains(testData.nodeArray[0]));
    assertTrue(forwardMatch.contains(testData.nodeArray[1]));

    RelationCountFilter reverseMatcher =
        new RelationCountFilter(testData.countSet, null, countTest);

    Collection<GraphNode> reverseMatch = testData.computeNodes(reverseMatcher);
    assertEquals(2, reverseMatch.size());
    assertTrue(reverseMatch.contains(testData.nodeArray[3]));
    assertTrue(reverseMatch.contains(testData.nodeArray[4]));
  }

  public void testNextMatch_outside() {
    TestData testData = new TestData(5, true, true);

    CountPredicate countTest = new IncludeOutside(1, 2);
    RelationCountFilter forwardMatcher =
        new RelationCountFilter(testData.countSet, countTest, null);

    Collection<GraphNode> forwardMatch = testData.computeNodes(forwardMatcher);
    assertEquals(3, forwardMatch.size());
    assertTrue(forwardMatch.contains(testData.nodeArray[0]));
    assertTrue(forwardMatch.contains(testData.nodeArray[1]));
    assertTrue(forwardMatch.contains(testData.nodeArray[4]));

    RelationCountFilter reverseMatcher =
        new RelationCountFilter(testData.countSet, null, countTest);

    Collection<GraphNode> reverseMatch = testData.computeNodes(reverseMatcher);
    assertEquals(3, reverseMatch.size());
    assertTrue(reverseMatch.contains(testData.nodeArray[0]));
    assertTrue(reverseMatch.contains(testData.nodeArray[3]));
    assertTrue(reverseMatch.contains(testData.nodeArray[4]));
  }

  private static class TestData {

    public GraphModel testModel;
    public GraphNode[] nodeArray;
    public Set<GraphNode>nodeSet;
    private RelationSet countSet;
    private MapContext testContext;

    private TestData(int size, boolean forward, boolean reverse) {
      nodeArray = TestUtils.buildNodes(size);
      testModel = TestUtils.buildComplete(nodeArray, TestUtils.RELATION);
      nodeSet = TestUtils.toSet(nodeArray);

      countSet = RelationSets.createSingle(TestUtils.RELATION);

      Map<ContextKey, Object> mappings = Maps.newHashMap();
      mappings.put(Base.UNIVERSE, testModel);
      testContext = new MapContext(mappings);
    }

    public Collection<GraphNode> computeNodes(ContextualFilter filter) {
      filter.receiveContext(testContext);
      return filter.computeNodes(nodeSet);
    }
  }
}
