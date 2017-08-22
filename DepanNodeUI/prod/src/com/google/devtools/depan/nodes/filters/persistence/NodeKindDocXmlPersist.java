/*
 * Copyright 2016 The Depan Project Authors
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

import com.google.devtools.depan.nodes.filters.model.NodeKindDocument;
import com.google.devtools.depan.persistence.AbstractDocXmlPersist;
import com.google.devtools.depan.persistence.ObjectXmlPersist;
import com.google.devtools.depan.persistence.XStreamFactory;

import java.net.URI;

/**
 * Provide easy to use load and save methods for
 * {@link NodeKindDocument}s.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class NodeKindDocXmlPersist
    extends AbstractDocXmlPersist<NodeKindDocument> {

  private final static NodeKindDocXStreamConfig docConfig =
      new NodeKindDocXStreamConfig();

  public NodeKindDocXmlPersist(ObjectXmlPersist xmlPersist) {
    super(xmlPersist);
  }

  public static NodeKindDocXmlPersist build(boolean readable) {
    ObjectXmlPersist persist = XStreamFactory.build(readable, docConfig);
    return new NodeKindDocXmlPersist(persist);
  }

  /////////////////////////////////////
  // Hook method implementations for AbstractDocXmlPersist

  @Override
  protected NodeKindDocument coerceLoad(Object load) {
      return (NodeKindDocument) load;
  }

  @Override
  protected String buildLoadErrorMsg(URI uri) {
    return formatErrorMsg("Unable to load NodeKind from {0}", uri);
  }

  @Override
  public String buildSaveErrorMsg(URI uri) {
    return formatErrorMsg("Unable to save NodeKind to {0}", uri);
  }
}
