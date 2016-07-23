/*
 * Copyright 2009 The Depan Project Authors
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

package com.google.devtools.depan.java.bytecode.impl;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.google.devtools.depan.filesystem.graph.DirectoryElement;
import com.google.devtools.depan.filesystem.graph.FileSystemElement;
import com.google.devtools.depan.filesystem.graph.FileSystemRelation;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.java.graph.JavaElement;
import com.google.devtools.depan.java.graph.JavaRelation;
import com.google.devtools.depan.java.graph.PackageElement;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.api.GraphBuilder;
import com.google.devtools.depan.model.builder.api.GraphBuilders;
import com.google.devtools.depan.model.builder.chain.DependenciesDispatcher;
import com.google.devtools.depan.model.builder.chain.DependenciesListener;
import com.google.devtools.depan.model.builder.chain.ElementFilter;

/**
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class PackageTreeBuilderTest {

  private GraphBuilder graphBuilder;

  private DependenciesListener builder;

  private GraphModel graph;

  @Before
  public void setUp() throws Exception {
    graphBuilder = GraphBuilders.createGraphModelBuilder();
    builder = new DependenciesDispatcher(
        ElementFilter.ALL_NODES, graphBuilder);
  }

  @Test
  public void testBasic() {
    PackageTreeBuilder packageBuilder = new PackageTreeBuilder(builder);
    File packageFile = new File("com/google/depan/view");
    File treeFile = new File("/a/b/c/com/google/depan/view");

    PackageElement packageNode = packageBuilder.installPackageTree(
        packageFile, treeFile);
    assertEquals("java:com.google.depan.view", packageNode.getId());

    graph = graphBuilder.createGraphModel();
    assertDirectoryTree("/a/b/c/com", "google", "com");
    assertDirectoryTree("/a/b/c/com/google", "depan", "com.google");
    assertDirectoryTree("/a/b/c/com/google/depan", "view", "com.google.depan");
    assertDirectoryBase("/a/b/c/com/google/depan/view", "com.google.depan.view");

    assertPackageTree("com", ".google");
    assertPackageTree("com.google", ".depan");
    assertPackageTree("com.google.depan", ".view");
  }

  @Test
  public void testPerverse() {
    PackageTreeBuilder packageBuilder = new PackageTreeBuilder(builder);
    File packageFile = new File("com/google/depan/view");
    File treeFile = new File("/blix/blax");

    PackageElement packageNode = packageBuilder.installPackageTree(
        packageFile, treeFile);
    assertEquals("java:com.google.depan.view", packageNode.getId());

    graph = graphBuilder.createGraphModel();
    assertDirectoryBase("/blix/blax", "com.google.depan.view");
    assertDirectoryPath("/blix", "/blix/blax", "com.google.depan");

    // TODO: How are these supposed to work on a Unix file system?
    // assertDirectoryPath("/blix/..", "/blix", "com.google");
    // assertDirectoryPath("/blix/../..", "/blix/..", "com");

    assertPackageTree("com", ".google");
    assertPackageTree("com.google", ".depan");
    assertPackageTree("com.google.depan", ".view");
  }

  private void assertDirectoryTree(
      String parentPath, String childName, String packageLabel) {
    File parentFile = new File(parentPath);
    File childFile = new File(parentPath, childName);
    assertDirectoryPath(parentFile, childFile, packageLabel); }

  private void assertDirectoryPath(
      String parentPath, String childPath, String packageLabel) {
    File parentFile = new File(parentPath);
    File childFile = new File(childPath);
    assertDirectoryPath(parentFile, childFile, packageLabel);
  }

  private void assertDirectoryPath(
      File parentFile, File childFile, String packageLabel) {
    DirectoryElement parentNode = getDirectoryElement(parentFile);
    DirectoryElement childNode = getDirectoryElement(childFile);
    PackageElement packageNode = getPackageElement(packageLabel);

    assertEdge(FileSystemRelation.CONTAINS_DIR, parentNode, childNode);
    assertEdge(JavaRelation.PACKAGEDIR, parentNode, packageNode);
  }

  private void assertDirectoryBase(
      String parentPath, String packageLabel) {
    File parentFile = new File(parentPath);
    DirectoryElement parentNode = getDirectoryElement(parentFile);
    PackageElement packageNode = getPackageElement(packageLabel);

    assertEdge(JavaRelation.PACKAGEDIR, parentNode, packageNode);
  }

  private void assertPackageTree(
      String parentLabel, String childSuffix) {
    PackageElement parentPackage = getPackageElement(parentLabel);
    PackageElement childPackage = getPackageElement(parentLabel + childSuffix);

    assertEdge(JavaRelation.PACKAGE, parentPackage, childPackage);
  }

  private void assertEdge(Relation kind, GraphNode head, GraphNode tail) {
    assertNotNull(graph.findEdge(kind, head, tail));
  }

  private DirectoryElement getDirectoryElement(File dirPath) {
    DirectoryElement dirNode = (DirectoryElement) graph.findNode(
        FileSystemElement.FILESYSTEM_ID_PREFIX + ":" + dirPath.getPath());
    return dirNode;
  }

  private PackageElement getPackageElement(String packageName) {
    PackageElement packageNode = (PackageElement) graph.findNode(
      JavaElement.JAVA_ID_PREFIX + ":" + packageName);
    return packageNode;
  }
}
