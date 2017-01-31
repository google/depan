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

package com.google.devtools.depan.eclipse.visualization.ogl;

import java.awt.Color;

/**
 * Provide fill and stroke colors for nodes.
 * 
 * Instances exist for each node.
 * 
 * @author Lee Carver
 */
public interface NodeColorSupplier {
  Color getFillColor();
  Color getStrokeColor();

  public static final NodeColorSupplier DEFAULT =
      new Monochrome(GLConstants.FOREGROUND);

  public static class Monochrome implements NodeColorSupplier {
    private final Color color;

    public Monochrome(Color color) {
      this.color = color;
    }

    @Override
    public Color getFillColor() {
      return color;
    }

    @Override
    public Color getStrokeColor() {
      return color;
    }
  }

  public static class FillStroke implements NodeColorSupplier {
    private final Color fillColor;
    private final Color strokeColor;

    public FillStroke(Color fillColor, Color strokeColor) {
      this.fillColor = fillColor;
      this.strokeColor = strokeColor;
    }

    @Override
    public Color getFillColor() {
      return fillColor;
    }

    @Override
    public Color getStrokeColor() {
      return strokeColor;
    }
  }
}
