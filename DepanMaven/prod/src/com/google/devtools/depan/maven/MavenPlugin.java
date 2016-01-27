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

import com.google.devtools.depan.eclipse.persist.XStreamFactory.Config;
import com.google.devtools.depan.eclipse.plugins.ElementClassTransformer;
import com.google.devtools.depan.eclipse.plugins.ElementTransformer;
import com.google.devtools.depan.eclipse.plugins.AbstractSourcePlugin;
import com.google.devtools.depan.eclipse.utils.ElementEditor;
import com.google.devtools.depan.eclipse.visualization.ogl.GLEntity;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.maven.eclipse.MavenIconTransformer;
import com.google.devtools.depan.maven.eclipse.MavenImageTransformer;
import com.google.devtools.depan.maven.eclipse.MavenNodeComparator;
import com.google.devtools.depan.maven.eclipse.MavenNodePainter;
import com.google.devtools.depan.maven.eclipse.MavenShapeTransformer;
import com.google.devtools.depan.maven.eclipse.NewMavenPomWizard;
import com.google.devtools.depan.maven.editors.MavenElementEditors;
import com.google.devtools.depan.maven.graph.ArtifactElement;
import com.google.devtools.depan.maven.graph.MavenRelation;
import com.google.devtools.depan.maven.graph.PropertyElement;
import com.google.devtools.depan.maven.integration.MavenDefinitions;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.RelationSetDescriptor;
import com.google.devtools.depan.model.RelationSetDescriptor.Builder;

import com.google.common.collect.Lists;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import java.awt.Color;
import java.util.Collection;
import java.util.Comparator;

/**
 * DepAn Plug-in that understand Maven POM definition files.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class MavenPlugin extends AbstractSourcePlugin {

  /**
   * List of Analysis wizards to include in the DepAn perspective.
   */
  private static final Collection<String> ANALYSIS_WIZARD_IDS;

  /**
   * Collection of classes of element types.
   */
  protected static final Collection<Class<? extends Element>> classes;

  /**
   * Collection of built-in relation types.
   */
  protected static final Collection<Relation> relations;

  /**
   * Collection of built-in relationship sets for a file system.
   */
  protected static final Collection<RelationSetDescriptor> builtinSets =
      Lists.newArrayList();

  /**
   * Default built-in relationship set. It represents any container relations
   * between elements of this File System Plug-in.
   */
  protected static final RelationSetDescriptor MVN_CONTAINER;

  private static final RelationSetDescriptor MODULE_MEMBER;

  private static final RelationSetDescriptor PARENT_MEMBER;

  private static final RelationSetDescriptor PROPERTY_MEMBER;

  private static final RelationSetDescriptor DEPENDENT_MODULES;

  static {
    ANALYSIS_WIZARD_IDS = Lists.newArrayList();
    ANALYSIS_WIZARD_IDS.add(NewMavenPomWizard.ANALYSIS_WIZARD_ID);

    classes = Lists.newArrayList();
    classes.add(ArtifactElement.class);
    classes.add(PropertyElement.class);

    Builder dependentBuilder = RelationSetDescriptor.createBuilder(
        "Dependent Modules");
    dependentBuilder.addRelation(MavenRelation.COMPILE_SCOPE);
    dependentBuilder.addRelation(MavenRelation.IMPORT_SCOPE);
    dependentBuilder.addRelation(MavenRelation.PROVIDED_SCOPE);
    dependentBuilder.addRelation(MavenRelation.RUNTIME_SCOPE);
    dependentBuilder.addRelation(MavenRelation.SYSTEM_SCOPE);
    dependentBuilder.addRelation(MavenRelation.TEST_SCOPE);
    DEPENDENT_MODULES = dependentBuilder.build();
    builtinSets.add(DEPENDENT_MODULES);

    Builder moduleBuilder = RelationSetDescriptor.createBuilder(
        "Maven Modules");
    moduleBuilder.addRelation(MavenRelation.MODULE_DEPEND);
    MODULE_MEMBER = moduleBuilder.build();
    builtinSets.add(MODULE_MEMBER);

    Builder parentBuilder = RelationSetDescriptor.createBuilder(
        "Parent Modules");
    parentBuilder.addRelation(MavenRelation.PARENT_DEPEND);
    PARENT_MEMBER = parentBuilder.build();
    builtinSets.add(PARENT_MEMBER);

    Builder propertyBuilder = RelationSetDescriptor.createBuilder(
        "Property Elements");
    propertyBuilder.addRelation(MavenRelation.PROPERTY_DEPEND);
    PROPERTY_MEMBER = propertyBuilder.build();
    builtinSets.add(PROPERTY_MEMBER);

    Builder containerBuilder = RelationSetDescriptor.createBuilder(
        "All Maven");
    relations = Lists.newArrayList(); 
    for (Relation r : MavenRelation.values()) {
      relations.add(r);
      containerBuilder.addRelation(r);
    }

    MVN_CONTAINER = containerBuilder.build();
    builtinSets.add(MVN_CONTAINER);
  }

  /**
   * Returns the collection of classes of element types in File System Plug-in.
   *
   * @return Collection of classes of element types.
   */
  public static Collection<? extends Class<? extends Element>>
      getClassesStatic() {
    return classes;
  }

  /**
   * Returns the collection of built-in relation types of File System Plug-in.
   *
   * @return Collection of built-in relation types.
   */
  public static Collection<? extends Relation> getRelationsStatic() {
    return relations;
  }

  public MavenPlugin() {
    setupEdgeMatchers();
  }

  @Override
  public Collection<Class<? extends Element>> getElementClasses() {
    return classes;
  }

  @Override
  public Collection<? extends Relation> getRelations() {
    return relations;
  }

  @Override
  public Collection<? extends RelationSetDescriptor> getBuiltinRelationshipSets() {
    return builtinSets;
  }

  @Override
  public RelationSetDescriptor getDefaultRelationSetDescriptor() {
    return MVN_CONTAINER;
  }

  @Override
  public ElementTransformer<Color> getElementColorProvider() {
    return MavenNodePainter.getInstance();
  }

  @Override
  public ElementClassTransformer<Class<? extends ElementEditor>>
      getElementEditorProvider() {
    return MavenElementEditors.getInstance();
  }

  @Override
  public ElementTransformer<ImageDescriptor>
      getElementImageDescriptorProvider() {
    return MavenIconTransformer.getInstance();
  }

  @Override
  public ElementTransformer<Image> getElementImageProvider() {
    return MavenImageTransformer.getInstance();
  }

  @Override
  public ElementTransformer<GLEntity> getElementShapeProvider() {
    return MavenShapeTransformer.getInstance();
  }

  @Override
  public Comparator<Element> getElementSorter() {
    return MavenNodeComparator.getInstance();
  }

  @Override
  public Config getXMLConfig() {
    return MavenDefinitions.getInstance();
  }

  @Override
  public Collection<String> getNewAnalysisIds() {
    return ANALYSIS_WIZARD_IDS;
  }
}
