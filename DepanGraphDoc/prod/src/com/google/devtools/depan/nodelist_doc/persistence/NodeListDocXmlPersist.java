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
import com.google.devtools.depan.nodelist_doc.model.NodeListDocument;
import com.google.devtools.depan.persistence.AbstractDocXmlPersist;
import com.google.devtools.depan.persistence.ObjectXmlPersist;
import com.google.devtools.depan.persistence.XStreamFactory;

import org.eclipse.core.resources.IFile;

import java.io.File;
import java.net.URI;

/**
 * Provide easy to use load and save methods for {@link NodeListDocument}s.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class NodeListDocXmlPersist
    extends AbstractDocXmlPersist<NodeListDocument> {

  private final static NodeListDocXStreamConfig DOC_CONFIG =
      new NodeListDocXStreamConfig();

  public NodeListDocXmlPersist(ObjectXmlPersist xmlPersist) {
    super(xmlPersist);
  }

  public static NodeListDocXmlPersist buildForSave() {
    ObjectXmlPersist persist = XStreamFactory.build(false, DOC_CONFIG);
    return new NodeListDocXmlPersist(persist);
  }

  /**
   * Since {@link NodeListDocument}s includes a relative
   * {@link GraphModelReference}, the loader needs to know the location
   * of the source document.  When a {@link NodeListDocument} is opened
   * as part of a Depan Workspace, use a project-based source
   * document location.
   */
  public static NodeListDocXmlPersist buildForLoad(IFile projectSrc) {
    NodeListDocXStreamConfig docConfig = new NodeListDocXStreamConfig();
    ObjectXmlPersist persist = XStreamFactory.build(true, docConfig);
    docConfig.setProjectSource(projectSrc);

    NodeListDocXmlPersist result = new NodeListDocXmlPersist(persist);
    return result;
  }

  /**
   * Since {@link NodeListDocument}s includes a relative
   * {@link GraphModelReference}, the loader needs to know the location
   * of the source document.  When a {@link NodeListDocument} is opened
   * outside of a Depan Workspace, use a file-system relative source
   * document location.
   */
  public static NodeListDocXmlPersist buildForLoad(File relativeSrc) {
    NodeListDocXStreamConfig docConfig = new NodeListDocXStreamConfig();
    ObjectXmlPersist persist = XStreamFactory.build(true, docConfig);
    docConfig.setRelativeSource(relativeSrc);

    NodeListDocXmlPersist result = new NodeListDocXmlPersist(persist);
    return result;
  }

  /////////////////////////////////////
  // Hook method implementations for AbstractDocXmlPersist

  @Override
  protected NodeListDocument coerceLoad(Object load) {
    return (NodeListDocument) load;
  }

  @Override
  protected String buildLoadErrorMsg(URI uri) {
    return formatErrorMsg("Unable to load NodeList from {0}", uri);
  }

  @Override
  public String buildSaveErrorMsg(URI uri) {
    return formatErrorMsg("Unable to save NodeList to {0}", uri);
  }
}
