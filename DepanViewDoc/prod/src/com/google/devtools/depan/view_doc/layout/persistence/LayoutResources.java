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

import com.google.devtools.depan.platform.PlatformTools;
import com.google.devtools.depan.resources.ResourceContainer;
import com.google.devtools.depan.resources.analysis.AnalysisResources;
import com.google.devtools.depan.view_doc.layout.grid.GridLayoutPlan;
import com.google.devtools.depan.view_doc.layout.keep.KeepPositionsPlan;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlan;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlanDocument;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * A resource container for {@link LayoutPlanDocument}.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class LayoutResources {

  /** Name of resource tree container for layout plan resources. */
  public static final String LAYOUTS = "layouts";

  /** Base file name for a new layout plan resource. */
  public static final String BASE_NAME = "Layout";

  /** Expected extensions for a layout plan resource. */
  public static final String EXTENSION = "lpxml";

  private LayoutResources() {
    // Prevent instantiation.
  }

  public static void installResources(ResourceContainer root) {
    // Create the resource root
    ResourceContainer layouts = root.addChild(LAYOUTS);
    LayoutResources.addLayoutPlan(
        layouts, "Keep Positions Layout", KeepPositionsPlan.INSTANCE);
    LayoutResources.addLayoutPlan(
        layouts, "Grid Layout", GridLayoutPlan.GRID_LAYOUT_PLAN);
  }

  public static <T extends LayoutPlan> void addLayoutPlan(
      ResourceContainer layouts, String name, T plan) {
    LayoutPlanDocument<T> doc = new LayoutPlanDocument<T>(name, plan);
    layouts.addResource(doc);
  }

  public static List<LayoutPlanDocument<?>> getTopLayouts() {
    List<LayoutPlanDocument<?>> result = Lists.newArrayList();
    for (Object item : LayoutResources.getContainer().getResources()) {
      LayoutPlanDocument<?> layout = getLayoutDocument(item);
      if (null != layout)
        result.add(layout);
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  public static LayoutPlanDocument<? extends LayoutPlan> getLayoutDocument(
      Object item) {
    if (item instanceof LayoutPlanDocument<?>) {
      return (LayoutPlanDocument<? extends LayoutPlan>) item;
    }
    return null;
  }

  public static ResourceContainer getContainer() {
    return getContainer(AnalysisResources.getRoot());
  }

  public static ResourceContainer getContainer(ResourceContainer root) {
    return root.getChild(LAYOUTS);
  }

  public static String getBaseNameExt() {
    return PlatformTools.getBaseNameExt(BASE_NAME, EXTENSION);
  }
}
