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

package com.google.devtools.depan.nodes.filters.persistence;

import com.google.devtools.depan.eclipse.ui.nodes.NodesUIResources;
import com.google.devtools.depan.nodes.filters.model.ContextualFilterDocument;
import com.google.devtools.depan.persistence.plugins.XStreamConfig;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;

import com.thoughtworks.xstream.XStream;

import org.osgi.framework.Bundle;

import java.util.Collection;
import java.util.Collections;

/**
 * Prepare an {@link XStream} for serializing an {@link RelationSetDescriptor}.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class ContextualFilterXStreamConfig implements XStreamConfig {

  public static final String CONTEXTUAL_FILTER_DOCUMENT_INFO_TAG =
      "filter-info";

  @Override
  public void config(XStream xstream) {
    xstream.setMode(XStream.NO_REFERENCES);
    xstream.alias(
        CONTEXTUAL_FILTER_DOCUMENT_INFO_TAG, ContextualFilterDocument.class);
  }

  @Override
  public Collection<? extends Bundle> getDocumentBundles() {
    return Collections.singletonList(NodesUIResources.BUNDLE);
  }
}
