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

import com.google.devtools.depan.eclipse.editors.GraphModelReference;
import com.google.devtools.depan.eclipse.editors.ViewDocument;
import com.google.devtools.depan.eclipse.editors.ViewDocument.Components;
import com.google.devtools.depan.eclipse.editors.ViewPreferences;
import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;

import com.google.common.collect.Sets;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Custom {@code XStream} converter for {@code ViewDocument}s.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class ViewDocumentConverter implements Converter {

  public static final String VIEW_INFO_TAG = "view-info";

  private static final Logger logger =
      Logger.getLogger(ViewDocumentConverter.class.getName());

  private static final String VIEW_NODES = "view-nodes";

  /** Legacy tag for the view-nodes section. */
  private static final Object SET_LEGACY = "set";

  private final Mapper mapper;

  public ViewDocumentConverter(Mapper mapper) {
    this.mapper = mapper;
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

  @Override
  @SuppressWarnings("rawtypes")  // Parent type uses raw type Class
  public boolean canConvert(Class type) {
    return ViewDocument.class.equals(type);
  }

  /**
   * No need to start a node, since the caller ensures we are wrapped correctly.
   */
  @Override
  public void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context) {
    ViewDocument viewInfo = (ViewDocument) source;
    Components components = viewInfo.getComponents();

    // Save all node references.
    marshalObject(components.getParentGraph(), writer, context);

    // Save all node references.
    marshallNodes(components.getViewNodes(), VIEW_NODES, writer, context);

    // Save the preferences.
    marshalObject(components.getUserPrefs(), writer, context);
  }

  private void marshalObject(Object item,
      HierarchicalStreamWriter writer, MarshallingContext context) {
    String nodeLabel = mapper.serializedClass(item.getClass());
    writer.startNode(nodeLabel);
    context.convertAnother(item);
    writer.endNode();
  }

  private void marshallNodes(Collection<GraphNode> nodes, String nodeLabel,
      HierarchicalStreamWriter writer, MarshallingContext context) {
    // Save all nodes.
    writer.startNode(nodeLabel);
    for (GraphNode node : nodes) {
      marshalObject(node, writer, context);
    }
    writer.endNode();
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation temporarily injects the current {@code GraphModel}
   * instance into the {@code UnmarshallingContext} with the key
   * {@code GraphModel.class}.  This allows the {@link EdgeReferenceConverter} to
   * translate node ids directly into node references.
   * 
   * @see EdgeConverter#unmarshal(HierarchicalStreamReader, UnmarshallingContext)
   */
  @Override
  public Object unmarshal(
      HierarchicalStreamReader reader, UnmarshallingContext context) {
    // There should not be two graphs in the same serialization,
    // but just in case ....
    GraphDocument prior = getGraphDocument(context);

    try {
      GraphModelReference viewInfo =
          (GraphModelReference) unmarshalObject(reader, context);
      putGraphDocument(context, viewInfo.getGraph());
      context.put(GraphModel.class, viewInfo.getGraph());

      Collection<GraphNode> viewNodes = loadGraphNodes(reader, context);

      // TODO: Converter for ViewPreferences
      ViewPreferences viewPrefs = (ViewPreferences) unmarshalObject(reader, context);
      viewPrefs.initTransients();
      viewPrefs.afterUnmarshall();

      return new ViewDocument(viewInfo, viewNodes, viewPrefs);
    } finally {
      putGraphDocument(context, prior);
    }
  }

  /**
   * Isolate unchecked conversion.
   */
  private Collection<GraphNode> loadGraphNodes(
      HierarchicalStreamReader reader, UnmarshallingContext context) {

    reader.moveDown();
    if (!isViewNodes(reader)) {
      reader.moveUp();
      logger.info("Can't load nodes from section " + reader.getNodeName());

      return Collections.emptySet();
    }

    Set<GraphNode> result = Sets.newHashSet();

    while (reader.hasMoreChildren()) {
      reader.moveDown();
      String nodeName = reader.getNodeName();
      Class<?> childClass = mapper.realClass(nodeName);
      GraphNode node = (GraphNode) context.convertAnother(null, childClass);
      result.add(node);
      reader.moveUp();
    }
    reader.moveUp();

    return result;
  }

  private boolean isViewNodes(HierarchicalStreamReader reader) {
    String childName = reader.getNodeName();
    if (VIEW_NODES.equals(childName)) {
      return true;
    }
    if (SET_LEGACY.equals(childName)) {
      return true;
    }
    return false;
  }

  private Object unmarshalObject(
      HierarchicalStreamReader reader, UnmarshallingContext context) {
    reader.moveDown();
    String childName = reader.getNodeName();
    Class<?> childClass = mapper.realClass(childName);

    Object result = context.convertAnother(null, childClass);

    reader.moveUp();
    return result;
  }
}
