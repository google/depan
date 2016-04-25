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

package com.google.devtools.depan.eclipse.plugins;

import java.awt.Color;
import java.util.Comparator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.google.devtools.depan.eclipse.utils.ElementEditor;
import com.google.devtools.depan.eclipse.visualization.ogl.GLEntity;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;

/**
 * Contains a list of {@link ElementTransformer} and
 * {@link ElementClassTransformer} associated to a {@link SourcePluginEntry}.
 *
 * @author Yohann Coppel
 *
 */
public class ElementTransformers {
  public final SourcePluginEntry plugin;
  public final ElementTransformer<Image> image;
  public final ElementTransformer<ImageDescriptor> imageDescriptor;
  public final ElementTransformer<Color> color;
  public final ElementTransformer<GLEntity> shape;
  public final ElementClassTransformer<Class<? extends ElementEditor>> editor;
  public final Comparator<Element> sorter;

  public ElementTransformers(SourcePluginEntry p,
      ElementTransformer<Image> image,
      ElementTransformer<ImageDescriptor> imageDescriptor,
      ElementTransformer<Color> color, ElementTransformer<GLEntity> shape,
      ElementClassTransformer<Class<? extends ElementEditor>> editor,
      Comparator<Element> sorter) {
    this.plugin = p;
    this.image = image;
    this.imageDescriptor = imageDescriptor;
    this.color = color;
    this.shape = shape;
    this.editor = editor;
    this.sorter = sorter;
  }
}
