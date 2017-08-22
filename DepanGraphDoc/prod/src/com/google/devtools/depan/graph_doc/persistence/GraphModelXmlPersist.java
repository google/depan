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

package com.google.devtools.depan.graph_doc.persistence;

import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.persistence.AbstractDocXmlPersist;
import com.google.devtools.depan.persistence.ObjectXmlPersist;
import com.google.devtools.depan.persistence.XStreamFactory;

import java.net.URI;

/**
 * Provide easy to use load and save methods for {@link GraphDocument}s.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class GraphModelXmlPersist extends AbstractDocXmlPersist<GraphDocument> {

  private final static GraphDocXStreamConfig GRAPH_DOC_CONFIG =
      new GraphDocXStreamConfig();

  public GraphModelXmlPersist(ObjectXmlPersist xmlPersist) {
    super(xmlPersist);
  }

  public static GraphModelXmlPersist build(boolean readable) {
    ObjectXmlPersist persist = XStreamFactory.build(readable, GRAPH_DOC_CONFIG);
    return new GraphModelXmlPersist(persist);
  }

  /////////////////////////////////////
  // Hook method implementations for AbstractDocXmlPersist

  @Override
  protected GraphDocument coerceLoad(Object load) {
    return (GraphDocument) load;
  }

  @Override
  protected String buildLoadErrorMsg(URI uri) {
    return formatErrorMsg("Unable to load GraphModel from {0}", uri);
  }

  @Override
  public String buildSaveErrorMsg(URI uri) {
    return formatErrorMsg("Unable to save GraphModel to {0}", uri);
  }
}
