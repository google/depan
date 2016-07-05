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
import com.google.devtools.depan.model.GraphNode;

/**
 * Interpret Maven parent elements, and create appropriate DepAn
 * relationships in the builder graph.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class ParentLoader extends NestingElementHandler {

  public static final String PARENT = "parent";
  public static final String RELATIVE_PATH = "relativePath";

  private LabelCapture label = new LabelCapture();

  @Override
  public boolean isFor(String name) {
    return PARENT.equals(name);
  }

  @Override
  public void end() {
  }

  @Override
  public ElementHandler newChild(String name) {
    ElementHandler labelHandler = label.captureElement(name);
    if (null != labelHandler) {
      return labelHandler;
    }

    if (RELATIVE_PATH.equals(name)) {
      return new TextElementHandler(name);
    }
    return super.newChild(name);
  }

  public GraphNode getGraphNode(MavenContext context) {
    return label.buildReferenceNode(context);
  }
}
