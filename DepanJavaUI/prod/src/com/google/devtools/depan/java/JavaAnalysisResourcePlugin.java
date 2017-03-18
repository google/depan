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

import com.google.devtools.depan.analysis_doc.model.AnalysisProperties;
import com.google.devtools.depan.edges.matchers.GraphEdgeMatchers;
import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.matchers.persistence.GraphEdgeMatcherResources;
import com.google.devtools.depan.model.GraphEdgeMatcher;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.persistence.RelationSetResources;
import com.google.devtools.depan.resources.ResourceContainer;
import com.google.devtools.depan.resources.analysis.AnalysisResourceInstaller;

/**
 * Captures many of the capabilities provided by the legacy
 * {@code JavaPlugin} mechanism.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class JavaAnalysisResourcePlugin implements
    AnalysisResourceInstaller {

  @Override
  public void installResource(ResourceContainer installRoot) {
    installMatchers(GraphEdgeMatcherResources.getContainer());
    installRelSets(RelationSetResources.getContainer());
  }

  private void installMatchers(ResourceContainer matchers) {
    for (RelationSetDescriptor descr : JavaRelationSets.getBuiltinSets()) {
      RelationSet relSet = descr.getInfo();
      GraphEdgeMatcher matcher =
          GraphEdgeMatchers.createForwardEdgeMatcher(relSet);
      GraphEdgeMatcherDescriptor resource = 
          new GraphEdgeMatcherDescriptor(
              descr.getName(), descr.getModel(), matcher);
      matchers.addResource(descr.getName(), resource);
    }

    RelationSetDescriptor defRelSet = JavaRelationSets.getDefaultDescriptor();
    GraphEdgeMatcherDescriptor defResource = (GraphEdgeMatcherDescriptor)
        matchers.getResource(defRelSet.getName());
    defResource.setProperty(
        AnalysisProperties.DEFAULT_PROP, JavaRelationContributor.ID);
  }

  private void installRelSets(ResourceContainer relSets) {
    for (RelationSetDescriptor descr : JavaRelationSets.getBuiltinSets()) {
      relSets.addResource(descr.getName(), descr);
    }

    RelationSetDescriptor defRelSet = JavaRelationSets.getDefaultDescriptor();
    RelationSetDescriptor defResource = (RelationSetDescriptor)
        relSets.getResource(defRelSet.getName());
    defResource.setProperty(
        AnalysisProperties.DEFAULT_PROP, JavaRelationContributor.ID);
  }
}
