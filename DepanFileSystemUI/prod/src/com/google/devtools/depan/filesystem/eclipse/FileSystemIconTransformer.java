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

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Responsible for providing the correct {@link ImageDescriptor}.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public class FileSystemIconTransformer
    extends FileSystemElementDispatcher<ImageDescriptor>
    implements ElementTransformer<ImageDescriptor> {
  /**
   * An instance of this class used by other classes.
   */
  private static final FileSystemIconTransformer INSTANCE =
      new FileSystemIconTransformer();

  /**
   * Returns the singleton instance of this class.
   *
   * @return The singleton  instance of this class.
   */
  public static FileSystemIconTransformer getInstance() {
    return INSTANCE;
  }

  private FileSystemIconTransformer() {
    // prevent instantiation by others
  }

  /**
   * Returns the {@link ImageDescriptor} for the given element.
   *
   * @param element The element whose associated {@link ImageDescriptor} is
   * requested.
   * @return {@link ImageDescriptor} associated with {@link FileElement}s.
   */
  @Override
  public ImageDescriptor match(FileElement element) {
    return FileSystemActivator.IMAGE_DESC_FILE;
  }

  /**
   * Returns the {@link ImageDescriptor} for the given element.
   *
   * @param element The element whose associated {@link ImageDescriptor} is
   * requested.
   * @return {@link ImageDescriptor} associated with {@link DirectoryElement}s.
   */
  @Override
  public ImageDescriptor match(DirectoryElement element) {
    return FileSystemActivator.IMAGE_DESC_DIRECTORY;
  }

  /**
   * Returns the {@link ImageDescriptor} for the given element.
   *
   * @param element The element whose associated {@link ImageDescriptor} is
   * requested.
   * @return {@link ImageDescriptor} associated with the given element.
   */
  @Override
  public ImageDescriptor transform(Element element) {
    return match(element);
  }
}
