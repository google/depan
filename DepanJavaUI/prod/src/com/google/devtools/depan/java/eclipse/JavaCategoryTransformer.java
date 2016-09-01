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

package com.google.devtools.depan.java.eclipse;

import com.google.devtools.depan.java.graph.FieldElement;
import com.google.devtools.depan.java.graph.InterfaceElement;
import com.google.devtools.depan.java.graph.JavaElement;
import com.google.devtools.depan.java.graph.JavaElementDispatcher;
import com.google.devtools.depan.java.graph.MethodElement;
import com.google.devtools.depan.java.graph.PackageElement;
import com.google.devtools.depan.java.graph.TypeElement;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;

import org.eclipse.swt.graphics.Image;

/**
 * Responsible for providing the correct {@link Image}.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public class JavaCategoryTransformer
    extends JavaElementDispatcher<Integer>
    implements ElementTransformer<Integer> {

  public static final int CATEGORY_PACKAGE = 2000;
  public static final int CATEGORY_INTERFACE = 2001;
  public static final int CATEGORY_TYPE = 2002;
  public static final int CATEGORY_FIELD = 2003;
  public static final int CATEGORY_METHOD = 2004;

  /**
   * Returns the lowest category value for all elements in Java Plug-in.
   * This system must be replaced by a smarter system. Two plug-ins that use
   * overlapping constants would cause problems.
   *
   * @return The lowest value of category constants.
   */
  public static int getLowestCategory() {
    return CATEGORY_PACKAGE;
  }

  /**
   * An instance of this class used by other classes.
   */
  private static final JavaCategoryTransformer INSTANCE =
      new JavaCategoryTransformer();

  /**
   * Returns the singleton instance of this class.
   *
   * @return The singleton instance of this class.
   */
  public static JavaCategoryTransformer getInstance() {
    return INSTANCE;
  }

  /**
   * Provides the category for the supplied {@link #element}.
   */
  public static int getCategory(JavaElement element) {
    return getInstance().match(element);
  }

  private JavaCategoryTransformer() {
    // Prevent instantiation by others.
  }

  /**
   * Returns the {@link Image} for the given element.
   *
   * @param element The element whose associated {@link Image} is requested.
   * @return {@link Image} associated with given {@link Element}.
   */
  @Override
  public Integer transform(Element element) {
    return match(element);
  }

  @Override
  public Integer match(TypeElement e) {
    return CATEGORY_TYPE;
  }

  @Override
  public Integer match(MethodElement e) {
    return CATEGORY_METHOD;
  }

  @Override
  public Integer match(FieldElement e) {
    return CATEGORY_FIELD;
  }

  @Override
  public Integer match(InterfaceElement e) {
    return CATEGORY_INTERFACE;
  }

  @Override
  public Integer match(PackageElement e) {
    return CATEGORY_PACKAGE;
  }
}
