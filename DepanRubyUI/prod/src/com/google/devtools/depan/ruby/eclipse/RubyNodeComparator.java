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

package com.google.devtools.depan.ruby.eclipse;

import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.ruby.graph.RubyElement;

import java.util.Comparator;

/**
 * Responsible for providing a <code>Comparator</code> for
 * {@link RubyElement}s.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class RubyNodeComparator implements Comparator<Element> {

  /**
   * An instance of this class used by other classes.
   */
  private static final RubyNodeComparator INSTANCE =
      new RubyNodeComparator();

  private RubyNodeComparator() {
    // prevent instantiation by others
  }

  /**
   * Returns the singleton instance of this class.
   */
  public static RubyNodeComparator getInstance() {
    return INSTANCE;
  }

  /**
   * Compares two {@link Element} objects.
   *
   * @param element1 The first element to compare.
   * @param element2 The second element to compare.
   * @return If both elements are <code>RubyElement</code>s, returns the
   * result of comparing two <code>RubyElement</code>s.
   * <p>
   * Returns <code>1</code> if only the first element is a
   * <code>RubyElement</code>.
   * <p>
   * Returns <code>-1</code> if only the second element is a
   * <code>RubyElement</code>.
   * <p>
   * If none of the elements
   * are <code>RubyElement</code>s, returns the subtraction of hash code
   * of the second element from the first element.
   */
  @Override
  public int compare(Element element1, Element element2) {
    if (isRubyElement(element1)) {
      return compare((RubyElement) element1, element2);
    } else if (isRubyElement(element2)) {
      return (-1) * compare((RubyElement) element2, element1);
    }
    return element1.hashCode() - element2.hashCode();
  }

  /**
   * Compares two {@link RubyElement} objects.
   *
   * @param element1 The first element to compare.
   * @param element2 The second element to compare.
   * @return <code>1</code> if element1 is a {@link RubyElement} and element2 is
   * a {@link DirectoryElement}; <code>-1</code> if the opposite. If both
   * elements are of the same type, returns the String comparison of their
   * names.
   */
  private int compare(RubyElement element1, RubyElement element2) {
    int category1 = getCategory(element1);
    int category2 = getCategory(element2);
    if (category1 != category2) {
      return category1 - category2;
    }
    return element1.friendlyString().compareTo(element2.friendlyString());
  }

  /**
   * Compares one {@link RubyElement} object and one {@link Element}
   * object.
   *
   * @param element1 The first element to compare which must be a
   * <code>RubyElement</code>.
   * @param element2 The second element to compare.
   * @return <code>1</code> if element2 is not a <code>RubyElement</code>.
   * Otherwise, returns the result of comparing two
   * <code>RubyElement</code>s.
   */
  private int compare(RubyElement element1, Element element2) {
    if (isRubyElement(element2)) {
      return compare(element1, (RubyElement) element2);
    }
    return 1;
  }

  private static boolean isRubyElement(Object element) {
    return (element instanceof RubyElement);
  }

  private int getCategory(RubyElement node) {
    return RubyCategoryTransformer.getCategory(node);
  }
}
