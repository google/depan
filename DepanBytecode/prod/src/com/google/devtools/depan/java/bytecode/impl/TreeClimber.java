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

import java.io.File;

/**
 * Ascend up a directory tree, even beyond the apparent root of the tree.
 * If the "natural" parents in the tree are exhausted, additional level are
 * inferred through "/.." path elements.
 *
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class TreeClimber {
  private static final String PARENT_PATH = File.separator + "..";

  private File treeFile;
  private StringBuilder parentPath;

  /**
   * Create a {@code TreeClimber} for the indicated File path.
   *
   * @param treeFile path to climb.
   */
  public TreeClimber(File treeFile) {
    super();
    this.treeFile = treeFile;
  }

  /**
   * Move higher in the tree, beyond even an apparent root of tree.
   */
  public void ascendTree() {
    // Original tree already exhausted.  Just add more inferred parents.
    if (null != parentPath ) {
      parentPath.append(PARENT_PATH);
      return;
    }

    // Attempt to ascend within the existing tree.
    // Check for null parent avoids a path with an unnamed root.
    File parentTree = calcParentTree();
    if (null != parentTree) {
      treeFile = parentTree;
      return;
    }

    // Nope - no parents above, so use last tree element as base for
    // implicit parents.
    parentPath = new StringBuilder(provideLastName());
    parentPath.append(PARENT_PATH);
  }

  /**
   * Get the path for the current level within (or above) the tree.
   * 
   * @return path for current tree level
   */
  public String getTreePath() {
    if (null != parentPath) {
      return parentPath.toString();
    }
    return treeFile.getPath();
  }

  /**
   * Determine if the current tree has a non-root parent.  The trick is the
   * non-root part, as the Java library happily provides the root as a parent.
   * But that leads to unanchored file names, which are not very useful.
   * 
   * @return parent of current tree, or {@code null} if there isn't one
   */
  private File calcParentTree() {
    // Attempt to ascend within the existing tree.
    // Check for null grandparent avoids a path with an unnamed root.
    File parentTree = treeFile.getParentFile();
    if (null == parentTree) {
      return null;
    }

    if (treeFile.isAbsolute() && (null == parentTree.getParentFile())) {
      return null;
    }

    return parentTree;
  }

  /**
   * Provide the name of the last tree element, being careful to avoid an
   * empty element (unless it is just the bare root).
   * 
   * @return top name in tree
   */
  private String provideLastName() {
    // If there is anything left, us it.
    if (!treeFile.getName().isEmpty()) {
      return treeFile.getPath();
    }

    // For empty relative paths, inject a dot ('.') as base
    if (!treeFile.isAbsolute()) {
      return ".";
    }

    // For empty absolute paths, let the PARENT_PATH define the root
    return "";
  }
}
