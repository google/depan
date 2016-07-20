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

package com.google.devtools.depan.java;

import com.google.devtools.depan.edges.matchers.GraphEdgeMatchers;
import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.graph_doc.eclipse.ui.plugins.AnalysisResourceInstaller;
import com.google.devtools.depan.graph_doc.eclipse.ui.resources.AnalysisResources;
import com.google.devtools.depan.java.graph.JavaRelation;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.model.GraphEdgeMatcher;
import com.google.devtools.depan.model.RelationSets;
import com.google.devtools.depan.platform.resources.FeatureMatcher;
import com.google.devtools.depan.platform.resources.ModelResource;
import com.google.devtools.depan.platform.resources.PropertyResources;
import com.google.devtools.depan.platform.resources.ResourceContainer;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;

import com.google.common.collect.ImmutableList;

import java.util.Collections;

/**
 * Captures many of the capabilities provided by the legacy
 * {@code JavaPlugin} mechanism.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class JavaAnalysisResourcePlugin implements
    AnalysisResourceInstaller {

  private static final FeatureMatcher JAVA_MATCHER =
      new FeatureMatcher(
          Collections.<String>emptyList(), 
          // TO-BE: ImmutableList.<String>of(FileSystemNodeContributor.ID),
          ImmutableList.<String>of(JavaRelationContributor.ID));

  public static final RelationSet JAVA_RELSET =
      RelationSets.createArray(JavaRelation.values());

  @Override
  public void installResource(ResourceContainer installRoot) {
    ResourceContainer matchers =
        installRoot.getChild(AnalysisResources.MATCHERS);
    ResourceContainer relSets =
        installRoot.getChild(AnalysisResources.RELATION_SETS);

    installMatchers(matchers);
    installRelSets(relSets);
  }

  private void installMatchers(ResourceContainer matchers) {
    for (RelationSetDescriptor descr : JavaRelationSets.builtins) {
      RelationSet relSet = descr.getRelationSet();
      GraphEdgeMatcher matcher =
          GraphEdgeMatchers.createForwardEdgeMatcher(relSet);
      GraphEdgeMatcherDescriptor install = 
          new GraphEdgeMatcherDescriptor(descr.getName(), matcher);
      ModelResource<GraphEdgeMatcherDescriptor> resource =
          new ModelResource<GraphEdgeMatcherDescriptor>(install, JAVA_MATCHER);
      matchers.addResource(descr.getName(), resource);
    }

    @SuppressWarnings("unchecked")
    ModelResource<GraphEdgeMatcherDescriptor> defResource =
        (ModelResource<GraphEdgeMatcherDescriptor>)
        matchers.getResource(JavaRelationSets.CONTAINER.getName());
    defResource.setProperty(
        PropertyResources.PROP_DEFAULT, JavaRelationContributor.ID);
  }

  private void installRelSets(ResourceContainer relSets) {
    for (RelationSetDescriptor descr : JavaRelationSets.builtins) {
      ModelResource<RelationSetDescriptor> resource =
          new ModelResource<RelationSetDescriptor>(descr, JAVA_MATCHER);
      relSets.addResource(descr.getName(), resource);
    }

    @SuppressWarnings("unchecked")
    ModelResource<RelationSetDescriptor> defResource =
        (ModelResource<RelationSetDescriptor>)
        relSets.getResource(JavaRelationSets.CONTAINER.getName());
    defResource.setProperty(
        PropertyResources.PROP_DEFAULT, JavaRelationContributor.ID);
  }
}
