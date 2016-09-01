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

package com.google.devtools.depan.maven;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.maven.graph.MavenElements;
import com.google.devtools.depan.maven.graph.MavenRelation;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.models.RelationSetDescriptor.Builder;

import com.google.common.collect.Lists;

import java.util.Collection;

/**
 * Built-in relation sets for Maven analysis.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class MavenRelationSets { // extends AbstractSourcePlugin {

  private static final Collection<RelationSetDescriptor> BUILT_IN_SETS;

  private static final RelationSetDescriptor DEPENDENT_MODULES;

  private static final RelationSetDescriptor MODULE_MEMBER;

  private static final RelationSetDescriptor PARENT_MEMBER;

  private static final RelationSetDescriptor PROPERTY_MEMBER;

  private static final RelationSetDescriptor MVN_ALL;

  static {
    RelationSetDescriptor.Builder dependentBuilder =
        createRelSetBuilder("Dependent Modules");
    dependentBuilder.addRelation(MavenRelation.COMPILE_SCOPE);
    dependentBuilder.addRelation(MavenRelation.IMPORT_SCOPE);
    dependentBuilder.addRelation(MavenRelation.PROVIDED_SCOPE);
    dependentBuilder.addRelation(MavenRelation.RUNTIME_SCOPE);
    dependentBuilder.addRelation(MavenRelation.SYSTEM_SCOPE);
    dependentBuilder.addRelation(MavenRelation.TEST_SCOPE);
    DEPENDENT_MODULES = dependentBuilder.build();

    RelationSetDescriptor.Builder moduleBuilder =
        createRelSetBuilder("Maven Modules");
    moduleBuilder.addRelation(MavenRelation.MODULE_DEPEND);
    MODULE_MEMBER = moduleBuilder.build();

    RelationSetDescriptor.Builder parentBuilder =
        createRelSetBuilder("Parent Modules");
    parentBuilder.addRelation(MavenRelation.PARENT_DEPEND);
    PARENT_MEMBER = parentBuilder.build();

    RelationSetDescriptor.Builder propertyBuilder =
        createRelSetBuilder("Property Elements");
    propertyBuilder.addRelation(MavenRelation.PROPERTY_DEPEND);
    PROPERTY_MEMBER = propertyBuilder.build();

    RelationSetDescriptor.Builder containerBuilder =
        createRelSetBuilder("All Maven");
    for (Relation r : MavenElements.RELATIONS) {
      containerBuilder.addRelation(r);
    }
    MVN_ALL = containerBuilder.build();

    BUILT_IN_SETS = Lists.newArrayList();
    BUILT_IN_SETS.add(DEPENDENT_MODULES);
    BUILT_IN_SETS.add(MODULE_MEMBER);
    BUILT_IN_SETS.add(PARENT_MEMBER);
    BUILT_IN_SETS.add(PROPERTY_MEMBER);
    BUILT_IN_SETS.add(MVN_ALL);
  }

  private MavenRelationSets() {
    // Prevent instantiation.
  }

  /**
   * Returns all built-in relationship sets defined by the Maven plug-in.
   */
  public static Collection<RelationSetDescriptor> getBuiltinSets() {
    return BUILT_IN_SETS;
  }

  /**
   * Returns the default relationship set for Maven graphs.
   */
  public static RelationSetDescriptor getDefaultDescriptor() {
    return MVN_ALL;
  }

  private static Builder createRelSetBuilder(String name) {
    return RelationSetDescriptor.createBuilder(
        name, MavenPluginActivator.MAVEN_MODEL);
  }
}
