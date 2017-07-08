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
import com.google.devtools.depan.filesystem.builder.TreeLoader;
import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.java.JavaPluginActivator;
import com.google.devtools.depan.java.bytecode.eclipse.AsmFactory;
import com.google.devtools.depan.java.bytecode.eclipse.ClassAnalysisStats;
import com.google.devtools.depan.java.bytecode.eclipse.ClassFileReader;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.builder.api.GraphBuilder;
import com.google.devtools.depan.model.builder.api.GraphBuilders;
import com.google.devtools.depan.model.builder.chain.DependenciesDispatcher;
import com.google.devtools.depan.model.builder.chain.DependenciesListener;
import com.google.devtools.depan.model.builder.chain.ElementFilter;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class JavaAnalyst implements DependencyAnalyst {

  private final AsmFactory asmFactory;

  private final String classPath;

  private final ElementFilter filter;

  private final ClassAnalysisStats analysisStats;

  public JavaAnalyst(
      AsmFactory asmFactory, String classPath, ElementFilter filter) {
    this.asmFactory = asmFactory;
    this.filter = filter;
    this.classPath = classPath;
    analysisStats = new ClassAnalysisStats();
  }

  @Override
  public GraphDocument runAnalysis() throws IOException {
    GraphBuilder graphBuilder = GraphBuilders.createGraphModelBuilder();
    DependenciesListener builder =
        new DependenciesDispatcher(filter, graphBuilder);

    if (classPath.endsWith(".jar") || classPath.endsWith(".zip")) {
      readZipFile(classPath, builder);
    } else {
      readTree(classPath, builder);
    }

    CmdLogger.LOG.info(
        analysisStats.getClassesLoaded() + "/" + analysisStats.getClassesTotal()
        + " classes loaded. " + analysisStats.getClassesFailed() + " failed.");

    GraphModel resultGraph = graphBuilder.createGraphModel();

    return new GraphDocument(JavaPluginActivator.JAVA_MODEL, resultGraph);
  }

  /**
   * Build Java dependencies from a Jar file.
   * 
   * @param classPath path to Jar file
   * @param builder destination of discovered dependencies
   * @param progress indicator for user interface
   * @throws IOException
   */
  private void readZipFile(
      String classPath, DependenciesListener builder) throws IOException {

    ClassFileReader reader =
        new ClassFileReader(asmFactory, analysisStats);
    ZipFile zipFile = new ZipFile(classPath);
    JarFileLister jarReader =
        new JarFileLister(zipFile, builder, reader);
    jarReader.start();
  }

  /**
   * Build Java dependencies from a file system tree.
   * 
   * @param classPath root of directory tree
   * @param builder destination of discovered dependencies
   * @param progress indicator for user interface
   * @throws IOException
   */
  private void readTree(
      String classPath, DependenciesListener builder) throws IOException {

    // TODO(leeca): Instead of just assuming one level of path retention,
    // let the user decide like in NewFileSystemWizard.  But first, that needs
    // to be cleaned up and refactored.
    String treePrefix = new File(classPath).getParent();

    ClassFileReader reader = new ClassFileReader(asmFactory, analysisStats);

    TreeLoader loader =
        new ClassTreeLoader(treePrefix, builder, reader);
    loader.analyzeTree(classPath);
  }
}
