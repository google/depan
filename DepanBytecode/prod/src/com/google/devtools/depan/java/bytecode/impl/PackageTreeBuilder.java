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

import com.google.devtools.depan.filesystem.graph.DirectoryElement;
import com.google.devtools.depan.filesystem.graph.FileSystemRelation;
import com.google.devtools.depan.java.graph.JavaRelation;
import com.google.devtools.depan.java.graph.PackageElement;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.chain.DependenciesListener;

import java.io.File;


/**
 * Create a tree structure for packages and their corresponding file-system
 * directories.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class PackageTreeBuilder {

  /** Dependency builder */
  private final DependenciesListener builder;

  private PackageElement packageNode;
  private DirectoryElement packageDir;

  /**
   * @param builder
   */
  public PackageTreeBuilder(DependenciesListener builder) {
    this.builder = builder;
  }

  /**
   * Install a package tree and its corresponding directory tree for
   * the package and tree defined by the argument.  For conventionally
   * structured packages, the @c{@code packageFile} will be a suffix of the
   * {@code treeFile}.  However, this is not required.  The {@code treeFile}
   * may be shorter then the {@code packageFile}, which lead to inferred
   * parent directories.  It may also contain directory names that do not
   * correspond to package names.  These "perverse" embeddings of packages
   * into trees can result in unexpected associations.
   * 
   * @param packageFile full Java path for package
   * @param treeFile directory path that contains the package
   * @return {@code PackageElement} for {@code packageFile}
   */
  public PackageElement installPackageTree(
      File packageFile, File treeFile) {
    createPackageDir(packageFile, treeFile.getPath());
    PackageElement result = packageNode;

    createPackageParents(packageFile, treeFile);
    return result;
  }

  /**
   * Ascend both the package tree and directory tree, creating any dependencies
   * that are required.  If any package already exists, assume the rest of the
   * tree is complete and stop.
   * 
   * @param packageFile path to a package
   * @param treeFile path to a directory
   */
  private void createPackageParents(File packageFile, File treeFile) {
    if (null == packageFile) {
      return;
    }

    TreeClimber treePath = new TreeClimber(treeFile);
    GraphNode lookupNode = builder.newNode(packageNode);

    // If the lookup is not the same node, then a node with that identity
    // already exists.  No need to add that package (or its parents) again.
    while (lookupNode == packageNode) {
      // Set up for parent of current entities
      packageFile = packageFile.getParentFile();
      treePath.ascendTree();

      // Never parent a named packaged with the unnamed package
      if (null == packageFile) {
        return;
      }

      PackageElement childNode = packageNode;
      DirectoryElement childDir = packageDir;

      createPackageDir(packageFile, treePath.getTreePath());

      builder.newDep(packageNode, childNode, JavaRelation.PACKAGE);
      builder.newDep(packageDir, childDir, FileSystemRelation.CONTAINS_DIR);
      lookupNode = builder.newNode(packageNode);
    }
  }

  /**
   * Create both a package and directory, and the container dependency
   * between them.
   * 
   * @param packageFile path to package
   * @param treePath path to directory
   */
  private void createPackageDir(File packageFile, String treePath) {
    packageNode = createPackage(packageFile);

    packageDir = new DirectoryElement(treePath);
    builder.newDep(packageDir, packageNode, JavaRelation.PACKAGEDIR);
  }

  private PackageElement createPackage(File packageFile) {
    if (null == packageFile) {
      return createPackage("");
    }
    return createPackage(packageFile.getPath());
  }

  private PackageElement createPackage(String packagePath) {
    if (packagePath.isEmpty()) {
      return new PackageElement("<unnamed>");
    }

    return new PackageElement(packagePath.replace(File.separatorChar, '.'));
  }
}
