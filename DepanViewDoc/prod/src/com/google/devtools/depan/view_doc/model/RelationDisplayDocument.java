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

import com.google.devtools.depan.analysis_doc.model.ModelAnalysisDocument;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph_doc.model.DependencyModel;

import java.util.Map;

/**
 * Define a persistent document for relation display properties.
 * This document type is intentionally consistent with the
 * {@link ViewDocument}'s relation display properties data structure,
 * and can be created from or inserted into that component.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class RelationDisplayDocument
  extends ModelAnalysisDocument<Map<Relation, EdgeDisplayProperty>> {

  public RelationDisplayDocument(
      String name, DependencyModel model,
      Map<Relation, EdgeDisplayProperty> relationProperties) {
    super(name, model, relationProperties);
  }
}
