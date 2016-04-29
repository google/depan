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

import com.google.devtools.depan.eclipse.persist.XStreamFactory.Config;
import com.google.devtools.depan.eclipse.plugins.AbstractSourcePlugin;
import com.google.devtools.depan.eclipse.plugins.ElementClassTransformer;
import com.google.devtools.depan.eclipse.utils.ElementEditor;
import com.google.devtools.depan.eclipse.visualization.ogl.GLEntity;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;
import com.google.devtools.depan.relations.RelationSetDescriptor;
import com.google.devtools.depan.relations.RelationSetDescriptor.Builder;
import com.google.devtools.depan.ruby.eclipse.RubyIconTransformer;
import com.google.devtools.depan.ruby.eclipse.RubyImageTransformer;
import com.google.devtools.depan.ruby.eclipse.RubyNodeComparator;
import com.google.devtools.depan.ruby.eclipse.RubyNodePainter;
import com.google.devtools.depan.ruby.eclipse.RubyShapeTransformer;
import com.google.devtools.depan.ruby.editors.RubyElementEditors;
import com.google.devtools.depan.ruby.graph.ClassElement;
import com.google.devtools.depan.ruby.graph.ClassMethodElement;
import com.google.devtools.depan.ruby.graph.InstanceMethodElement;
import com.google.devtools.depan.ruby.graph.RubyRelation;
import com.google.devtools.depan.ruby.graph.SingletonMethodElement;
import com.google.devtools.depan.ruby.integration.RubyDefinitions;

import com.google.common.collect.Lists;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * DepAn Plug-in that understand Ruby definition files.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class RubyPlugin extends AbstractSourcePlugin {

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
  protected static final RelationSetDescriptor RUBY_CONTAINER;

  private static final RelationSetDescriptor MEMBER_ELEMENTS;

  private static final RelationSetDescriptor EXTENDS_TYPE;

  static {
    classes = Lists.newArrayList();
    classes.add(ClassElement.class);
    classes.add(ClassMethodElement.class);
    classes.add(InstanceMethodElement.class);
    classes.add(SingletonMethodElement.class);

    Builder dependentBuilder = RelationSetDescriptor.createBuilder(
        "Ruby Extends");
    dependentBuilder.addRelation(RubyRelation.EXTENDS_TYPE);
    EXTENDS_TYPE = dependentBuilder.build();
    builtinSets.add(EXTENDS_TYPE);

    Builder moduleBuilder = RelationSetDescriptor.createBuilder(
        "Ruby Members");
    moduleBuilder.addRelation(RubyRelation.CLASS_MEMBER);
    moduleBuilder.addRelation(RubyRelation.INSTANCE_MEMBER);
    moduleBuilder.addRelation(RubyRelation.SINGLETON_MEMBER);
    MEMBER_ELEMENTS = moduleBuilder.build();
    builtinSets.add(MEMBER_ELEMENTS);

    Builder containerBuilder = RelationSetDescriptor.createBuilder(
        "All Ruby");
    relations = Lists.newArrayList(); 
    for (Relation r : RubyRelation.values()) {
      relations.add(r);
      containerBuilder.addRelation(r);
    }

    RUBY_CONTAINER = containerBuilder.build();
    builtinSets.add(RUBY_CONTAINER);
  }

  /**
   * Returns the collection of classes of element types in Ruby Plug-in.
   *
   * @return Collection of classes of element types.
   */
  public static Collection<? extends Class<? extends Element>>
      getClassesStatic() {
    return classes;
  }

  /**
   * Returns the collection of built-in relation types of Ruby Plug-in.
   *
   * @return Collection of built-in relation types.
   */
  public static Collection<? extends Relation> getRelationsStatic() {
    return relations;
  }

  public RubyPlugin() {
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
    return RUBY_CONTAINER;
  }

  @Override
  public ElementTransformer<Color> getElementColorProvider() {
    return RubyNodePainter.getInstance();
  }

  @Override
  public ElementClassTransformer<Class<? extends ElementEditor>>
      getElementEditorProvider() {
    return RubyElementEditors.getInstance();
  }

  @Override
  public ElementTransformer<ImageDescriptor>
      getElementImageDescriptorProvider() {
    return RubyIconTransformer.getInstance();
  }

  @Override
  public ElementTransformer<Image> getElementImageProvider() {
    return RubyImageTransformer.getInstance();
  }

  @Override
  public ElementTransformer<GLEntity> getElementShapeProvider() {
    return RubyShapeTransformer.getInstance();
  }

  @Override
  public Comparator<Element> getElementSorter() {
    return RubyNodeComparator.getInstance();
  }

  @Override
  public Config getXMLConfig() {
    return RubyDefinitions.getInstance();
  }

  @Override
  public Collection<String> getNewAnalysisIds() {
    // Ruby dependency analyzes are generated externally.
    return Collections.emptyList();
  }
}
