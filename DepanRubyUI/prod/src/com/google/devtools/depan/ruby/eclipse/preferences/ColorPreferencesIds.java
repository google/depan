/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.ruby.eclipse.preferences;

import com.google.devtools.depan.ruby.eclipse.RubyActivator;

/**
 * An namespace class for Maven graph element color preferences IDs.
 *
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public final class ColorPreferencesIds {

  private ColorPreferencesIds() {
    // Prevent instantiation.
  }

  public static final String COLORS_PREFIX =
      RubyActivator.MVN_PREF_PREFIX + "color_";

  public static final String COLOR_CLASS = COLORS_PREFIX + "class";
  public static final String COLOR_CLASS_METHOD =
      COLORS_PREFIX + "class_method";
  public static final String COLOR_INSTANCE_METHOD =
      COLORS_PREFIX + "instance_method";
  public static final String COLOR_SINGLETON_METHOD =
      COLORS_PREFIX + "singleton_method";
}
