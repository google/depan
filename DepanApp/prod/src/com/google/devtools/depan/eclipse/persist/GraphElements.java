/*
 * Copyright 2009 Google Inc.
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

import com.google.devtools.depan.eclipse.persist.XStreamFactory.Config;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;

import com.thoughtworks.xstream.XStream;

/**
 * Define how {@code GraphModel} elements effect the {@code XStream}
 * persistence.
 *
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class GraphElements {

  /**
   * Prevent instantiation of this name-space class.
   */
  private GraphElements() {
  }

  public static final Config CONFIG_XML_PERSIST = new Config() {
    public void  config(XStream xstream) {
      xstream.alias("graph-model", GraphModel.class);
      xstream.alias("graph-edge", GraphEdge.class);
      xstream.registerConverter(new EdgeConverter(xstream.getMapper()));
      xstream.registerConverter(new GraphModelConverter(xstream.getMapper()));
    }
  };
}
