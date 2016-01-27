/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.maven.eclipse;

import com.google.devtools.depan.eclipse.editors.GraphDocument;
import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.wizards.AbstractAnalysisWizard;
import com.google.devtools.depan.eclipse.wizards.PushDownXmlHandler.DocumentHandler;
import com.google.devtools.depan.maven.builder.MavenContext;
import com.google.devtools.depan.maven.builder.MavenDocumentHandler;
import com.google.devtools.depan.maven.builder.Tools;
import com.google.devtools.depan.maven.graph.ArtifactElement;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.DependenciesDispatcher;
import com.google.devtools.depan.model.builder.DependenciesListener;

import com.google.common.collect.Maps;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Wizard for converting a Maven POM file into a DepAn analysis graph.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class NewMavenPomWizard extends AbstractAnalysisWizard {

  /**
   * Eclipse extension identifier for this wizard.
   */
  public static final String ANALYSIS_WIZARD_ID =
      "com.google.devtools.depan.maven.eclipse.NewMavenPomWizard";

  public NewMavenPomPage page;

  /**
   * Constructor for FileSystem wizard.
   */
  public NewMavenPomWizard() {
    super();
    setNeedsProgressMonitor(true);
  }

  /**
   * Adding the page to the wizard.
   */
  @Override
  public void addPages() {
    page = new NewMavenPomPage(getSelection());
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
    return 3;
  }

  /**
   * Create an analysis graph by traversing the file system tree from
   * the named starting point.
   *
   * Note that this generates two (2) monitor.worked() calls.
   */
  @Override
  protected GraphDocument generateAnalysisDocument(IProgressMonitor monitor)
      throws IOException {

    // Step 1) Create the GraphModel to hold the analysis results
    // TODO(leeca): Add filters, etc.
    // TODO(leeca): Extend UI to allow lists of directories.

    GraphModel analysisGraph = new GraphModel();
    DependenciesListener builder =
        new DependenciesDispatcher(analysisGraph.getBuilder());

    monitor.worked(1);

    // Step 2) Read through the file system to build the analysis graph
    monitor.setTaskName("Loading Maven POM...");

    try {
      processModule(builder, page.getPathFile());
    } catch (Exception err) {
      Tools.warnThrown(
          "Unable to analyze Maven POM at " + page.getPathText(), err);
    }

    monitor.worked(1);

    // Step 3) Resolve artifact references to matching artifact definitions.
    monitor.setTaskName("Resolving references...");
    MavenGraphResolver resolver = new MavenGraphResolver();
    GraphModel result = resolver.resolveReferences(analysisGraph);
    monitor.worked(1);

    // Done
    return createGraphDocument(result,
      MavenActivator.PLUGIN_ID, Resources.PLUGIN_ID);
  }

  private void processModule(DependenciesListener builder, File moduleFile)
      throws Exception {
    File pomFile = Tools.getPomFile(moduleFile);
    File mavenDir = pomFile.getParentFile();
    MavenContext context = new MavenContext(builder, mavenDir);

    InputSource pomSource = getPomSource(pomFile, context);

    // TODO: Improve error handling ?? Add err state to context?
    if (null == pomSource) {
      return;
    }

    DocumentHandler pomLoader = new MavenDocumentHandler(context);
    Tools.loadModule(pomLoader, pomSource);
  }

  private InputSource getPomSource(File pomFile, MavenContext context)
      throws IOException, InterruptedException {
    switch (page.getProcessing()) {
    case EFFECTIVE:
      return Tools.loadEffectivePom(pomFile, context);
    case NONE:
      FileInputStream stream = new FileInputStream(pomFile);
      return new InputSource(stream);
    }

    Tools.LOG.warning("Unexpected processing for " + pomFile.getPath());
    return null;
  }
}
