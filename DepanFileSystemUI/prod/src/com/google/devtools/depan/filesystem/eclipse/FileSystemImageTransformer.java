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

import com.google.devtools.depan.filesystem.graph.DirectoryElement;
import com.google.devtools.depan.filesystem.graph.FileElement;
import com.google.devtools.depan.filesystem.graph.FileSystemElementDispatcher;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;

import org.eclipse.swt.graphics.Image;

/**
 * Responsible for providing the correct {@link Image}.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public class FileSystemImageTransformer
    extends FileSystemElementDispatcher<Image>
    implements ElementTransformer<Image> {
  /**
   * An instance of this class used by other classes.
   */
  private static final FileSystemImageTransformer INSTANCE =
      new FileSystemImageTransformer();

  /**
   * Returns the singleton instance of this class.
   *
   * @return The singleton instance of this class.
   */
  public static FileSystemImageTransformer getInstance() {
    return INSTANCE;
  }

  private FileSystemImageTransformer() {
    // prevent instantiation by others
  }

  /**
   * Returns the {@link Image} for the given element.
   *
   * @param element The element whose associated {@link Image} is requested.
   * @return {@link Image} associated with {@link FileElement}s.
   */
  @Override
  public Image match(FileElement element) {
    return FileSystemActivator.IMAGE_FILE;
  }

  /**
   * Returns the {@link Image} for the given element.
   *
   * @param element The element whose associated {@link Image} is requested.
   * @return {@link Image} associated with {@link DirectoryElement}s.
   */
  @Override
  public Image match(DirectoryElement element) {
    return FileSystemActivator.IMAGE_DIRECTORY;
  }

  /**
   * Returns the {@link Image} for the given element.
   *
   * @param element The element whose associated {@link Image} is requested.
   * @return {@link Image} associated with given {@link Element}.
   */
  @Override
  public Image transform(Element element) {
    return match(element);
  }
}
