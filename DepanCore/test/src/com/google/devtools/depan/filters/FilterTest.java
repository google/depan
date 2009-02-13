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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.basic.MockElement;
import com.google.devtools.depan.graph.basic.MockRelation;
import com.google.devtools.depan.graph.basic.MultipleDirectedRelationFinder;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.RelationshipSetAdapter;
import com.google.devtools.depan.model.interfaces.GraphBuilder;

import junit.framework.TestCase;

import java.util.Collection;

/**
 * Tests <code>PathExpression</code> filters.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public class FilterTest extends TestCase {

  private static Collection<Relation> relations = Lists.newArrayList();
  static {
    for (Relation r : MockRelation.values()) {
      relations.add(r);
    }
  }

  /**
   * Tests PathMatcherTerm.isRecursive()
   */
  public void testPathMatcherTermIsRecursive() {
    MultipleDirectedRelationFinder finder =
        new MultipleDirectedRelationFinder();
    // set the recursiveness to true
    RelationshipSetAdapter setAdapter =
        new RelationshipSetAdapter("Temporary", finder, relations);
    PathMatcherTerm term = new PathMatcherTerm(setAdapter, true, false);

    assertTrue("This PathMatcherTerm has to be recursive",
        term.isRecursive());

    term.setRecursive(false);
    assertFalse("This PathMatcherTerm has to be non-recursive",
        term.isRecursive());
  }

  /**
   * Tests RelationshipSetMatcher.matchBackward(Relation) &
   * RelationshipSetMatcher.matchForward(Relation)
   */
  public void testRelationshipSetMatcherMatchBackwardForward() {
    MultipleDirectedRelationFinder original =
        new MultipleDirectedRelationFinder();

    original.addRelation(MockRelation.CALL, true, false);

    RelationshipSetAdapter relSetAdapter =
        new RelationshipSetAdapter("Temporary", original, relations);

    assertFalse("Must not match backward",
        relSetAdapter.matchBackward(MockRelation.CALL));

    assertTrue("Must match forward",
        relSetAdapter.matchForward(MockRelation.CALL));
  }

  /**
   * Tests MultipleDirectedRelationFinder.toString()
   */
  public void testMultipleDirectedRelationFinderToString() {
    String callString = "(called,caller,F)";
    String classString = "(class,package,R)";
    String extendsString = "(extends,super,F,R)";
    String interfaceString = "";

    assertMultipleDirectedRelationFinderToStringIsCorrect(
        MockRelation.CALL, true, false, callString);
    assertMultipleDirectedRelationFinderToStringIsCorrect(
        MockRelation.CLASS, false, true, classString);
    assertMultipleDirectedRelationFinderToStringIsCorrect(
        MockRelation.EXTENDS, true, true, extendsString);
    assertMultipleDirectedRelationFinderToStringIsCorrect(
        MockRelation.INTERFACE_EXTENDS, false, false, interfaceString);
  }

  /**
   * Checks if the <code>MultipleDirectedRelationFinder.toString()</code>
   * returns the expected <code>String</code>.
   *
   * @param relation The Relation that will be inserted to
   * <code>MultipleDirectedRelationFinder</code> object.
   * @param forward Whether forward relation is true.
   * @param backward Whether backward relation is true.
   * @param expected The expected result.
   */
  private void assertMultipleDirectedRelationFinderToStringIsCorrect(
      Relation relation, boolean forward, boolean backward, String expected) {
    MultipleDirectedRelationFinder original =
        new MultipleDirectedRelationFinder();
    original.addRelation(relation, forward, backward);

    assertEquals(expected, original.toString());
  }
  /**
   * Tests RelationshipSetMatcher.getDisplayName()
   */
  public void testRelationshipSetMatcherGetDisplayName() {
    String name = "Containers";
    MultipleDirectedRelationFinder multipleFinder =
        new MultipleDirectedRelationFinder();
    RelationshipSetAdapter setAdapter =
        new RelationshipSetAdapter(name, multipleFinder, relations);

    assertEquals(name, setAdapter.getDisplayName());
  }

  /**
   * Tests non-recursive version of RelationshipSetMatcher.nextMatch()
   */
  public void testPathMatcherTermNextMatchNonRecursive() {
    GraphModel graph = new GraphModel();
    GraphNode[] nodes = fillGraphModel(graph);

    MultipleDirectedRelationFinder finder =
        new MultipleDirectedRelationFinder();

    finder.addRelation(MockRelation.DIRECTORY, true, false);

    // non-recursive
    RelationshipSetAdapter setAdapter =
        new RelationshipSetAdapter("Temporary", finder, relations);
    PathMatcherTerm term = new PathMatcherTerm(setAdapter, false, false);

    Collection<GraphNode> output = term.getPathMatcher().nextMatch(graph,
        buildSingleSet(nodes[0]));

    assertEquals(1, output.size());
    assertGraphContainsElement(output, nodes, 1);
  }

  /**
   * Tests non-recursive version of
   * PathExpression.nextMatch(GraphModel, Collection<GraphNode>)
   */
  public void testPathExpressionNextMatchNonRecursive() {
    GraphModel graph = new GraphModel();
    GraphNode[] nodes = fillGraphModel(graph);

    PathExpression pathExpression = createPathExpression(false, false, false);

    Collection<GraphNode> output = pathExpression.nextMatch(graph,
        buildSingleSet(nodes[0]));

    assertEquals(1, output.size());
    assertGraphContainsElement(output, nodes, 6);
  }

  /**
   * Tests recursive version of PathExpression.nextMatch()
   */
  public void testPathExpressionNextMatchRecursiveShort() {
    GraphModel graph = new GraphModel();
    GraphNode[] nodes = fillGraphModel(graph);

    MultipleDirectedRelationFinder finder =
        new MultipleDirectedRelationFinder();

    finder.addRelation(MockRelation.DIRECTORY, true, false);

    // make it recursive
    RelationshipSetAdapter relSetAdapter =
        new RelationshipSetAdapter("Temporary", finder, relations);
    PathMatcherTerm term = new PathMatcherTerm(relSetAdapter, true, false);

    PathExpression pathExp = new PathExpression();
    pathExp.addPathMatcher(term);

    Collection<GraphNode> output = pathExp.nextMatch(graph,
        buildSingleSet(nodes[0]));

    assertEquals(2, output.size());
    assertGraphContainsElement(output, nodes, 1);
    assertGraphContainsElement(output, nodes, 2);
  }

  /**
   * Tests recursive version of
   * PathExpression.nextMatch(GraphModel, Collection<GraphNode>)
   */
  public void testPathExpressionNextMatchRecursive() {
    GraphModel graph = new GraphModel();
    GraphNode[] nodes = fillGraphModel(graph);

    PathExpression pathExpression = createPathExpression(true, false, true);

    Collection<GraphNode> output = pathExpression.nextMatch(graph,
        buildSingleSet(nodes[0]));

    assertEquals(2, output.size());
    assertGraphContainsElement(output, nodes, 4);
    assertGraphContainsElement(output, nodes, 6);
  }

  /**
   * Tests non-recursive and cumulative version of
   * PathExpression.nextMatch(GraphModel, Collection<GraphNode>)
   */
  public void testPathExpressionNextMatchCumulativeNonRecursive() {

    GraphModel graph = new GraphModel();
    GraphNode[] nodes = fillGraphModel(graph);

    PathExpression pathExpression = createPathExpression(false, true, false);

    Collection<GraphNode> output = pathExpression.nextMatch(graph,
        buildSingleSet(nodes[0]));

    assertEquals(2, output.size());
    assertGraphContainsElement(output, nodes, 5);
    assertGraphContainsElement(output, nodes, 6);
  }

  /**
   * Tests recursive and cumulative version of
   * PathExpression.nextMatch(GraphModel, Collection<GraphNode>)
   */
  public void testPathExpressionNextMatchCumulativeRecursive() {

    PathExpression pathExpression = createPathExpression(true, true, true);

    GraphModel graph = new GraphModel();
    GraphNode[] nodes = fillGraphModel(graph);

    Collection<GraphNode> output = pathExpression.nextMatch(graph,
        buildSingleSet(nodes[0]));

    assertEquals(6, output.size());
    assertGraphContainsElement(output, nodes, 1);
    assertGraphContainsElement(output, nodes, 2);
    assertGraphContainsElement(output, nodes, 3);
    assertGraphContainsElement(output, nodes, 4);
    assertGraphContainsElement(output, nodes, 5);
    assertGraphContainsElement(output, nodes, 6);
  }

  private void assertGraphContainsElement(
      Collection<GraphNode> output, GraphNode[] nodes, int index) {
    assertTrue("Output does not contain the " + index + " element in the graph",
        output.contains(nodes[index]));
  }

  /**
   * Creates a new input set including the given node.
   *
   * @param inputNode The input.
   * @return The new set.
   */
  private Collection<GraphNode> buildSingleSet(GraphNode inputNode) {
    Collection<GraphNode> input = Sets.newHashSet();
    input.add(inputNode);
    return input;
  }

  /**
   * Creates a PathExpression from a template.
   *
   * @param twoFiltersInFirstMatcher Denotes whether there should be a DIRECTORY
   * relationship along with a CLASSFILE in the first <code>PathMatcher</code>.
   * @param cumulativeSecondMatcher Denotes whether the second
   * <code>PathMatcher</code> should be cumulative.
   * @param recursive Denotes whether the first and the second
   * <code>PathMatcher</code>s should be recursive.
   * @return A new <code>PathExpression</code> with the given properties.
   */
  private PathExpression createPathExpression(boolean twoFiltersInFirstMatcher,
      boolean cumulativeSecondMatcher, boolean recursive) {
    PathExpression pathExpression = new PathExpression();
    MultipleDirectedRelationFinder finder;
    PathMatcherTerm term;

    finder = new MultipleDirectedRelationFinder();
    finder.addRelation(MockRelation.CLASSFILE, true, false);

    if (twoFiltersInFirstMatcher) {
      finder.addRelation(MockRelation.DIRECTORY, true, false);
    }

    RelationshipSetAdapter setAdapter =
        new RelationshipSetAdapter("Temporary", finder, relations);
    term = new PathMatcherTerm(setAdapter, recursive, false);
    pathExpression.addPathMatcher(term);

    finder = new MultipleDirectedRelationFinder();
    finder.addRelation(MockRelation.CLASS, true, false);
    // cumulative!
    setAdapter = new RelationshipSetAdapter("Temporary", finder, relations);
    term = new PathMatcherTerm(setAdapter, recursive, cumulativeSecondMatcher);
    pathExpression.addPathMatcher(term);
    return pathExpression;
  }

  /**
   * Creates new GraphNodes, adds edges and puts them in the provided graph so
   * that it can be used in tests.
   *
   * @param graph The <code>GraphModel</code> that will be filled with nodes.
   * @return The nodes array that holds the newly-created <code>GraphNode</code>
   * objects.
   */
  private GraphNode[] fillGraphModel(GraphModel graph) {
    GraphNode[] nodes = new GraphNode[7];
    GraphBuilder builder = graph.getBuilder();

    nodes[0] = builder.newNode(new MockElement("Package1"));
    nodes[1] = builder.newNode(new MockElement("Package2"));
    nodes[2] = builder.newNode(new MockElement("Package3"));
    nodes[3] = builder.newNode(new MockElement("DirectoryElement1"));
    nodes[4] = builder.newNode(new MockElement("Source1"));
    nodes[5] = builder.newNode(new MockElement("DirectoryElement2"));
    nodes[6] = builder.newNode(new MockElement("Source2"));

    graph.addEdge(MockRelation.DIRECTORY, nodes[0], nodes[1]);
    graph.addEdge(MockRelation.DIRECTORY, nodes[1], nodes[2]);
    graph.addEdge(MockRelation.CLASSFILE, nodes[2], nodes[3]);
    graph.addEdge(MockRelation.CLASS, nodes[3], nodes[4]);
    graph.addEdge(MockRelation.CLASSFILE, nodes[0], nodes[5]);
    graph.addEdge(MockRelation.CLASS, nodes[5], nodes[6]);
    return nodes;
  }
}
