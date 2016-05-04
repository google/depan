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

package com.google.devtools.depan.persistence;

import java.io.IOException;
import java.net.URI;

/**
 * Provide easy to use load and save methods for many document types.
 * 
 * Concrete types must provide a {@link #coerceLoad(Object)} method to
 * properly handle Java type matching with generic erasure.
 * 
 * Derived types are encouraged to supply a static build method that
 * properly configures an XStreamConfig for the supplied document type
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public abstract class AbstractDocXmlPersist<T> {

  protected final ObjectXmlPersist xmlPersist;

  public AbstractDocXmlPersist(ObjectXmlPersist xmlPersist) {
    this.xmlPersist = xmlPersist;
  }

  protected abstract T coerceLoad(Object load);

  @SuppressWarnings("unchecked")
  public T load(URI uri) {
    try {
      return (T) xmlPersist.load(uri);
    } catch (IOException errIo) {
      String msg = "Unable to load EdgeMatcher from " + uri;
      PersistenceLogger.logException(msg, errIo);
      throw new RuntimeException(msg, errIo);
    }
  }

  public void save(URI uri, T doc) {
    try {
      xmlPersist.save(uri, doc);
    } catch (IOException errIo) {
      String msg = "Unable to save EdgeMatcher to " + uri;
      PersistenceLogger.logException(msg, errIo);
      throw new RuntimeException(msg, errIo);
    }
  }
}
