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

package com.google.devtools.depan.cmd.analyzers;

import com.google.devtools.depan.cmd.CmdLogger;
import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.maven.MavenPluginActivator;
import com.google.devtools.depan.maven.builder.MavenAnalysisProperties;
import com.google.devtools.depan.maven.builder.MavenContext;
import com.google.devtools.depan.maven.builder.MavenDocumentHandler;
import com.google.devtools.depan.maven.builder.MavenGraphResolver;
import com.google.devtools.depan.maven.builder.PomProcessing;
import com.google.devtools.depan.maven.builder.PomTools;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.builder.api.GraphBuilder;
import com.google.devtools.depan.model.builder.api.GraphBuilders;
import com.google.devtools.depan.model.builder.chain.DependenciesDispatcher;
import com.google.devtools.depan.model.builder.chain.DependenciesListener;
import com.google.devtools.depan.pushxml.PushDownXmlHandler.DocumentHandler;

import org.xml.sax.InputSource;

import java.io.File;
import java.io.IOException;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class MavenAnalyst implements DependencyAnalyst {

  private final String mavenPath;
  private final PomProcessing processing;

  public MavenAnalyst(String mavenPath, PomProcessing processing) {
    this.mavenPath = mavenPath;
    this.processing = processing;
  }

  @Override
  public GraphDocument runAnalysis() throws IOException {

    // Step 1) Create the GraphModel to hold the analysis results
    // TODO(leeca): Add filters, etc.
    // TODO(leeca): Extend UI to allow lists of directories.

    GraphBuilder graphBuilder = GraphBuilders.createGraphModelBuilder();
    DependenciesListener builder = new DependenciesDispatcher(graphBuilder);

    try {
      processModule(builder);
    } catch (Exception err) {
      CmdLogger.LOG.error(
          "Unable to analyze Maven POM at " + getPathText(), err);
    }

    GraphModel analysisGraph = graphBuilder.createGraphModel();
    MavenGraphResolver resolver = new MavenGraphResolver();
    GraphModel resultGraph = resolver.resolveReferences(analysisGraph);

    return new GraphDocument(MavenPluginActivator.MAVEN_MODEL, resultGraph);
  }

  private String getPathText() {
    return mavenPath;
  }

  private File getPathFile() {
    return new File(mavenPath);
  }

  private void processModule(DependenciesListener builder)
      throws Exception {
    File moduleFile = getPathFile();
    File pomFile = PomTools.getPomFile(moduleFile);
    File mavenDir = pomFile.getParentFile();
    MavenContext context = buildMavenContext(builder, mavenDir);

    InputSource pomSource = PomTools.getPomSource(pomFile, context, processing);

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

    String mavenExe = MavenAnalysisProperties.MVN_ANALYSIS_EXECUTABLE;
    String effPomCmd = MavenAnalysisProperties.MVN_ANALYSIS_EFFECTIVEPOM;
    String javaHome = getJavaHome();

    return new MavenContext(builder, mavenDir, javaHome, mavenExe, effPomCmd);
  }

  private String getJavaHome() {
    return System.getProperty("java.home");
  }
}
