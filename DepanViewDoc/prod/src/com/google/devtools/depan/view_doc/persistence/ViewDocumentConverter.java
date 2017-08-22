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
import com.google.devtools.depan.graph_doc.model.GraphModelReference;
import com.google.devtools.depan.graph_doc.persistence.EdgeConverter;
import com.google.devtools.depan.graph_doc.persistence.EdgeReferenceConverter;
import com.google.devtools.depan.graph_doc.persistence.ReferencedGraphDocumentConverter;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.persistence.PersistenceLogger;
import com.google.devtools.depan.persistence.PropertyDocumentReferenceContext;
import com.google.devtools.depan.resources.ResourceContainer;
import com.google.devtools.depan.view_doc.model.ViewDocument;
import com.google.devtools.depan.view_doc.model.ViewDocument.Components;
import com.google.devtools.depan.view_doc.model.ViewPreferences;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import org.eclipse.core.resources.IProject;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom {@code XStream} converter for {@code ViewDocument}s.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class ViewDocumentConverter
    extends ReferencedGraphDocumentConverter {

  private static final String VIEW_INFO_TAG = "view-info";

  private static final String VIEW_NODES = "view-nodes";

  /** Legacy tag for the view-nodes section. */
  private static final Object SET_LEGACY = "set";

  private IProject project;

  private ResourceContainer root;

  public ViewDocumentConverter(Mapper mapper) {
    super(mapper);
  }

  @Override
  public Class<?> getType() {
    return ViewDocument.class;
  }

  public static ViewDocumentConverter configXStream(XStream xstream) {
    ViewDocumentConverter result =
        new ViewDocumentConverter(xstream.getMapper());
    result.registerWithTag(xstream, VIEW_INFO_TAG);
    return result;
  }

  /**
   * No need to start a node, since the caller ensures we are wrapped correctly.
   */
  @Override
  public void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context) {
    ViewDocument viewInfo = (ViewDocument) source;
    Components components = viewInfo.getComponents();

    // Save the graph reference.
    marshalObject(components.getParentGraph(), writer, context);

    // Save all node references.
    marshalNodes(components.getViewNodes(), VIEW_NODES, writer, context);

    // Save the preferences.
    marshalObject(components.getUserPrefs(), writer, context);
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
    setupReferenceDocuments(context);

    try {
      GraphModelReference viewInfo =
          unmarshalGraphModelReference(reader, context);
      context.put(GraphModel.class, viewInfo.getGraph());

      Collection<GraphNode> viewNodes = unmarshalNodes(reader, context);

      // TODO: Converter for ViewPreferences
      ViewPreferences viewPrefs = (ViewPreferences) unmarshalObject(reader, context);
      viewPrefs.afterUnmarshall();
      viewPrefs.initTransients();

      return new ViewDocument(viewInfo, viewNodes, viewPrefs);
    } finally {
      putGraphDocument(context, prior);
    }
  }

  private void setupReferenceDocuments(UnmarshallingContext context) {
    if (null != project) {
      PropertyDocumentReferenceContext.setProjectSource(context, project);
    }
    if (null != root) {
      PropertyDocumentReferenceContext.setResourceRoot(context, root);
    }
  }

  private Collection<GraphNode> unmarshalNodes(
      HierarchicalStreamReader reader, UnmarshallingContext context) {

    reader.moveDown();
    try {
      if (!isViewNodes(reader)) {
        PersistenceLogger.LOG.info(
            "Can't load nodes from section {}", reader.getNodeName());

        return Collections.emptySet();
      }

      return unmarshalGraphNodes(reader, context);
    } finally {
      reader.moveUp();
    }
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

  public void setProjectSource(IProject project) {
    this.project = project;
  }

  public void setResourceRoot(ResourceContainer root) {
    this.root = root;
  }
}
