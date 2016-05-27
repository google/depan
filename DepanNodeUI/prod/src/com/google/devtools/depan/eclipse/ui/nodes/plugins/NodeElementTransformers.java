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
import java.util.Comparator;

/**
 * Contains a list of {@link ElementTransformer} and
 * {@link ElementClassTransformer} associated to a {@link Contribution}.
 *
 * @author Yohann Coppel
 */
public class NodeElementTransformers {
  public final String pluginId;
  public final ElementTransformer<Image> image;
  public final ElementTransformer<ImageDescriptor> imageDescriptor;
  public final ElementTransformer<Color> color;
  public final ElementTransformer<Integer> category;
  public final Comparator<Element> sorter;

  public NodeElementTransformers(
      String pluginId,
      ElementTransformer<Image> image,
      ElementTransformer<ImageDescriptor> imageDescriptor,
      ElementTransformer<Color> color,
      ElementTransformer<Integer> category,
      Comparator<Element> sorter) {
    this.pluginId = pluginId;
    this.image = image;
    this.imageDescriptor = imageDescriptor;
    this.color = color;
    this.category = category;
    this.sorter = sorter;
  }
}
