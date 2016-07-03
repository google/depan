/*
 * Copyright 2007 The Depan Project Authors
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

import com.google.devtools.depan.eclipse.visualization.ogl.GLEntity;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.java.eclipse.JavaNodeComparator;
import com.google.devtools.depan.java.eclipse.JavaIconTransformer;
import com.google.devtools.depan.java.eclipse.JavaImageTransformer;
import com.google.devtools.depan.java.eclipse.JavaShapeTransformer;
import com.google.devtools.depan.java.eclipse.JavaNodePainter;
import com.google.devtools.depan.java.editors.ElementEditors;
import com.google.devtools.depan.java.graph.FieldElement;
import com.google.devtools.depan.java.graph.InterfaceElement;
import com.google.devtools.depan.java.graph.MethodElement;
import com.google.devtools.depan.java.graph.PackageElement;
import com.google.devtools.depan.java.graph.TypeElement;
import com.google.devtools.depan.java.graph.JavaRelation;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;
import com.google.devtools.depan.persistence.XStreamConfig;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.remap_doc.plugins.ElementEditor;
import com.google.devtools.depan.view_doc.eclipse.ui.plugins.ElementClassTransformer;

import com.google.common.collect.Lists;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import java.awt.Color;
import java.util.Collection;
import java.util.Comparator;

public class JavaPlugin { // extends AbstractSourcePlugin {

  final static Collection<Class<? extends Element>> classes;
  final static Collection<Relation> relations;
  final static JavaNodePainter nodeColors;

  private static final Collection<String> BUILD_ANALYSIS_WIZARD_IDS;

  // This should be contributed by the ByteCode plugin, but it does not
  // directly contribute a SourcePlugin.  Fixing this requires adding
  // a separate extension site for analysis wizards.
  private static final String BYTECODE_ANALYSIS_WIZARD_ID =
      "com.google.devtools.depan.java.bytecode.eclipse.NewGraphWizard";

  static {
    BUILD_ANALYSIS_WIZARD_IDS = Lists.newArrayList();
    BUILD_ANALYSIS_WIZARD_IDS.add(BYTECODE_ANALYSIS_WIZARD_ID);
  }

  static {
    classes = Lists.newArrayList();
    classes.add(FieldElement.class);
    classes.add(InterfaceElement.class);
    classes.add(MethodElement.class);
    classes.add(PackageElement.class);
    classes.add(TypeElement.class);

    relations = Lists.newArrayList();
    for (Relation r : JavaRelation.values()) {
      relations.add(r);
    }

    nodeColors = new JavaNodePainter();
  }

  public JavaPlugin() {
    // setupEdgeMatchers();
  }

  // @Override
  public Collection<Class<? extends Element>> getElementClasses() {
    return classes;
  }

  // @Override
  public Collection<? extends Relation> getRelations() {
    return relations;
  }

  /// @Override
  public Collection<? extends RelationSetDescriptor> getBuiltinRelationshipSets() {
    return JavaRelationSets.builtins;
  }

  // @Override
  public RelationSetDescriptor getDefaultRelationSetDescriptor() {
    return JavaRelationSets.PKG_MEMBER;
  }

  // @Override
  public ElementTransformer<Color> getElementColorProvider() {
    return JavaNodePainter.getInstance();
  }

  // @Override
  public ElementClassTransformer<Class<? extends ElementEditor>>
      getElementEditorProvider() {
    return ElementEditors.getInstance();
  }

  // @Override
  public ElementTransformer<ImageDescriptor>
      getElementImageDescriptorProvider() {
    return JavaIconTransformer.getInstance();
  }

  // @Override
  public ElementTransformer<Image> getElementImageProvider() {
    return JavaImageTransformer.getInstance();
  }

  // @Override
  public ElementTransformer<GLEntity> getElementShapeProvider() {
    return JavaShapeTransformer.getInstance();
  }

  // @Override
  public Comparator<Element> getElementSorter() {
    return JavaNodeComparator.INSTANCE;
  }

  // @Override
  public XStreamConfig getXMLConfig() {
    return null; // JavaDefinitions.configXmlPersist;
  }

  /**
   * Java class file analysis is actually provided by the Bytecode
   * source plugin.
   */
  // @Override
  public Collection<String> getNewAnalysisIds() {
    return BUILD_ANALYSIS_WIZARD_IDS;
  }
}
