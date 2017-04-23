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

/**
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public final class RadialTreeLayoutPlan extends TreeLayoutPlan {

  @Override
  protected HierarchicalTreeLayout buildTreeLayout(LayoutContext context) {
    return new RadialTreeLayout(
        context.getGraphModel(),
        context.getEdgeMatcher().getInfo(),
        context.getViewport());
  }

  @Override
  public String buildSummary() {
    return "Radial";
  }
}
