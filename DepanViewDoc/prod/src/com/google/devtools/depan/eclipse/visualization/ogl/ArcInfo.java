/*
 * Copyright 2018 The Depan Project Authors
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

package com.google.devtools.depan.eclipse.visualization.ogl;

import java.awt.geom.Point2D;

public class ArcInfo {

  public ArcBuilder builder;

  public int headSeg;
  public int tailSeg;

  public Vec2 headVec;
  public Vec2 tailVec;
  private GLEntity headShape;
  private GLEntity tailShape;

  public static ArcInfo buildArcInfo(
      Vec2 headVec, Vec2 tailVec,  GLEntity headShape, GLEntity tailShape) {

    ArcBuilder builder = new ArcBuilder(headVec, tailVec);
    builder.calcSegments();

    ArcInfo result = new ArcInfo();
    result.builder = builder;
    result.headSeg = builder.getHeadSegment(headShape);
    result.tailSeg = builder.getTailSegment(tailShape);

    // match data
    result.headVec = headVec;
    result.tailVec = tailVec;
    result.headShape = headShape;
    result.tailShape = tailShape;
    return result;
  }

  public Point2D getPoint(int segIndex) {
    return builder.getPoint(segIndex);
  }

  public Point2D midpoint() {
    return builder.midpoint(headSeg, tailSeg);
  }

  public boolean isFor(
      Vec2 headVec, Vec2 tailVec, GLEntity headShape, GLEntity tailShape) {
    if (!this.headVec.equals(headVec)) {
      return false;
    }
    if (!this.tailVec.equals(tailVec)) {
      return false;
    }
    if (!this.headShape.equals(headShape)) {
      return false;
    }
    if (!this.tailShape.equals(tailShape)) {
      return false;
    }
    return true;
  }
}
