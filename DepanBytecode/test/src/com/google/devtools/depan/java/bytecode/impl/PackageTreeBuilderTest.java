/*
 * Copyright 2009 Google Inc.
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

import com.google.devtools.depan.filesystem.graph.DirectoryElement;
import com.google.devtools.depan.filesystem.graph.FileSystemElement;
import com.google.devtools.depan.filesystem.graph.FileSystemRelation;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.java.graph.JavaElement;
import com.google.devtools.depan.java.graph.JavaRelation;
import com.google.devtools.depan.java.graph.PackageElement;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.DependenciesDispatcher;
import com.google.devtools.depan.model.builder.DependenciesListener;
import com.google.devtools.depan.model.builder.ElementFilter;

import junit.framework.TestCase;

import java.io.File;

/**
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class PackageTreeBuilderTest extends TestCase {

  private GraphModel graph;
  private DependenciesListener builder;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    graph = new GraphModel();
    builder = new DependenciesDispatcher(
        ElementFilter.ALL_NODES, graph.getBuilder());
  }

  public void testBasic() {
    PackageTreeBuilder packageBuilder = new PackageTreeBuilder(builder);
    File packageFile = new File("/com/google/depan/view");
    File treeFile = new File("/a/b/c/com/google/depan/view");

    PackageElement packageNode = packageBuilder.installPackageTree(
        packageFile, treeFile);
    assertEquals("java:com.google.depan.view", packageNode.getId());

    assertDirectoryTree("/a/b/c/com", "/google", "com");
    assertDirectoryTree("/a/b/c/com/google", "/depan", "com.google");
    assertDirectoryTree("/a/b/c/com/google/depan", "/view", "com.google.depan");
    assertDirectoryBase("/a/b/c/com/google/depan/view", "com.google.depan.view");

    assertPackageTree("com", ".google");
    assertPackageTree("com.google", ".depan");
    assertPackageTree("com.google.depan", ".view");
  }

  public void testPerverse() {
    PackageTreeBuilder packageBuilder = new PackageTreeBuilder(builder);
    File packageFile = new File("/com/google/depan/view");
    File treeFile = new File("/blix/blax");

    PackageElement packageNode = packageBuilder.installPackageTree(
        packageFile, treeFile);
    assertEquals("java:com.google.depan.view", packageNode.getId());

    assertDirectoryBase("/blix/blax", "com.google.depan.view");
    assertDirectoryPath("/blix", "/blix/blax", "com.google.depan");
    assertDirectoryPath("/blix/..", "/blix", "com.google");
    assertDirectoryPath("/blix/../..", "/blix/..", "com");

    assertPackageTree("com", ".google");
    assertPackageTree("com.google", ".depan");
    assertPackageTree("com.google.depan", ".view");
  }

  private void assertDirectoryTree(
      String parentPath, String childPath, String packageLabel) {
    assertDirectoryPath(parentPath, parentPath + childPath, packageLabel);
  }

  private void assertDirectoryPath(
      String parentPath, String childPath, String packageLabel) {
    DirectoryElement parentNode = getDirectoryElement(parentPath);
    DirectoryElement childNode = getDirectoryElement(childPath);
    PackageElement packageNode = getPackageElement(packageLabel);

    assertEdge(FileSystemRelation.CONTAINS_DIR, parentNode, childNode);
    assertEdge(JavaRelation.PACKAGEDIR, parentNode, packageNode);
  }

  private void assertDirectoryBase(
      String parentPath, String packageLabel) {
    DirectoryElement parentNode = getDirectoryElement(parentPath);
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

  private DirectoryElement getDirectoryElement(String dirPath) {
    DirectoryElement dirNode = (DirectoryElement) graph.findNode(
        FileSystemElement.FILESYSTEM_ID_PREFIX + ":" + dirPath);
    return dirNode;
  }

  private PackageElement getPackageElement(String packageName) {
    PackageElement packageNode = (PackageElement) graph.findNode(
      JavaElement.JAVA_ID_PREFIX + ":" + packageName);
    return packageNode;
  }
}
