// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.devtools.depan.filesystem.builder;

import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.DependenciesListener;
import com.google.devtools.depan.model.builder.SimpleDependencyListener;

import junit.framework.TestCase;

import java.io.File;

/**
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class FileSystemTreeBuilderTest extends TestCase {

  public void testInsertLeaf() {
    GraphModel test = new GraphModel();
    DependenciesListener builder = new SimpleDependencyListener(test.getBuilder());
    FileSystemTreeBuilder treeBuilder = new FileSystemTreeBuilder(builder);
    PathInfo leafInfo = new FilePathInfo(new File("this/is/a test/path"));

    GraphNode leaf = treeBuilder.insertLeaf(leafInfo);

    assertEquals("fs:this/is/a test/path", leaf.getId());
    assertEquals(4, test.getNodes().size());
    assertEquals(3, test.getEdges().size());
  }

  public void testInsertLeaf_withDuplicate() {
    GraphModel test = new GraphModel();
    DependenciesListener builder = new SimpleDependencyListener(test.getBuilder());
    FileSystemTreeBuilder treeBuilder = new FileSystemTreeBuilder(builder);
    PathInfo leafInfo = new FilePathInfo(new File("this/is/a test/path"));

    GraphNode leaf = treeBuilder.insertLeaf(leafInfo);

    assertEquals("fs:this/is/a test/path", leaf.getId());
    assertEquals(4, test.getNodes().size());
    assertEquals(3, test.getEdges().size());

    GraphNode dupl = treeBuilder.insertLeaf(leafInfo);

    assertEquals(leaf, dupl);
    assertEquals("fs:this/is/a test/path", dupl.getId());
    assertEquals(4, test.getNodes().size());
    assertEquals(3, test.getEdges().size());
  }
}
