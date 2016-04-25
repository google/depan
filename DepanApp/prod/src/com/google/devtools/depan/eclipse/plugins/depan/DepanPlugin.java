/*
 * Copyright 2010 The Depan Project Authors
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

package com.google.devtools.depan.eclipse.plugins.depan;

import com.google.devtools.depan.eclipse.persist.XStreamFactory.Config;
import com.google.devtools.depan.eclipse.plugins.AbstractSourcePlugin;
import com.google.devtools.depan.eclipse.plugins.ElementClassTransformer;
import com.google.devtools.depan.eclipse.utils.ElementEditor;
import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.visualization.ogl.GLEntity;
import com.google.devtools.depan.eclipse.visualization.ogl.ShapeFactory;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;
import com.google.devtools.depan.model.RelationSetDescriptor;
import com.google.devtools.depan.model.RelationSetDescriptors;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * Allow the base application to contribute analysis resources as if it
 * was a regular plugin.  This is most useful for RelSets and composite
 * metric computations.
 *
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class DepanPlugin extends AbstractSourcePlugin {

  /**
   * Configure an {@link XStream} for serializing undifferentiated DepAn nodes.
   * 
   * <p>For these nodes, no configuration is needed.
   */
  private static final Config XSTREAM_CONFIG = new Config() {
    @Override
    public void config(XStream xstream) {
    }
  };

  /**
   * Define the migration editor for undifferentiated DepAn nodes.
   * 
   * <p>This implementation has no editor for any node.
   */
  private static final ElementClassTransformer<Class<? extends ElementEditor>>
      EDITOR_XFORM = new ElementClassTransformer<Class<? extends ElementEditor>>() {

        @Override
        public Class<? extends ElementEditor> transform(
            Class<? extends Element> element) {
          return null;
        }
  };

  /**
   * Define the node comparator for undifferentiated DepAn nodes.
   * 
   * <p>This implementation says that all nodes are equal.
   */
  private static final Comparator<Element> ELEMENT_SORTER =
      new Comparator<Element>() {

        @Override
        public int compare(Element o1, Element o2) {
          return 0;
        }
  };

  /**
   * Define the color transformer for undifferentiated DepAn nodes.
   * 
   * <p>This implementation provides {@code RED} for all nodes.
   */
  private static final ElementTransformer<Color> COLOR_XFORM =
      new ElementTransformer<Color>() {

        @Override
        public Color transform(Element element) {
          return Color.RED;
        }
  };

  /**
   * Define the image descriptor transformer for undifferentiated DepAn nodes.
   * 
   * <p>This implementation provides {@code IMAGE_DESC_DEFAULT} for all nodes.
   */
  private static final ElementTransformer<ImageDescriptor> IMAGE_DESCR_XFORM =
      new ElementTransformer<ImageDescriptor>() {

        @Override
        public ImageDescriptor transform(Element element) {
          return Resources.IMAGE_DESC_DEFAULT;
        }
  };

  /**
   * Define the image transformer for undifferentiated DepAn nodes.
   * 
   * <p>This implementation provides {@code IMAGE_DEFAULT} for all nodes.
   */
  private static final ElementTransformer<Image> IMAGE_XFORM = 
      new ElementTransformer<Image>() {
  
        @Override
        public Image transform(Element element) {
          return Resources.IMAGE_DEFAULT;
        }
  };

  /**
   * Define the shap transformer for undifferentiated DepAn nodes.
   * 
   * <p>This implementation provides a triangle for all nodes.
   */
  private static final ElementTransformer<GLEntity> SHAPE_XFORM =
      new ElementTransformer<GLEntity>() {

        @Override
        public GLEntity transform(Element element) {
          return ShapeFactory.createRegularPolygon(3);
        }
  };

  /**
   * Collection of built-in relationship sets defined by this build plug-in.
   */
  private static final Collection<RelationSetDescriptor> BUILT_IN_SETS;

  static {
    BUILT_IN_SETS = Lists.newArrayList();
    BUILT_IN_SETS.add(RelationSetDescriptors.EMPTY);
  }

  public DepanPlugin() {
    setupEdgeMatchers();
  }

  /////////////////////////////////////
  // Creation and persistence properties.

  @Override
  public Collection<String> getNewAnalysisIds() {
    return Collections.emptyList();
  }

  @Override
  public Config getXMLConfig() {
    return XSTREAM_CONFIG;
  }

  /////////////////////////////////////
  // Relation and RelSet properties.

  @Override
  public Collection<? extends Relation> getRelations() {
    return Collections.emptyList();
  }

  @Override
  public Collection<? extends RelationSetDescriptor> getBuiltinRelationshipSets() {
    return BUILT_IN_SETS;
  }

  @Override
  public RelationSetDescriptor getDefaultRelationSetDescriptor() {
    return RelationSetDescriptors.EMPTY;
  }


  /////////////////////////////////////
  // Element properties.

  @Override
  public Collection<Class<? extends Element>> getElementClasses() {
    return Collections.emptyList();
  }

  @Override
  public ElementClassTransformer<Class<? extends ElementEditor>>
      getElementEditorProvider() {
    return EDITOR_XFORM;
  }

  @Override
  public Comparator<Element> getElementSorter() {
    return ELEMENT_SORTER;
  }

  /////////////////////////////////////
  // Element transformers.

  @Override
  public ElementTransformer<Color> getElementColorProvider() {
    return COLOR_XFORM;
  }

  @Override
  public ElementTransformer<ImageDescriptor>
      getElementImageDescriptorProvider() {
    return IMAGE_DESCR_XFORM;
  }

  @Override
  public ElementTransformer<Image> getElementImageProvider() {
    return IMAGE_XFORM;
  }

  @Override
  public ElementTransformer<GLEntity> getElementShapeProvider() {
    return SHAPE_XFORM;
  }
}
