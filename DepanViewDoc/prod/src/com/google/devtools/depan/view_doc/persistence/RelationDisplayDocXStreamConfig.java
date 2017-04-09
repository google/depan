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

import com.google.devtools.depan.persistence.plugins.XStreamConfig;
import com.google.devtools.depan.view_doc.eclipse.ViewDocResources;
import com.google.devtools.depan.view_doc.model.RelationDisplayDocument;

import com.thoughtworks.xstream.XStream;

import org.osgi.framework.Bundle;

import java.util.Collection;
import java.util.Collections;

/**
 * Prepare an {@link XStream} for serializing an {@link RelationDisplayDocument}.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class RelationDisplayDocXStreamConfig implements XStreamConfig {

  public static final String RELATION_DISPLAY_INFO_TAG = "rel-display-info";

  @Override
  public void config(XStream xstream) {
    xstream.setMode(XStream.NO_REFERENCES);
    xstream.alias(RELATION_DISPLAY_INFO_TAG, RelationDisplayDocument.class);
  }

  @Override
  public Collection<? extends Bundle> getDocumentBundles() {
    // RelationDisplayDocument is an implicit types derived from ViewDocument.
    return Collections.singletonList(ViewDocResources.BUNDLE);
  }
}
