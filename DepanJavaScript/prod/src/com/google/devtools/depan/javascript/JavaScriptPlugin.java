/*
 * Copyright 2009 Google Inc.
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

import com.google.devtools.depan.eclipse.plugins.ElementClassTransformer;
import com.google.devtools.depan.eclipse.plugins.ElementTransformer;
import com.google.devtools.depan.eclipse.plugins.SourcePlugin;
import com.google.devtools.depan.eclipse.utils.ElementEditor;
import com.google.devtools.depan.eclipse.visualization.ogl.GLEntity;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.javascript.eclipse.IconTransformer;
import com.google.devtools.depan.javascript.eclipse.ImageTransformer;
import com.google.devtools.depan.javascript.eclipse.NodeComparator;
import com.google.devtools.depan.javascript.eclipse.NodePainter;
import com.google.devtools.depan.javascript.eclipse.ShapeTransformer;
import com.google.devtools.depan.javascript.editors.ElementEditors;
import com.google.devtools.depan.javascript.integration.JavaScriptDefinitions;
import com.google.devtools.depan.javascript.integration.JavaScriptRelationSets;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.RelationshipSet;
import com.google.devtools.depan.model.XmlPersistentObject.Config;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * Describe of the kind of graph nodes and relations that are provided by the
 * JavaScript plug-in.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class JavaScriptPlugin implements SourcePlugin {

  private static final NodePainter NODE_PAINTER =
      new NodePainter();
  private static final ElementEditors EDITOR_TRANSFORMER =
      new ElementEditors();
  private static final IconTransformer ICON_TRANSFORMER =
      new IconTransformer();
  private static final ImageTransformer IMAGE_TRANSFORMER =
      new ImageTransformer();
  private static final ShapeTransformer SHAPE_TRANSFORMER =
      new ShapeTransformer();
  private static final NodeComparator NODE_COMPARATOR =
      new NodeComparator();

  @Override
  public Collection<? extends RelationshipSet> getBuiltinRelationshipSets() {
    return JavaScriptRelationSets.getBuiltinSets();
  }

  @Override
  public RelationshipSet getDefaultRelationshipSet() {
    return JavaScriptRelationSets.getDefaultRelationshipSet();
  }

  @Override
  public Collection<Class<? extends Element>> getElementClasses() {
    return JavaScriptDefinitions.getClasses();
  }

  @Override
  public Collection<Relation> getRelations() {
    return JavaScriptDefinitions.getRelations();
  }

  @Override
  public Config getXMLConfig() {
    return JavaScriptDefinitions.getXMLConfig();
  }

  @Override
  public ElementTransformer<Color> getElementColorProvider() {
    return NODE_PAINTER;
  }

  @Override
  public ElementClassTransformer<Class<? extends ElementEditor>>
      getElementEditorProvider() {
    return EDITOR_TRANSFORMER;
  }

  @Override
  public ElementTransformer<ImageDescriptor>
      getElementImageDescriptorProvider() {
    return ICON_TRANSFORMER;
  }

  @Override
  public ElementTransformer<Image> getElementImageProvider() {
    return IMAGE_TRANSFORMER;
  }

  @Override
  public ElementTransformer<GLEntity> getElementShapeProvider() {
    return SHAPE_TRANSFORMER;
  }

  @Override
  public Comparator<Element> getElementSorter() {
    return NODE_COMPARATOR;
  }

  @Override
  public Collection<String> getNewAnalysisIds() {
    // JavaScript dependency analysis supplied in a separate plug-in.
    return Collections.emptyList();
  }
}
