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
import com.google.devtools.depan.cmd.dispatch.AbstractCommandExec;
import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.graph_doc.persistence.GraphModelXmlPersist;
import com.google.devtools.depan.graphml.builder.GraphFactory;
import com.google.devtools.depan.java.bytecode.eclipse.AsmFactory;
import com.google.devtools.depan.java.bytecode.eclipse.DefaultElementFilter;
import com.google.devtools.depan.maven.builder.PomProcessing;
import com.google.devtools.depan.maven.graphml.MavenGraphFactory;
import com.google.devtools.depan.model.builder.chain.ElementFilter;

import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class AnalyzerCommand extends AbstractCommandExec {

  @Override
  public void exec() {
    String analyzer = getParm(0);
    if ("filesystem".equals(analyzer)) {
      analyzeFileSystem();
      return;
    }
    if ("graphml".equals(analyzer)) {
      analyzeGraphML();
      return;
    }
    if ("java".equals(analyzer)) {
      analyzeJava();
      return;
    }
    if ("maven".equals(analyzer)) {
      analyzeMaven();
      return;
    }
    String msg = MessageFormat.format(
        "Unknown analysis type {0}.", analyzer);
    failWithMessage(msg);
  }

  private void analyzeFileSystem() {
    URI location = buildLocation(getParm(1));
    String treePrefix = getParm(2);
    String pathText = getParm(3);

    FileSystemAnalyst analyzer =
        new FileSystemAnalyst(treePrefix, pathText);

    try {
      performAnalysis(analyzer, location);
    } catch (IOException errIo) {
      CmdLogger.LOG.error("File system dependency analysis failed", errIo);
    }
  }

  private void analyzeGraphML() {
    URI location = buildLocation(getParm(1));
    String graphMLPath = getParm(2);
    GraphFactory pathText = buildGraphFactory(getParm(3));

    GraphMLAnalyst analyzer =
        new GraphMLAnalyst(graphMLPath, pathText);

    try {
      performAnalysis(analyzer, location);
    } catch (IOException errIo) {
      CmdLogger.LOG.error("GraphML dependency analysis failed", errIo);
    }
  }

  /**
   * @param parm
   * @return
   */
  private GraphFactory buildGraphFactory(String parm) {
    if ("maven".equals(parm)) {
      return new MavenGraphFactory();
    }

    return null;
  }

  private void analyzeJava() {
    try {
      URI location = buildLocation(getParm(1));
      String classPath = getParm(2);
      String filterText = getParm(3, "");
      AsmFactory asmFactory = getAsmFactory(getParm(4));

      ElementFilter filter = DefaultElementFilter.build(filterText);
      JavaAnalyst analyzer = new JavaAnalyst(asmFactory, classPath, filter);

      performAnalysis(analyzer, location);
    } catch (IOException errIo) {
      CmdLogger.LOG.error("Java dependency analysis failed", errIo);
    }
  }

  private AsmFactory getAsmFactory(String parm) {
    if ("asm4".equals(parm)) {
      return AsmFactory.ASM4_FACTORY;
    }
    if ("asm5".equals(parm)) {
      return AsmFactory.ASM5_FACTORY;
    }

    return AsmFactory.ASM5_FACTORY;
  }

  private void analyzeMaven() {
    URI location = buildLocation(getParm(1));
    String mavenPath = getParm(2);
    PomProcessing processing = getPomProcessing(getParm(3));

    MavenAnalyst analyzer =
        new MavenAnalyst(mavenPath, processing);

    try {
      performAnalysis(analyzer, location);
    } catch (IOException errIo) {
      CmdLogger.LOG.error("Maven dependency analysis failed", errIo);
    }
  }

  private PomProcessing getPomProcessing(String parm) {
    if ("compute".equals(parm)) {
      return PomProcessing.EFFECTIVE;
    }

    return  PomProcessing.NONE;
  }

  private void performAnalysis(DependencyAnalyst analyzer, URI location)
      throws IOException {

    GraphModelXmlPersist persist = GraphModelXmlPersist.build(false);
    GraphDocument analysis = analyzer.runAnalysis();
    persist.save(location, analysis);
  }
}
