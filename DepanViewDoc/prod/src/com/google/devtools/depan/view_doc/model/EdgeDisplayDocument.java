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

package com.google.devtools.depan.view_doc.model;

import com.google.devtools.depan.graph.api.Relation;

import java.util.Map;

/**
 * Define a persistent document for edge display properties.
 * This document type is intentionally consistent with the
 * {@link ViewDocument}'s relation display properties data structure,
 * and can be created from or inserted into that component.
 * 
 * This should be a project managed resource document.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class EdgeDisplayDocument {

  public static final String EXTENSION = "vedxml";

  /**
   * Hash map that contains a list of edge display property objects
   * for known relations.
   */
  private final Map<Relation, EdgeDisplayProperty> relationProperties;

  private final String name;

  public EdgeDisplayDocument(String name,
      Map<Relation, EdgeDisplayProperty> relationProperties) {
    super();
    this.name = name;
    this.relationProperties = relationProperties;
  }

  public Map<Relation, EdgeDisplayProperty> getRelationProperties() {
    return relationProperties;
  }

  public String getName() {
    return name;
  }
}
