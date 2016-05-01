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

package com.google.devtools.depan.view_doc.model;

import java.awt.Color;

/**
 * Handles display properties of edges. The editable properties are edge color,
 * edge line style (solid vs. dashed) and arrow head style
 * (filled/unfilled etc.)
 * 
 * Edge visibility is handled separately.  Display properties describe the
 * attributes of the edge if it were rendered.  The decision to render is
 * managed separately.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public class EdgeDisplayProperty {

  /**
   * The <code>Color</code> of this edge.
   */
  private Color color;

  /**
   * Shows the {@link LineStyle} of this edge.
   */
  private LineStyle lineStyle;

  /**
   * Shows the {@link ArrowheadStyle} of this edge.
   */
  private ArrowheadStyle arrowhead;

  /**
   * Handles different line styles for edges.
   */
  public static enum LineStyle {
    SOLID {
      @Override
      public String getDisplayName() {
        return "Solid";
      }
    },
    DASHED {
      @Override
      public String getDisplayName() {
        return "Dashed";
      }
    };

    /**
     * Returns the default <code>LineStyle</code> type.
     *
     * @return Default <code>LineStyle</code> type.
     */
    public static LineStyle getDefault() {
      return SOLID;
    }

    /**
     * Returns the display name of this <code>LineStyle</code>.
     *
     * @return Display name of this object.
     */
    public abstract String getDisplayName();
  } // End enum LineStyle

  /**
   * Handles different arrow head styles for edges.
   */
  public static enum ArrowheadStyle {
    OPEN {
      @Override
      public String getDisplayName() {
        return "Open";
      }
    },
    TRIANGLE {
      @Override
      public String getDisplayName() {
        return "Triangle";
      }
    },
    FILLED {
      @Override
      public String getDisplayName() {
        return "Filled";
      }
    },
    ARTISTIC {
      @Override
      public String getDisplayName() {
        return "Artistic";
      }
    };

    /**
     * Returns the default {@link ArrowheadStyle} type.
     *
     * @return Default {@link ArrowheadStyle} type.
     */
    public static ArrowheadStyle getDefault() {
      return ARTISTIC;
    }

    /**
     * Returns the display name of this <code>LineStyle</code>.
     *
     * @return Display name of this object.
     */
    public abstract String getDisplayName();
  } // End enum ArrowheadStyle

  /**
   * Creates a default <code>EdgeDisplayProperty</code> object with default line
   * style, default arrow head style and default color.
   */
  public EdgeDisplayProperty() {
    this(null, LineStyle.getDefault(), ArrowheadStyle.getDefault());
  }

  /**
   * Creates an <code>EdgeDisplayProperty</code> object with the given styles.
   *
   * @param lineStyle {@link LineStyle} of the object.
   * @param arrowhead {@link ArrowheadStyle} of the object.
   * @param lineColor <code>Color</code> of the object.
   */
  public EdgeDisplayProperty(
      Color lineColor, LineStyle lineStyle, ArrowheadStyle arrowhead) {
    this.color = lineColor;
    this.lineStyle = lineStyle;
    this.arrowhead = arrowhead;
  }

  /**
   * Create a new edge display property by cloning an existing one.
   * @param current
   */
  public EdgeDisplayProperty(EdgeDisplayProperty current) {
    this(current.getColor(), current.getLineStyle(), current.getArrowhead());
  }

  /**
   * Returns the <code>Color</code> of associated edge.
   *
   * @return <code>Color</code> of this edge. <code>null</code> if color is not
   * set.
   */
  public Color getColor() {
    return color;
  }

  /**
   * Sets the <code>Color</code> of associated edge.
   *
   * @param color New <code>Color</code> of the edge.
   */
  public void setColor(Color color) {
    this.color = color;
  }

  /**
   * Returns the {@link LineStyle} of associated edge.
   *
   * @return {@link LineStyle} of associated edge.
   */
  public LineStyle getLineStyle() {
    return lineStyle;
  }

  /**
   * Sets the {@link LineStyle} of associated edge.
   *
   * @param lineStyle New {@link LineStyle} of associated edge.
   */
  public void setLineStyle(LineStyle lineStyle) {
    this.lineStyle = lineStyle;
  }

  /**
   * Returns the {@link ArrowheadStyle} of associated edge.
   *
   * @return {@link ArrowheadStyle} of associated edge.
   */
  public ArrowheadStyle getArrowhead() {
    return arrowhead;
  }

  /**
   * Sets the {@link ArrowheadStyle} of associated edge.
   *
   * @param arrowhead New {@link ArrowheadStyle} of associated edge.
   */
  public void setArrowhead(ArrowheadStyle arrowhead) {
    this.arrowhead = arrowhead;
  }
}
