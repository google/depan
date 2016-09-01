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

package com.google.devtools.depan.maven;

import com.google.devtools.depan.eclipse.ui.nodes.plugins.NodeElementPlugin;
import com.google.devtools.depan.maven.eclipse.MavenCategoryTransformer;
import com.google.devtools.depan.maven.eclipse.MavenIconTransformer;
import com.google.devtools.depan.maven.eclipse.MavenImageTransformer;
import com.google.devtools.depan.maven.eclipse.MavenNodeComparator;
import com.google.devtools.depan.maven.eclipse.MavenNodePainter;
import com.google.devtools.depan.maven.graph.MavenElements;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import java.awt.Color;
import java.util.Collection;
import java.util.Comparator;

/**
 * DepAn Plug-in that models Maven.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public class MavenNodePlugin implements NodeElementPlugin {

  @Override
  public Collection<Class<? extends Element>> getElementClasses() {
    return MavenElements.NODES;
  }

  @Override
  public ElementTransformer<Color> getElementColorProvider() {
    return MavenNodePainter.getInstance();
  }

  @Override
  public ElementTransformer<ImageDescriptor>
      getElementImageDescriptorProvider() {
    return MavenIconTransformer.getInstance();
  }

  @Override
  public ElementTransformer<Image> getElementImageProvider() {
    return MavenImageTransformer.getInstance();
  }

  @Override
  public Comparator<Element> getElementSorter() {
    return MavenNodeComparator.getInstance();
  }

  @Override
  public ElementTransformer<Integer> getElementCategoryProvider() {
    return MavenCategoryTransformer.getInstance();
  }
}
