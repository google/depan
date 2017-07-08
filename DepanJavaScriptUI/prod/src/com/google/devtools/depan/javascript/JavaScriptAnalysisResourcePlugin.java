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

package com.google.devtools.depan.javascript;

import com.google.devtools.depan.analysis_doc.model.AnalysisProperties;
import com.google.devtools.depan.filesystem.graph.FileSystemRelation;
import com.google.devtools.depan.java.JavaRelationContributor;
import com.google.devtools.depan.javascript.graph.JavaScriptRelation;
import com.google.devtools.depan.matchers.persistence.GraphEdgeMatcherResources;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.models.RelationSetDescriptor.Builder;
import com.google.devtools.depan.relations.persistence.RelationSetResources;
import com.google.devtools.depan.resources.ResourceContainer;
import com.google.devtools.depan.resources.Resources;
import com.google.devtools.depan.resources.analysis.AnalysisResourceInstaller;

import com.google.common.collect.Lists;

import java.util.Collection;

/**
 * Captures many of the capabilities provided by the legacy
 * {@code MavenPlugin} mechanism.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class JavaScriptAnalysisResourcePlugin implements
    AnalysisResourceInstaller {

  /**
   * A standard containment relationship for all JavaScript components,
   * including the common FileSystem relations.
   */
  private static final RelationSetDescriptor LEXICAL_CONTAINMENT;

  /**
   * A containment relationship for JavaScript components that only include
   * scope binding.
   */
  private static final RelationSetDescriptor BINDING_CONTAINMENT;

  /**
   * The default RelationshipSets exported by the JavaScript plug-in.
   */
  private static final RelationSetDescriptor DEFAULT_REL_SET;

  /**
   * List of all built-in RelationshipSets. Makes it easier to iterate.
   */
  private static final Collection<RelationSetDescriptor> BUILT_IN_SETS;

  static {
    // container relationships
    RelationSetDescriptor.Builder lexBuilder =
        createRelSetBuilder("JavaScript Lexical Containment");
    lexBuilder.addRelation(FileSystemRelation.CONTAINS_DIR);
    lexBuilder.addRelation(FileSystemRelation.CONTAINS_FILE);
    lexBuilder.addRelation(FileSystemRelation.SYMBOLIC_LINK);
    lexBuilder.addRelation(JavaScriptRelation.DEFINES_NAME);
    lexBuilder.addRelation(JavaScriptRelation.IMPLIES_NAME);
    lexBuilder.addRelation(JavaScriptRelation.BINDS_ELEMENT);
    LEXICAL_CONTAINMENT = lexBuilder.build();

    RelationSetDescriptor.Builder bindBuilder =
        createRelSetBuilder("JavaScript Binding Containment");
    bindBuilder.addRelation(JavaScriptRelation.BINDS_ELEMENT);
    BINDING_CONTAINMENT = bindBuilder.build();

    // Publish the built-in relation sets
    BUILT_IN_SETS = Lists.newArrayList();
    BUILT_IN_SETS.add(LEXICAL_CONTAINMENT);
    BUILT_IN_SETS.add(BINDING_CONTAINMENT);

    // Publish the default relation set
    DEFAULT_REL_SET = LEXICAL_CONTAINMENT;
  }

  @Override
  public void installResource(ResourceContainer installRoot) {
    installMatchers(GraphEdgeMatcherResources.getContainer());
    installRelSets(RelationSetResources.getContainer());
  }

  private static void installMatchers(ResourceContainer matchers) {
    GraphEdgeMatcherResources.installMatchers(matchers, BUILT_IN_SETS);
    Resources.setProperty(matchers, DEFAULT_REL_SET.getName(),
        AnalysisProperties.DEFAULT_PROP, JavaScriptRelationContributor.ID);
  }

  private static void installRelSets(ResourceContainer relSets) {
    RelationSetResources.installRelSets(relSets, BUILT_IN_SETS);
    Resources.setProperty(relSets, DEFAULT_REL_SET.getName(),
        AnalysisProperties.DEFAULT_PROP, JavaRelationContributor.ID);
  }

  private static Builder createRelSetBuilder(String name) {
    return RelationSetDescriptor.createBuilder(
        name, JavaScriptPluginActivator.JAVASCRIPT_MODEL);
  }
}
