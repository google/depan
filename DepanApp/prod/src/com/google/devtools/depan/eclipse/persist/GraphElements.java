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

import com.google.devtools.depan.eclipse.editors.GraphModelReference;
import com.google.devtools.depan.eclipse.editors.ViewDocument;
import com.google.devtools.depan.eclipse.editors.ViewPreferences;
import com.google.devtools.depan.eclipse.persist.XStreamFactory.Config;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;

import com.thoughtworks.xstream.XStream;

import java.awt.geom.Point2D;

/**
 * Define how {@code GraphModel} elements effect the {@code XStream}
 * persistence.
 *
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class GraphElements {

  /**
   * Prevent instantiation of this name-space class.
   */
  private GraphElements() {
  }

  public static final Config GRAPH_XML_PERSIST = new Config() {
    public void  config(XStream xstream) {

      xstream.alias(GraphModelConverter.GRAPH_DEF_TAG, GraphModel.class);
      xstream.registerConverter(new GraphModelConverter(xstream.getMapper()));

      xstream.alias(EdgeConverter.EDGE_DEF_TAG, GraphEdge.class);
      xstream.registerConverter(new EdgeConverter(xstream.getMapper()));
    }
  };

  public static final Config REF_XML_PERSIST = new Config() {
    public void  config(XStream xstream) {

      xstream.alias(
          GraphModelReferenceConverter.GRAPH_REF_TAG,
          GraphModelReference.class);
      xstream.registerConverter(
          new GraphModelReferenceConverter(xstream.getMapper()));

      xstream.aliasType(EdgeReferenceConverter.EDGE_REF_TAG, GraphEdge.class);
      xstream.registerConverter(
          new EdgeReferenceConverter(xstream.getMapper()));

      xstream.aliasType(NodeReferenceConverter.NODE_REF_TAG, GraphNode.class);
      xstream.registerConverter(new NodeReferenceConverter());

      xstream.alias(ViewDocumentConverter.VIEW_INFO_TAG, ViewDocument.class);
      xstream.registerConverter(new ViewDocumentConverter(xstream.getMapper()));

      xstream.alias("view-prefs", ViewPreferences.class);

      xstream.aliasType(Point2DConverter.POS_TAG, Point2D.class);
      xstream.registerConverter(new Point2DConverter(xstream.getMapper()));
    }
  };
}
