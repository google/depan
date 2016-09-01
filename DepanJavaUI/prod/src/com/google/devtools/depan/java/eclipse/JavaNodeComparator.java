/*
 * Copyright 2007 The Depan Project Authors
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

package com.google.devtools.depan.java.eclipse;

import com.google.devtools.depan.java.graph.JavaElement;
import com.google.devtools.depan.model.Element;

import java.util.Comparator;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class JavaNodeComparator implements Comparator<Element> {

  private final static JavaNodeComparator INSTANCE = new JavaNodeComparator();

  private JavaNodeComparator() {
    // Prevent instantiation by others.
  }

  /**
   * Provide the singleton instance of this class.
   */
  public static JavaNodeComparator getInstance() {
    return INSTANCE;
  }

  @Override
  public int compare(Element element1, Element element2) {
    if (isJavaElement(element1)) {
      return compare((JavaElement) element1, element2);
    } else if (isJavaElement(element2)) {
      return (-1) * compare((JavaElement) element2, element1);
    }
    return element1.hashCode() - element2.hashCode();
  }

  private int compare(JavaElement e1, JavaElement e2) {
    int cat1 = getCategory(e1);
    int cat2 = getCategory(e2);
    if (cat1 != cat2) {
      return cat1 - cat2;
    }
    // categories are the same, fall back to string comparison
    return e1.friendlyString().compareTo(e2.friendlyString());
  }

  private int compare(JavaElement element1, Element element2) {
    if (isJavaElement(element2)) {
      return compare(element1, (JavaElement) element2);
    }
    return 1;
  }

  private static boolean isJavaElement(Object element) {
    return (element instanceof JavaElement);
  }

  private int getCategory(JavaElement node) {
    return JavaCategoryTransformer.getCategory(node);
  }
}
