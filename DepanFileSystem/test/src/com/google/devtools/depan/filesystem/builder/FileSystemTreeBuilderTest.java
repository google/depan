// Copyright 2010 The Depan Project Authors

package com.google.devtools.depan.filesystem.builder;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.api.GraphBuilder;
import com.google.devtools.depan.model.builder.api.GraphBuilders;
import com.google.devtools.depan.model.builder.chain.DependenciesListener;
import com.google.devtools.depan.model.builder.chain.SimpleDependencyListener;

/**
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class FileSystemTreeBuilderTest {

  @Test
  public void testInsertLeaf() {
    GraphBuilder builder = GraphBuilders.createGraphModelBuilder();
    DependenciesListener listener = new SimpleDependencyListener(builder);
    FileSystemTreeBuilder treeBuilder = new FileSystemTreeBuilder(listener);
    PathInfo leafInfo = new FilePathInfo(new File("this/is/a test/path"));

    GraphNode leaf = treeBuilder.insertLeaf(leafInfo);
    GraphModel test = builder.createGraphModel();

    assertPaths("fs:this/is/a test/path", leaf.getId());
    assertEquals(4, test.getNodes().size());
    assertEquals(3, test.getEdges().size());
  }

  @Test
  public void testInsertLeaf_withDuplicate() {
    GraphBuilder builder = GraphBuilders.createGraphModelBuilder();
    DependenciesListener listener = new SimpleDependencyListener(builder);
    FileSystemTreeBuilder treeBuilder = new FileSystemTreeBuilder(listener);
    PathInfo leafInfo = new FilePathInfo(new File("this/is/a test/path"));

    GraphNode leaf = treeBuilder.insertLeaf(leafInfo);
    GraphModel test = builder.createGraphModel();

    assertPaths("fs:this/is/a test/path", leaf.getId());
    assertEquals(4, test.getNodes().size());
    assertEquals(3, test.getEdges().size());

    GraphNode dupl = treeBuilder.insertLeaf(leafInfo);

    assertEquals(leaf, dupl);
    assertPaths("fs:this/is/a test/path", dupl.getId());
    assertEquals(4, test.getNodes().size());
    assertEquals(3, test.getEdges().size());
  }

  private static void assertPaths(String expectedPath, String actualPath) {
    File expectedFile = new File(expectedPath);
    File actualFile = new File(actualPath);
    assertEquals(expectedFile.getPath(), actualFile.getPath());
  }
}
