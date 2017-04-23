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

import com.google.devtools.depan.view_doc.layout.LayoutContext;
import com.google.devtools.depan.view_doc.layout.model.IntegerOption;
import com.google.devtools.depan.view_doc.layout.model.Options;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public final class LinearTreeLayoutPlan extends TreeLayoutPlan {

  private IntegerOption horizontalSpace;
  private IntegerOption verticalSpace;

  public LinearTreeLayoutPlan(
      IntegerOption horizontalSpace, IntegerOption verticalSpace) {
    this.horizontalSpace = horizontalSpace;
    this.verticalSpace = verticalSpace;
  }

  @Override
  protected HierarchicalTreeLayout buildTreeLayout(LayoutContext context) {
    int horzSpac = Options.getValue(horizontalSpace, 12);
    int vertSpac = Options.getValue(verticalSpace, 1);
    return new LinearTreeLayout(
        context.getGraphModel(),
        context.getEdgeMatcher().getInfo(),
        context.getViewport(), horzSpac, vertSpac);
  }

  @Override
  public String buildSummary() {
    return "Tree";
  }
}