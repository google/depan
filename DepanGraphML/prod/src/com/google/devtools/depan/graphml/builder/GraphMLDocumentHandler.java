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

import com.google.devtools.depan.pushxml.PushDownXmlHandler.DocumentHandler;
import com.google.devtools.depan.pushxml.PushDownXmlHandler.ElementHandler;

/**
 * Interpret the GraphML element, the document level element for
 * GraphML definitions.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class GraphMLDocumentHandler extends DocumentHandler {

  private final GraphMLContext context;

  private GraphMLLoader graphml;

  public GraphMLDocumentHandler(GraphMLContext context) {
    this.context = context;
  }

  @Override
  protected ElementHandler newDocumentElement(String name) {
    if (GraphMLLoader.GRAPHML.equals(name)) {
      graphml = new GraphMLLoader(context);
      return graphml;
    }

    return null;
  }

  public GraphMLContext getContext() {
    return context;
  }
}
