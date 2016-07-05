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
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.GraphNode;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Interpret Maven dependencies elements, and create appropriate DepAn
 * relationships in the builder graph.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class DependenciesLoader extends NestingElementHandler {

  public static final String DEPENDENCIES = "dependencies";

  private List<DependencyLoader> dependencies = Lists.newArrayList();

  @Override
  public boolean isFor(String name) {
    return DEPENDENCIES.equals(name);
  }

  @Override
  public void end() {
  }

  @Override
  public ElementHandler newChild(String name) {
    if (DependencyLoader.DEPENDENCY.equals(name)) {
      DependencyLoader dependency = new DependencyLoader();
      dependencies.add(dependency);
      return dependency;
    }
    return super.newChild(name);
  }

  public void addDependent(MavenContext context, GraphNode dependent) {
    for (DependencyLoader dependency : dependencies) {
      GraphNode moduleNode = dependency.buildGraphNode(context);
      Relation relation = dependency.getRelation();
      context.newDep(dependent, moduleNode, relation);
    }
  }
}
