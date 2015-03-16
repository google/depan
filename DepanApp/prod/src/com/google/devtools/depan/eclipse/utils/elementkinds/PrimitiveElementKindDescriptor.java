/*
 * Copyright 2008 The Depan Project Authors
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
import com.google.devtools.depan.model.Element;

/**
 * Implement a simplistic {@code ElementKindDescriptor} that is suitable
 * for retro-fitting non-compliant plugins.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class PrimitiveElementKindDescriptor 
     extends PluginElementKindDescriptor {

  private final Class<? extends Element> elementKind;

  public PrimitiveElementKindDescriptor(
      Class<? extends Element> elementKind, SourcePlugin plugin) {
    super(plugin);
    this.elementKind = elementKind;
  }

  @Override
  public String getElementKindName() {
    String className = elementKind.getSimpleName();
    return stripSuffix(className, ELEMENT_TEXT);
  }

  @Override
  public Class<? extends Element> getElementKind() {
    return elementKind;
  }
}
