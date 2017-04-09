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

import com.google.devtools.depan.graph_doc.GraphDocResources;
import com.google.devtools.depan.graph_doc.persistence.GraphModelReferenceConverter;
import com.google.devtools.depan.graph_doc.persistence.NodeReferenceConverter;
import com.google.devtools.depan.nodelist_doc.model.NodeListDocument;
import com.google.devtools.depan.persistence.plugins.XStreamConfig;

import com.thoughtworks.xstream.XStream;

import org.eclipse.core.resources.IFile;
import org.osgi.framework.Bundle;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

/**
 * Prepare an {@link XStream} for serializing an {@link NodeListDocument}.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class NodeListDocXStreamConfig implements XStreamConfig {

  public static final String NODE_LIST_INFO_TAG = "node-list-info";

  private NodeListDocumentConverter converter;

  @Override
  public void config(XStream xstream) {
    xstream.setMode(XStream.NO_REFERENCES);

    converter = NodeListDocumentConverter.configXStream(xstream);
    NodeReferenceConverter.configXStream(xstream, converter);

    GraphModelReferenceConverter.configXStream(xstream);
  }

  @Override
  public Collection<? extends Bundle> getDocumentBundles() {
    return Collections.singletonList(GraphDocResources.BUNDLE);
  }

  /**
   * This method should not be called until after {@link #config(XStream)}
   * in invoked.  In normal usage, this occurs internally as part of
   * the {@code XStreamFactory.build()}. The practical consequence is that
   * any call to {@link #setProjectSource(IFile)} should come after the call
   * to {@code XStreamFactory.build()}.
   */
  public void setProjectSource(IFile projectSrc) {
    converter.setProjectSource(projectSrc);
  }

  /**
   * This method should not be called until after {@link #config(XStream)}
   * in invoked.  In normal usage, this occurs internally as part of
   * the {@code XStreamFactory.build()}. The practical consequence is that
   * any call to {@link #setRelativeSource(File)} should come after the call
   * to {@code XStreamFactory.build()}.
   */
  public void setRelativeSource(File relativeSrc) {
    converter.setRelativeSource(relativeSrc);
  }
}
