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
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.maven.graph.MavenRelation;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphNode;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Interpret Maven dependency elements, and create appropriate DepAn
 * relationships in the builder graph.
 * 
 * Different DepAn relationships are created based on the scope defined
 * with the dependency element.  In the normal case where the scope is
 * unspecified, a compile scope is inferred and results in a
 * {@link MavenRelation#COMPILE_SCOPE} edge.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class DependencyLoader extends NestingElementHandler {
  
  public static final String DEPENDENCY = "dependency";

  public static final String SCOPE = "scope";

  public static final Relation DEFAULT_SCOPE = MavenRelation.COMPILE_SCOPE;
  
  private static final Map<String, Relation> SCOPE_MAP =
      Maps.newHashMap();

  private static final String COMPILE = "compile";
  private static final String PROVIDED = "provided";
  private static final String RUNTIME = "runtime";
  private static final String TEST = "test";
  private static final String SYSTEM = "system";
  private static final String IMPORT = "import";

  static {
    SCOPE_MAP.put(COMPILE, MavenRelation.COMPILE_SCOPE);
    SCOPE_MAP.put(PROVIDED, MavenRelation.PROVIDED_SCOPE);
    SCOPE_MAP.put(RUNTIME, MavenRelation.RUNTIME_SCOPE);
    SCOPE_MAP.put(TEST, MavenRelation.TEST_SCOPE);
    SCOPE_MAP.put(SYSTEM, MavenRelation.SYSTEM_SCOPE);
    SCOPE_MAP.put(IMPORT, MavenRelation.IMPORT_SCOPE);
    SCOPE_MAP.put(COMPILE, MavenRelation.COMPILE_SCOPE);
  }

  private LabelCapture label = new LabelCapture();

  private TextElementHandler scope;

  @Override
  public boolean isFor(String name) {
    return DEPENDENCY.equals(name);
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
    if (SCOPE.equals(name)) {
      scope = new TextElementHandler(name);
    }
    return super.newChild(name);
  }

  public GraphNode buildGraphNode(MavenContext context) {
    return label.buildReferenceNode(context);
  }

  /**
   * Provide the {@link Relation} to use for the {@link GraphEdge} to this
   * dependency.
   */
  public Relation getRelation() {
    if (null == scope) {
      return DEFAULT_SCOPE;
    }
    String scopeText = scope.getText();
    if (Strings.isNullOrEmpty(scopeText)) {
      return DEFAULT_SCOPE;
    }
    Relation result = SCOPE_MAP.get(scopeText);
    if (null != result) {
      return result;
    }
    return DEFAULT_SCOPE;
  }
}
