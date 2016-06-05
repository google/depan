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

package com.google.devtools.depan.filesystem;

import com.google.devtools.depan.eclipse.ui.nodes.plugins.NodeElementPlugin;
import com.google.devtools.depan.filesystem.eclipse.FileSystemCategoryTransformer;
import com.google.devtools.depan.filesystem.eclipse.FileSystemIconTransformer;
import com.google.devtools.depan.filesystem.eclipse.FileSystemImageTransformer;
import com.google.devtools.depan.filesystem.eclipse.FileSystemNodeComparator;
import com.google.devtools.depan.filesystem.eclipse.FileSystemNodePainter;
import com.google.devtools.depan.filesystem.graph.FileSystemElements;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import java.awt.Color;
import java.util.Collection;
import java.util.Comparator;

/**
 * DepAn Plug-in that models the File System. It is intended to be included from
 * other plug-ins that use a file system; however, it is possible to use this
 * plug-in in DepAn as is.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public class FileSystemNodePlugin implements NodeElementPlugin {

  @Override
  public Collection<Class<? extends Element>> getElementClasses() {
    return FileSystemElements.NODES;
  }

  @Override
  public ElementTransformer<Color> getElementColorProvider() {
    return FileSystemNodePainter.getInstance();
  }

  @Override
  public ElementTransformer<ImageDescriptor>
      getElementImageDescriptorProvider() {
    return FileSystemIconTransformer.getInstance();
  }

  @Override
  public ElementTransformer<Image> getElementImageProvider() {
    return FileSystemImageTransformer.getInstance();
  }

  @Override
  public Comparator<Element> getElementSorter() {
    return FileSystemNodeComparator.getInstance();
  }

  @Override
  public ElementTransformer<Integer> getElementCategoryProvider() {
    return FileSystemCategoryTransformer.getInstance();
  }
}
