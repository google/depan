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
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptors;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.models.RelationSetDescriptors;
import com.google.devtools.depan.resources.PropertyDocument;
import com.google.devtools.depan.resources.ResourceContainer;
import com.google.devtools.depan.resources.analysis.AnalysisResources;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Set;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class GraphResourceBuilder {

  private final ResourceContainer root;

  private final DependencyModel model;

  private List<GraphEdgeMatcherDescriptor> knownMatchers =
      Lists.newArrayList();

  private List<RelationSetDescriptor> knownRelSets =
      Lists.newArrayList();

  private LinkedHashMultimap<String, GraphEdgeMatcherDescriptor> defMatchers =
      LinkedHashMultimap.create();

  private LinkedHashMultimap<String, RelationSetDescriptor> defRelSets =
      LinkedHashMultimap.create();

  public GraphResourceBuilder(
      ResourceContainer root, DependencyModel model) {
    this.root = root;
    this.model = model;
  }

  public static GraphResources forModel(DependencyModel model) {
    ResourceContainer root = AnalysisResources.getRoot();
    GraphResourceBuilder result = new GraphResourceBuilder(root, model);
    return result.build();
  }

  public GraphResources build() {
    ResourceContainer relSetCntr =
        root.getChild(AnalysisResources.RELATION_SETS);
    buildRelationSets(relSetCntr, model);
    RelationSetDescriptor defRelSet = calcDefRelSet();

    ResourceContainer matcherCntr =
        root.getChild(AnalysisResources.MATCHERS);
    buildMatcherSets(matcherCntr, model);
    GraphEdgeMatcherDescriptor defMatcher = calcDefMatcher();

    return new GraphResources(
        model, knownRelSets, knownMatchers, defRelSet, defMatcher);
  }

  private GraphEdgeMatcherDescriptor calcDefMatcher() {
    GraphEdgeMatcherDescriptor bestMatcher = getBestMatcher();
    if (null != bestMatcher) {
      return bestMatcher;
    }
    if (!knownMatchers.isEmpty()) {
      return knownMatchers.get(0);
    }
    return GraphEdgeMatcherDescriptors.FORWARD;
  }

  private GraphEdgeMatcherDescriptor getBestMatcher() {
    if (defMatchers.isEmpty()) {
      return null;
    }
    // Check the contributions in priority order the default matcher.
    for (String contribs : model.getRelationContribs()) {
      Set<GraphEdgeMatcherDescriptor> matchers = defMatchers.get(contribs);
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

  private RelationSetDescriptor calcDefRelSet() {
    RelationSetDescriptor bestRelSet = getBestRelSet();
    if (null != bestRelSet) {
      return bestRelSet;
    }
    if (!knownRelSets.isEmpty()) {
      return knownRelSets.get(0);
    }
    return RelationSetDescriptors.EMPTY;
  }

  private RelationSetDescriptor getBestRelSet() {
    if (defRelSets.isEmpty()) {
      return null;
    }
    // Check the contributions in priority order the default relset.
    for (String contribs : model.getRelationContribs()) {
      Set<RelationSetDescriptor> relSets = defRelSets.get(contribs);
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
      ResourceContainer tree, DependencyModel model) {
    for (Object resource : tree.getResources()) {
      if (resource instanceof RelationSetDescriptor) {
        RelationSetDescriptor checkRes = (RelationSetDescriptor) resource;
        if (checkRes.forModel(model)) {
          knownRelSets.add(checkRes);
          String defModel = getDefault(checkRes);
          if (null != defModel) {
            defRelSets.put(defModel, checkRes);
          }
        }
      }
    }
  }

  private void buildMatcherSets(
      ResourceContainer tree, DependencyModel model) {

    for (Object resource : tree.getResources()) {
      if (resource instanceof GraphEdgeMatcherDescriptor) {
        GraphEdgeMatcherDescriptor checkRes =
            (GraphEdgeMatcherDescriptor) resource;
        if (checkRes.forModel(model)) {
          knownMatchers.add(checkRes);
          String defModel = getDefault(checkRes);
          if (null != defModel) {
            defMatchers.put(defModel, checkRes);
          }
        }
      }
    }
  }

  private String getDefault(Object resource) {
    if (!(resource instanceof PropertyDocument)) {
      return null;
    }
    PropertyDocument<?> propRes = (PropertyDocument<?>) resource;
    String propVal = propRes.getProperty(AnalysisProperties.DEFAULT_PROP);
    if (model.getRelationContribs().contains(propVal)) {
      return propVal;
    }
    if (model.getNodeContribs().contains(propVal)) {
      return propVal;
    }
    return null;
  }
}
