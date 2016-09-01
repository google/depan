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
import com.google.devtools.depan.model.ElementTransformer;
import com.google.devtools.depan.ruby.graph.ClassElement;
import com.google.devtools.depan.ruby.graph.ClassMethodElement;
import com.google.devtools.depan.ruby.graph.InstanceMethodElement;
import com.google.devtools.depan.ruby.graph.RubyElement;
import com.google.devtools.depan.ruby.graph.RubyElementDispatcher;
import com.google.devtools.depan.ruby.graph.SingletonMethodElement;

import org.eclipse.swt.graphics.Image;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class RubyCategoryTransformer
    extends RubyElementDispatcher<Integer>
    implements ElementTransformer<Integer> {

  public static final int CATEGORY_CLASS = 4000;
  public static final int CATEGORY_CLASS_METHOD = 4010;
  public static final int CATEGORY_INSTANCE_METHOD = 4020;
  public static final int CATEGORY_SINGLETON_METHOD = 4030;

  /**
   * Returns the lowest category value for all elements in Ruby Plug-in.
   * This system must be replaced by a smarter system. Two plug-ins that use
   * overlapping constants would cause problems.
   *
   * @return The lowest value of category constants.
   */
  public static int getLowestCategory() {
    return RubyCategoryTransformer.CATEGORY_CLASS;
  }

  /**
   * An instance of this class used by other classes.
   */
  private static final RubyCategoryTransformer INSTANCE =
      new RubyCategoryTransformer();

  /**
   * Returns the singleton instance of this class.
   */
  public static RubyCategoryTransformer getInstance() {
    return INSTANCE;
  }

  /**
   * Provides the category for the supplied {@link #element}.
   */
  public static int getCategory(RubyElement element) {
    return getInstance().match(element);
  }

  private RubyCategoryTransformer() {
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
}
