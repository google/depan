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
import com.google.devtools.depan.maven.MavenLogger;
import com.google.devtools.depan.maven.MavenPluginActivator;
import com.google.devtools.depan.maven.builder.MavenContext;
import com.google.devtools.depan.maven.builder.MavenDocumentHandler;
import com.google.devtools.depan.maven.builder.MavenGraphResolver;
import com.google.devtools.depan.maven.builder.PomTools;
import com.google.devtools.depan.maven.eclipse.preferences.AnalysisPreferenceIds;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.builder.api.GraphBuilder;
import com.google.devtools.depan.model.builder.api.GraphBuilders;
import com.google.devtools.depan.model.builder.chain.DependenciesDispatcher;
import com.google.devtools.depan.model.builder.chain.DependenciesListener;
import com.google.devtools.depan.pushxml.PushDownXmlHandler.DocumentHandler;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.IOException;

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

    monitor.worked(1);

    // Step 2) Read through the file system to build the analysis graph
    monitor.setTaskName("Loading Maven POM...");

    try {
      processModule(builder, page.getPathFile());
    } catch (Exception err) {
      MavenLogger.LOG.error(
          "Unable to analyze Maven POM at {}", page.getPathText(), err);
    }

    monitor.worked(1);

    // Step 3) Resolve artifact references to matching artifact definitions.
    monitor.setTaskName("Resolving references...");

    GraphModel analysisGraph = graphBuilder.createGraphModel();
    MavenGraphResolver resolver = new MavenGraphResolver();
    GraphModel resultGraph = resolver.resolveReferences(analysisGraph);
    monitor.worked(1);

    return new GraphDocument(MavenPluginActivator.MAVEN_MODEL, resultGraph);
  }

  private void processModule(DependenciesListener builder, File moduleFile)
      throws Exception {
    File pomFile = PomTools.getPomFile(moduleFile);
    File mavenDir = pomFile.getParentFile();
    MavenContext context = buildMavenContext(builder, mavenDir);

    InputSource pomSource =
        PomTools.getPomSource(pomFile, context, page.getProcessing());

    // TODO: Improve error handling ?? Add err state to context?
    if (null == pomSource) {
      return;
    }

    DocumentHandler pomLoader = new MavenDocumentHandler(context);
    PomTools.loadModule(pomLoader, pomSource);
  }

  private MavenContext buildMavenContext(
      DependenciesListener builder, File moduleFile) {
    File pomFile = PomTools.getPomFile(moduleFile);
    File mavenDir = pomFile.getParentFile();

    IPreferenceStore prefs = MavenActivator.getDefault().getPreferenceStore();
    String mavenExe =
        prefs.getString(AnalysisPreferenceIds.MVN_ANALYSIS_EXECUTABLE);
    String effPomCmd =
        prefs.getString(AnalysisPreferenceIds.MVN_ANALYSIS_EFFECTIVEPOM);
    String javaHome = getJavaHome(prefs);

    return new MavenContext(builder, mavenDir, javaHome, mavenExe, effPomCmd);
  }

  private String getJavaHome(IPreferenceStore prefs) {
    boolean useSystemJava =
        prefs.getBoolean(AnalysisPreferenceIds.MVN_ANALYSIS_SYSTEMJAVA);
    if (useSystemJava) {
      return System.getProperty("java.home");
    }
    return prefs.getString(AnalysisPreferenceIds.MVN_ANALYSIS_JAVAHOME);
  }
}
