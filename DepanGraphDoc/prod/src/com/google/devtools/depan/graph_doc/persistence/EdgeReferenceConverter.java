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
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * {@code XStream} converter to handle {@code GraphEdge}s.  This converter
 * assumes that a {@code GraphModel}, used to find nodes, can be retrieved
 * from the {@code UnmarshallingContext}.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class EdgeReferenceConverter implements Converter {

  public static final String EDGE_REF_TAG = "edge-ref";

  private static final String RELATION_TAG = "relation";
  private static final String HEAD_TAG = "head";
  private static final String TAIL_TAG = "tail";

  private final Mapper mapper;

  /** Source of information about known edges and nodes. */
  private final ReferencedGraphDocumentConverter refConverter;

  public EdgeReferenceConverter(
      Mapper mapper, ReferencedGraphDocumentConverter refConverter) {
    this.mapper = mapper;
    this.refConverter = refConverter;
  }

  public static void configXStream(
      XStream xstream, ReferencedGraphDocumentConverter refConverter) {
    xstream.aliasType(EDGE_REF_TAG, GraphEdge.class);
    xstream.registerConverter(
        new EdgeReferenceConverter(xstream.getMapper(), refConverter));
  }

  @Override
  @SuppressWarnings("rawtypes")  // Parent type uses raw type Class
  public boolean canConvert(Class type) {
    return GraphEdge.class.isAssignableFrom(type);
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
   * This implementation assumes that a {@code GraphModel}, used to find nodes,
   * can be retrieved from the {@code UnmarshallingContext}.
   */
  @Override
  public Object unmarshal(HierarchicalStreamReader reader,
      UnmarshallingContext context) {
    try {
      GraphModel graph = refConverter.getGraphModel(context);

      reader.moveDown();
      Relation relation = unmarshallRelation(reader, context);
      reader.moveUp();

      reader.moveDown();
      GraphNode head = unmarshallGraphNode(reader, context, graph);
      reader.moveUp();

      reader.moveDown();
      GraphNode tail = unmarshallGraphNode(reader, context, graph);
      reader.moveUp();

      GraphEdge result = (GraphEdge) graph.findEdge(relation, head, tail);
      return result;
    } catch (RuntimeException err) {
      // TODO(leeca): Add some error diagnostics, or eliminate as dead code.
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
      HierarchicalStreamReader reader, UnmarshallingContext context,
      GraphModel graph) {
    String nodeId = reader.getValue();
    GraphNode result = (GraphNode) graph.findNode(nodeId);
    if (null == result) {
      throw new IllegalStateException(
          "Edge reference to undefined node " + nodeId);
    }
    return result;
  }
}