/*
 * Copyright 2017 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.devtools.depan.matchers.eclipse.ui.widgets;

import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.matchers.persistence.EdgeMatcherDocXmlPersist;
import com.google.devtools.depan.matchers.persistence.GraphEdgeMatcherResources;
import com.google.devtools.depan.persistence.AbstractDocXmlPersist;
import com.google.devtools.depan.platform.eclipse.ui.widgets.SaveLoadConfig;
import com.google.devtools.depan.resources.ResourceContainer;

/**
 * Control for saving and loading {@link GraphEdgeMatcherDescriptor}s
 * (a.k.a. {@code EdgeMatcherDocument}).  The class defines the type-specific
 * strings and factories for the supplied generic type
 * {@link GraphEdgeMatcherDescriptor}.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class EdgeMatcherSaveLoadConfig
    extends SaveLoadConfig<GraphEdgeMatcherDescriptor> {

  // Only need one.
  public static SaveLoadConfig<GraphEdgeMatcherDescriptor> CONFIG =
      new EdgeMatcherSaveLoadConfig();

  @Override
  public ResourceContainer getContainer() {
    return GraphEdgeMatcherResources.getContainer();
  }

  @Override
  public AbstractDocXmlPersist<GraphEdgeMatcherDescriptor> getDocXmlPersist(
      boolean readable) {
    return EdgeMatcherDocXmlPersist.build(readable);
  }

  @Override
  public String getSaveLabel() {
    return "Save as EdgeMatcher...";
  }

  @Override
  public String getLoadLabel() {
    return "Load from EdgeMatcher...";
  }

  @Override
  public String getBaseName() {
    return GraphEdgeMatcherResources.BASE_NAME;
  }

  @Override
  public String getExension() {
    return GraphEdgeMatcherResources.EXTENSION;
  }
}