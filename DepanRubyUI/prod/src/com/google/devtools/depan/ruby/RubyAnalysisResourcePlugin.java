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

package com.google.devtools.depan.ruby;

import com.google.devtools.depan.analysis_doc.model.AnalysisProperties;
import com.google.devtools.depan.matchers.persistence.GraphEdgeMatcherResources;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.models.RelationSetDescriptor.Builder;
import com.google.devtools.depan.relations.persistence.RelationSetResources;
import com.google.devtools.depan.resources.ResourceContainer;
import com.google.devtools.depan.resources.Resources;
import com.google.devtools.depan.resources.analysis.AnalysisResourceInstaller;
import com.google.devtools.depan.ruby.graph.RubyElements;
import com.google.devtools.depan.ruby.graph.RubyRelation;

import com.google.common.collect.Lists;

import java.util.Collection;

/**
 * Captures many of the capabilities provided by the legacy
 * {@code RubyPlugin} mechanism.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class RubyAnalysisResourcePlugin implements
    AnalysisResourceInstaller {

  private static final RelationSetDescriptor EXTENDS_TYPE;

  private static final RelationSetDescriptor MEMBER_ELEMENTS;

  private static final RelationSetDescriptor RUBY_ALL;

  private static final RelationSetDescriptor DEFAULT_REL_SET;

  /**
   * List of all built-in RelationshipSets. Make it easier to iterate.
   */
  private static final Collection<RelationSetDescriptor> BUILT_IN_SETS;

  static {

    RelationSetDescriptor.Builder dependentBuilder =
        createRelSetBuilder("Ruby Extends");
    dependentBuilder.addRelation(RubyRelation.EXTENDS_TYPE);
    EXTENDS_TYPE = dependentBuilder.build();

    RelationSetDescriptor.Builder moduleBuilder =
        createRelSetBuilder("Ruby Members");
    moduleBuilder.addRelation(RubyRelation.CLASS_MEMBER);
    moduleBuilder.addRelation(RubyRelation.INSTANCE_MEMBER);
    moduleBuilder.addRelation(RubyRelation.SINGLETON_MEMBER);
    MEMBER_ELEMENTS = moduleBuilder.build();

    RelationSetDescriptor.Builder containerBuilder =
        createRelSetBuilder("All Ruby");
    containerBuilder.addRelations(RubyElements.RELATIONS);
    RUBY_ALL = containerBuilder.build();

    BUILT_IN_SETS = Lists.newArrayList();
    BUILT_IN_SETS.add(MEMBER_ELEMENTS);
    BUILT_IN_SETS.add(EXTENDS_TYPE);
    BUILT_IN_SETS.add(RUBY_ALL);

    DEFAULT_REL_SET = RUBY_ALL;
  }

  @Override
  public void installResource(ResourceContainer installRoot) {
    installMatchers(GraphEdgeMatcherResources.getContainer());
    installRelSets(RelationSetResources.getContainer());
  }

  private static void installMatchers(ResourceContainer matchers) {
    GraphEdgeMatcherResources.installMatchers(matchers, BUILT_IN_SETS);
    Resources.setProperty(matchers, DEFAULT_REL_SET.getName(),
        AnalysisProperties.DEFAULT_PROP, RubyRelationContributor.ID);
  }

  private static void installRelSets(ResourceContainer relSets) {
    RelationSetResources.installRelSets(relSets, BUILT_IN_SETS);
    Resources.setProperty(relSets, DEFAULT_REL_SET.getName(),
        AnalysisProperties.DEFAULT_PROP, RubyRelationContributor.ID);
  }

  private static Builder createRelSetBuilder(String name) {
    return RelationSetDescriptor.createBuilder(
        name, RubyPluginActivator.RUBY_MODEL);
  }
}
