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

package com.google.devtools.depan.eclipse.persist;

import com.google.devtools.depan.eclipse.editors.GraphDocument;

import java.io.IOException;
import java.net.URI;

/**
 * Provide easy to use load and save methods for {@link GraphDocument}s.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class GraphModelXmlPersist {

  protected final ObjectXmlPersist xmlPersist;

  public GraphModelXmlPersist() {
    this.xmlPersist = 
        new ObjectXmlPersist(XStreamFactory.getSharedGraphXStream());
  }

  public GraphDocument load(URI uri) {
    try {
      return (GraphDocument) xmlPersist.load(uri);
    } catch (IOException errIo) {
      throw new RuntimeException(
          "Unable to load GraphModel from " + uri, errIo);
    }
  }

  public void save(URI uri, GraphDocument graph) {
    try {
      xmlPersist.save(uri, graph);
    } catch (IOException errIo) {
      throw new RuntimeException(
          "Unable to save GraphModel to " + uri, errIo);
    }
  }
}
