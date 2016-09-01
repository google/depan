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

package com.google.devtools.depan.javascript;

import com.google.devtools.depan.eclipse.ui.nodes.plugins.NodeElementPlugin;
import com.google.devtools.depan.javascript.eclipse.JavaScriptCategoryTransformer;
import com.google.devtools.depan.javascript.eclipse.JavaScriptIconTransformer;
import com.google.devtools.depan.javascript.eclipse.JavaScriptImageTransformer;
import com.google.devtools.depan.javascript.eclipse.JavaScriptNodeComparator;
import com.google.devtools.depan.javascript.eclipse.JavaScriptNodePainter;
import com.google.devtools.depan.javascript.graph.JavaScriptElements;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import java.awt.Color;
import java.util.Collection;
import java.util.Comparator;

/**
 * DepAn Plug-in that models JavaScript.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public class JavaScriptNodePlugin implements NodeElementPlugin {

  @Override
  public Collection<Class<? extends Element>> getElementClasses() {
    return JavaScriptElements.NODES;
  }

  @Override
  public ElementTransformer<Color> getElementColorProvider() {
    return JavaScriptNodePainter.getInstance();
  }

  @Override
  public ElementTransformer<ImageDescriptor>
      getElementImageDescriptorProvider() {
    return JavaScriptIconTransformer.getInstance();
  }

  @Override
  public ElementTransformer<Image> getElementImageProvider() {
    return JavaScriptImageTransformer.getInstance();
  }

  @Override
  public Comparator<Element> getElementSorter() {
    return JavaScriptNodeComparator.getInstance();
  }

  @Override
  public ElementTransformer<Integer> getElementCategoryProvider() {
    return JavaScriptCategoryTransformer.getInstance();
  }
}
