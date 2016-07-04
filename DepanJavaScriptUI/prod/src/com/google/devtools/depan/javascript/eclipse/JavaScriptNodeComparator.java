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
import com.google.devtools.depan.java.eclipse.JavaNodeComparator;
import com.google.devtools.depan.javascript.graph.JavaScriptBuiltinElement;
import com.google.devtools.depan.javascript.graph.JavaScriptClassElement;
import com.google.devtools.depan.javascript.graph.JavaScriptElement;
import com.google.devtools.depan.javascript.graph.JavaScriptElementDispatcher;
import com.google.devtools.depan.javascript.graph.JavaScriptEnumElement;
import com.google.devtools.depan.javascript.graph.JavaScriptFieldElement;
import com.google.devtools.depan.javascript.graph.JavaScriptFunctionElement;
import com.google.devtools.depan.javascript.graph.JavaScriptVariableElement;
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

  private static final class JavaScriptCategoryDispatcher extends
      JavaScriptElementDispatcher<Integer> {

    @Override
    public Integer match(JavaScriptBuiltinElement builtinElement) {
      return JavaNodeComparator.CATEGORY_FIELD;
    }
  
    @Override
    public Integer match(JavaScriptClassElement classElement) {
      return JavaNodeComparator.CATEGORY_TYPE;
    }

    @Override
    public Integer match(JavaScriptEnumElement enumElement) {
      return JavaNodeComparator.CATEGORY_FIELD;
    }
  
    @Override
    public Integer match(JavaScriptFieldElement fieldElement) {
      return JavaNodeComparator.CATEGORY_FIELD;
    }
  
    @Override
    public Integer match(JavaScriptFunctionElement functionElement) {
      return JavaNodeComparator.CATEGORY_METHOD;
    }
  
    @Override
    public Integer match(JavaScriptVariableElement variableElement) {
      return JavaNodeComparator.CATEGORY_FIELD;
    }
  }

  private static final JavaScriptCategoryDispatcher CATEGORY_DISPATCHER =
      new JavaScriptCategoryDispatcher();

  /**
   * The category value of rules. It must be less than the lowest category value
   * of file system elements so that rules appear before file system elements.
   */
  public static final int CATEGORY_RULE =
      FileSystemNodeComparator.getLowestCategory() - 1;

  /**
   * Returns the lowest category value in this plug-in. Other plug-in must use
   * lower category values.
   *
   * @return Lowest category value in this plug-in.
   */
  public static int getLowestCategory() {
    return CATEGORY_RULE;
  }

  /**
   * Returns the category value of the given Build Element.
   *
   * @param node Build Element whose category value is requested.
   * @return Category value of the given Build Element.
   */
  public int category(JavaScriptElement node) {
    return CATEGORY_DISPATCHER.match(node);
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
  public int compare(JavaScriptElement element1, JavaScriptElement element2) {
    int category1 = category(element1);
    int category2 = category(element2);
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
  public int compare(JavaScriptElement element1, Element element2) {
    if (element2 instanceof JavaScriptElement) {
      return compare(element1, (JavaScriptElement) element2);
    }
    return -1;
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
    if (element1 instanceof JavaScriptElement) {
      return compare((JavaScriptElement) element1, element2);
    } else if (element2 instanceof JavaScriptElement) {
      return (-1) * compare((JavaScriptElement) element2, element1);
    }
    return FileSystemNodeComparator.getInstance().compare(element1, element2);
  }
}
