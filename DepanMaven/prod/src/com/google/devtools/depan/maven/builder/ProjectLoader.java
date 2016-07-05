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
import com.google.devtools.depan.maven.graph.MavenRelation;
import com.google.devtools.depan.model.GraphNode;

/**
 * Interpret Maven project elements, and create appropriate DepAn
 * relationships in the builder graph.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class ProjectLoader extends NestingElementHandler {
  
  public static final String PROJECT = "project";

  private final MavenContext context;

  private LabelCapture label = new LabelCapture();

  private ParentLoader parent;

  private ModulesLoader modules;

  private DependenciesLoader dependencies;

  private PropertiesLoader properties;

  public ProjectLoader(MavenContext context) {
    this.context = context;
  }

  @Override
  public boolean isFor(String name) {
    return PROJECT.equals(name);
  }

  @Override
  public void end() {
    GraphNode projectNode = getProjectNode();

    if (null != parent) {
      GraphNode parentNode = parent.getGraphNode(context);
      context.newDep(parentNode, projectNode, MavenRelation.PARENT_DEPEND);
    }

    if (null != modules) {
      modules.addMaster(context, projectNode);
    }

    if (null != dependencies) {
      dependencies.addDependent(context, projectNode);
    }

    if (null != properties) {
      properties.addDependent(context, projectNode);
    }
  }

  @Override
  public ElementHandler newChild(String name) {
    ElementHandler labelHandler = label.captureElement(name);
    if (null != labelHandler ) {
      return labelHandler;
    }
    if (ParentLoader.PARENT.equals(name)) {
      parent = new ParentLoader();
      return parent;
    }

    if (PropertiesLoader.PROPERTIES.equals(name)) {
      properties = new PropertiesLoader();
      return properties;
    }

    if (ModulesLoader.MODULES.equals(name)) {
      modules = new ModulesLoader();
      return modules;
    }

    if (DependenciesLoader.DEPENDENCIES.equals(name)) {
      dependencies = new DependenciesLoader();
      return dependencies;
    }

    return super.newChild(name);
  }

  /**
   * Provide the project's own GraphNode.
   */
  public GraphNode getProjectNode() {
    return label.buildDefinitionNode(context);
  }
}
