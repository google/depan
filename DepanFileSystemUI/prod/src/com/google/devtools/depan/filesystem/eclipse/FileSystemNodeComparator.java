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
import com.google.devtools.depan.filesystem.graph.FileSystemElement;
import com.google.devtools.depan.filesystem.graph.FileSystemElementDispatcher;
import com.google.devtools.depan.model.Element;

import java.util.Comparator;

/**
 * Responsible for providing a <code>Comparator</code> for
 * {@link FileSystemElement}s.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public class FileSystemNodeComparator implements Comparator<Element> {
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
  private static final FileSystemNodeComparator INSTANCE =
      new FileSystemNodeComparator();

  /**
   * Returns the singleton instance of this class.
   *
   * @return The singleton instance of this class.
   */
  public static FileSystemNodeComparator getInstance() {
    return INSTANCE;
  }

  private FileSystemNodeComparator() {
    // prevent instantiation by others
  }

  /**
   * Returns the category of the given {@link FileSystemElement}.
   *
   * @param element Element whose category is requested.
   * @return The category of the given {@link FileSystemElement}.
   */
  public int category(FileSystemElement element) {
    final FileSystemElementDispatcher<Integer> fsDispatcher =
        new FileSystemElementDispatcher<Integer>() {
      @Override
      public Integer match(FileElement e) {
        return CATEGORY_FILE;
      }
      @Override
      public Integer match(DirectoryElement e) {
        return CATEGORY_DIRECTORY;
      }
    };
    return fsDispatcher.match(element);
  }

  /**
   * Compares two {@link FileSystemElement} objects.
   *
   * @param element1 The first element to compare.
   * @param element2 The second element to compare.
   * @return <code>1</code> if element1 is a {@link FileElement} and element2 is
   * a {@link DirectoryElement}; <code>-1</code> if the opposite. If both
   * elements are of the same type, returns the String comparison of their
   * names.
   */
  public int compare(FileSystemElement element1, FileSystemElement element2) {
    int category1 = category(element1);
    int category2 = category(element2);
    if (category1 != category2) {
      return category1 - category2;
    }
    return element1.friendlyString().compareTo(element2.friendlyString());
  }

  /**
   * Compares one {@link FileSystemElement} object and one {@link Element}
   * object.
   *
   * @param element1 The first element to compare which must be a
   * <code>FileSystemElement</code>.
   * @param element2 The second element to compare.
   * @return <code>1</code> if element2 is not a <code>FileSystemElement</code>.
   * Otherwise, returns the result of comparing two
   * <code>FileSystemElement</code>s.
   */
  public int compare(FileSystemElement element1, Element element2) {
    if (element2 instanceof FileSystemElement) {
      return compare(element1, (FileSystemElement) element2);
    }
    return 1;
  }

  /**
   * Compares two {@link Element} objects.
   *
   * @param element1 The first element to compare.
   * @param element2 The second element to compare.
   * @return If both elements are <code>FileSystemElement</code>s, returns the
   * result of comparing two <code>FileSystemElement</code>s.
   * <p>
   * Returns <code>1</code> if only the first element is a
   * <code>FileSystemElement</code>.
   * <p>
   * Returns <code>-1</code> if only the second element is a
   * <code>FileSystemElement</code>.
   * <p>
   * If none of the elements
   * are <code>FileSystemElement</code>s, returns the substraction of hash code
   * of the second element from the first element.
   */
  @Override
  public int compare(Element element1, Element element2) {
    if (element1 instanceof FileSystemElement) {
      return compare((FileSystemElement) element1, element2);
    } else if (element2 instanceof FileSystemElement) {
      return (-1) * compare((FileSystemElement) element2, element1);
    }
    return element1.hashCode() - element2.hashCode();
  }
}
