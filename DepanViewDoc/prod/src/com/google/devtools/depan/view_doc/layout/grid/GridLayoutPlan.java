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

package com.google.devtools.depan.view_doc.layout.grid;

import com.google.devtools.depan.view_doc.layout.LayoutContext;
import com.google.devtools.depan.view_doc.layout.LayoutRunner;
import com.google.devtools.depan.view_doc.layout.model.GenericOption;
import com.google.devtools.depan.view_doc.layout.model.IntegerOption;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlan;
import com.google.devtools.depan.view_doc.layout.model.Orientation;

/**
 * Part of ViewDoc plug to ensure that one layout is always available.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class GridLayoutPlan implements LayoutPlan {

  public static final GridLayoutPlan GRID_LAYOUT_PLAN =
      new GridLayoutPlan(
          new GenericOption<Orientation>(Orientation.LANDSCAPE),
          IntegerOption.UNSET_INT,
          IntegerOption.UNSET_INT,
          IntegerOption.UNSET_INT,
          IntegerOption.UNSET_INT);

  private GenericOption<Orientation> orientation;
  private IntegerOption rows;
  private IntegerOption columns;
  private IntegerOption horizontalSpace;
  private IntegerOption verticalSpace;

  public GridLayoutPlan(
      GenericOption<Orientation> orientation,
      IntegerOption columns,
      IntegerOption rows,
      IntegerOption horizontalSpace,
      IntegerOption verticalSpace) {
    this.orientation = orientation;
    this.columns = columns;
    this.rows = rows;
    this.horizontalSpace = horizontalSpace;
    this.verticalSpace = verticalSpace;
  }

  @Override
  public LayoutRunner buildLayout(LayoutContext context) {
    GridLayoutRunner result = build(context);
    if (horizontalSpace.isSet()) {
      result.setHorizontalSpace(horizontalSpace.getValue());
    }
    if (verticalSpace.isSet()) {
      result.setHorizontalSpace(verticalSpace.getValue());
    }
    return result;
  }

  private GridLayoutRunner build(LayoutContext context) {
    if (orientation.isSet()) {
      switch (orientation.getValue()) {
      case LANDSCAPE:
        return GridLayoutRunner.buildLandscape(context);
      case PORTRAIT:
        return GridLayoutRunner.buildPortrait(context);
      }
    }
    if (rows.isSet()) {
      return GridLayoutRunner.buildByRows(context, rows.getValue());
    }
    if (columns.isSet()) {
      return GridLayoutRunner.buildByColumns(context, columns.getValue());
    }
    return new GridLayoutRunner(context);
  }

  @Override
  public String buildSummary() {
    return "Grid layout";
  }
}
