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

package com.google.devtools.depan.view_doc.layout.eclipse.ui.widgets;

import com.google.devtools.depan.persistence.AbstractDocXmlPersist;
import com.google.devtools.depan.platform.eclipse.ui.widgets.SaveLoadConfig;
import com.google.devtools.depan.resources.ResourceContainer;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlan;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlanDocument;
import com.google.devtools.depan.view_doc.layout.persistence.LayoutPlanDocXmlPersist;
import com.google.devtools.depan.view_doc.layout.persistence.LayoutResources;

/**
 * Describe the standard Save/Load parameters for {@link LayoutPlanDocument}s.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class LayoutPlanSaveLoadConfig
    extends SaveLoadConfig<LayoutPlanDocument<LayoutPlan>> {

  public static SaveLoadConfig<LayoutPlanDocument<LayoutPlan>> CONFIG =
      new LayoutPlanSaveLoadConfig();

  @Override
  public ResourceContainer getContainer() {
    return LayoutResources.getContainer();
  }

  @Override
  public AbstractDocXmlPersist<LayoutPlanDocument<LayoutPlan>> getDocXmlPersist(
      boolean readable) {
    return LayoutPlanDocXmlPersist.build(readable);
  }

  @Override
  public String getSaveLabel() {
    return "Save layout plan resource...";
  }

  @Override
  public String getLoadLabel() {
    return "Load layout plan resource...";
  }

  @Override
  public String getBaseName() {
    return LayoutResources.BASE_NAME;
  }

  @Override
  public String getExension() {
    return LayoutResources.EXTENSION;
  }
}
