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

import com.google.devtools.depan.graph_doc.eclipse.ui.wizards.AbstractAnalysisWizard;
import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.graphml.builder.GraphFactory;
import com.google.devtools.depan.graphml.builder.GraphMLContext;
import com.google.devtools.depan.graphml.builder.GraphMLDocumentHandler;
import com.google.devtools.depan.maven.MavenLogger;
import com.google.devtools.depan.maven.MavenPluginActivator;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.builder.api.GraphBuilder;
import com.google.devtools.depan.model.builder.api.GraphBuilders;
import com.google.devtools.depan.model.builder.chain.DependenciesDispatcher;
import com.google.devtools.depan.model.builder.chain.DependenciesListener;
import com.google.devtools.depan.pushxml.PushDownXmlHandler;
import com.google.devtools.depan.pushxml.PushDownXmlHandler.DocumentHandler;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.IOException;

/**
 * Wizard for converting a GraphML file into a DepAn analysis graph.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class NewGraphMLWizard extends AbstractAnalysisWizard {

  /**
   * Eclipse extension identifier for this wizard.
   */
  public static final String ANALYSIS_WIZARD_ID =
      "com.google.devtools.depan.maven.eclipse.NewGraphMLWizard";

  public NewGraphMLPage page;

  /**
   * Adding the page to the wizard.
   */
  @Override
  public void addPages() {
    page = new NewGraphMLPage(getSelection());
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
    return 3;
  }

  /**
   * Create an analysis graph by traversing the file system tree from
   * the named starting point.
   *
   * Note that this generates two (2) monitor.worked() calls.
   */
  @Override
  protected GraphDocument createNewDocument(IProgressMonitor monitor)
      throws IOException {

    // Step 1) Create the GraphModel to hold the analysis results
    // TODO(leeca): Add filters, etc.
    // TODO(leeca): Extend UI to allow lists of directories.

    GraphBuilder graphBuilder = GraphBuilders.createGraphModelBuilder();
    DependenciesListener builder = new DependenciesDispatcher(graphBuilder);

    GraphMLProcessing processing = page.getProcessing();
    GraphFactory graphFactory = processing.getGraphFactory();
    GraphMLContext context = new GraphMLContext(builder, graphFactory);

    monitor.worked(1);

    // Step 2) Read through the file system to build the analysis graph
    monitor.setTaskName("Loading GraphML...");

    try {
      processModule(context, page.getPathFile());
    } catch (Exception err) {
      MavenLogger.LOG.error(
          "Unable to analyze GraphML at {}", page.getPathText(), err);
    }

    GraphModel resultGraph = graphBuilder.createGraphModel();
    monitor.worked(1);

    // Done
    return new GraphDocument(MavenPluginActivator.MAVEN_MODEL, resultGraph);
  }

  private void processModule(GraphMLContext context, File graphMLFile)
      throws Exception {
    InputSource docSource = PushDownXmlHandler.getInputSource(graphMLFile);

    // TODO: Improve error handling ?? Add err state to context?
    if (null == docSource) {
      return;
    }

    DocumentHandler docLoader = new GraphMLDocumentHandler(context);
    PushDownXmlHandler.parseDocument(docLoader, docSource);
  }
}
