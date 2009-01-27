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


import com.google.devtools.depan.graph.basic.BasicGraph;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.core.DefaultConverterLookup;
import com.thoughtworks.xstream.core.TreeMarshallingStrategy;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;

import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.Iterator;

/**
 * Class in charge of loading and saving an instance of GraphModel.
 *
 * Higher level components handle any caching.
 *
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class XmlPersistentGraph extends XmlPersistentObject<GraphModel> {
  /**
   * Adapts a GraphModel to XStream's DataHolder interface.
   */
  private static class GraphModelDataHolder implements DataHolder {
    private final GraphModel graph;

    private GraphModelDataHolder(GraphModel graph) {
      this.graph = graph;
    }

    public Object get(Object key) {
      return graph.findNode((String) key);
    }

    @SuppressWarnings("unchecked")
    public Iterator keys() {
      return graph.getNodesMap().keySet().iterator();
    }

    public void put(Object key, Object value) {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * Ensure that GraphModel unmarshalling uses the graph's Data
   * Holder as a lookup resource.
   */
  private static class GraphModelTreeMarshallingStrategy extends
      TreeMarshallingStrategy {
    private final DataHolder nodeMap;

    private GraphModelTreeMarshallingStrategy(DataHolder nodeMap) {
      this.nodeMap = nodeMap;
    }

    @Override
    public Object unmarshal(Object root, HierarchicalStreamReader reader,
        DataHolder dataHolder, ConverterLookup converterLookup, Mapper mapper) {
      return super.unmarshal(root, reader, nodeMap, converterLookup, mapper);
    }

    @Override
    public Object unmarshal(Object root, HierarchicalStreamReader reader,
        DataHolder dataHolder, DefaultConverterLookup converterLookup,
        ClassMapper classMapper) {
      return super.unmarshal(root, reader, nodeMap, converterLookup, classMapper);
    }
  }

  /**
   * Load the graph at the given URI. If the graph has already been loaded,
   * don't try to load it again, just return the previous instance.
   *
   * @param uri
   * @return a PersistentGraph instance containing the graph, or null if loading
   *         failed
   */
  @Override
  public GraphModel load(URI uri) {

    try {
      InputStreamReader src = new FileReader(new File(uri));
      return loadGraphModel(src);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    } catch (Throwable errAny) {
      errAny.printStackTrace();
      return null;
    }
  }

  /**
   * Load the graph from the given input source.
   *
   * @return a PersistentGraph instance containing the graph, or null if loading
   *         failed
   * @throws IOException
   * @throws ClassNotFoundException
   */
  public GraphModel loadGraphModel(InputStreamReader source)
      throws IOException, ClassNotFoundException {

    final GraphModel result = new GraphModel();
    setXstreamMarshallContext(result);

    ObjectInputStream objs = xstream.createObjectInputStream(source);

    // Not quite VarZs, but useful for debugging.
    int elementCnt = 0;
    int edgeCnt = 0;
    int nodeCnt = 0;

    while (true) {
      Object element;
      try {
        element = objs.readObject();
        elementCnt++;
      } catch (EOFException eof) {
        break;
      }

      if (element instanceof GraphEdge) {
        result.addEdge((GraphEdge) element);
        edgeCnt++;
      } else if (element instanceof GraphNode) {
        addNode(result, element);
        nodeCnt++;
      }
      // Ignore other objects in stream
    }

    return result;
  }

  /**
   * Add the node to the graph if it is not present.  Report and ignore
   * and duplicate nodes that occur.
   * @param result
   * @param element
   */
  private void addNode(final GraphModel result, Object element) {
    try {
      result.addNode((GraphNode) element);
    } catch (BasicGraph.DuplicateNodeException errNode) {
      System.err.println(errNode.getMessage());
    }
  }

  private void setXstreamMarshallContext(GraphModel graph) {
    DataHolder nodeMap = new GraphModelDataHolder(graph);

    xstream.setMarshallingStrategy(
        new GraphModelTreeMarshallingStrategy(nodeMap));
  }

  /**
   * Save the wrapped {@link GraphModel} into a file at the given URI.
   *
   * @param uri where to save the {@link GraphModel}.
   */
  @Override
  public void save(URI uri, GraphModel graph) throws IOException {
    OutputStreamWriter source = null;
    ObjectOutputStream objs = null;

    try {
      source = new FileWriter(new File(uri));
      objs = xstream.createObjectOutputStream(source, "graph-model");

      saveGraphModel(objs, graph);

    } catch (IOException e) {
      e.printStackTrace();

    } finally {
      if (null != objs) {
        objs.close();
      }
      if (null != source) {
        source.close();
      }
    }
  }

  /**
   * Writes the given {@link GraphModel} to the given stream.
   *
   * @param objs Output stream where this graph model will be written.
   * @param graph Graph that will be saved to persistent storage.
   * @throws IOException is writing to object stream fails.
   */
  public void saveGraphModel(
      ObjectOutputStream objs, GraphModel graph) throws IOException {

    // Save all nodes.
    for (Object node : graph.getNodes()) {
      objs.writeObject(node);
    }

    // Save all edges.
    for (GraphEdge edge : graph.getEdges()) {
      objs.writeObject(edge);
    }
  }
}
