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

package com.google.devtools.depan.graph_doc.persistence;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.basic.BasicEdge;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.api.GraphBuilder;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * {@code XStream} converter to handle {@code GraphEdge}s.  This converter
 * assumes that the {@code UnmarshallingContext} has a {@code GraphBuilder}
 * entry be obtained using the
 * {@link GraphModelConverter#contextGraphBuilder(com.thoughtworks.xstream.converters.DataHolder)}
 * method.
 * 
 * @author Original lost in the mists of time
 */
public class EdgeConverter implements Converter {

  public static final String EDGE_DEF_TAG = "graph-edge";

  private static final String TAIL_TAG = "tail";
  private static final String HEAD_TAG = "head";
  private static final String RELATION_TAG = "relation";
  private final Mapper mapper;

  public EdgeConverter(Mapper mapper) {
    this.mapper = mapper;
  }

  public static void configXStream(XStream xstream) {
    xstream.alias(EdgeConverter.EDGE_DEF_TAG, GraphEdge.class);
    xstream.registerConverter(new EdgeConverter(xstream.getMapper()));
  }

  @Override
  @SuppressWarnings("rawtypes")  // Parent type uses raw type Class
  public boolean canConvert(Class type) {
    return GraphEdge.class.equals(type);
  }

  @Override
  public void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context) {
    GraphEdge edge = (GraphEdge) source;

    Relation relation = edge.getRelation();
    writer.startNode(RELATION_TAG);

    Class<?> actualType = relation.getClass();
    Class<?> defaultType = mapper.defaultImplementationOf(BasicEdge.class);
    if (!actualType.equals(defaultType)) {
        writer.addAttribute(
            mapper.aliasForAttribute("class"),
            mapper.serializedClass(actualType));
    }

    context.convertAnother(relation);
    writer.endNode();

    writer.startNode(HEAD_TAG);
    context.convertAnother(edge.getHead().getId());
    writer.endNode();

    writer.startNode(TAIL_TAG);
    context.convertAnother(edge.getTail().getId());
    writer.endNode();
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation assumes the {@code GraphBuilder} entry be obtained
   * from the {@code context} using the
   * {@link GraphModelConverter#contextGraphBuilder(com.thoughtworks.xstream.converters.DataHolder)}
   * method.
   */
  @Override
  public Object unmarshal(HierarchicalStreamReader reader,
      UnmarshallingContext context) {
    try {
      GraphBuilder builder = GraphModelConverter.contextGraphBuilder(context);

      reader.moveDown();
      Relation relation = unmarshallRelation(reader, context);
      reader.moveUp();

      reader.moveDown();
      GraphNode head = unmarshallGraphNode(reader, context, builder);
      reader.moveUp();

      reader.moveDown();
      GraphNode tail = unmarshallGraphNode(reader, context, builder);
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
    Class<?> resultClass = mapper.realClass(classAttribute);
    Relation relation = (Relation) context.convertAnother(null, resultClass);
    return relation;
  }

  private GraphNode unmarshallGraphNode(
      HierarchicalStreamReader reader,
      UnmarshallingContext context,
      GraphBuilder builder) {
    String nodeId = reader.getValue();
    GraphNode result = builder.findNode(nodeId);
    if (null == result) {
      throw new IllegalStateException(
          "Edge reference to undefined node " + nodeId);
    }
    return result;
  }
}