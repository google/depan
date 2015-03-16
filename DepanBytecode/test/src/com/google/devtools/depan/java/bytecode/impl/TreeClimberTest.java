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

import org.junit.Test;

/**
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class TreeClimberTest {

  @Test
  public void testRootedTree() {
    TreeClimber treeClimber = new TreeClimber(new File("/rooted/tree"));
    assertPaths("/rooted/tree", treeClimber.getTreePath());

    treeClimber.ascendTree();
    assertPaths("/rooted", treeClimber.getTreePath());

    // TODO: Fix for Windows
    // treeClimber.ascendTree();
    // assertPaths("/rooted/..", treeClimber.getTreePath());

    // treeClimber.ascendTree();
    // assertPaths("/rooted/../..", treeClimber.getTreePath());
  }

  @Test
  public void testRelativeTree() {
    TreeClimber treeClimber = new TreeClimber(new File("relative/tree"));
    assertPaths("relative/tree", treeClimber.getTreePath());

    treeClimber.ascendTree();
    assertPaths("relative", treeClimber.getTreePath());

    treeClimber.ascendTree();
    assertPaths("relative/..", treeClimber.getTreePath());

    treeClimber.ascendTree();
    assertPaths("relative/../..", treeClimber.getTreePath());
  }

  @Test
  public void testEmptyRoot() {
    TreeClimber treeClimber = new TreeClimber(new File("/"));
    assertPaths("/", treeClimber.getTreePath());

    // TODO: Fix for Windows
    // treeClimber.ascendTree();
    // assertPaths("/..", treeClimber.getTreePath());
  }

  @Test
  public void testEmptyTree() {
    TreeClimber treeClimber = new TreeClimber(new File(""));
    assertPaths("", treeClimber.getTreePath());

    treeClimber.ascendTree();
    assertPaths("./..", treeClimber.getTreePath());
  }

  @Test
  public void testRelativeBase() {
    TreeClimber treeClimber = new TreeClimber(new File("./base"));
    assertPaths("./base", treeClimber.getTreePath());

    treeClimber.ascendTree();
    assertPaths(".", treeClimber.getTreePath());

    treeClimber.ascendTree();
    assertPaths("./..", treeClimber.getTreePath());
  }

  private static void assertPaths(String expectedPath, String actualPath) {
    File expectedFile = new File(expectedPath);
    File actualFile = new File(actualPath);
    assertEquals(expectedFile.getPath(), actualFile.getPath());
  }
}
