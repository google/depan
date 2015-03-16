/*
 * Copyright 2010 The Depan Project Authors
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

package com.google.devtools.depan.eclipse.utils.elementkinds;

import com.google.devtools.depan.eclipse.plugins.SourcePlugin;

/*
 * An abstract descriptor for ElementKinds that knows how to deal with plugins.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public abstract class PluginElementKindDescriptor
    implements ElementKindDescriptor {

  protected static final String ELEMENT_TEXT = "Element";
  protected static final String PLUGIN_TEXT = "Plugin";

  protected final SourcePlugin plugin;

  public PluginElementKindDescriptor(SourcePlugin plugin) {
    this.plugin = plugin;
  }

  // KLUDGE(leeca): when element descriptors are provided by the plugins,
  // this should be unnecessary.
  protected static String stripSuffix(String className, String suffix) {
    if (!className.endsWith(suffix)) {
      return className;
    }
    int limit = className.length() - suffix.length();
    return className.substring(0, limit);
  }

  protected static String expandCamel(String className) {
    StringBuffer result = new StringBuffer();
    result.append(className.charAt(0));
    for (int index = 1; index < className.length(); index++) {
      char text = className.charAt(index);
      if (Character.isUpperCase(text)) {
        result.append(" ");
      }
      result.append(text);
    }
    return result.toString();
  }

  @Override
  public String getPluginName() {
    String className = plugin.getClass().getSimpleName();
    return expandCamel(stripSuffix(className, PLUGIN_TEXT));
  }
}
