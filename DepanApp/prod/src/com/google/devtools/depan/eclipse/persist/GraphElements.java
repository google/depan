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

import com.google.devtools.depan.eclipse.persist.GraphModelConverter;
import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.graph_doc.persistence.EdgeConverter;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.relations.persistence.RelationSetConverters;
import com.google.devtools.depan.view_doc.model.GraphModelReference;
import com.google.devtools.depan.view_doc.model.ViewDocument;
import com.google.devtools.depan.view_doc.model.ViewPreferences;
import com.google.devtools.depan.view_doc.persistence.CameraDirConverter;
import com.google.devtools.depan.view_doc.persistence.CameraPosConverter;
import com.google.devtools.depan.view_doc.persistence.EdgeReferenceConverter;
import com.google.devtools.depan.view_doc.persistence.GraphModelReferenceConverter;
import com.google.devtools.depan.view_doc.persistence.NodeReferenceConverter;
import com.google.devtools.depan.view_doc.persistence.Point2DConverter;
import com.google.devtools.depan.view_doc.persistence.ViewDocumentConverter;

import com.thoughtworks.xstream.XStream;

import java.awt.geom.Point2D;

/**
 * Define how {@code GraphModel} elements effect the {@code XStream}
 * persistence.
 *
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class GraphElements {
  public static final String GRAPH_INFO_TAG = "graph-info";

  /**
   * Prevent instantiation of this name-space class.
   */
  private GraphElements() {
  }

  public static final Config GRAPH_XML_PERSIST = new Config() {

    @Override
    public void config(XStream xstream) {

      xstream.alias(GRAPH_INFO_TAG, GraphDocument.class);

      xstream.alias(GraphModelConverter.GRAPH_DEF_TAG, GraphModel.class);
      xstream.registerConverter(new GraphModelConverter(xstream.getMapper()));

      xstream.alias(EdgeConverter.EDGE_DEF_TAG, GraphEdge.class);
      xstream.registerConverter(new EdgeConverter(xstream.getMapper()));

      SourcePluginConverter.configXStream(xstream);
    }

  };

  public static final Config REF_XML_PERSIST = new Config() {

    @Override
    public void config(XStream xstream) {

      xstream.alias(
          GraphModelReferenceConverter.GRAPH_REF_TAG,
          GraphModelReference.class);
      xstream.registerConverter(
          new GraphModelReferenceConverter(xstream.getMapper()));

      // View converter knows how to handle a referenced GraphDocument.
      ViewDocumentConverter viewConverter =
          new ViewDocumentConverter(xstream.getMapper());

      xstream.aliasType(EdgeReferenceConverter.EDGE_REF_TAG, GraphEdge.class);
      xstream.registerConverter(
          new EdgeReferenceConverter(xstream.getMapper(), viewConverter));

      xstream.aliasType(NodeReferenceConverter.NODE_REF_TAG, GraphNode.class);
      xstream.registerConverter(new NodeReferenceConverter(viewConverter));

      xstream.alias(ViewDocumentConverter.VIEW_INFO_TAG, ViewDocument.class);
      xstream.registerConverter(viewConverter);

      xstream.alias("view-prefs", ViewPreferences.class);

      xstream.aliasType(Point2DConverter.POS_TAG, Point2D.class);
      xstream.registerConverter(new Point2DConverter(xstream.getMapper()));

      CameraDirConverter.configXStream(xstream);
      CameraPosConverter.configXStream(xstream);
      RelationSetConverters.configXStream(xstream);
    }
  };
}
