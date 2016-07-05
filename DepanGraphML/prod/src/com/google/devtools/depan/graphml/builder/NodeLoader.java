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
 * Interpret GraphML node elements, and create appropriate DepAn
 * relationships in the builder graph.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class NodeLoader extends NestingElementHandler {

  public static final String NODE = "node";
  private static final String ID = "id";

  private final GraphMLContext context;

  private DataLoader data;
  private String id;

  public NodeLoader(GraphMLContext context) {
    this.context = context;
  }

  @Override
  public boolean isFor(String name) {
    return NODE.equals(name);
  }

  @Override
  public void end() {
    context.addGraphMLNode(id, data.getNodeLabel());
  }

  @Override
  public void processAttributes(Attributes attributes) {
    id = attributes.getValue(ID);
  }

  @Override
  public ElementHandler newChild(String name) {
    if (DataLoader.DATA.equals(name)) {
      data = new DataLoader();
      return data;
    }

    return super.newChild(name);
  }

  /**
   * Provide the label associated with this node.
   * 
   * For Maven, this comes from the text content of the nested
   * y:NodeLabel element.
   */
  public String getNodeLabel() {
    return data.getNodeLabel();
  }

  private static class DataLoader extends NestingElementHandler {

    public static final String DATA = "data";

    private ShapeNodeLoader shapeNode;

    @Override
    public boolean isFor(String name) {
      return DATA.equals(name);
    }

    @Override
    public ElementHandler newChild(String name) {
      if (ShapeNodeLoader.SHAPE_NODE.equals(name)) {
        shapeNode = new ShapeNodeLoader();
        return shapeNode;
      }

      return super.newChild(name);
    }

    public String getNodeLabel() {
      return shapeNode.getNodeLabel();
    }
  }

  private static class ShapeNodeLoader extends NestingElementHandler {

    // TODO: Use namespace aware API
    // assumes xmlns:y="http://www.yworks.com/xml/graphml"
    public static final String SHAPE_NODE = "y:ShapeNode";
    public static final String NODE_LABEL = "y:NodeLabel";

    private TextElementHandler nodeLabel;

    @Override
    public boolean isFor(String name) {
      return SHAPE_NODE.equals(name);
    }

    @Override
    public ElementHandler newChild(String name) {
      if (NODE_LABEL.equals(name)) {
        nodeLabel = new TextElementHandler(name);
        return nodeLabel;
      }

      return super.newChild(name);
    }

    public String getNodeLabel() {
      return nodeLabel.getText();
    }
  }
}
