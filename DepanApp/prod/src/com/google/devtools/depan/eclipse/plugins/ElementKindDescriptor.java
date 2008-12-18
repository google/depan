/*
 * Copyright 2008 Google Inc.
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

package com.google.devtools.depan.eclipse.plugins;

import com.google.devtools.depan.model.Element;

/**
 * Define the properties of each Element kind defined by a plugin.
 *
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public interface ElementKindDescriptor {

  /**
   * Provide the display name for the Element kind.
   * @return display name for the Element kind
   */
  public String getElementKindName();

  /**
   * Provide the display name for the providing plugin.
   * @return display name for the plugin
   */
  public String getPluginName();

  /**
   * Provide the actual class for this Element kind.
   * @return actual class for this Element kind
   */
  public Class<? extends Element> getElementKind();

}