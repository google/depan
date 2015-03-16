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
import com.google.devtools.depan.model.Element;

/**
 * Support views of ElementKinds that have simulated node types (e.g. Total,
 * Other, etc.).
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class PsuedoElementKindDescriptor
    extends PluginElementKindDescriptor {

  private final String kindName;

  /**
   * @param plugin
   */
  public PsuedoElementKindDescriptor(String kindName, SourcePlugin plugin) {
    super(plugin);
    this.kindName = kindName;
  }

  /**
   * {@inheritDoc}
   * 
   * <p>This implementation throws {@code UnsupportedOperationException} if
   * this method is called on an instance.
   * 
   * @throws UnsupportedOperationException if this method is called
   *     on an instance.
   */
  @Override
  public Class<? extends Element> getElementKind() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getElementKindName() {
    return kindName;
  }
}
