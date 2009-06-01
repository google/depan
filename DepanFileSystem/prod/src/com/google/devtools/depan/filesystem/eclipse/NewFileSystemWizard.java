/*
 * Copyright 2008 Google Inc.
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

import com.google.devtools.depan.eclipse.wizards.AbstractAnalysisWizard;
import com.google.devtools.depan.filesystem.graph.DirectoryElement;
import com.google.devtools.depan.filesystem.graph.FileElement;
import com.google.devtools.depan.filesystem.graph.FileSystemRelation;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.DependenciesDispatcher;
import com.google.devtools.depan.model.builder.DependenciesListener;
import com.google.devtools.depan.model.interfaces.GraphBuilder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import java.io.File;
import java.io.IOException;

/**
 * Wizard for converting a file system tree into a DepAn analysis graph.
 */
public class NewFileSystemWizard extends AbstractAnalysisWizard {

  /**
   * Eclipse extension identifier for this wizard.
   */
  public static final String ANALYSIS_WIZARD_ID =
      "com.google.devtools.depan.filesystem.eclipse.NewFileSystemWizard";

  public NewFileSystemPage page;

  private String prefixPath;

  /**
   * Constructor for FileSystem wizard.
   */
  public NewFileSystemWizard() {
    super();
    setNeedsProgressMonitor(true);
  }

  /**
   * Adding the page to the wizard.
   */
  @Override
  public void addPages() {
    page = new NewFileSystemPage(getSelection());
    addPage(page);
  }

  @Override
  protected String getOutputFileName() {
    return page.getOutputFileName();
  }

  @Override
  protected IFile getOutputFile() throws CoreException {
    return page.getOutputFile();
  }

  @Override
  protected int countAnalysisWork() {
    return 2;
  }

  /**
   * Create an analysis graph by traversing the file system tree from
   * the named starting point.
   *
   * Note that this generates two (2) monitor.worked() calls.
   */
  @Override
  protected GraphModel generateAnalysisGraph(IProgressMonitor monitor)
      throws IOException {

    // Step 1) Create the GraphModel to hold the analysis results
    prefixPath = page.getTreePrefix();
    // TODO(leeca): Add filters, etc.
    // TODO(leeca): Extend UI to allow lists of directories.

    GraphModel result = new GraphModel();
    DependenciesListener builder =
        new FileSystemDependencyDispatcher(result.getBuilder());

    monitor.worked(1);

    // Step 2) Read through the file system to build the analysis graph
    monitor.setTaskName("Loading file tree...");
    File treeFile = page.getPathFile();

    // If it is just a file, it's pretty uninteresting - one node
    if (treeFile.isFile()) {
      result.getBuilder().newNode(createFile(treeFile));
    }
    // If it's a directory, traverse the full tree
    else if (treeFile.isDirectory()) {
      DirectoryElement parentNode = createDirectory(treeFile);
      traverseTree(builder, parentNode, treeFile);
    }
    monitor.worked(1);

    // Done
    return result;
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
      } else if (child.isDirectory()) {
        GraphNode dir = createDirectory(child);
        builder.newDep(rootNode, dir, FileSystemRelation.CONTAINS_DIR);
        traverseTree(builder, dir, child);
      } else {
        System.err.print(
            "Unknown file system object " + child.getCanonicalPath());
      }
    } catch (IOException e) {
      System.err.println("Unable to acces tree entity " + child.getPath());
      e.printStackTrace();
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

  /**
   * Trivial filter for processing directory objects.
   */
  private static class FileSystemDependencyDispatcher
      extends DependenciesDispatcher {

    public FileSystemDependencyDispatcher(GraphBuilder builder) {
      super(builder);
    }

    @Override
    protected boolean passFilter(GraphNode node) {
      return true;
    }
  }
}
