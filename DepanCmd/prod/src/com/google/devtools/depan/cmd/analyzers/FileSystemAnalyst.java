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

import com.google.devtools.depan.filesystem.builder.FileSystemAnalyzer;
import com.google.devtools.depan.graph_doc.model.GraphDocument;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import java.io.IOException;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class FileSystemAnalyst implements DependencyAnalyst {

  private final String treePrefix;
  private final String pathText;

  public FileSystemAnalyst(String treePrefix, String pathText) {
    this.treePrefix = treePrefix;
    this.pathText = pathText;
  }

  @Override
  public GraphDocument runAnalysis() throws IOException {
    FileSystemAnalyzer analyzer =
        new FileSystemAnalyzer(treePrefix, pathText);
    IProgressMonitor monitor = new NullProgressMonitor();
    return analyzer.generateAnalysisDocument(monitor);
  }
}
