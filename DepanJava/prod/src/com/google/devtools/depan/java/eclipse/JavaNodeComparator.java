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

import com.google.devtools.depan.filesystem.elements.DirectoryElement;
import com.google.devtools.depan.filesystem.elements.FileElement;
import com.google.devtools.depan.java.JavaElementDispatcher;
import com.google.devtools.depan.java.elements.FieldElement;
import com.google.devtools.depan.java.elements.InterfaceElement;
import com.google.devtools.depan.java.elements.JavaElement;
import com.google.devtools.depan.java.elements.MethodElement;
import com.google.devtools.depan.java.elements.PackageElement;
import com.google.devtools.depan.java.elements.TypeElement;
import com.google.devtools.depan.model.Element;

import java.util.Comparator;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class JavaNodeComparator implements Comparator<Element> {

  public final static JavaNodeComparator INSTANCE = new JavaNodeComparator();

  private JavaNodeComparator() {
  }

  @SuppressWarnings("unchecked")
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
        return 7;
      }
      @Override
      public Integer match(FieldElement e) {
        return 6;
      }
      @Override
      public Integer match(TypeElement e) {
        return 5;
      }
      @Override
      public Integer match(InterfaceElement e) {
        return 4;
      }
      @Override
      public Integer match(PackageElement e) {
        return 3;
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
