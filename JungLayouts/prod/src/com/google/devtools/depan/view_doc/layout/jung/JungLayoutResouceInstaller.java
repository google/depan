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

package com.google.devtools.depan.view_doc.layout.jung;

import com.google.devtools.depan.resources.ResourceContainer;
import com.google.devtools.depan.resources.analysis.AnalysisResourceInstaller;
import com.google.devtools.depan.view_doc.layout.persistence.LayoutResources;

/**
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class JungLayoutResouceInstaller implements
    AnalysisResourceInstaller {

  @Override
  public void installResource(ResourceContainer installRoot) {
    installLayouts(LayoutResources.getContainer(installRoot));
  }

  private static void installLayouts(ResourceContainer layouts) {
    LayoutResources.addLayoutPlan(
        layouts, "FR Layout", JungLayoutPlan.BASE_FR_LAYOUT);
    LayoutResources.addLayoutPlan(
        layouts, "FR2 Layout", JungLayoutPlan.BASE_FR2_LAYOUT);
    LayoutResources.addLayoutPlan(
        layouts, "Isometric Layout", JungLayoutPlan.BASE_ISOM_LAYOUT);
    LayoutResources.addLayoutPlan(
        layouts, "KK Layout", JungLayoutPlan.BASE_KK_LAYOUT);
    LayoutResources.addLayoutPlan(
        layouts, "Spring Layout", JungLayoutPlan.BASE_SPRING_LAYOUT);
    LayoutResources.addLayoutPlan(
        layouts, "Spring 2 Layout", JungLayoutPlan.BASE_SPRING2_LAYOUT);
  }
}
