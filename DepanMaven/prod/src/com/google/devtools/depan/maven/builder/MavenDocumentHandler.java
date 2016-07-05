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

import com.google.devtools.depan.pushxml.PushDownXmlHandler.DocumentHandler;
import com.google.devtools.depan.pushxml.PushDownXmlHandler.ElementHandler;
import com.google.devtools.depan.model.GraphNode;

/**
 * Interpret the Maven project element, the document level element for
 * POM definitions.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class MavenDocumentHandler extends DocumentHandler {

  private final MavenContext context;

  private ProjectLoader project;

  public MavenDocumentHandler(MavenContext context) {
    this.context = context;
  }

  @Override
  protected ElementHandler newDocumentElement(String name) {
    if (ProjectLoader.PROJECT.equals(name)) {
      project = new ProjectLoader(context);
      return project;
    }

    return null;
  }

  public MavenContext getContext() {
    return context;
  }

  public GraphNode getProjectNode() {
    return project.getProjectNode();
  }
}
