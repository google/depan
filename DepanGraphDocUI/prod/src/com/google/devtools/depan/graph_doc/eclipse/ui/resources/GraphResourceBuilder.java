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

import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptors;
import com.google.devtools.depan.platform.resources.ModelResource;
import com.google.devtools.depan.platform.resources.PropertyResource;
import com.google.devtools.depan.platform.resources.PropertyResources;
import com.google.devtools.depan.platform.resources.ResourceContainer;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.models.RelationSetDescriptors;

import com.google.common.collect.Lists;

import java.util.List;

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

  private List<GraphEdgeMatcherDescriptor> defMatchers =
      Lists.newArrayList();

  private List<RelationSetDescriptor> defRelSets =
      Lists.newArrayList();

  public GraphResourceBuilder(
      ResourceContainer root, DependencyModel model) {
    this.root = root;
    this.model = model;
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
    // TODO: Alternatives when there is more then one apparent default
    if (!defMatchers.isEmpty()) {
      return defMatchers.get(0);
    }
    if (!knownMatchers.isEmpty()) {
      return knownMatchers.get(0);
    }
    return GraphEdgeMatcherDescriptors.FORWARD;
  }

  private RelationSetDescriptor calcDefRelSet() {
    // TODO: Alternatives when there is more then one apparent default
    if (!defRelSets.isEmpty()) {
      return defRelSets.get(0);
    }
    if (!knownRelSets.isEmpty()) {
      return knownRelSets.get(0);
    }
    return RelationSetDescriptors.EMPTY;
  }

  @SuppressWarnings("unchecked")
  private void buildRelationSets(
      ResourceContainer tree, DependencyModel model) {
    for (Object resource : tree.getResources()) {
      if (resource instanceof ModelResource<?>) {
        ModelResource<RelationSetDescriptor> checkRes =
            (ModelResource<RelationSetDescriptor>) resource;
        if (checkRes.forModel(model)) {
          RelationSetDescriptor info = checkRes.getInfo();
          knownRelSets.add(info);
          if (isDefault(resource)) {
            defRelSets.add(info);
          }
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  public void buildMatcherSets(
      ResourceContainer tree, DependencyModel model) {

    for (Object resource : tree.getResources()) {
      if (resource instanceof ModelResource<?>) {
        ModelResource<GraphEdgeMatcherDescriptor> checkRes =
            (ModelResource<GraphEdgeMatcherDescriptor>) resource;
        if (checkRes.forModel(model)) {
          GraphEdgeMatcherDescriptor info = checkRes.getInfo();
          knownMatchers.add(info);
          if (isDefault(resource)) {
            defMatchers.add(info);
          }
        }
      }
    }
  }

  private boolean isDefault(Object resource) {
    if (!(resource instanceof PropertyResource)) {
      return false;
    }
    PropertyResource propRes = (PropertyResource) resource;
    String propVal = propRes.getProperty(PropertyResources.PROP_DEFAULT);
    boolean modelContrib = model.getRelationContribs().contains(propVal);
    if (modelContrib) {
      return true;
    }
    return model.getNodeContribs().contains(propVal);
  }
}
