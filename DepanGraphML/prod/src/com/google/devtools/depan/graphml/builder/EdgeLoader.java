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

package com.google.devtools.depan.graphml.builder;

import com.google.devtools.depan.pushxml.PushDownXmlHandler.ElementHandler;
import com.google.devtools.depan.pushxml.PushDownXmlHandler.NestingElementHandler;
import com.google.devtools.depan.pushxml.PushDownXmlHandler.TextElementHandler;

import org.xml.sax.Attributes;

/**
 * Interpret GraphML edge elements, and create appropriate DepAn
 * relationships in the builder graph.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class EdgeLoader extends NestingElementHandler {

  // Maven defined XML element and attribute names
  public static final String EDGE = "edge";
  private static final String SOURCE = "source";
  private static final String TARGET = "target";

  private final GraphMLContext context;

  private String source;
  private String target;
  private DataLoader data;

  public EdgeLoader(GraphMLContext context) {
    this.context = context;
  }

  @Override
  public boolean isFor(String name) {
    return EDGE.equals(name);
  }

  @Override
  public void end() {
    context.addRelation(source, target, data.getEdgeLabel());
  }


  @Override
  public void processAttributes(Attributes attributes) {
    source = attributes.getValue(SOURCE);
    target = attributes.getValue(TARGET);
  }

  @Override
  public ElementHandler newChild(String name) {
    if (DataLoader.DATA.equals(name)) {
      data = new DataLoader();
      return data;
    }

    return super.newChild(name);
  }

  private static class DataLoader extends NestingElementHandler {

    public static final String DATA = "data";

    private PolyLineEdgeLoader edge;

    @Override
    public boolean isFor(String name) {
      return DATA.equals(name);
    }

    @Override
    public ElementHandler newChild(String name) {
      if (PolyLineEdgeLoader.POLY_LINE_EDGE.equals(name)) {
        edge = new PolyLineEdgeLoader();
        return edge;
      }

      return super.newChild(name);
    }

    public String getEdgeLabel() {
      return edge.getEdgeLabel();
    }
  }

  private static class PolyLineEdgeLoader extends NestingElementHandler {

    // TODO: Use namespace aware API
    // assumes xmlns:y="http://www.yworks.com/xml/graphml"
    public static final String POLY_LINE_EDGE = "y:PolyLineEdge";
    public static final String EDGE_LABEL = "y:EdgeLabel";

    private TextElementHandler edgeLabel;

    @Override
    public boolean isFor(String name) {
      return POLY_LINE_EDGE.equals(name);
    }

    @Override
    public ElementHandler newChild(String name) {
      if (EDGE_LABEL.equals(name)) {
        edgeLabel = new TextElementHandler(name);
        return edgeLabel;
      }

      return super.newChild(name);
    }

    public String getEdgeLabel() {
      return edgeLabel.getText();
    }
  }
}
