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

package com.google.devtools.depan.maven.builder;

import com.google.devtools.depan.pushxml.PushDownXmlHandler.ElementHandler;
import com.google.devtools.depan.pushxml.PushDownXmlHandler.NestingElementHandler;
import com.google.devtools.depan.pushxml.PushDownXmlHandler.TextElementHandler;
import com.google.devtools.depan.maven.MavenLogger;
import com.google.devtools.depan.maven.graph.MavenRelation;
import com.google.devtools.depan.model.GraphNode;

import com.google.common.collect.Lists;

import org.xml.sax.InputSource;

import java.io.File;
import java.util.List;

/**
 * Interpret Maven modules elements, and create appropriate DepAn
 * relationships in the builder graph.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class ModulesLoader extends NestingElementHandler {

  public static final String MODULES = "modules";
  public static final String MODULE = "module";

  private List<TextElementHandler> modules = Lists.newArrayList();

  @Override
  public boolean isFor(String name) {
    return MODULES.equals(name);
  }

  @Override
  public void end() {
  }

  @Override
  public ElementHandler newChild(String name) {
    if (MODULE.equals(name)) {
      TextElementHandler module = new TextElementHandler(name);
      modules.add(module);
      return module;
    }
    return super.newChild(name);
  }

  /**
   * Add supplied master node to every aggregated project.
   */
  public void addMaster(MavenContext context, GraphNode master) {
    for (TextElementHandler module : modules) {
      String modulePath = module.getText();
      try {
        processModule(context, master, modulePath);
      } catch (Exception err) {
        MavenLogger.LOG.error(
            "Unable to process dependent module {}", modulePath, err);
      }
    }
  }

  private void processModule(
      MavenContext context, GraphNode master, String modulePath)
      throws Exception {
    File docFile = context.getModuleFile(modulePath);
    InputSource docSource = PomTools.loadEffectivePom(docFile, context);

    MavenDocumentHandler pomLoader = new MavenDocumentHandler(context);
    PomTools.loadModule(pomLoader, docSource);
    context.newDep(
        master, pomLoader.getProjectNode(), MavenRelation.MODULE_DEPEND);
  }
}
