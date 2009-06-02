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
package com.google.devtools.depan.filesystem.eclipse;

import com.google.devtools.depan.filesystem.graph.DirectoryElement;
import com.google.devtools.depan.filesystem.graph.FileElement;
import com.google.devtools.depan.filesystem.graph.FileSystemRelation;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.DependenciesListener;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class TreeLoader {

  private static Logger logger =
      Logger.getLogger(TreeLoader.class.getName());

  private final DependenciesListener builder;
  private final String prefixPath;

  /**
   * @param builder
   */
  public TreeLoader(DependenciesListener builder, String prefixPath) {
    this.builder = builder;
    this.prefixPath = prefixPath;
  }

  public void analyzeTree(String treePath) throws IOException {
    File treeFile = new File(treePath);

    // If it is just a file, it's pretty uninteresting - one node
    if (treeFile.isFile()) {
      getBuilder().newNode(createFile(treeFile));
      return;
    }

    // If it's a directory, traverse the full tree
    if (treeFile.isDirectory()) {
      DirectoryElement parentNode = createDirectory(treeFile);
      traverseTree(getBuilder(), parentNode, treeFile);
    }

    // Hmmm .. something unexpected
    logger.info("Unable to load tree from " + treePath);
  }

  private DependenciesListener getBuilder() {
    return builder;
  }

  private void traverseTree(
      DependenciesListener builder, GraphNode rootNode, File rootFile) {

    // TODO(leeca):  Based on performance, maybe revise to sort into
    // lists of files and directories, and process each type in batches.
    for (File child : rootFile.listFiles()) {
      buildChild(builder, rootNode, child);
    }
  }

  /**
   * @param builder
   * @param rootNode
   * @param child
   */
  private void buildChild(
      DependenciesListener builder, GraphNode rootNode, File child) {
    try {
      if (child.isFile()) {
        GraphNode file = createFile(child);
        builder.newDep(rootNode, file, FileSystemRelation.CONTAINS_FILE);
        return;
      }
      if (child.isDirectory()) {
        GraphNode dir = createDirectory(child);
        builder.newDep(rootNode, dir, FileSystemRelation.CONTAINS_DIR);
        traverseTree(builder, dir, child);
        return;
      }
      logger.warning(
            "Unknown file system object " + child.getCanonicalPath());
    } catch (IOException e) {
      logger.severe("Unable to access tree entity " + child.getPath());
    }
  }

  private DirectoryElement createDirectory(File directory)
      throws IOException {
    String dirPath = getElementPath(directory);
    return new DirectoryElement(dirPath);
  }

  private FileElement createFile(File file) throws IOException {
    String dirPath = getElementPath(file);
    return new FileElement(dirPath);
  }

  /**
   * @param directory
   * @return
   * @throws IOException
   */
  private String getElementPath(File directory) throws IOException {
    String dirPath = directory.getCanonicalPath();
    if (dirPath.startsWith(prefixPath)) {
      return dirPath.substring(prefixPath.length());
    }
    return dirPath;
  }
}
