/*
 * Copyright 2008 Google Inc.
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

package com.google.devtools.depan.filters;

import com.google.devtools.depan.filters.RelationCountMatcher.EdgeCountPredicate;
import com.google.devtools.depan.filters.RelationCountMatcher.IncludeAbove;
import com.google.devtools.depan.filters.RelationCountMatcher.IncludeBelow;
import com.google.devtools.depan.filters.RelationCountMatcher.IncludeEquals;
import com.google.devtools.depan.filters.RelationCountMatcher.IncludeInRange;
import com.google.devtools.depan.filters.RelationCountMatcher.IncludeOutside;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.RelationshipSetAdapter;
import com.google.devtools.depan.model.testing.TestUtils;
import com.google.devtools.depan.view.SampleRelation;

import junit.framework.TestCase;

import java.util.Collection;
import java.util.Set;

public class RelationCountMatcherTest extends TestCase {

  public void testNextMatch_ignore() {
    TestData testData = new TestData(5, true, true);

    RelationCountMatcher matcher =
        new RelationCountMatcher(testData.countSet, null, null);
    assertTrue(testData.matchNodes(matcher).isEmpty());
  }

  public void testNextMatch_above() {
    TestData testData = new TestData(5, true, true);

    EdgeCountPredicate countTest = new IncludeAbove(3);
    RelationCountMatcher forwardMatcher =
        new RelationCountMatcher(testData.countSet, countTest, null);

    Collection<GraphNode> forwardMatch = testData.matchNodes(forwardMatcher);
    assertEquals(1, forwardMatch.size());
    assertTrue(forwardMatch.contains(testData.nodeArray[0]));

    RelationCountMatcher reverseMatcher =
        new RelationCountMatcher(testData.countSet, null, countTest);

    Collection<GraphNode> reverseMatch = testData.matchNodes(reverseMatcher);
    assertEquals(1, reverseMatch.size());
    assertTrue(reverseMatch.contains(testData.nodeArray[4]));
  }

  public void testNextMatch_below() {
    TestData testData = new TestData(5, true, true);

    EdgeCountPredicate countTest = new IncludeBelow(3);
    RelationCountMatcher forwardMatcher =
        new RelationCountMatcher(testData.countSet, countTest, null);

    Collection<GraphNode> forwardMatch = testData.matchNodes(forwardMatcher);
    assertEquals(3, forwardMatch.size());
    assertTrue(forwardMatch.contains(testData.nodeArray[2]));
    assertTrue(forwardMatch.contains(testData.nodeArray[3]));
    assertTrue(forwardMatch.contains(testData.nodeArray[4]));

    RelationCountMatcher reverseMatcher =
        new RelationCountMatcher(testData.countSet, null, countTest);

    Collection<GraphNode> reverseMatch = testData.matchNodes(reverseMatcher);
    assertEquals(3, reverseMatch.size());
    assertTrue(reverseMatch.contains(testData.nodeArray[0]));
    assertTrue(reverseMatch.contains(testData.nodeArray[1]));
    assertTrue(reverseMatch.contains(testData.nodeArray[2]));
  }

  // Zero is somewhat a special case, so ensure that we get this one right
  public void testNextMatch_zero() {
    TestData testData = new TestData(5, true, true);

    EdgeCountPredicate countTest = new IncludeEquals(0);
    RelationCountMatcher forwardMatcher =
        new RelationCountMatcher(testData.countSet, countTest, null);

    Collection<GraphNode> forwardMatch = testData.matchNodes(forwardMatcher);
    assertEquals(1, forwardMatch.size());
    assertTrue(forwardMatch.contains(testData.nodeArray[4]));

    RelationCountMatcher reverseMatcher =
        new RelationCountMatcher(testData.countSet, null, countTest);

    Collection<GraphNode> reverseMatch = testData.matchNodes(reverseMatcher);
    assertEquals(1, reverseMatch.size());
    assertTrue(reverseMatch.contains(testData.nodeArray[0]));
  }

  public void testNextMatch_equals() {
    TestData testData = new TestData(5, true, true);

    EdgeCountPredicate countTest = new IncludeEquals(2);
    RelationCountMatcher forwardMatcher =
        new RelationCountMatcher(testData.countSet, countTest, null);

    Collection<GraphNode> forwardMatch = testData.matchNodes(forwardMatcher);
    assertEquals(1, forwardMatch.size());
    assertTrue(forwardMatch.contains(testData.nodeArray[2]));

    RelationCountMatcher reverseMatcher =
        new RelationCountMatcher(testData.countSet, null, countTest);

    Collection<GraphNode> reverseMatch = testData.matchNodes(reverseMatcher);
    assertEquals(1, reverseMatch.size());
    assertTrue(reverseMatch.contains(testData.nodeArray[2]));
  }

  public void testNextMatch_range() {
    TestData testData = new TestData(5, true, true);

    EdgeCountPredicate countTest = new IncludeInRange(3, 10);
    RelationCountMatcher forwardMatcher =
        new RelationCountMatcher(testData.countSet, countTest, null);

    Collection<GraphNode> forwardMatch = testData.matchNodes(forwardMatcher);
    assertEquals(2, forwardMatch.size());
    assertTrue(forwardMatch.contains(testData.nodeArray[0]));
    assertTrue(forwardMatch.contains(testData.nodeArray[1]));

    RelationCountMatcher reverseMatcher =
        new RelationCountMatcher(testData.countSet, null, countTest);

    Collection<GraphNode> reverseMatch = testData.matchNodes(reverseMatcher);
    assertEquals(2, reverseMatch.size());
    assertTrue(reverseMatch.contains(testData.nodeArray[3]));
    assertTrue(reverseMatch.contains(testData.nodeArray[4]));
  }

  public void testNextMatch_outside() {
    TestData testData = new TestData(5, true, true);

    EdgeCountPredicate countTest = new IncludeOutside(1, 2);
    RelationCountMatcher forwardMatcher =
        new RelationCountMatcher(testData.countSet, countTest, null);

    Collection<GraphNode> forwardMatch = testData.matchNodes(forwardMatcher);
    assertEquals(3, forwardMatch.size());
    assertTrue(forwardMatch.contains(testData.nodeArray[0]));
    assertTrue(forwardMatch.contains(testData.nodeArray[1]));
    assertTrue(forwardMatch.contains(testData.nodeArray[4]));

    RelationCountMatcher reverseMatcher =
        new RelationCountMatcher(testData.countSet, null, countTest);

    Collection<GraphNode> reverseMatch = testData.matchNodes(reverseMatcher);
    assertEquals(3, reverseMatch.size());
    assertTrue(reverseMatch.contains(testData.nodeArray[0]));
    assertTrue(reverseMatch.contains(testData.nodeArray[3]));
    assertTrue(reverseMatch.contains(testData.nodeArray[4]));
  }

  private static class TestData {

    public GraphModel testModel;
    public GraphNode[] nodeArray;
    public Set<GraphNode>nodeSet;
    public RelationshipSetAdapter countSet;

    private TestData(int size, boolean forward, boolean reverse) {
      testModel = new GraphModel();
      nodeArray = TestUtils.buildComplete(
          testModel, size, SampleRelation.sampleRelation);
      nodeSet = TestUtils.toSet(nodeArray);

      countSet = new RelationshipSetAdapter("count");
      countSet.addRelation(SampleRelation.sampleRelation, forward, reverse);
    }

    public Collection<GraphNode> matchNodes(PathMatcher matcher) {
      return matcher.nextMatch(testModel, nodeSet);
    }
  }
}
