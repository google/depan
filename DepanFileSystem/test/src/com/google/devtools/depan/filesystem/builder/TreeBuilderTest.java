/*
 * Copyright 2010 The Depan Project Authors
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

package com.google.devtools.depan.filesystem.builder;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.ElementVisitor;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.api.GraphBuilder;
import com.google.devtools.depan.model.builder.api.GraphBuilders;

/**
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class TreeBuilderTest {

  @Test
  public void testInsertLeaf() {

    MockBuilder treeBuilder = new MockBuilder();
    MockPathInfo leafInfo = new MockPathInfo(new File("this/is/a test/path"));
    GraphNode leaf = treeBuilder.insertLeaf(leafInfo);
    GraphModel test = treeBuilder.buildGraph();

    assertPaths("tst:this/is/a test/path", leaf.getId());
    assertEquals(4, test.getNodes().size());
    assertEquals(3, test.getEdges().size());
  }

  @Test
  public void testInsertLeaf_withDuplicate() {
    MockBuilder treeBuilder = new MockBuilder();
    MockPathInfo leafInfo = new MockPathInfo(new File("this/is/a test/path"));
    GraphNode leaf = treeBuilder.insertLeaf(leafInfo);
    GraphModel test = treeBuilder.buildGraph();

    assertPaths("tst:this/is/a test/path", leaf.getId());
    assertEquals(4, test.getNodes().size());
    assertEquals(3, test.getEdges().size());

    GraphNode dupl = treeBuilder.insertLeaf(leafInfo);

    assertEquals(leaf, dupl);
    assertPaths("tst:this/is/a test/path", dupl.getId());
    assertEquals(4, test.getNodes().size());
    assertEquals(3, test.getEdges().size());
}

  private static class MockBuilder extends TreeBuilder {
    private final GraphBuilder builder =
        GraphBuilders.createGraphModelBuilder();

    @Override
    protected void insertEdge(
        GraphNode parentNode, GraphNode childNode, PathInfo childInfo) {
      GraphEdge treeEdge = new GraphEdge(
        builder.mapNode(parentNode), builder.mapNode(childNode),
        childInfo.getToRelation());
      builder.addEdge(treeEdge);
    }

    @Override
    protected GraphNode lookupNode(PathInfo path) {
      GraphNode probe = path.createNode();
      return builder.findNode(probe.getId());
    }

    public GraphModel buildGraph() {
      return builder.createGraphModel();
    }
  }

  private static class MockPathInfo implements PathInfo {

    private final File file;

    public MockPathInfo(File file) {
      this.file = file;
    }

    @Override
    public GraphNode createNode() {
      return new MockNode(file);
    }

    @Override
    public String getId() {
      return createNode().getId();
    }

    @Override
    public PathInfo getParentInfo() {
      File parent = file.getParentFile();
      if (null == parent) {
        return null;
      }
      return new MockPathInfo(parent);
    }

    @Override
    public Relation getToRelation() {
      return MockRelation.MOCK;
    }
  }

  private static class MockNode extends GraphNode {
    private final File path;

    public MockNode(File path) {
      super();
      this.path = path;
    }

    @Override
    public String friendlyString() {
      return path.getPath();
    }

    @Override
    public void accept(ElementVisitor visitor) {
    }

    @Override
    public String getId() {
      return "tst:" + friendlyString();
    }
  }

  private static class MockRelation implements Relation {
    public final static Relation MOCK = new MockRelation();

    @Override
    public String getForwardName() {
      return "contains";
    }

    @Override
    public String getReverseName() {
      return "member";
    }
  }

  private static void assertPaths(String expectedPath, String actualPath) {
    File expectedFile = new File(expectedPath);
    File actualFile = new File(actualPath);
    assertEquals(expectedFile.getPath(), actualFile.getPath());
  }
}
