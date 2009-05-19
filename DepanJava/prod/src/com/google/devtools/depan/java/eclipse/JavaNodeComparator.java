/*
 * Copyright 2007 Google Inc.
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

import com.google.devtools.depan.java.graph.FieldElement;
import com.google.devtools.depan.java.graph.InterfaceElement;
import com.google.devtools.depan.java.graph.JavaElement;
import com.google.devtools.depan.java.graph.MethodElement;
import com.google.devtools.depan.java.graph.PackageElement;
import com.google.devtools.depan.java.graph.TypeElement;
import com.google.devtools.depan.java.integration.JavaElementDispatcher;
import com.google.devtools.depan.model.Element;

import java.util.Comparator;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class JavaNodeComparator implements Comparator<Element> {

  public static final int CATEGORY_PACKAGE = 3;
  public static final int CATEGORY_INTERFACE = 4;
  public static final int CATEGORY_TYPE = 5;
  public static final int CATEGORY_FIELD = 6;
  public static final int CATEGORY_METHOD = 7;

  public final static JavaNodeComparator INSTANCE = new JavaNodeComparator();

  private JavaNodeComparator() {
  }

  private static boolean isJavaElement(Object element) {
    return (element instanceof JavaElement);
  }

  public int compare(JavaElement e1, JavaElement e2) {
    int cat1 = category(e1);
    int cat2 = category(e2);
    if (cat1 != cat2) {
      return cat1 - cat2;
    }
    // categories are the same, fall back to string comparison
    return e1.friendlyString().compareTo(e2.friendlyString());
  }

  public int category(Element node) {
    final JavaElementDispatcher<Integer> d = new JavaElementDispatcher<Integer>() {
      @Override
      public Integer match(MethodElement e) {
        return CATEGORY_METHOD;
      }
      @Override
      public Integer match(FieldElement e) {
        return CATEGORY_FIELD;
      }
      @Override
      public Integer match(TypeElement e) {
        return CATEGORY_TYPE;
      }
      @Override
      public Integer match(InterfaceElement e) {
        return CATEGORY_INTERFACE;
      }
      @Override
      public Integer match(PackageElement e) {
        return CATEGORY_PACKAGE;
      }
    };
    return d.match(node);
  }

  @Override
  public int compare(Element e1, Element e2) {
    if (isJavaElement(e1) && isJavaElement(e2)) {
      return compare((JavaElement) e1, (JavaElement) e2);
    }
    return e1.hashCode() - e2.hashCode();
  }
}
