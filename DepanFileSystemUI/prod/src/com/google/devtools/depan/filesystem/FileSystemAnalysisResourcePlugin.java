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

import com.google.devtools.depan.analysis_doc.model.AnalysisProperties;
import com.google.devtools.depan.filesystem.graph.FileSystemElements;
import com.google.devtools.depan.matchers.persistence.GraphEdgeMatcherResources;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.models.RelationSetDescriptor.Builder;
import com.google.devtools.depan.relations.persistence.RelationSetResources;
import com.google.devtools.depan.resources.ResourceContainer;
import com.google.devtools.depan.resources.Resources;
import com.google.devtools.depan.resources.analysis.AnalysisResourceInstaller;

import java.util.Collection;
import java.util.Collections;

/**
 * Captures many of the capabilities provided by the legacy
 * {@code FileSystemPlugin} mechanism.
 * 
 * Simpler then other {@link AnalysisResourceInstaller}s,
 * since there is only one built-in {@link RelationSetDescriptor}:
 * {@link #FILE_SYSTEM_RELSET}.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class FileSystemAnalysisResourcePlugin implements
    AnalysisResourceInstaller {

  private static final RelationSetDescriptor FILE_SYSTEM_BASE;

  private static final RelationSetDescriptor DEFAULT_REL_SET;

  private static final Collection<RelationSetDescriptor> BUILT_IN_SETS;

  static {
    RelationSetDescriptor.Builder fileSysBuilder =
        createRelSetBuilder("Filesystem Containers");
    fileSysBuilder.addRelations(FileSystemElements.RELATIONS);
    FILE_SYSTEM_BASE = fileSysBuilder.build();

    // Publish the built-in relation sets
    BUILT_IN_SETS = Collections.singletonList(FILE_SYSTEM_BASE);
    DEFAULT_REL_SET = FILE_SYSTEM_BASE;
  }

  @Override
  public void installResource(ResourceContainer installRoot) {
    installMatchers(GraphEdgeMatcherResources.getContainer());
    installRelSets(RelationSetResources.getContainer());
  }

  private static void installMatchers(ResourceContainer matchers) {
    GraphEdgeMatcherResources.installMatchers(matchers, BUILT_IN_SETS);
    Resources.setProperty(matchers, DEFAULT_REL_SET.getName(),
        AnalysisProperties.DEFAULT_PROP, FileSystemRelationContributor.ID);
  }

  private static void installRelSets(ResourceContainer relSets) {
    RelationSetResources.installRelSets(relSets, BUILT_IN_SETS);
    Resources.setProperty(relSets, DEFAULT_REL_SET.getName(),
        AnalysisProperties.DEFAULT_PROP, FileSystemRelationContributor.ID);
  }

  private static Builder createRelSetBuilder(String name) {
    return RelationSetDescriptor.createBuilder(
        name, FileSystemPluginActivator.FILE_SYSTEM_MODEL);
  }
}
