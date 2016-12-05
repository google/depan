/*
 * Copyright 2008 The Depan Project Authors
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

import com.google.devtools.depan.filesystem.builder.FileSystemAnalyzer;
import com.google.devtools.depan.graph_doc.eclipse.ui.wizards.AbstractAnalysisWizard;
import com.google.devtools.depan.graph_doc.model.GraphDocument;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import java.io.IOException;

/**
 * Wizard for converting a file system tree into a DepAn analysis graph.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class NewFileSystemWizard extends AbstractAnalysisWizard {

  /**
   * Eclipse extension identifier for this wizard.
   */
  public static final String ANALYSIS_WIZARD_ID =
      "com.google.devtools.depan.filesystem.eclipse.NewFileSystemWizard";

  public NewFileSystemPage page;

  /**
   * Adding the page to the wizard.
   */
  @Override
  public void addPages() {
    page = new NewFileSystemPage(getSelection());
    addPage(page);
  }

  @Override
  protected String getOutputFilename() {
    return page.getOutputFilename();
  }

  @Override
  protected IFile getOutputFile() throws CoreException {
    return page.getOutputFile();
  }

  @Override
  protected int countCreateWork() {
    return FileSystemAnalyzer.countAnalysisWork(
        page.getTreePrefix(), page.getPathText());
  }

  /**
   * Create an analysis graph by traversing the file system tree from
   * the named starting point.
   */
  @Override
  protected GraphDocument createNewDocument(IProgressMonitor monitor)
      throws IOException {

    // TODO(leeca): Add filters, etc.
    // TODO(leeca): Extend UI to allow lists of directories.
    FileSystemAnalyzer analyzer = new FileSystemAnalyzer(
        page.getTreePrefix(), page.getPathText());
    return analyzer.generateAnalysisDocument(monitor);
  }
}
