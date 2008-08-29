/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.model;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.basic.BasicEdge;
import com.google.devtools.depan.model.XmlPersistentObject.Config;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class GraphElements {

  public static Config configXmlPersist = new Config() {
    public void  config(XStream xstream) {
      xstream.alias("graph-edge", GraphEdge.class);
      xstream.registerConverter(new EdgeConverter(xstream.getMapper()));
    }
  };

  public static class EdgeConverter implements Converter {

    private final Mapper mapper;

    public EdgeConverter(Mapper mapper) {
      this.mapper = mapper;
    }

    // @Override
    public boolean canConvert(Class type) {
      return GraphEdge.class.equals(type);
    }

    // @Override
    public void marshal(Object source, HierarchicalStreamWriter writer,
        MarshallingContext context) {
      BasicEdge edge = (BasicEdge) source;

      Relation relation = edge.getRelation();
      writer.startNode("relation");

      Class actualType = relation.getClass();
      Class defaultType = mapper.defaultImplementationOf(BasicEdge.class);
      if (!actualType.equals(defaultType)) {
          writer.addAttribute(
              mapper.aliasForAttribute("class"),
              mapper.serializedClass(actualType));
      }

      context.convertAnother(relation);
      writer.endNode();

      writer.startNode("head");
      context.convertAnother(edge.getHead().getId());
      writer.endNode();

      writer.startNode("tail");
      context.convertAnother(edge.getTail().getId());
      writer.endNode();
    }

    // @Override
    public Object unmarshal(HierarchicalStreamReader reader,
        UnmarshallingContext context) {
      try {
        reader.moveDown();
        Relation relation = unmarshallRelation(reader, context);
        reader.moveUp();

        reader.moveDown();
        GraphNode head = unmarshallGraphNode(reader, context);
        reader.moveUp();

        reader.moveDown();
        GraphNode tail = unmarshallGraphNode(reader, context);
        reader.moveUp();

        GraphEdge result = new GraphEdge(head, tail, relation);
        return result;
      } catch (RuntimeException err) {
        // TODO Auto-generated catch block
        err.printStackTrace();
        throw err;
      }
    }

    private Relation unmarshallRelation(
        HierarchicalStreamReader reader, UnmarshallingContext context) {
      String classAttribute =
          reader.getAttribute(mapper.aliasForAttribute("class"));
      Class resultClass = mapper.realClass(classAttribute);
      Relation relation = (Relation) context.convertAnother(null, resultClass);
      return relation;
    }

    private GraphNode unmarshallGraphNode(
        HierarchicalStreamReader reader, UnmarshallingContext context) {
      String nodeId = reader.getValue();
      GraphNode result = (GraphNode) context.get(nodeId);
      if (null == result) {
        throw new IllegalStateException(
            "Edge reference to undefined node " + nodeId);
      }
      return result;
    }
  }
}
