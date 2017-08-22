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

package com.google.devtools.depan.filesystem.builder;

import com.google.devtools.depan.filesystem.graph.DirectoryElement;
import com.google.devtools.depan.filesystem.graph.FileElement;
import com.google.devtools.depan.filesystem.graph.FileSystemRelation;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.chain.DependenciesListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Populate a GraphModel based on the contents of an accessible
 * file system tree.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class TreeLoader {

  private static final Logger LOG =
      LoggerFactory.getLogger(TreeLoader.class.getName());

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
    beginAnalysis(treePath);
    processRoot(treePath);
    finishAnalysis(treePath);
  }

  protected DependenciesListener getBuilder() {
    return builder;
  }

  /**
   * Indicates the beginning of a tree traversal.
   * 
   * @param treePath directory tree being traversed
   */
  protected void beginAnalysis(String treePath) {
  }

  /**
   * Indicates the completion of a tree traversal.
   * 
   * @param treePath directory tree that was traversed
   */
  protected void finishAnalysis(String treePath) {
  }

  /**
   * Provides a {@code FileElement} for a discovered file.
   * 
   * @param treeFile path name to file within the analysis tree
   * @throws IOException
   */
  protected FileElement visitFile(File treeFile) throws IOException {
    return createFile(treeFile);
  }

  /**
   * Provides a {@code DirectoryElement} for a discovered directory.
   * 
   * @param treeFile path name to directory within the analysis tree
   * @throws IOException
   */
  protected DirectoryElement visitDirectory(File treeFile) throws IOException {
    DirectoryElement parentNode = createDirectory(treeFile);
    return parentNode;
  }

  /**
   * Process the root of the tree specially, since none of the elements
   * have relations with containers.
   * 
   * @param treePath
   * @throws IOException
   */
  private void processRoot(String treePath) throws IOException {
    File treeFile = new File(treePath);

    // If it is just a file, it's pretty uninteresting - one node
    if (treeFile.isFile()) {
      FileElement fileNode = visitFile(treeFile);
      getBuilder().newNode(fileNode);
      return;
    }

    // If it's a directory, traverse the full tree
    if (treeFile.isDirectory()) {
      DirectoryElement parentNode = visitDirectory(treeFile);
      traverseTree(parentNode, treeFile);
      return;
    }

    // Hmmm .. something unexpected
    LOG.info("Unable to load tree from {}", treePath);
  }

  private void traverseTree(GraphNode rootNode, File rootFile) {

    // TODO(leeca):  Based on performance, maybe revise to sort into
    // lists of files and directories, and process each type in batches.
    for (File child : rootFile.listFiles()) {
      buildChild(rootNode, child);
    }
  }

  /**
   * Handle a single child for a node.  If it is a directory, the directory's
   * children are processed recursively.
   * 
   * @param rootNode Node for the parent directory
   * @param child a child element of the parent directory
   */
  private void buildChild(GraphNode rootNode, File child) {
    try {
      if (child.isFile()) {
        GraphNode file = visitFile(child);
        getBuilder().newDep(rootNode, file, FileSystemRelation.CONTAINS_FILE);
        return;
      }
      if (child.isDirectory()) {
        GraphNode dir = visitDirectory(child);
        getBuilder().newDep(rootNode, dir, FileSystemRelation.CONTAINS_DIR);
        traverseTree(dir, child);
        return;
      }
      LOG.warn("Unknown file system object {}", child.getCanonicalPath());
    } catch (IOException e) {
      LOG.error("Unable to access tree entity {}", child.getPath());
    }
  }

  private DirectoryElement createDirectory(File directory)
      throws IOException {
    String dirPath = getElementPath(directory);
    return new DirectoryElement(dirPath);
  }

  private FileElement createFile(File file) throws IOException {
    String filePath = getElementPath(file);
    return new FileElement(filePath);
  }

  /**
   * Tidy up the path for elements, mostly by removing the prefix path if
   * it is present.
   * 
   * @param elementPath path to file system element
   * @return canonical name for element
   * @throws IOException
   */
  private String getElementPath(File elementPath) throws IOException {
    String dirPath = elementPath.getCanonicalPath();
    if (dirPath.startsWith(prefixPath)) {
      return dirPath.substring(prefixPath.length());
    }
    return dirPath;
  }
}
