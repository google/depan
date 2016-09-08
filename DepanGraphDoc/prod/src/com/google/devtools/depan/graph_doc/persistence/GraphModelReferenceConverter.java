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

import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.graph_doc.model.GraphModelReference;
import com.google.devtools.depan.model.GraphModel;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import java.io.File;

/**
 * Custom {@code XStream} converter for {@code GraphModelReference}s.
 * This serializes only the file name, and reconstitutes the {@code GraphModel}
 * by reading the file contents on loads.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class GraphModelReferenceConverter implements Converter {

  private enum GraphModelSource {
    PROJECT, RELATIVE
  }

  public static final String GRAPH_REF_TAG = "graph-ref";

  public GraphModelReferenceConverter() {
  }

  public static void configXStream(XStream xstream) {
    xstream.aliasType(GRAPH_REF_TAG, GraphModelReference.class);
    xstream.registerConverter(new GraphModelReferenceConverter());
  }

  /**
   * Store the project-based path for the XML source in the supplied
   * {@link UnmarshallingContext}.
   */
  public static void setProjectSource(UnmarshallingContext context, IFile source) {
    context.put(GraphModelSource.PROJECT, source);
  }

  private IFile getProjectSource(UnmarshallingContext context) {
    return (IFile) context.get(GraphModelSource.PROJECT);
  }

  /**
   * Store the file-system path for the XML source in the supplied
   * {@link UnmarshallingContext}.
   */
  public static void setRelativeSource(UnmarshallingContext context, File source) {
    context.put(GraphModelSource.RELATIVE, source);
  }

  private File getRelativeSource(UnmarshallingContext context) {
    return (File) context.get(GraphModelSource.RELATIVE);
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
    context.convertAnother(graphRef.getGraphPath());
  }

  /**
   * {@inheritDoc}
   * <p>
   * Obtain the {@link GraphModelReference}, including loading the saved
   * {@link GraphModel} from a project-based or file-system relative location.
   * 
   * @see EdgeConverter#unmarshal(HierarchicalStreamReader, UnmarshallingContext)
   */
  @Override
  public Object unmarshal(
      HierarchicalStreamReader reader, UnmarshallingContext context) {

    String graphPath = (String) context.convertAnother(null, String.class);
    if (null == graphPath) {
      throw new RuntimeException("Missing location for dependencies");
    }
    if (graphPath.startsWith("/")) {
      return unmarshalProjectGraphFile(graphPath, context);
    }
    return unmarshalRelativeGraphFile(graphPath, context);
  }

  private GraphModelReference unmarshalRelativeGraphFile(
      String graphPath, UnmarshallingContext context) {

    // Try project-based first - synthesize a project-based path relative
    // to the source file.
    IFile projectSrc = getProjectSource(context);
    if (null != projectSrc) {
      IPath namePath = Path.fromPortableString(graphPath);
      IFile graphFile = projectSrc.getParent().getFile(namePath);
      String graphProjectPath = graphFile.getFullPath().toPortableString();
      return unmarshalProjectGraphFile(graphProjectPath, context);
    }

    // Try relative to file-system path for source file.
    File relativeSrc = getRelativeSource(context);
    if (null != relativeSrc) {
      GraphModelXmlPersist persist = GraphModelXmlPersist.build(true);

      File relativeFile = relativeSrc.getParentFile();
      File graphFile = new File(relativeFile, graphPath);
      GraphDocument graphDoc = persist.load(graphFile.toURI());
      return new GraphModelReference(graphPath, graphDoc);
    }

    throw new RuntimeException(
        "Can't locate resource " + graphPath + " for dependency information");
  }

  private GraphModelReference unmarshalProjectGraphFile(
      String graphPath, UnmarshallingContext context) {

    IFile graphRsrc = GraphModelReference.getLocation(graphPath);
    if (null == graphRsrc) {
      throw new RuntimeException(
          "Can't locate resource " + graphPath + " for dependency information");
    }
    if (!(graphRsrc instanceof IFile)) {
      throw new RuntimeException("Resource " + graphPath + " is not a file");
    }

    GraphDocument graph = ResourceCache.fetchGraphDocument((IFile) graphRsrc);
    return new GraphModelReference(graphRsrc, graph);
  }
}
