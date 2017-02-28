/*
 * Copyright 2007 The Depan Project Authors
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

package com.google.devtools.depan.matchers.models;

import com.google.devtools.depan.analysis_doc.model.ModelAnalysisDocument;
import com.google.devtools.depan.analysis_doc.model.ModelMatcher;
import com.google.devtools.depan.graph.api.EdgeMatcher;
import com.google.devtools.depan.graph_doc.model.DependencyModel;

/**
 * Document type for named {@link EdgeMatcher} resources.
 * 
 * @since 2015 Based on legacy {@code RelationshipSet}.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class GraphEdgeMatcherDescriptor
    extends ModelAnalysisDocument<EdgeMatcher<String>>{

  public GraphEdgeMatcherDescriptor(
      String name, DependencyModel model, EdgeMatcher<String> info) {
    super(name, model, info);
  }

  public GraphEdgeMatcherDescriptor(
      String name, ModelMatcher matcher, EdgeMatcher<String> info) {
    super(name, matcher, info);
  }
}
