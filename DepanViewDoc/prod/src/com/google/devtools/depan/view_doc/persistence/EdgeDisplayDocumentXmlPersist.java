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

package com.google.devtools.depan.view_doc.persistence;

import com.google.devtools.depan.persistence.AbstractDocXmlPersist;
import com.google.devtools.depan.persistence.ObjectXmlPersist;
import com.google.devtools.depan.persistence.XStreamFactory;
import com.google.devtools.depan.view_doc.model.EdgeDisplayDocument;

import java.net.URI;

/**
 * Provide easy to use load and save methods for {@link EdgeDisplayDocument}s.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class EdgeDisplayDocumentXmlPersist
    extends AbstractDocXmlPersist<EdgeDisplayDocument> {

  private final static EdgeDisplayDocumentXStreamConfig docConfig =
      new EdgeDisplayDocumentXStreamConfig();

  public EdgeDisplayDocumentXmlPersist(ObjectXmlPersist xmlPersist) {
    super(xmlPersist);
  }

  public static EdgeDisplayDocumentXmlPersist build(boolean readable) {
    ObjectXmlPersist persist = XStreamFactory.build(readable, docConfig);
    return new EdgeDisplayDocumentXmlPersist(persist);
  }

  /////////////////////////////////////
  // Hook method implementations for AbstractDocXmlPersist

  @Override
  protected EdgeDisplayDocument coerceLoad(Object load) {
      return (EdgeDisplayDocument) load;
  }

  @Override
  protected String logLoadException(URI uri, Exception err) {
    return logException(
        "Unable to load edge display properties from {0}", uri, err);
  }

  @Override
  public String logSaveException(URI uri, Exception err) {
    return logException(
        "Unable to load edge display properties to {0}", uri, err);
  }
}
