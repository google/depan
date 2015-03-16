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

import com.google.common.collect.Lists;
import com.google.devtools.depan.eclipse.editors.ViewDocument;
import com.google.devtools.depan.eclipse.plugins.SourcePlugin;
import com.google.devtools.depan.model.Element;

import java.util.Collection;

/**
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class ElementKindDescriptors {

  // Prevent instantiation of this name-space class.
  private ElementKindDescriptors() {
  }

  /**
   * Provide the list of {@link ElementKindDescriptor}s that are active
   * for a view.
   * 
   * <p>In this implementation, the result is the list of {@code Node} types
   * defined in any of the active analysis plugins.
   */
  public static Collection<ElementKindDescriptor> buildViewChoices(
      ViewDocument viewInfo) {
    Collection<ElementKindDescriptor> elementKinds = Lists.newArrayList();
    for (SourcePlugin plugin : viewInfo.getBuiltinAnalysisPlugins()) {
      for (Class<? extends Element> elementClass 
          : plugin.getElementClasses()) {
        elementKinds.add(
            new PrimitiveElementKindDescriptor(elementClass, plugin));
      }
    }
    return elementKinds;
  }
}
