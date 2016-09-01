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

package com.google.devtools.depan.javascript.eclipse;

import com.google.devtools.depan.java.eclipse.JavaCategoryTransformer;
import com.google.devtools.depan.javascript.graph.JavaScriptBuiltinElement;
import com.google.devtools.depan.javascript.graph.JavaScriptClassElement;
import com.google.devtools.depan.javascript.graph.JavaScriptElement;
import com.google.devtools.depan.javascript.graph.JavaScriptElementDispatcher;
import com.google.devtools.depan.javascript.graph.JavaScriptEnumElement;
import com.google.devtools.depan.javascript.graph.JavaScriptFieldElement;
import com.google.devtools.depan.javascript.graph.JavaScriptFunctionElement;
import com.google.devtools.depan.javascript.graph.JavaScriptVariableElement;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;

/**
 * Shares category values with {@link JavaCategoryTransformer}.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class JavaScriptCategoryTransformer
    extends JavaScriptElementDispatcher<Integer>
    implements ElementTransformer<Integer> {

  /**
   * Returns the lowest category value for all elements in Java Plug-in.
   * This system must be replaced by a smarter system. Two plug-ins that use
   * overlapping constants would cause problems.
   *
   * @return The lowest value of category constants.
   */
  public static int getLowestCategory() {
    return JavaCategoryTransformer.CATEGORY_PACKAGE;
  }

  /**
   * An instance of this class used by other classes.
   */
  private static final JavaScriptCategoryTransformer INSTANCE =
      new JavaScriptCategoryTransformer();

  /**
   * Returns the singleton instance of this class.
   *
   * @return The singleton instance of this class.
   */
  public static JavaScriptCategoryTransformer getInstance() {
    return INSTANCE;
  }

  /**
   * Provides the category for the supplied {@link #element}.
   */
  public static int getCategory(JavaScriptElement element) {
    return getInstance().match(element);
  }

  private JavaScriptCategoryTransformer() {
    // Prevent instantiation by others.
  }

  @Override
  public Integer transform(Element element) {
    return match(element);
  }

  @Override
  public Integer match(JavaScriptBuiltinElement builtinElement) {
    return JavaCategoryTransformer.CATEGORY_FIELD;
  }

  @Override
  public Integer match(JavaScriptClassElement classElement) {
    return JavaCategoryTransformer.CATEGORY_TYPE;
  }

  @Override
  public Integer match(JavaScriptEnumElement enumElement) {
    return JavaCategoryTransformer.CATEGORY_FIELD;
  }

  @Override
  public Integer match(JavaScriptFieldElement fieldElement) {
    return JavaCategoryTransformer.CATEGORY_FIELD;
  }

  @Override
  public Integer match(JavaScriptFunctionElement functionElement) {
    return JavaCategoryTransformer.CATEGORY_METHOD;
  }

  @Override
  public Integer match(JavaScriptVariableElement variableElement) {
    return JavaCategoryTransformer.CATEGORY_FIELD;
  }
}
