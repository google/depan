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

import com.google.devtools.depan.eclipse.ui.nodes.plugins.NodeElementPlugin;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;
import com.google.devtools.depan.ruby.eclipse.RubyCategoryTransformer;
import com.google.devtools.depan.ruby.eclipse.RubyIconTransformer;
import com.google.devtools.depan.ruby.eclipse.RubyImageTransformer;
import com.google.devtools.depan.ruby.eclipse.RubyNodeComparator;
import com.google.devtools.depan.ruby.eclipse.RubyNodePainter;
import com.google.devtools.depan.ruby.graph.RubyElements;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import java.awt.Color;
import java.util.Collection;
import java.util.Comparator;

/**
 * Define properties for Ruby nodes.
 * 
 * Based on legacy {@code RubyPlugin}.
 *
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class RubyNodePlugin implements NodeElementPlugin {

  @Override
  public Collection<Class<? extends Element>> getElementClasses() {
    return RubyElements.NODES;
  }

  @Override
  public ElementTransformer<Color> getElementColorProvider() {
    return RubyNodePainter.getInstance();
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
  public Comparator<Element> getElementSorter() {
    return RubyNodeComparator.getInstance();
  }

  @Override
  public ElementTransformer<Integer> getElementCategoryProvider() {
    return RubyCategoryTransformer.getInstance();
  }
}
