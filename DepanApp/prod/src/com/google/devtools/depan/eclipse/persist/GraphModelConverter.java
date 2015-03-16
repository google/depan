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

import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.logging.Logger;

/**
 * Custom {@code XStream} converter for {@code GraphModel}s.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class GraphModelConverter implements Converter {

  public static final String GRAPH_DEF_TAG = "graph-model";

  private static final Logger logger =
      Logger.getLogger(GraphModelConverter.class.getName());

  private final Mapper mapper;

  public GraphModelConverter(Mapper mapper) {
    this.mapper = mapper;
  }

  @Override
  @SuppressWarnings("rawtypes")  // Parent type uses raw type Class
  public boolean canConvert(Class type) {
    return GraphModel.class.equals(type);
  }

  /**
   * No need to start a node, since the caller ensures we are wrapped correctly.
   */
  @Override
  public void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context) {
    GraphModel graph = (GraphModel) source;

    // Save all nodes.
    for (GraphNode node : graph.getNodes()) {
      marshalObject(node, writer, context);
    }

    // Save all edges.
    for (GraphEdge edge : graph.getEdges()) {
      marshalObject(edge, writer, context);
    }
  }

  protected void marshalObject(Object item,
      HierarchicalStreamWriter writer, MarshallingContext context) {
    String nodeLabel = mapper.serializedClass(item.getClass());
    writer.startNode(nodeLabel);
    context.convertAnother(item);
    writer.endNode();
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation temporarily injects the current {@code GraphModel}
   * instance into the {@code UnmarshallingContext} with the key
   * {@code GraphModel.class}.  This allows the {@link EdgeConverter} to
   * translate node ids directly into node references.
   * 
   * @see EdgeConverter#unmarshal(HierarchicalStreamReader, UnmarshallingContext)
   */
  @Override
  public Object unmarshal(
      HierarchicalStreamReader reader, UnmarshallingContext context) {
    // There should not be two graphs in the same serialization,
    // but just in case ....
    Object prior = context.get(GraphModel.class);

    try {
      GraphModel result = new GraphModel();
      context.put(GraphModel.class, result);

      while (reader.hasMoreChildren()) {
        reader.moveDown();
        String childName = reader.getNodeName();
        Class<?> childClass = mapper.realClass(childName);

        if (GraphNode.class.isAssignableFrom(childClass)) {
          GraphNode node = (GraphNode) context.convertAnother(null, childClass);
          result.addNode(node);
        }
        else if (GraphEdge.class.isAssignableFrom(childClass)) {
          GraphEdge edge =
              (GraphEdge) context.convertAnother(null, childClass);
          result.addEdge(edge);
        } else {
          logger.info("Skipped object with tag " + childName);
        }

        reader.moveUp();
      }

      return result;
    } catch (RuntimeException err) {
      // TODO Auto-generated catch block
      err.printStackTrace();
      throw err;
    } finally {
      context.put(GraphModel.class, prior);
    }
  }
}