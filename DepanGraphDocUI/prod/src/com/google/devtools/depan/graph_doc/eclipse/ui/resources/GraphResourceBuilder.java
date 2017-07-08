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

package com.google.devtools.depan.graph_doc.eclipse.ui.resources;

import com.google.devtools.depan.analysis_doc.model.AnalysisProperties;
import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.matchers.persistence.GraphEdgeMatcherResources;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.persistence.RelationSetResources;
import com.google.devtools.depan.resources.PropertyDocument;
import com.google.devtools.depan.resources.PropertyDocumentReference;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.base.Strings;

import java.util.List;
import java.util.Set;

/**
 * Provide a {@link GraphResource} Builder that uses the "well-known"
 * {@link GraphEdgeMatcherResources} and {@link RelationSetResources} suppliers.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class GraphResourceBuilder {

  private final DependencyModel model;

  private List<PropertyDocumentReference<GraphEdgeMatcherDescriptor>>
      knownMatchers = Lists.newArrayList();

  private List<PropertyDocumentReference<RelationSetDescriptor>>
      knownRelSets = Lists.newArrayList();

  private LinkedHashMultimap<
          String, PropertyDocumentReference<GraphEdgeMatcherDescriptor>>
      defMatchers = LinkedHashMultimap.create();

  private LinkedHashMultimap<
          String, PropertyDocumentReference<RelationSetDescriptor>>
      defRelSets = LinkedHashMultimap.create();

  public GraphResourceBuilder(DependencyModel model) {
    this.model = model;
  }

  public static GraphResources forModel(DependencyModel model) {
    GraphResourceBuilder result = new GraphResourceBuilder(model);
    return result.build();
  }

  public GraphResources build() {

    buildMatcherSets(GraphEdgeMatcherResources.getMatchers(model));
    buildRelationSets(RelationSetResources.getRelationSets(model));

    PropertyDocumentReference<GraphEdgeMatcherDescriptor> defMatcher =
        calcDefMatcherRef();
    PropertyDocumentReference<RelationSetDescriptor> defRelSet = calcDefRelSet();

    return new GraphResources(
        model, knownRelSets, knownMatchers, defRelSet , defMatcher);
  }

  private PropertyDocumentReference<GraphEdgeMatcherDescriptor>
  calcDefMatcherRef() {
    PropertyDocumentReference<GraphEdgeMatcherDescriptor> bestMatcher =
        getBestMatcher();
    if (null != bestMatcher) {
      return bestMatcher;
    }
    if (!knownMatchers.isEmpty()) {
      return knownMatchers.get(0);
    }
    return GraphEdgeMatcherResources.FORWARD_REF;
  }

  private PropertyDocumentReference<GraphEdgeMatcherDescriptor>
  getBestMatcher() {
    if (defMatchers.isEmpty()) {
      return null;
    }
    // Check the contributions in priority order for the default matcher.
    // Since getRelationContribs() sequences elements highest priority first,
    // return the first non-empty matcher candidate.
    for (String contribs : model.getRelationContribs()) {
      Set<PropertyDocumentReference<GraphEdgeMatcherDescriptor>> matchers =
          defMatchers.get(contribs);
      if (null == matchers) {
        continue;
      }
      if (matchers.isEmpty()) {
        continue;
      }
      return matchers.iterator().next();
    }
    return null;
  }

  private PropertyDocumentReference<RelationSetDescriptor> calcDefRelSet() {
    PropertyDocumentReference<RelationSetDescriptor> bestRelSet =
        getBestRelSet();
    if (null != bestRelSet) {
      return bestRelSet;
    }
    if (!knownRelSets.isEmpty()) {
      return knownRelSets.get(0);
    }
    return RelationSetResources.EMPTY_REF;
  }

  private PropertyDocumentReference<RelationSetDescriptor> getBestRelSet() {
    if (defRelSets.isEmpty()) {
      return null;
    }
    // Check the contributions in priority order the default relset.
    for (String contribs : model.getRelationContribs()) {
      Set<PropertyDocumentReference<RelationSetDescriptor>> relSets =
          defRelSets.get(contribs);
      if (null == relSets) {
        continue;
      }
      if (relSets.isEmpty()) {
        continue;
      }
      return relSets.iterator().next();
    }
    return null;
  }

  private void buildRelationSets(
      List<PropertyDocumentReference<RelationSetDescriptor>> relSets) {

    knownRelSets.addAll(relSets);

    for (PropertyDocumentReference<RelationSetDescriptor> matcher : relSets) {
      String defModel = getDefault(matcher);
      if (null != defModel) {
        defRelSets.put(defModel, matcher);
      }
    }
  }

  private void buildMatcherSets(
      List<PropertyDocumentReference<GraphEdgeMatcherDescriptor>> matchers) {

    knownMatchers.addAll(matchers);

    for (PropertyDocumentReference<GraphEdgeMatcherDescriptor> matcher : matchers) {
      String defModel = getDefault(matcher);
      if (null != defModel) {
        defMatchers.put(defModel, matcher);
      }
    }
  }

  private String getDefault(Object resource) {
    if (!(resource instanceof PropertyDocumentReference)) {
      return null;
    }
    PropertyDocument<?> propRes =
        ((PropertyDocumentReference<?>) resource).getDocument();
    String propVal = propRes.getProperty(AnalysisProperties.DEFAULT_PROP);
    if (Strings.isNullOrEmpty(propVal)) {
      return null;
    }
    if (model.getRelationContribs().contains(propVal)) {
      return propVal;
    }
    if (model.getNodeContribs().contains(propVal)) {
      return propVal;
    }
    return null;
  }
}
