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

import com.google.devtools.depan.filesystem.graph.DirectoryElement;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.ruby.graph.ClassElement;
import com.google.devtools.depan.ruby.graph.ClassMethodElement;
import com.google.devtools.depan.ruby.graph.InstanceMethodElement;
import com.google.devtools.depan.ruby.graph.RubyElement;
import com.google.devtools.depan.ruby.graph.SingletonMethodElement;

import java.util.Comparator;

/**
 * Responsible for providing a <code>Comparator</code> for
 * {@link RubyElement}s.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class RubyNodeComparator implements Comparator<Element> {

  public static final int CATEGORY_CLASS = 1000;
  public static final int CATEGORY_CLASS_METHOD = 1010;
  public static final int CATEGORY_INSTANCE_METHOD = 1020;
  public static final int CATEGORY_SINGLETON_METHOD = 1030;

  /**
   * Returns the lowest category value for all elements in Maven Plug-in.
   * This system must be replaced by a smarter system. Two plug-ins that use
   * overlapping constants would cause problems.
   *
   * @return The lowest value of category constants.
   */
  public static int getLowestCategory() {
    return CATEGORY_CLASS;
  }

  /**
   * An instance of this class used by other classes.
   */
  private static final RubyNodeComparator INSTANCE =
      new RubyNodeComparator();

  /**
   * Returns the singleton instance of this class.
   *
   * @return The singleton instance of this class.
   */
  public static RubyNodeComparator getInstance() {
    return INSTANCE;
  }

  private RubyNodeComparator() {
    // prevent instantiation by others
  }

  /**
   * Returns the category of the given {@link RubyElement}.
   *
   * @param element Element whose category is requested.
   * @return The category of the given {@link RubyElement}.
   */
  public int category(RubyElement element) {
    final RubyElementDispatcher<Integer> fsDispatcher =
        new RubyElementDispatcher<Integer>() {

          @Override
          public Integer match(ClassElement element) {
            return CATEGORY_CLASS;
          }

          @Override
          public Integer match(ClassMethodElement element) {
            return CATEGORY_CLASS_METHOD;
          }

          @Override
          public Integer match(InstanceMethodElement element) {
            return CATEGORY_INSTANCE_METHOD;
          }

          @Override
          public Integer match(SingletonMethodElement element) {
            return CATEGORY_SINGLETON_METHOD;
          }
    };
    return fsDispatcher.match(element);
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
  public int compare(RubyElement element1, RubyElement element2) {
    int category1 = category(element1);
    int category2 = category(element2);
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
   * <code>MavenElement</code>.
   * @param element2 The second element to compare.
   * @return <code>1</code> if element2 is not a <code>MavenElement</code>.
   * Otherwise, returns the result of comparing two
   * <code>MavenElement</code>s.
   */
  public int compare(RubyElement element1, Element element2) {
    if (element2 instanceof RubyElement) {
      return compare(element1, (RubyElement) element2);
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
    if (element1 instanceof RubyElement) {
      return compare((RubyElement) element1, element2);
    } else if (element2 instanceof RubyElement) {
      return (-1) * compare((RubyElement) element2, element1);
    }
    return element1.hashCode() - element2.hashCode();
  }
}
