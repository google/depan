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

package com.google.devtools.depan.relations.eclipse.ui.widgets;

import com.google.devtools.depan.persistence.AbstractDocXmlPersist;
import com.google.devtools.depan.platform.eclipse.ui.widgets.SaveLoadConfig;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.persistence.RelationSetDescriptorXmlPersist;
import com.google.devtools.depan.relations.persistence.RelationSetResources;
import com.google.devtools.depan.resources.ResourceContainer;

/**
 * @author <a href='mailto:leeca@pnambic.com'>Lee Carver</a>
 */
class RelationSetSaveLoadConfig
    extends SaveLoadConfig<RelationSetDescriptor> {

  @Override
  public ResourceContainer getContainer() {
    return RelationSetResources.getContainer();
  }

  @Override
  public AbstractDocXmlPersist<RelationSetDescriptor> getDocXmlPersist(
      boolean readable) {
    return RelationSetDescriptorXmlPersist.build(readable);
  }

  @Override
  public String getSaveLabel() {
    return "Save as RelationSet...";
  }

  @Override
  public String getLoadLabel() {
    return "Load from RelationSet...";
  }

  @Override
  public String getBaseName() {
    return RelationSetResources.BASE_NAME;
  }

  @Override
  public String getExension() {
    return RelationSetResources.EXTENSION;
  }
}