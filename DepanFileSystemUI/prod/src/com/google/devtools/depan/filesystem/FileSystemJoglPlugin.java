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

import com.google.devtools.depan.eclipse.visualization.ogl.GLEntity;
import com.google.devtools.depan.filesystem.eclipse.FileSystemShapeTransformer;
import com.google.devtools.depan.filesystem.graph.FileSystemElements;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;
import com.google.devtools.depan.view_doc.eclipse.ui.plugins.JoglPlugin;

import java.util.Collection;

/**
 * DepAn Plug-in that models the File System. It is intended to be included from
 * other plug-ins that use a file system; however, it is possible to use this
 * plug-in in DepAn as is.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public class FileSystemJoglPlugin implements JoglPlugin {

  @Override
  public Collection<Class<? extends Element>> getElementClasses() {
    return FileSystemElements.NODES;
  }

  @Override
  public ElementTransformer<GLEntity> getElementShapeProvider() {
    return FileSystemShapeTransformer.getInstance();
  }
}
