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

package com.google.devtools.depan.nodelist_doc.persistence;

import com.google.devtools.depan.graph_doc.model.GraphModelReference;
import com.google.devtools.depan.graph_doc.persistence.GraphModelReferenceConverter;
import com.google.devtools.depan.graph_doc.persistence.ReferencedGraphDocumentConverter;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.nodelist_doc.model.NodeListDocument;
import com.google.devtools.depan.persistence.PersistenceLogger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import org.eclipse.core.resources.IFile;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

/**
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class NodeListDocumentConverter
    extends ReferencedGraphDocumentConverter {

  public static final String NODE_LIST_INFO_TAG = "node-list-info";

  private static final String NODE_LIST = "node-list";

  private IFile projectSrc;

  private File relativeSrc;

  public NodeListDocumentConverter(Mapper mapper) {
    super(mapper);
  }

  @Override
  public Class<?> getType() {
    return NodeListDocument.class;
  }

  public static NodeListDocumentConverter configXStream(XStream xstream) {
    NodeListDocumentConverter result =
        new NodeListDocumentConverter(xstream.getMapper());
    result.registerWithTag(xstream, NODE_LIST_INFO_TAG);
    return result;
  }

  public void setProjectSource(IFile projectSrc) {
    this.projectSrc = projectSrc;
  }

  public void setRelativeSource(File relativeSrc) {
    this.relativeSrc = relativeSrc;
  }

  @Override
  public void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context) {

    NodeListDocument nodeListInfo = (NodeListDocument) source;

    // Save the graph reference.
    marshalObject(nodeListInfo.getReferenceGraph(), writer, context);

    // Save all node references.
    marshalNodes(nodeListInfo.getNodes(), NODE_LIST, writer, context);
  }

  @Override
  public Object unmarshal(
      HierarchicalStreamReader reader, UnmarshallingContext context) {
    // Prepare context for GraphModelReference unmarshalling.
    GraphModelReferenceConverter.setProjectSource(context, projectSrc);
    GraphModelReferenceConverter.setRelativeSource(context, relativeSrc);

    GraphModelReference graphRef =
        unmarshalGraphModelReference(reader, context);

    Collection<GraphNode> nodes = unmarshalNodes(reader, context);

    return new NodeListDocument(graphRef, nodes);
  }

  private Collection<GraphNode> unmarshalNodes(
      HierarchicalStreamReader reader, UnmarshallingContext context) {

    reader.moveDown();
    try {
      if (!isNodeList(reader)) {
        PersistenceLogger.LOG.info(
            "Can't load nodes from section {}", reader.getNodeName());

        return Collections.emptySet();
      }

      return unmarshalGraphNodes(reader, context);
    } finally {
      reader.moveUp();
    }
  }

  private boolean isNodeList(HierarchicalStreamReader reader) {
    String childName = reader.getNodeName();
    if (NODE_LIST.equals(childName)) {
      return true;
    }
    return false;
  }
}
