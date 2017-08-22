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

import org.xml.sax.InputSource;

import java.io.File;
import java.io.IOException;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class GraphMLAnalyst implements DependencyAnalyst {

  private final String graphMLPath;

  private final GraphFactory graphFactory;

  public GraphMLAnalyst(String graphMLPath, GraphFactory graphFactory) {
    this.graphMLPath = graphMLPath;
    this.graphFactory = graphFactory;
  }

  @Override
  public GraphDocument runAnalysis() throws IOException {

    GraphBuilder graphBuilder = GraphBuilders.createGraphModelBuilder();
    DependenciesListener builder = new DependenciesDispatcher(graphBuilder);

    GraphMLContext context = new GraphMLContext(builder, graphFactory);

    try {
      processModule(context);
    } catch (Exception err) {
      MavenLogger.LOG.error(
          "Unable to analyze GraphML at {}", graphMLPath, err);
    }
    GraphModel resultGraph = graphBuilder.createGraphModel();

    // Done
    return new GraphDocument(MavenPluginActivator.MAVEN_MODEL, resultGraph);
  }

  private void processModule(GraphMLContext context)
      throws Exception {
    File graphMLFile = new File(graphMLPath);
    InputSource docSource = PushDownXmlHandler.getInputSource(graphMLFile);

    // TODO: Improve error handling ?? Add err state to context?
    if (null == docSource) {
      return;
    }

    DocumentHandler docLoader = new GraphMLDocumentHandler(context);
    PushDownXmlHandler.parseDocument(docLoader, docSource);
  }
}
