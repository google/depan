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

package com.google.devtools.depan.matchers.persistence;

import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.persistence.AbstractDocXmlPersist;
import com.google.devtools.depan.persistence.ObjectXmlPersist;
import com.google.devtools.depan.persistence.XStreamFactory;

import com.thoughtworks.xstream.XStream;

/**
 * Provide easy to use load and save methods for {@link GraphEdgeMatcherDescriptor}s.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class EdgeMatcherDocXmlPersist
    extends AbstractDocXmlPersist<GraphEdgeMatcherDescriptor> {

  private final static EdgeMatcherDocXStreamConfig docConfig =
      new EdgeMatcherDocXStreamConfig();

  public EdgeMatcherDocXmlPersist(ObjectXmlPersist xmlPersist) {
    super(xmlPersist);
  }

  @Override
  protected GraphEdgeMatcherDescriptor coerceLoad(Object load) {
      return (GraphEdgeMatcherDescriptor) load;
  }

  public static EdgeMatcherDocXmlPersist build(boolean readable) {
    XStream xstream = XStreamFactory.newXStream(readable);
    XStreamFactory.configureXStream(xstream);
    docConfig.config(xstream);
    ObjectXmlPersist persist = new ObjectXmlPersist(xstream);
    return new EdgeMatcherDocXmlPersist(persist);
  }
}
