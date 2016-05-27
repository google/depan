/*
 * Copyright 2008 The Depan Project Authors
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

package com.google.devtools.depan.eclipse.ui.nodes.plugins;

import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;

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
 */
public interface NodeElementPlugin {

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
   * @return a comparator to compare two {@link Element}s this plugin provide.
   */
  Comparator<Element> getElementSorter();

  /**
   * @return an {@link ElementTransformer} providing a category for each
   * type of {@link Element} this plugin handle.
   */
  ElementTransformer<Integer> getElementCategoryProvider();

  /**
   * @return Collection of element classes that should use these
   *   {@link ElementTransformer}s.
   */
  Collection<Class<? extends Element>> getElementClasses();

}
