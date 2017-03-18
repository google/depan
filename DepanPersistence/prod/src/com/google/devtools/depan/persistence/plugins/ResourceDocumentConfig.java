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

package com.google.devtools.depan.persistence.plugins;

import com.google.devtools.depan.persistence.AbstractDocXmlPersist;

import org.eclipse.core.resources.IFile;

/**
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public interface ResourceDocumentConfig {

  /**
   * Indicate if the {@link ResourceDocumentConfig} instance is suitable
   * for the supplied file name extension.
   */
  boolean forExtension(String docExt);

  /**
   * Provide an XML persistence object configured for the supplied
   * {@link IFile}.
   * 
   * @param file source or destination for serialization
   * @param readable true if the file is to be read, false if the file will
   *    be written.
   * @return a fully configured XML persistence object
   */
  AbstractDocXmlPersist<?> getDocXmlPersist(IFile file, boolean readable);
}
