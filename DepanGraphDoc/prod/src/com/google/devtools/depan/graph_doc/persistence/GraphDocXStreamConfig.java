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

package com.google.devtools.depan.graph_doc.persistence;

import com.google.devtools.depan.graph_doc.GraphDocResources;
import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.persistence.plugins.XStreamConfig;

import com.thoughtworks.xstream.XStream;

import org.osgi.framework.Bundle;

import java.util.Collection;
import java.util.Collections;

/**
 * Prepare an {@link XStream} for serializing an {@link GraphDocument}.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class GraphDocXStreamConfig implements XStreamConfig {

  public static final String GRAPH_INFO_TAG = "graph-info";

  @Override
  public void config(XStream xstream) {
    xstream.setMode(XStream.NO_REFERENCES);
    xstream.alias(GRAPH_INFO_TAG, GraphDocument.class);

    GraphModelConverter.configXStream(xstream);
    EdgeConverter.configXStream(xstream);
  }

  @Override
  public Collection<? extends Bundle> getDocumentBundles() {
    return Collections.singletonList(GraphDocResources.BUNDLE);
  }
}
