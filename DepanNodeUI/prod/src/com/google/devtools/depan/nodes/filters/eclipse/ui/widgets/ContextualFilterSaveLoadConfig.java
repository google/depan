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

package com.google.devtools.depan.nodes.filters.eclipse.ui.widgets;

import com.google.devtools.depan.nodes.filters.model.ContextualFilter;
import com.google.devtools.depan.nodes.filters.model.ContextualFilterDocument;
import com.google.devtools.depan.nodes.filters.persistence.ContextualFilterResources;
import com.google.devtools.depan.nodes.filters.persistence.ContextualFilterXmlPersist;
import com.google.devtools.depan.persistence.AbstractDocXmlPersist;
import com.google.devtools.depan.platform.eclipse.ui.widgets.SaveLoadConfig;
import com.google.devtools.depan.resources.ResourceContainer;

/**
 * Describe the standard Save/Load parameters for {@link ContextualFilter}s.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class ContextualFilterSaveLoadConfig
    extends SaveLoadConfig<ContextualFilterDocument> {

  public static SaveLoadConfig<ContextualFilterDocument> CONFIG =
      new ContextualFilterSaveLoadConfig();

  @Override
  public ResourceContainer getContainer() {
    return ContextualFilterResources.getContainer();
  }

  @Override
  public AbstractDocXmlPersist<ContextualFilterDocument> getDocXmlPersist(
      boolean readable) {
    return ContextualFilterXmlPersist.build(readable);
  }

  @Override
  public String getSaveLabel() {
    return "Save filter resource...";
  }

  @Override
  public String getLoadLabel() {
    return "Load filter resource...";
  }

  @Override
  public String getBaseName() {
    return ContextualFilterResources.BASE_NAME;
  }

  @Override
  public String getExension() {
    return ContextualFilterResources.EXTENSION;
  }
}
