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

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.DependenciesListener;

import java.io.File;

/**
 * The common shared context for the analysis of a Maven POM definition.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class MavenContext {

  private final DependenciesListener builder;

  private final File mavenDir;

  public MavenContext(DependenciesListener builder, File mavenDir) {
    this.builder = builder;
    this.mavenDir = mavenDir;
  }

  public File getMavenDir() {
    return mavenDir;
  }

  public File getModuleFile(String modulePath) {
    return Tools.getPomFile(new File(mavenDir, modulePath));
  }

  public GraphNode lookup(GraphNode node) {
    return builder.lookup(node);
  }

  public void newDep(
      GraphNode head, GraphNode tail, Relation relation) {
    builder.newDep(head, tail, relation);
  }
}
