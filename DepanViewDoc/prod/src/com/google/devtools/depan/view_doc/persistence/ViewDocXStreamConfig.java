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

package com.google.devtools.depan.view_doc.persistence;

import com.google.devtools.depan.graph_doc.persistence.EdgeReferenceConverter;
import com.google.devtools.depan.graph_doc.persistence.GraphModelReferenceConverter;
import com.google.devtools.depan.graph_doc.persistence.NodeReferenceConverter;
import com.google.devtools.depan.persistence.plugins.XStreamConfig;
import com.google.devtools.depan.resources.ResourceContainer;
import com.google.devtools.depan.view_doc.eclipse.ViewDocResources;
import com.google.devtools.depan.view_doc.model.ViewDocument;
import com.google.devtools.depan.view_doc.model.ViewPreferences;

import com.thoughtworks.xstream.XStream;

import org.eclipse.core.resources.IProject;
import org.osgi.framework.Bundle;

import java.util.Collection;
import java.util.Collections;

/**
 * Prepare an {@link XStream} for serializing an {@link ViewDocument}.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class ViewDocXStreamConfig implements XStreamConfig {

  private static final String VIEW_PREFS = "view-prefs";

  private ViewDocumentConverter converter;

  @Override
  public void config(XStream xstream) {
    xstream.setMode(XStream.NO_REFERENCES);

    xstream.alias(VIEW_PREFS, ViewPreferences.class);

    converter = ViewDocumentConverter.configXStream(xstream);
    EdgeReferenceConverter.configXStream(xstream, converter);
    NodeReferenceConverter.configXStream(xstream, converter);

    CameraPosConverter.configXStream(xstream);
    CameraDirConverter.configXStream(xstream);
    GraphModelReferenceConverter.configXStream(xstream);
    Point2DConverter.configXStream(xstream);
    ViewExtensionConverter.configXStream(xstream);
  }

  @Override
  public Collection<? extends Bundle> getDocumentBundles() {
    return Collections.singletonList(ViewDocResources.BUNDLE);
  }

  public void setProjectSource(IProject project) {
    converter.setProjectSource(project);
  }

  public void setResourceRoot(ResourceContainer root) {
    converter.setResourceRoot(root);
  }
}
