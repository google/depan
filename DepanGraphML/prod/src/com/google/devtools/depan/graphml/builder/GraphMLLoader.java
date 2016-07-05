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

package com.google.devtools.depan.graphml.builder;

import com.google.devtools.depan.pushxml.PushDownXmlHandler.ElementHandler;
import com.google.devtools.depan.pushxml.PushDownXmlHandler.NestingElementHandler;

/**
 * Interpret GraphML elements, and create appropriate DepAn
 * relationships in the builder graph.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class GraphMLLoader extends NestingElementHandler {
  
  public static final String GRAPHML = "graphml";

  private final GraphMLContext context;

  private GraphLoader graph;

  public GraphMLLoader(GraphMLContext context) {
    this.context = context;
  }

  @Override
  public boolean isFor(String name) {
    return GRAPHML.equals(name);
  }

  @Override
  public void end() {
  }

  @Override
  public ElementHandler newChild(String name) {
    if (GraphLoader.GRAPH.equals(name)) {
      graph = new GraphLoader(context);
      return graph;
    }

    return super.newChild(name);
  }
}
