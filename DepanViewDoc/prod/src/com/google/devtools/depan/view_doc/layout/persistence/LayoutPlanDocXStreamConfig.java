/*
 * Copyright 2017 The Depan Project Authors
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

package com.google.devtools.depan.view_doc.layout.persistence;

import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.persistence.plugins.XStreamConfig;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlanDocument;

import com.thoughtworks.xstream.XStream;

import org.osgi.framework.Bundle;

import java.util.Collection;
import java.util.Collections;

/**
 * Prepare an {@link XStream} for serializing an
 * {@link GraphEdgeMatcherDescriptor}.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class LayoutPlanDocXStreamConfig implements XStreamConfig {

  public static final String LAYOUT_PLAN_INFO_TAG = "layout-plan-info";

  @Override
  public void config(XStream xstream) {
    xstream.setMode(XStream.NO_REFERENCES);
    xstream.alias(LAYOUT_PLAN_INFO_TAG, LayoutPlanDocument.class);
  }

  @Override
  public Collection<? extends Bundle> getDocumentBundles() {
    // Bundle for document module is provided by
    // contribution to XStreamConfigRegistry.
    return Collections.emptyList();
  }
}
