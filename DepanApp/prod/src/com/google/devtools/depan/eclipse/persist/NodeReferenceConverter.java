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

import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * {@code XStream} converter to handle {@code GraphEdge}s.  This converter
 * assumes that the {@code UnmarshallingContext} has an {@code GraphModel}
 * entry that can be retrieved by the {@code GraphModel.class} key.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class NodeReferenceConverter implements Converter {

  public static final String NODE_REF_TAG = "node-ref";

  private final ViewDocumentConverter viewConverter;

  /**
   * @param viewConverter
   */
  public NodeReferenceConverter(ViewDocumentConverter viewConverter) {
    this.viewConverter = viewConverter;
  }

  @Override
  @SuppressWarnings("rawtypes")  // Parent type uses raw type Class
  public boolean canConvert(Class type) {
    return GraphNode.class.isAssignableFrom(type);
  }

  @Override
  public void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context) {
    context.convertAnother(((GraphNode) source).getId());
  }

  @Override
  public Object unmarshal(HierarchicalStreamReader reader,
      UnmarshallingContext context) {
    GraphModel graph = viewConverter.getGraphModel(context);
    String nodeId = reader.getValue();
    GraphNode result = (GraphNode) graph.findNode(nodeId);
    if (null == result) {
      throw new IllegalStateException(
          "Edge reference to undefined node " + nodeId);
    }
    return result;
  }
}
