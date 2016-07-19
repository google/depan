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
import com.google.devtools.depan.platform.resources.ModelMatcher;
import com.google.devtools.depan.platform.resources.ModelResource;
import com.google.devtools.depan.platform.resources.ResourceContainer;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.models.RelationSetDescriptors;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class AnalysisResources {

  public static final String ROOT = "resources";

  // Some pre-defined resource containers
  public static final String MATCHERS = "matchers";
  public static final String RELATION_SETS = "relation_sets";

  private static final ResourceContainer ROOT_CONTAINER = 
      ResourceContainer.buildRootContainer(ROOT);

  static {
    ROOT_CONTAINER.addChild(MATCHERS);
    ROOT_CONTAINER.addChild(RELATION_SETS);
    buildAnalysisResources();
  }

  private AnalysisResources() {
    // Prevent instantiation.
  }

  public static ResourceContainer getRoot() {
    return ROOT_CONTAINER;
  }

  public static GraphResources buildAnalysisResources(DependencyModel model) {
    GraphResourceBuilder builder = new GraphResourceBuilder(
        AnalysisResources.getRoot(), model);
    return builder.build();
  }

  private static void buildAnalysisResources() {
    ModelMatcher allModels = new ModelMatcher() {

      @Override
      public boolean forModel(DependencyModel model) {
        return true;
      }
    };

    ResourceContainer matchers = ROOT_CONTAINER.getChild(MATCHERS);
    addMatcher(matchers, GraphEdgeMatcherDescriptors.FORWARD, allModels);
    addMatcher(matchers, GraphEdgeMatcherDescriptors.EMPTY, allModels);

    ResourceContainer relSets = ROOT_CONTAINER.getChild(RELATION_SETS);
    addRelSet(relSets, RelationSetDescriptors.EMPTY, allModels);
  }

  private static void addMatcher(
      ResourceContainer container,
      GraphEdgeMatcherDescriptor edgeMatcher,
      ModelMatcher modelMatcher) {

    ModelResource<GraphEdgeMatcherDescriptor> matcherResource =
        new ModelResource<GraphEdgeMatcherDescriptor>(
            edgeMatcher, modelMatcher);
    container.addResource(edgeMatcher.getName(), matcherResource);
  }

  private static void addRelSet(
      ResourceContainer container,
      RelationSetDescriptor relSet,
      ModelMatcher modelMatcher) {

    ModelResource<RelationSetDescriptor> relSetResource =
        new ModelResource<RelationSetDescriptor>(
            relSet, modelMatcher);
    container.addResource(relSet.getName(), relSetResource);
  }
}
