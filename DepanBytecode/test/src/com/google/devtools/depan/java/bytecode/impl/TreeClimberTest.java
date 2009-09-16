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

import junit.framework.TestCase;

import java.io.File;

/**
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class TreeClimberTest extends TestCase {

  public void testRootedTree() {
    TreeClimber treeClimber = new TreeClimber(new File("/rooted/tree"));
    assertEquals("/rooted/tree", treeClimber.getTreePath());

    treeClimber.ascendTree();
    assertEquals("/rooted", treeClimber.getTreePath());

    treeClimber.ascendTree();
    assertEquals("/rooted/..", treeClimber.getTreePath());

    treeClimber.ascendTree();
    assertEquals("/rooted/../..", treeClimber.getTreePath());
  }

  public void testRelativeTree() {
    TreeClimber treeClimber = new TreeClimber(new File("relative/tree"));
    assertEquals("relative/tree", treeClimber.getTreePath());

    treeClimber.ascendTree();
    assertEquals("relative", treeClimber.getTreePath());

    treeClimber.ascendTree();
    assertEquals("relative/..", treeClimber.getTreePath());

    treeClimber.ascendTree();
    assertEquals("relative/../..", treeClimber.getTreePath());
  }

  public void testEmptyRoot() {
    TreeClimber treeClimber = new TreeClimber(new File("/"));
    assertEquals("/", treeClimber.getTreePath());

    treeClimber.ascendTree();
    assertEquals("/..", treeClimber.getTreePath());
  }

  public void testEmptyTree() {
    TreeClimber treeClimber = new TreeClimber(new File(""));
    assertEquals("", treeClimber.getTreePath());

    treeClimber.ascendTree();
    assertEquals("./..", treeClimber.getTreePath());
  }

  public void testRelativeBase() {
    TreeClimber treeClimber = new TreeClimber(new File("./base"));
    assertEquals("./base", treeClimber.getTreePath());

    treeClimber.ascendTree();
    assertEquals(".", treeClimber.getTreePath());

    treeClimber.ascendTree();
    assertEquals("./..", treeClimber.getTreePath());
  }
}
