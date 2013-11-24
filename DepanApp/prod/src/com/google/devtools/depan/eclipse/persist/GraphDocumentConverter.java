/*
 * Copyright 2013 Pnambic Computing
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

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.google.common.collect.Lists;
import com.google.devtools.depan.eclipse.editors.GraphDocument;
import com.google.devtools.depan.eclipse.plugins.SourcePlugin;
import com.google.devtools.depan.model.GraphModel;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Custom {@code XStream} converter for {@code GraphModel}s.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class GraphDocumentConverter implements Converter {

  public static final String GRAPH_INFO_TAG = "graph-info";

  private static final String GRAPH_ANALYZERS_FLD = "graphAnalyzers";

  private static final String GRAPH_FLD = "graph";

  private static final Logger logger =
      Logger.getLogger(GraphDocumentConverter.class.getName());

  private final Mapper mapper;

  public static void register(XStream xstream) {
    xstream.alias(GRAPH_INFO_TAG, GraphDocument.class);
    xstream.registerConverter(new GraphDocumentConverter(xstream.getMapper()));
  }

  public GraphDocumentConverter(Mapper mapper) {
    this.mapper = mapper;
  }

  @Override
  @SuppressWarnings("rawtypes")  // Parent type uses raw type Class
  public boolean canConvert(Class type) {
    return GraphDocument.class.equals(type);
  }

  /**
   * No need to start a node, since the caller ensures we are wrapped correctly.
   */
  @Override
  public void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context) {
    GraphDocument doc = (GraphDocument) source;

    // Save the analyzer list.
    writer.startNode(GRAPH_ANALYZERS_FLD);
    for (SourcePlugin analyzer : doc.getAnalyzers()) {
      marshalObject(analyzer, writer, context);
    }
    writer.endNode();

    // Save the graph.
    writer.startNode(GRAPH_FLD);
    context.convertAnother(doc.getGraph());
    writer.endNode();
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
   * Since we need the member properties before we construct a GraphDocument,
   * unmarshall these properties and use them in the constructor.
   */
  @Override
  public Object unmarshal(
      HierarchicalStreamReader reader, UnmarshallingContext context) {

    try {
      List<SourcePlugin> analyzers = null;
      GraphModel graph = null;

      while (reader.hasMoreChildren()) {
        reader.moveDown();
        String childName = reader.getNodeName();
        if (GRAPH_ANALYZERS_FLD.equals(childName)) {
          analyzers = unmarshallAnalyzers(reader, context);
        } else if (GRAPH_FLD.equals(childName)) {
          graph = (GraphModel) context.convertAnother(null, GraphModel.class);
        } else {
          logger.info("Skipped GraphDocument element with tag " + childName);
        }

        reader.moveUp();
      }

      if (null == analyzers) {
        analyzers = Collections.emptyList();
        logger.info("Empty analyzer list for GraphDocument");
      }
      if (null == graph) {
        graph = new GraphModel();
        logger.info("Empty graph for GraphDocument");
      }

      return new GraphDocument(graph, analyzers);
    } catch (RuntimeException err) {
      // TODO Auto-generated catch block
      err.printStackTrace();
      throw err;
    }
  }

  private List<SourcePlugin> unmarshallAnalyzers(
      HierarchicalStreamReader reader, UnmarshallingContext context) {
    List<SourcePlugin> result = Lists.newArrayList();
    while (reader.hasMoreChildren()) {
      reader.moveDown();
      String childName = reader.getNodeName();
      Class<?> childClass = mapper.realClass(childName);

      if (SourcePlugin.class.isAssignableFrom(childClass)) {
        SourcePlugin analyzer = (SourcePlugin) context.convertAnother(null, childClass);
        result.add(analyzer);
      } else {
        logger.info("Skipped analyzer with tag " + childName);
      }

      reader.moveUp();
    }

    return result;
  }
}