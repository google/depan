/*
 * Copyright 2009 The Depan Project Authors
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

package com.google.devtools.depan.javascript.eclipse;

import com.google.devtools.depan.filesystem.eclipse.FileSystemNodeComparator;
import com.google.devtools.depan.javascript.graph.JavaScriptElement;
import com.google.devtools.depan.model.Element;

import java.util.Comparator;

/**
 * Responsible for comparing and ordering JavaScript graph elements.
 * In this implementation, the major component categories are "borrowed" from
 * the Java node comparator.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class JavaScriptNodeComparator implements Comparator<Element> {

  private final static JavaScriptNodeComparator INSTANCE =
      new JavaScriptNodeComparator();

  private JavaScriptNodeComparator() {
    // Prevent instantiation by others.
  }

  /**
   * Provide the singleton instance of this class.
   */
  public static JavaScriptNodeComparator getInstance() {
    return INSTANCE;
  }

  /**
   * Compares two elements. Returns the result of
   * compare(BuildElement, BuildElement) if both parameters are Build Elements.
   * Returns a negative number if only first element is a Build Element. Returns
   * a positive number if only second element is a Build Element. If none of
   * them is a Build Element, returns the result of
   * <code>FileSystemNodeComparator.compare(Element, Element)</code>.
   *
   * @param element1 First element to compare.
   * @param element2 Second element to compare.
   * @return Returns the result of compare(BuildElement, BuildElement) if both
   * parameters are Build Elements. Returns a negative number if only first
   * element is a Build Element. Returns a positive number if only second
   * element is a Build Element. If none of them is a Build Element, returns the
   * result of <code>FileSystemNodeComparator.compare(Element, Element)</code>.
   */
  @Override
  public int compare(Element element1, Element element2) {
    if (isJavaScriptElement(element1)) {
      return compare((JavaScriptElement) element1, element2);
    } else if (isJavaScriptElement(element2)) {
      return (-1) * compare((JavaScriptElement) element2, element1);
    }
    return FileSystemNodeComparator.getInstance().compare(element1, element2);
  }

  private static boolean isJavaScriptElement(Object element) {
    return (element instanceof JavaScriptElement);
  }

  /**
   * Compares two Build Elements. Returns a negative number if first build
   * element has a lower category value. Returns a positive number if the first
   * build element has a higher category value. If category values are equal,
   * returns the string comparison of their friendly strings.
   *
   * @param element1 First element to compare.
   * @param element2 Second element to compare.
   * @return Returns a negative number if first build
   * element has a lower category value. Returns a positive number if the first
   * build element has a higher category value. If category values are equal,
   * returns the string comparison of their friendly strings.
   */
  private int compare(JavaScriptElement element1, JavaScriptElement element2) {
    int category1 = getCategory(element1);
    int category2 = getCategory(element2);
    if (category1 != category2) {
      return category1 - category2;
    }
    return element1.friendlyString().compareTo(element2.friendlyString());
  }

  /**
   * Compares one Build Element with another element. Returns the result of
   * compare(BuildElement, BuildElement) if the second parameter is
   * a Build Element as well. Returns a negative number otherwise.
   *
   * @param element1 First element to compare.
   * @param element2 Second element to compare.
   * @return Returns the result of compare(BuildElement, BuildElement)
   * if the second parameter is a Build Element as well. Returns a negative
   * number otherwise.
   */
  private int compare(JavaScriptElement element1, Element element2) {
    if (isJavaScriptElement(element2)) {
      return compare(element1, (JavaScriptElement) element2);
    }
    return -1;
  }

  private int getCategory(JavaScriptElement node) {
    return JavaScriptCategoryTransformer.getCategory(node);
  }
}
