/*
 * Copyright 20016 The Depan Project Authors
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

package com.google.devtools.depan.ruby.editors;

import com.google.devtools.depan.eclipse.plugins.ElementClassTransformer;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.remap_doc.plugins.ElementEditor;

/**
 * Responsible for providing the correct {@link ElementEditor}.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class RubyElementEditors implements
    ElementClassTransformer<Class<? extends ElementEditor>> {

  /**
   * An instance of this class used by other classes.
   */
  private static final RubyElementEditors INSTANCE =
      new RubyElementEditors();

  /**
   * Returns the singleton instance of this class.
   *
   * @return The singleton instance of this class.
   */
  public static RubyElementEditors getInstance() {
    return INSTANCE;
  }

  private RubyElementEditors() {
    // prevent instantiation by others
  }

  /**
   * Returns the {@link ElementEditor} for objects of given class type.
   *
   * @param element The class type whose associated editor is requested.
   * @return {@link ElementEditor} associated with the given class type.
   */
  @Override
  public Class<? extends ElementEditor> transform(
      Class<? extends Element> element) {
    return null;
  }
}
