/*
 * Copyright 2009 The Depan Project Authors
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

package com.google.devtools.depan.javascript.editors;

import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.remap_doc.plugins.ElementEditor;
import com.google.devtools.depan.view_doc.eclipse.ui.plugins.ElementClassTransformer;

/**
 * Stub editor lookup for JavaScript graph elements.  This does nothing
 * except fulfill the contract of an ElementClassTransformer.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class ElementEditors
    implements ElementClassTransformer<Class<? extends ElementEditor>> {

  /**
   * {@inheritDoc}
   * @return <code>ElementEditor</code> always provides {@code null}
   */
  @Override
  public Class<? extends ElementEditor> transform(
      Class<? extends Element> element) {
    return null;
  }
}
