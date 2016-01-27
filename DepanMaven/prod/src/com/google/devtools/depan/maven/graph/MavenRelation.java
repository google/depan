/*
 * Copyright 2016 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.devtools.depan.maven.graph;

import com.google.devtools.depan.graph.api.Relation;

/**
 * Lists relations that takes place between various {@code MavenElement}s
 * 
 * Each Maven Scope is a separate kind of DepAn relation edge.
 * Additional relations model parent, module, property, and build tool
 * dependencies.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public enum MavenRelation implements Relation {
  // Various scope defined dependencies.
  COMPILE_SCOPE("build", "uses build"),
  IMPORT_SCOPE("imports", "uses import"),
  PROVIDED_SCOPE("provides", "uses provider"),
  RUNTIME_SCOPE("runtime use", "uses runtime"),
  SYSTEM_SCOPE("system use", "uses system"),
  TEST_SCOPE("test use", "uses test"),

  TOOL_DEPEND("tool", "uses"),
  PARENT_DEPEND("parent", "child"),
  MODULE_DEPEND("module", "master"),
  PROPERTY_DEPEND("property", "uses property")
  ;

  /**
   * Name of the element on the left of the relation.
   */
  public final String forwardName;

  /**
   * Name of the element on the right side of the relation.
   */
  public final String reverseName;

  /**
   * Constructor for a new Relation.
   * 
   * @param forwardName name of the left hand side element.
   * @param reverseName name of the right hand side element.
   */
  private MavenRelation(String reverseName, String forwardName) {
    this.forwardName = forwardName;
    this.reverseName = reverseName;
  }

  /**
   * Provides the forward name of this relation.
   *
   * @return Forward name.
   */
  @Override
  public String getForwardName() {
    return forwardName;
  }

  /**
   * Provides the reverse name of this relation.
   *
   * @return Reverse name.
   */
  @Override
  public String getReverseName() {
    return reverseName;
  }
}
