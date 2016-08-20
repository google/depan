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

import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.graph_doc.model.GraphModelReference;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.persistence.AbstractMappingConverter;

import com.google.common.collect.Sets;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.Collection;
import java.util.Set;

/**
 * Common base for documents that include a {@link GraphModelReference}.
 * 
 * This class stashes the referenced {@link GraphDocument} in the
 * {@link UnmarshallingContext} so other reference converters
 * (e.g. {@code NodeReferenceConverter}) obtain the correct objects from
 * the serialized form.
 * 
 * Based on the legacy version of {@code ViewDocumentConverter}.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public abstract class ReferencedGraphDocumentConverter
    extends AbstractMappingConverter {

  public ReferencedGraphDocumentConverter(Mapper mapper) {
    super(mapper);
  }

  /** Provide access to the referenced {@code GraphDocument}. */
  public GraphDocument getGraphDocument(UnmarshallingContext context) {
    return (GraphDocument) context.get(GraphDocument.class);
  }

  /**
   * Provide access to the {@code GraphModel} of the referenced
   * {@code GraphDocument}.
   */
  public GraphModel getGraphModel(UnmarshallingContext context) {
    return getGraphDocument(context).getGraph();
  }

  /** Save a reference to the referenced {@code GraphDocument}. */
  public void putGraphDocument(
      UnmarshallingContext context, GraphDocument graphDoc) {
    context.put(GraphDocument.class, graphDoc);
  }

  /////////////////////////////////////
  // Common marshalling services

  /**
   * Marshal a group of nodes for saving.  The collection is wrapped
   * with an XML element named {@link #nodeLabel}.
   */
  protected void marshalNodes(
      Collection<GraphNode> nodes, String nodeLabel,
      HierarchicalStreamWriter writer, MarshallingContext context) {

    writer.startNode(nodeLabel);
    try {
      for (GraphNode node : nodes) {
        marshalObject(node, writer, context);
      }
    } finally {
      writer.endNode();
    }
  }

  /**
   * Load the {@link GraphModelReference} from the {@code reader},
   * and register it with supplied {@code context}.
   * @return 
   */
  protected GraphModelReference unmarshalGraphModelReference(
      HierarchicalStreamReader reader, UnmarshallingContext context) {
    GraphModelReference result =
        (GraphModelReference) unmarshalObject(reader, context);
    putGraphDocument(context, result.getGraph());
    return result;
  }

  /**
   * Unmarshal a sequence of nodes.  In contrast to
   * {@link #marshalNodes(Collection, String, HierarchicalStreamWriter, MarshallingContext)},
   * this method does not handle the enclosing XML element.
   * The caller is responsible for validating any surrounding element tag.
   */
  protected Collection<GraphNode> unmarshalGraphNodes(
      HierarchicalStreamReader reader, UnmarshallingContext context) {

    Set<GraphNode> result = Sets.newHashSet();

    while (reader.hasMoreChildren()) {
      GraphNode node = (GraphNode) unmarshalObject(reader, context);
      result.add(node);
    }

    return result;
  }
}
