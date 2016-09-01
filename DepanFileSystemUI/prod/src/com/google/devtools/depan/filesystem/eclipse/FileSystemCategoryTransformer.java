/*
 * Copyright 2016 The Depan Project Authors
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
import com.google.devtools.depan.filesystem.graph.FileSystemElement;
import com.google.devtools.depan.filesystem.graph.FileSystemElementDispatcher;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;

import org.eclipse.swt.graphics.Image;

/**
 * Responsible for providing the correct {@link Image}.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public class FileSystemCategoryTransformer
    extends FileSystemElementDispatcher<Integer>
    implements ElementTransformer<Integer> {

  /**
   * The category value for {@link DirectoryElement} objects. It is an integer
   * constant to support easy comparison.
   */
  public static final int CATEGORY_DIRECTORY = 1000;

  /**
   * The category value for {@link FileElement} objects. It is an integer
   * constant to support easy comparison.
   */
  public static final int CATEGORY_FILE = 1001;

  /**
   * Returns the lowest category value for all elements in File System Plug-in.
   * This system must be replaced by a smarter system. Two plug-ins that use
   * overlapping constants would cause problems.
   *
   * @return The lowest value of category constants.
   */
  public static int getLowestCategory() {
    return CATEGORY_DIRECTORY;
  }

  /**
   * An instance of this class used by other classes.
   */
  private static final FileSystemCategoryTransformer INSTANCE =
      new FileSystemCategoryTransformer();

  /**
   * Returns the singleton instance of this class.
   *
   * @return The singleton instance of this class.
   */
  public static FileSystemCategoryTransformer getInstance() {
    return INSTANCE;
  }

  /**
   * Provides the category for the supplied {@link #element}.
   */
  public static int getCategory(FileSystemElement element) {
    return getInstance().match(element);
  }

  private FileSystemCategoryTransformer() {
    // prevent instantiation by others
  }

  /**
   * Returns the {@link Image} for the given element.
   *
   * @param element The element whose associated {@link Image} is requested.
   * @return {@link Image} associated with {@link FileElement}s.
   */
  @Override
  public Integer match(FileElement element) {
    return CATEGORY_FILE;
  }

  /**
   * Returns the {@link Image} for the given element.
   *
   * @param element The element whose associated {@link Image} is requested.
   * @return {@link Image} associated with {@link DirectoryElement}s.
   */
  @Override
  public Integer match(DirectoryElement element) {
    return CATEGORY_DIRECTORY;
  }

  /**
   * Returns the {@link Image} for the given element.
   *
   * @param element The element whose associated {@link Image} is requested.
   * @return {@link Image} associated with given {@link Element}.
   */
  @Override
  public Integer transform(Element element) {
    return match(element);
  }
}
