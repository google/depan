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
import com.google.devtools.depan.filesystem.graph.FileSystemRelation;
import com.google.devtools.depan.java.graph.JavaElements;
import com.google.devtools.depan.java.graph.JavaRelation;
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
 * {@code JavaPlugin} mechanism.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class JavaAnalysisResourcePlugin implements
    AnalysisResourceInstaller {

  private static final RelationSetDescriptor CLASS_MEMBER;

  private static final RelationSetDescriptor CONTAINER;

  private static final RelationSetDescriptor PKG_MEMBER;

  private static final RelationSetDescriptor STATIC_MEMBER;

  private static final RelationSetDescriptor INSTANCE_MEMBER;

  private static final RelationSetDescriptor EXTENSION;

  private static final RelationSetDescriptor ANNOTATIONS;

  private static final RelationSetDescriptor USES;

  private static final RelationSetDescriptor ALL;

  private static final RelationSetDescriptor DEFAULT_REL_SET;

  /**
   * List of all built-in RelationshipSets. Make it easier to iterate.
   */
  private static final Collection<RelationSetDescriptor> BUILT_IN_SETS;

  static {
    // class member relationships only
    Builder classBuilder = createRelSetBuilder("Class Members");
    classBuilder.addRelation(JavaRelation.INNER_TYPE);
    classBuilder.addRelation(JavaRelation.MEMBER_FIELD);
    classBuilder.addRelation(JavaRelation.MEMBER_METHOD);
    classBuilder.addRelation(JavaRelation.MEMBER_TYPE);
    classBuilder.addRelation(JavaRelation.STATIC_FIELD);
    classBuilder.addRelation(JavaRelation.STATIC_METHOD);
    classBuilder.addRelation(JavaRelation.STATIC_TYPE);
    classBuilder.addRelation(JavaRelation.ANONYMOUS_TYPE);
    CLASS_MEMBER = classBuilder.build();

    // package + class member relationships
    Builder pkgBuilder = createRelSetBuilder("Package Members");
    pkgBuilder.addRelation(JavaRelation.CLASS);
    pkgBuilder.addRelation(JavaRelation.PACKAGE);
    pkgBuilder.addBuilderSet(classBuilder);
    PKG_MEMBER = pkgBuilder.build();

    // container relationships
    Builder containerBuilder = createRelSetBuilder("Java Containers");
    containerBuilder.addRelation(FileSystemRelation.CONTAINS_DIR);
    containerBuilder.addRelation(FileSystemRelation.CONTAINS_FILE);
    containerBuilder.addRelation(JavaRelation.CLASSFILE);
    containerBuilder.addRelation(JavaRelation.LOCAL_VARIABLE);
    containerBuilder.addBuilderSet(classBuilder);
    CONTAINER = containerBuilder.build();

    // static class member relationships
    Builder staticBuilder = createRelSetBuilder("Static Members");
    staticBuilder.addRelation(JavaRelation.STATIC_FIELD);
    staticBuilder.addRelation(JavaRelation.STATIC_METHOD);
    staticBuilder.addRelation(JavaRelation.STATIC_TYPE);
    STATIC_MEMBER = staticBuilder.build();

    // instance class member relationships
    Builder instanceBuilder = createRelSetBuilder("Instance Members");
    instanceBuilder.addRelation(JavaRelation.MEMBER_FIELD);
    instanceBuilder.addRelation(JavaRelation.MEMBER_METHOD);
    instanceBuilder.addRelation(JavaRelation.MEMBER_TYPE);
    INSTANCE_MEMBER = instanceBuilder.build();

    // object extension relationships
    Builder extBuilder = createRelSetBuilder("Extensions");
    extBuilder.addRelation(JavaRelation.WRITE);
    extBuilder.addRelation(JavaRelation.EXTENDS);
    extBuilder.addRelation(JavaRelation.IMPLEMENTS);
    extBuilder.addRelation(JavaRelation.INTERFACE_EXTENDS);
    extBuilder.addRelation(JavaRelation.METHOD_OVERLOAD);
    extBuilder.addRelation(JavaRelation.METHOD_OVERRIDE);
    extBuilder.addRelation(JavaRelation.ERROR_HANDLING);
    EXTENSION = extBuilder.build();

    // object use relationships
    Builder useBuilder = createRelSetBuilder("Uses");
    useBuilder.addRelation(JavaRelation.CALL);
    useBuilder.addRelation(JavaRelation.READ);
    useBuilder.addRelation(JavaRelation.TYPE);
    USES = useBuilder.build();

    // object use relationships
    Builder annotationBuilder = createRelSetBuilder("Annotations");
    annotationBuilder.addRelation(JavaRelation.RUNTIME_ANNOTATION);
    annotationBuilder.addRelation(JavaRelation.COMPILE_ANNOTATION);
    ANNOTATIONS = annotationBuilder.build();

    // check all relationships
    Builder allBuilder = createRelSetBuilder("All Java");
    allBuilder.addRelations(JavaElements.RELATIONS);
    ALL = allBuilder.build();

    // add predefined sets to the built-in list
    BUILT_IN_SETS = Lists.newArrayList();
    BUILT_IN_SETS.add(CLASS_MEMBER);
    BUILT_IN_SETS.add(PKG_MEMBER);
    BUILT_IN_SETS.add(STATIC_MEMBER);
    BUILT_IN_SETS.add(ANNOTATIONS);
    BUILT_IN_SETS.add(INSTANCE_MEMBER);
    BUILT_IN_SETS.add(EXTENSION);
    BUILT_IN_SETS.add(USES);
    BUILT_IN_SETS.add(CONTAINER);
    BUILT_IN_SETS.add(ALL);

    DEFAULT_REL_SET = PKG_MEMBER;
  }

  @Override
  public void installResource(ResourceContainer installRoot) {
    installMatchers(GraphEdgeMatcherResources.getContainer());
    installRelSets(RelationSetResources.getContainer());
  }

  private static void installMatchers(ResourceContainer matchers) {
    GraphEdgeMatcherResources.installMatchers(matchers, BUILT_IN_SETS);
    Resources.setProperty(matchers, DEFAULT_REL_SET.getName(),
        AnalysisProperties.DEFAULT_PROP, JavaRelationContributor.ID);
  }

  private static void installRelSets(ResourceContainer relSets) {
    RelationSetResources.installRelSets(relSets, BUILT_IN_SETS);
    Resources.setProperty(relSets, DEFAULT_REL_SET.getName(),
        AnalysisProperties.DEFAULT_PROP, JavaRelationContributor.ID);
  }

  private static Builder createRelSetBuilder(String name) {
    return RelationSetDescriptor.createBuilder(
        name, JavaPluginActivator.JAVA_MODEL);
  }
}
