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

package com.google.devtools.depan.view_doc.layout.hierarchy;

import com.google.devtools.depan.resources.ResourceContainer;
import com.google.devtools.depan.resources.analysis.AnalysisResourceInstaller;
import com.google.devtools.depan.view_doc.layout.model.IntegerOption;
import com.google.devtools.depan.view_doc.layout.persistence.LayoutResources;

/**
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class HierarchicalLayoutResourceInstaller implements
    AnalysisResourceInstaller {

  @Override
  public void installResource(ResourceContainer installRoot) {
    installLayouts(LayoutResources.getContainer(installRoot));
  }

  private static void installLayouts(ResourceContainer layouts) {
    LayoutResources.addLayoutPlan(
        layouts, "Tree Layout", NEW_LINEAR_TREE_LAYOUT_PLAN);
    LayoutResources.addLayoutPlan(
        layouts, "Radial Layout", NEW_RADIAL_TREE_LAYOUT_PLAN);
  }

  public static final TreeLayoutPlan NEW_LINEAR_TREE_LAYOUT_PLAN =
      new LinearTreeLayoutPlan(IntegerOption.UNSET_INT, IntegerOption.UNSET_INT);

  public static final TreeLayoutPlan NEW_RADIAL_TREE_LAYOUT_PLAN =
      new RadialTreeLayoutPlan();
}
