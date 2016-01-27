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

package com.google.devtools.depan.maven.eclipse;

import com.google.devtools.depan.maven.graph.ArtifactElement;
import com.google.devtools.depan.maven.graph.MavenElement;
import com.google.devtools.depan.maven.graph.PropertyElement;
import com.google.devtools.depan.model.Element;

import java.util.Comparator;

/**
 * Responsible for providing a <code>Comparator</code> for
 * {@link MavenElement}s.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class MavenNodeComparator implements Comparator<Element> {

  public static final int CATEGORY_ARTIFACT = 1000;
  public static final int CATEGORY_PROPERTY = 1010;

  /**
   * Returns the lowest category value for all elements in Maven Plug-in.
   * This system must be replaced by a smarter system. Two plug-ins that use
   * overlapping constants would cause problems.
   *
   * @return The lowest value of category constants.
   */
  public static int getLowestCategory() {
    return CATEGORY_ARTIFACT;
  }

  /**
   * An instance of this class used by other classes.
   */
  private static final MavenNodeComparator INSTANCE =
      new MavenNodeComparator();

  /**
   * Returns the singleton instance of this class.
   *
   * @return The singleton instance of this class.
   */
  public static MavenNodeComparator getInstance() {
    return INSTANCE;
  }

  private MavenNodeComparator() {
    // prevent instantiation by others
  }

  /**
   * Returns the category of the given {@link MavenElement}.
   *
   * @param element Element whose category is requested.
   * @return The category of the given {@link MavenElement}.
   */
  public int category(MavenElement element) {
    final MavenElementDispatcher<Integer> fsDispatcher =
        new MavenElementDispatcher<Integer>() {
      @Override
      public Integer match(ArtifactElement e) {
        return CATEGORY_ARTIFACT;
      }
      @Override
      public Integer match(PropertyElement e) {
        return CATEGORY_ARTIFACT;
      }
    };
    return fsDispatcher.match(element);
  }

  /**
   * Compares two {@link MavenElement} objects.
   *
   * @param element1 The first element to compare.
   * @param element2 The second element to compare.
   * @return <code>1</code> if element1 is a {@link MavenElement} and element2 is
   * a {@link DirectoryElement}; <code>-1</code> if the opposite. If both
   * elements are of the same type, returns the String comparison of their
   * names.
   */
  public int compare(MavenElement element1, MavenElement element2) {
    int category1 = category(element1);
    int category2 = category(element2);
    if (category1 != category2) {
      return category1 - category2;
    }
    return element1.friendlyString().compareTo(element2.friendlyString());
  }

  /**
   * Compares one {@link MavenElement} object and one {@link Element}
   * object.
   *
   * @param element1 The first element to compare which must be a
   * <code>MavenElement</code>.
   * @param element2 The second element to compare.
   * @return <code>1</code> if element2 is not a <code>MavenElement</code>.
   * Otherwise, returns the result of comparing two
   * <code>MavenElement</code>s.
   */
  public int compare(MavenElement element1, Element element2) {
    if (element2 instanceof MavenElement) {
      return compare(element1, (MavenElement) element2);
    }
    return 1;
  }

  /**
   * Compares two {@link Element} objects.
   *
   * @param element1 The first element to compare.
   * @param element2 The second element to compare.
   * @return If both elements are <code>MavenElement</code>s, returns the
   * result of comparing two <code>MavenElement</code>s.
   * <p>
   * Returns <code>1</code> if only the first element is a
   * <code>MavenElement</code>.
   * <p>
   * Returns <code>-1</code> if only the second element is a
   * <code>MavenElement</code>.
   * <p>
   * If none of the elements
   * are <code>MavenElement</code>s, returns the subtraction of hash code
   * of the second element from the first element.
   */
  @Override
  public int compare(Element element1, Element element2) {
    if (element1 instanceof MavenElement) {
      return compare((MavenElement) element1, element2);
    } else if (element2 instanceof MavenElement) {
      return (-1) * compare((MavenElement) element2, element1);
    }
    return element1.hashCode() - element2.hashCode();
  }
}
