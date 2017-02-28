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

package com.google.devtools.depan.nodes.filters.model;

import com.google.devtools.depan.analysis_doc.model.ModelAnalysisDocument;
import com.google.devtools.depan.analysis_doc.model.ModelMatcher;
import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.model.Element;

import java.util.Collection;

/**
 * Document type for named resources with collections of {@link Element} types.
 * 
 * @since 2016 Based on legacy {@code RelationshipSet}.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class NodeKindDocument
    extends ModelAnalysisDocument<Collection<Class<? extends Element>>>{

  public NodeKindDocument(
      String name, DependencyModel model,
      Collection<Class<? extends Element>> info) {
    super(name, model, info);
  }

  public NodeKindDocument(
      String name, ModelMatcher matcher,
      Collection<Class<? extends Element>> info) {
    super(name, matcher, info);
  }
}
