/*
 * Copyright 2017 The Depan Project Authors
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

package com.google.devtools.depan.nodes.filters.persistence;

import com.google.devtools.depan.persistence.AbstractDocXmlPersist;
import com.google.devtools.depan.persistence.plugins.ResourceDocumentConfig;

import org.eclipse.core.resources.IFile;

/**
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class ContextualFilterDocConfig implements ResourceDocumentConfig {

  @Override
  public boolean forExtension(String docExt) {
    return ContextualFilterResources.EXTENSION.equals(docExt);
  }

  @Override
  public AbstractDocXmlPersist<?> getDocXmlPersist(
      IFile file, boolean readable) {
    return ContextualFilterXmlPersist.build(readable) ;
  }
}
