/*
 * Copyright 2008 The Depan Project Authors
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

import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Helper class made to create different shapes.
 *
 * @author Yohann Coppel
 */
public final class ShapeFactory {

  /**
   * Class not intended to be instantiated.
   */
  private ShapeFactory() {
  }

  public static GLEntity createEllipse() {
    return new AWTShape(new Ellipse2D.Float(-0.5f, -0.5f, 1f, 1f));
  }

  public static GLEntity createRegularPolygon(int edgeNumber) {
    if (edgeNumber < 3) {
      edgeNumber = 3;
    }
    if (edgeNumber == 4) {
      return new AWTShape(new Rectangle2D.Float(-0.5f, -0.5f, 1f, 1f));
    }
    return new Star(edgeNumber, 0f, 1.0f);
  }

  public static GLEntity createRegularStar(int pointNumber) {
    if (pointNumber < 3) {
      pointNumber = 3;
    }
    return new Star(pointNumber, 0f, 0.5f);
  }

  public static GLEntity createRoundedRectangle() {
    return new AWTShape(
        new RoundRectangle2D.Float(-0.5f, -0.5f, 1f, 1f, 0.2f, 0.2f));
  }
}
