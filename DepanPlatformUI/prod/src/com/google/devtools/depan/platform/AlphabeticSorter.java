/*
 * Copyright 2007 The Depan Project Authors
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

package com.google.devtools.depan.platform;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

/**
 * Sort the viewer data in a alphabetic order (using
 * {@link String#compareTo(String)}. Strings to compare are retrieved via a
 * {@link ViewerObjectToString}, or with the {@link Object#toString()} method
 * if the object is constructed with {@link AlphabeticSorter#AlphabeticSorter()}
 * or {@link AlphabeticSorter#AlphabeticSorter(boolean)}.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class AlphabeticSorter extends ViewerComparator {

  /**
   * {@link ViewerObjectToString} used to get a String for a given object to
   * sort. This is useful since the {@link Object#toString()} is sometimes not
   * enough, or doesn't return the same informations as in the {@link Viewer} we
   * want to sort.
   */
  private final ViewerObjectToString toString;

  /**
   * If the comparison must ignore the case or not.
   */
  private boolean ignoreCase = false;

  private static final ViewerObjectToString DEFAULT_TO_STRING =
    new ViewerObjectToString() {

    @Override
      public String getString(Object object) {
        return object.toString();
      }
    };

  /**
   * Create a new {@link AlphabeticSorter} using the given
   * {@link ViewerObjectToString}.
   *
   * @param toString a {@link ViewerObjectToString} used to get the string of
   *        the objects to compare.
   * @param ignoreCase true if the String comparison must ignore case, false
   *        otherwise.
   */
  public AlphabeticSorter(ViewerObjectToString toString, boolean ignoreCase) {
    this.toString = toString;
    this.ignoreCase = ignoreCase;
  }

  /**
   * Create a new {@link AlphabeticSorter} using the given
   * {@link ViewerObjectToString}.
   *
   * @param toString a {@link ViewerObjectToString} used to get the string of
   *        the objects to compare.
   */
  public AlphabeticSorter(ViewerObjectToString toString) {
    this(toString, false);
  }

  /**
   * Create a new {@link AlphabeticSorter} with default values: use toString in
   * {@link Object} to compare values, and <code>ignoreCase = false</code>;
   *
   * @see Object#toString()
   */
  public AlphabeticSorter() {
    this(DEFAULT_TO_STRING, false);
  }

  /**
   * Create a new {@link AlphabeticSorter} which uses the toString method of
   * {@link Object} to compare objects.
   *
   * @param ignoreCase true if the String comparison must ignore case, false
   *        otherwise.
   * @see Object#toString()
   */
  public AlphabeticSorter(boolean ignoreCase) {
    this(DEFAULT_TO_STRING, ignoreCase);
  }

  @Override
  public int compare(Viewer viewer, Object e1, Object e2) {
    String s1 = toString.getString(e1);
    String s2 = toString.getString(e2);
    if (ignoreCase) {
      return s1.compareToIgnoreCase(s2);
    }
    return s1.compareTo(s2);
  }
}
