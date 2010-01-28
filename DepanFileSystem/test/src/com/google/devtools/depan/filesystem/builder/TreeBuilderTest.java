// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.devtools.depan.filesystem.builder;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.ElementVisitor;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.interfaces.GraphBuilder;

import junit.framework.TestCase;

import java.io.File;

/**
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class TreeBuilderTest extends TestCase {

  public void testInsertLeaf() {
    GraphModel test = new GraphModel();

    MockBuilder treeBuilder = new MockBuilder(test.getBuilder());
    MockPathInfo leafInfo = new MockPathInfo(new File("this/is/a test/path"));
    GraphNode leaf = treeBuilder.insertLeaf(leafInfo);

    assertEquals("tst:this/is/a test/path", leaf.getId());
    assertEquals(4, test.getNodes().size());
    assertEquals(3, test.getEdges().size());
  }

  public void testInsertLeaf_withDuplicate() {
    GraphModel test = new GraphModel();

    MockBuilder treeBuilder = new MockBuilder(test.getBuilder());
    MockPathInfo leafInfo = new MockPathInfo(new File("this/is/a test/path"));
    GraphNode leaf = treeBuilder.insertLeaf(leafInfo);

    assertEquals("tst:this/is/a test/path", leaf.getId());
    assertEquals(4, test.getNodes().size());
    assertEquals(3, test.getEdges().size());

    GraphNode dupl = treeBuilder.insertLeaf(leafInfo);

    assertEquals(leaf, dupl);
    assertEquals("tst:this/is/a test/path", dupl.getId());
    assertEquals(4, test.getNodes().size());
    assertEquals(3, test.getEdges().size());
}

  private static class MockBuilder extends TreeBuilder {
    private final GraphBuilder builder;

    public MockBuilder(GraphBuilder builder) {
      super();
      this.builder = builder;
    }

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
      return (GraphNode) builder.getGraph().findNode(probe.getId());
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
}
