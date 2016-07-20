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

package com.google.devtools.depan.filesystem;

import com.google.devtools.depan.edges.matchers.GraphEdgeMatchers;
import com.google.devtools.depan.filesystem.graph.FileSystemRelation;
import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.graph_doc.eclipse.ui.plugins.AnalysisResourceInstaller;
import com.google.devtools.depan.graph_doc.eclipse.ui.resources.AnalysisResources;
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
 * {@code FileSystemPlugin} mechanism.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class FileSystemAnalysisResourcePlugin implements
    AnalysisResourceInstaller {

  private static final String FS_CONTAINER_LABEL = "Filesystem Containers";

  private static final FeatureMatcher FS_MATCHER =
      new FeatureMatcher(
          Collections.<String>emptyList(), 
          // TO-BE: ImmutableList.<String>of(FileSystemNodeContributor.ID),
          ImmutableList.<String>of(FileSystemRelationContributor.ID));

  public static final RelationSet FS_RELSET =
      RelationSets.createArray(FileSystemRelation.values());

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
    GraphEdgeMatcher matcher =
        GraphEdgeMatchers.createForwardEdgeMatcher(FS_RELSET);
    GraphEdgeMatcherDescriptor descr =
        new GraphEdgeMatcherDescriptor(FS_CONTAINER_LABEL, matcher);
    ModelResource<GraphEdgeMatcherDescriptor> resource =
        new ModelResource<GraphEdgeMatcherDescriptor>(descr, FS_MATCHER);
    resource.setProperty(
        PropertyResources.PROP_DEFAULT, FileSystemRelationContributor.ID);
    matchers.addResource(descr.getName(), resource);
  }

  private void installRelSets(ResourceContainer relSets) {
    RelationSetDescriptor descr =
        new RelationSetDescriptor(FS_CONTAINER_LABEL, FS_RELSET);
    ModelResource<RelationSetDescriptor> resource =
        new ModelResource<RelationSetDescriptor>(descr, FS_MATCHER);
    resource.setProperty(
        PropertyResources.PROP_DEFAULT, FileSystemRelationContributor.ID);
    relSets.addResource(descr.getName(), resource);
  }
}
