/*
 * Copyright 2008 Yohann R. Coppel
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

package com.google.devtools.depan.eclipse.plugins;

import com.google.devtools.depan.eclipse.utils.ElementEditor;
import com.google.devtools.depan.eclipse.visualization.ogl.GLEntity;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.RelationshipSet;
import com.google.devtools.depan.model.XmlPersistentObject.Config;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import java.awt.Color;
import java.util.Collection;
import java.util.Comparator;

/**
 * Interface implemented by every plugin providing dependency sources.
 * It provides a list of the possible elements and relations, as well as
 * transformers for visualization.
 *
 * @author Yohann Coppel
 *
 */
public interface SourcePlugin {

  /**
   * Return a collection of singletons representing different types of
   * relationships.
   *
   * @return a list of {@link Relation}s provided by this plugin.
   */
  Collection<? extends Relation> getRelations();

  /**
   * @return a collection of Classes extending Element provided by this plugin.
   */
  Collection<Class<? extends Element>> getElementClasses();

  /**
   * @return a list of RelationshipSets.
   */
  Collection<? extends RelationshipSet> getBuiltinRelationshipSets();

  /**
   * @return a RelationshipSet that should be considered by the default one for
   * this plugin.
   */
  RelationshipSet getDefaultRelationshipSet();

  /**
   * @return a Config object to setup the XML deserializer.
   */
  Config getXMLConfig();

  /**
   * @return Collection of NewWizard Ids to add to as new wizard shortcuts.
   */
  Collection<String> getNewAnalysisIds();

  /**
   * @return an {@link ElementTransformer} providing {@link Image}s for each
   * type of {@link Element} this plugin handle.
   */
  ElementTransformer<Image> getElementImageProvider();

  /**
   * @return an {@link ElementTransformer} providing an {@link ImageDescriptor}
   * for each type of {@link Element} this plugin handle.
   */
  ElementTransformer<ImageDescriptor> getElementImageDescriptorProvider();

  /**
   * @return an {@link ElementTransformer} providing {@link Color}s for each
   * type of {@link Element} this plugin handle.
   */
  ElementTransformer<Color> getElementColorProvider();

  /**
   * @return an {@link ElementTransformer} providing {@link GLEntity} for each
   * type of {@link Element} this plugin handle.
   */
  ElementTransformer<GLEntity> getElementShapeProvider();

  /**
   * @return an {@link ElementTransformer} providing an {@link ElementEditor}
   * class for each type of {@link Element} this plugin handle.
   */
  ElementClassTransformer<Class<? extends ElementEditor>> getElementEditorProvider();

  /**
   * @return a comparator to compare two {@link Element}s this plugin provide.
   */
  Comparator<Element> getElementSorter();

}
