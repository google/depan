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

package com.google.devtools.depan.view_doc.persistence;

import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.graph_doc.persistence.EdgeConverter;
import com.google.devtools.depan.platform.ResourceCache;
import com.google.devtools.depan.view_doc.model.GraphModelReference;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * Custom {@code XStream} converter for {@code GraphModelReference}s.
 * This serializes only the file name, and reconstitutes the {@code GraphModel}
 * by reading the file contents on loads.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class GraphModelReferenceConverter implements Converter {

  public static final String GRAPH_REF_TAG = "graph-ref";

  public GraphModelReferenceConverter() {
  }

  public static void configXStream(XStream xstream) {
    xstream.aliasType(GRAPH_REF_TAG, GraphModelReference.class);
    xstream.registerConverter(new GraphModelReferenceConverter());
  }

  @Override
  @SuppressWarnings("rawtypes")  // Parent type uses raw type Class
  public boolean canConvert(Class type) {
    return GraphModelReference.class.equals(type);
  }

  /**
   * Simply output the workspace relative name for the referenced GraphModel.
   */
  @Override
  public void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context) {
    GraphModelReference graphRef = (GraphModelReference) source;
    context.convertAnother(graphRef.getLocation().getFullPath().toString());
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation reads in the workspace-relative name of the dependency
   * graph file, and then tries to load that file as the graph model.
   * 
   * @see EdgeConverter#unmarshal(HierarchicalStreamReader, UnmarshallingContext)
   */
  @Override
  public Object unmarshal(
      HierarchicalStreamReader reader, UnmarshallingContext context) {

    IFile graphFile = unmarshallGraphLocation(context);

    GraphDocument graph = ResourceCache.fetchGraphDocument(graphFile);
    return new GraphModelReference(graphFile, graph);
  }

  /**
   * @param graphPath
   * @return
   */
  private IFile unmarshallGraphLocation(UnmarshallingContext context) {
    String graphPath = (String) context.convertAnother(null, String.class);
    if (null == graphPath) {
      throw new RuntimeException("Missing location for dependencies");
    }

    IResource graphRsrc = ResourcesPlugin.getWorkspace().getRoot()
        .findMember(graphPath);
    if (null == graphRsrc) {
      throw new RuntimeException(
          "Can't locate resource " + graphPath + " for dependency information");
    }
    if (!(graphRsrc instanceof IFile)) {
      throw new RuntimeException("Resource " + graphPath + " is not a file");
    }

    return (IFile) graphRsrc;
  }
}
