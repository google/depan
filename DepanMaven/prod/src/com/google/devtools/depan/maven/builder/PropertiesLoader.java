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
import com.google.devtools.depan.maven.graph.PropertyElement;
import com.google.devtools.depan.model.GraphNode;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Interpret Maven properties elements, and create appropriate DepAn
 * relationships in the builder graph.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class PropertiesLoader extends NestingElementHandler {

  public static final String PROPERTIES = "properties";

  private List<String> properties = Lists.newArrayList();

  @Override
  public boolean isFor(String name) {
    return PROPERTIES.equals(name);
  }

  @Override
  public void end() {
  }

  @Override
  public ElementHandler newChild(String name) {
    // Save the name, and consume the value
    properties.add(name);

    return super.newChild(name);
  }

  public void addDependent(MavenContext context, GraphNode dependent) {
    for (String property : properties) {
      GraphNode propNode = new PropertyElement(property);
      context.newDep(propNode, dependent, MavenRelation.PROPERTY_DEPEND);
    }
  }
}
