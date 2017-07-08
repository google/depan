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

package com.google.devtools.depan.filesystem.builder;

import com.google.devtools.depan.filesystem.FileSystemPluginActivator;
import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.builder.api.GraphBuilder;
import com.google.devtools.depan.model.builder.api.GraphBuilders;
import com.google.devtools.depan.model.builder.chain.DependenciesListener;

import org.eclipse.core.runtime.IProgressMonitor;

import java.io.IOException;

/**
 * Wizard for converting a file system tree into a DepAn analysis graph.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class FileSystemAnalyzer {

  private final String treePrefix;
  private final String pathText;

  public FileSystemAnalyzer(String treePrefix, String pathText) {
    this.treePrefix = treePrefix;
    this.pathText = pathText;
  }

  public static int countAnalysisWork(String treePrefix, String pathText) {
    return 2;
  }

  /**
   * Create {@link GraD} graph by traversing the file system tree from
   * the named starting point.
   *
   * Note that this generates two (2) monitor.worked() calls.
   */
  public GraphDocument generateAnalysisDocument(IProgressMonitor monitor)
      throws IOException {

    // Step 1) Create the GraphModel to hold the analysis results
    // TODO(leeca): Add filters, etc.
    // TODO(leeca): Extend UI to allow lists of directories.

    GraphBuilder graphBuilder = GraphBuilders.createGraphModelBuilder();
    DependenciesListener builder =
        new FileSystemDependencyDispatcher(graphBuilder);

    monitor.worked(1);

    // Step 2) Read through the file system to build the analysis graph
    monitor.setTaskName("Loading file tree...");

    TreeLoader loader = new TreeLoader(builder, treePrefix);
    loader.analyzeTree(pathText);

    GraphModel graph = graphBuilder.createGraphModel();
    monitor.worked(1);

    // Done
    return new GraphDocument(FileSystemPluginActivator.FILE_SYSTEM_MODEL, graph);
  }
}
