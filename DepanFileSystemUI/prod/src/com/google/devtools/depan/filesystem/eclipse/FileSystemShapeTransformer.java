/*
 * Copyright 2008 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.devtools.depan.filesystem.eclipse;

import com.google.devtools.depan.eclipse.visualization.ogl.GLEntity;
import com.google.devtools.depan.eclipse.visualization.ogl.ShapeFactory;
import com.google.devtools.depan.filesystem.graph.DirectoryElement;
import com.google.devtools.depan.filesystem.graph.FileElement;
import com.google.devtools.depan.filesystem.graph.FileSystemElementDispatcher;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;

/**
 * Provides the shapes that are drawn on the canvas.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public class FileSystemShapeTransformer
    extends FileSystemElementDispatcher<GLEntity>
    implements ElementTransformer<GLEntity> {
  /**
   * An instance of this class used by other classes.
   */
  private static final FileSystemShapeTransformer INSTANCE =
      new FileSystemShapeTransformer();

  /**
   * Returns the singleton instance of this class.
   *
   * @return The singleton instance of this class.
   */
  public static FileSystemShapeTransformer getInstance() {
    return INSTANCE;
  }

  private FileSystemShapeTransformer() {
    // prevent instantiation by others
  }

  /**
   * Returns the {@link GLEntity} (shape) for the given element.
   *
   * @param element The element whose associated shape is requested.
   * @return {@link GLEntity} object associated with {@link FileElement}s.
   */
  @Override
  public GLEntity match(FileElement element) {
    return ShapeFactory.createRegularPolygon(3);
  }

  /**
   * Returns the {@link GLEntity} (shape) for the given element.
   *
   * @param element The element whose associated shape is requested.
   * @return {@link GLEntity} object associated with {@link DirectoryElement}s.
   */
  @Override
  public GLEntity match(DirectoryElement element) {
    return ShapeFactory.createRoundedRectangle();
  }

  /**
   * Returns the {@link GLEntity} (shape) for the given element.
   *
   * @param element The element whose associated shape is requested.
   * @return {@link GLEntity} object associated with given {@link Element}.
   */
  @Override
  public GLEntity transform(Element element) {
    return match(element);
  }
}
