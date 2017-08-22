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
import com.google.devtools.depan.view_doc.model.RelationDisplayDocument;

import java.net.URI;

/**
 * Provide easy to use load and save methods for
 * {@link RelationDisplayDocument}s.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class RelationDisplayDocXmlPersist
    extends AbstractDocXmlPersist<RelationDisplayDocument> {

  private final static RelationDisplayDocXStreamConfig DOC_CONFIG =
      new RelationDisplayDocXStreamConfig();

  public RelationDisplayDocXmlPersist(ObjectXmlPersist xmlPersist) {
    super(xmlPersist);
  }

  public static RelationDisplayDocXmlPersist build(boolean readable) {
    ObjectXmlPersist persist = XStreamFactory.build(readable, DOC_CONFIG);
    return new RelationDisplayDocXmlPersist(persist);
  }

  /////////////////////////////////////
  // Hook method implementations for AbstractDocXmlPersist

  @Override
  protected RelationDisplayDocument coerceLoad(Object load) {
      return (RelationDisplayDocument) load;
  }

  @Override
  protected String buildLoadErrorMsg(URI uri) {
    return formatErrorMsg(
        "Unable to load relation display properties from {0}", uri);
  }

  @Override
  public String buildSaveErrorMsg(URI uri) {
    return formatErrorMsg(
        "Unable to load relation display properties to {0}", uri);
  }
}
