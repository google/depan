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

package com.google.devtools.depan.resources;

import com.google.devtools.depan.persistence.AbstractDocXmlPersist;

import org.eclipse.core.resources.IFile;

/**
 * Define a resource that is backed in the file system.
 * 
 * @author <a href='mailto:leeca@pnambic.com'>Lee Carver</a>
 */
public class FileDocumentReference<T extends PropertyDocument<?>>
    implements PropertyDocumentReference<T> {
  private IFile location;
  private T document;

  public FileDocumentReference(IFile location, T document) {
    this.location = location;
    this.document = document;
  }

  @Override
  public void saveResource(T document, AbstractDocXmlPersist<T> persist) {
    this.document = document;
    persist.saveDocument(location, document, null);
  }

  @Override
  public T getDocument() {
    return document;
  }

  public IFile getLocation() {
    return location;
  }

  public static <T extends PropertyDocument<?>>
      FileDocumentReference<T> buildFileReference(
          IFile location, T document) {
    return new FileDocumentReference<T>(location, document);
  }
}
