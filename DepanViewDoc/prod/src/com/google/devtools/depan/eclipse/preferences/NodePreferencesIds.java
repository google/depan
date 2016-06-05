/*
 * Copyright 2007 The Depan Project Authors
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

package com.google.devtools.depan.eclipse.preferences;

import com.google.devtools.depan.eclipse.ui.nodes.plugins.NodeElementPluginRegistry;
import com.google.devtools.depan.eclipse.visualization.ogl.ColorMap;
import com.google.devtools.depan.eclipse.visualization.ogl.GLEntity;
import com.google.devtools.depan.eclipse.visualization.ogl.ShapeFactory;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view_doc.eclipse.ui.plugins.JoglPluginRegistry;
import com.google.devtools.depan.view_doc.model.NodeDisplayProperty;

import java.awt.Color;

/**
 * Preferences for node rendering.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class NodePreferencesIds {

  private NodePreferencesIds() {
    // Prevent instantiation.
  }

  public static final String NODE_PREFIX =
      PreferencesIds.VIEW_PREFIX + "node_";

  // enum values
  /**
   * Node color mode. values are the enum {@link NodeColors}.
   * @see NodeColors
   */
  public static final String NODE_COLOR = NODE_PREFIX + "color";

  /**
   * Node shape drawing mode. values are the enum {@link NodeShape}.
   * @see NodeShape
   */
  public static final String NODE_SHAPE = NODE_PREFIX + "shape";

  /**
   * Node size rendering mode. values are the enum {@link NodeSize}
   * @see NodeSize
   */
  public static final String NODE_SIZE = NODE_PREFIX + "size";

  /**
   * Different modes for rendering node colors.
   *
   * @author ycoppel@google.com (Yohann Coppel)
   *
   */
  public enum NodeColors {
    /**
     * Color is given with the degree of the node.
     * Degree is the number of incoming plus outgoing connections.
     * @see edu.uci.ics.jung.graph.Hypergraph#degree(Object)
     */
    DEGREE {
      @Override
      public <E> Color getColor(GraphNode node, ColorMap cm,
          float voltagePercent, float degreePercent) {
        return cm.getColor(degreePercent);
      }
    },

    /**
     * Color is given by the role of the node (e.g. class, method, package...)
     */
    ROLE {
      @Override
      public <E> Color getColor(GraphNode node, ColorMap cm,
          float voltagePercent, float degreePercent) {
        return NodeColors.matchNode(node);
      }
    },

    /**
     * Color is given by a voltage. Voltage is a computed value representing
     * the importance of a node in a graph.
     */
    VOLTAGE {
      @Override
      public <E> Color getColor(GraphNode node, ColorMap cm,
          float voltagePercent, float degreePercent) {
        return cm.getColor(voltagePercent);
      }
    };

    /**
     * return the color for the given node.
     * @param <E> edge type.
     * @param nodeFillColor object rendering the node color.
     * @param node the node
     * @param painter a transformer to get a color given a node.
     * @return the color for the given node.
     */
    public abstract <E> Color getColor(GraphNode node,
        ColorMap cm, float voltagePercent, float degreePercent);

    public static NodeColors getDefault() {
      return DEGREE;
    }

    public static Color getDefaultColor() {
      return Color.BLUE;
    }

    public static Color matchNode(GraphNode node) {
      Color c = NodeElementPluginRegistry.getColor(node);
      if (c != null) {
        return c;
      }
      return getDefaultColor();
    }
  }

  /**
   * Different mode for rendering node shapes.
   *
   * @author ycoppel@google.com (Yohann Coppel)
   *
   */
  public enum NodeShape {
    /**
     * Shape is given with the degree of the node. Degree is the number
     * of incoming plus outgoing connections.
     * @see edu.uci.ics.jung.graph.Hypergraph#degree(Object)
     */
    DEGREE {
      @Override
      public GLEntity getShape(int degree, GraphNode node) {
        if (degree <= 2) {
          return ShapeFactory.createEllipse();
        } else if (degree <= 4) {
          return ShapeFactory.createRegularPolygon(degree);
        }
        return ShapeFactory.createRegularStar(degree);
      }
    },
    /**
     * Shape is given by the role of the node (e.g. class, method, package...)
     */
    ROLE {
      @Override
      public GLEntity getShape(int degree, GraphNode node) {
        GLEntity e = JoglPluginRegistry.getShape(node);
        if (null != e) {
          return e;
        }
        return getDefaultShape();
      }
    };

    /**
     * return a Shape for the given node.
     * @param degree (number of input/output edges)
     * @param node the node
     * @return a Shape for the given node
     */
    public abstract GLEntity getShape(
        int degree, GraphNode node);

    public static NodeShape getDefault() {
      return ROLE;
    }

    public static GLEntity getDefaultShape() {
      return ShapeFactory.createEllipse();
    }
  }

  /**
   * different mode to give a size to a node.
   *
   * @author ycoppel@google.com (Yohann Coppel)
   *
   */
  public enum NodeSize {
    /**
     * Size is given with the degree of the node. Degree is the number
     * of incoming plus outgoing connections.3
     * @see edu.uci.ics.jung.graph.Hypergraph#degree(Object)
     */
    DEGREE {
      @Override
      public float getSize(float voltagePercent, float degreePercent) {
        return convertToSize(degreePercent);
      }
    },
    /**
     * Color is given by a voltage. Voltage is a computed value representing
     * the importance of a node in a graph.
     */
    VOLTAGE {
      @Override
      public float getSize(float voltagePercent, float degreePercent) {
        return convertToSize(voltagePercent);
      }
    };

    // TODO: preferences for these MIN / MAX values
    private static final float STD_SIZE = 15f;
    private static final float MIN_SIZE = 0.5f * STD_SIZE;
    private static final float MAX_SIZE = 2f * STD_SIZE;

    private static float convertToSize(float percent) {
      if (percent == 0.5) {
        return STD_SIZE;
      } else if (percent < 0.5) {
        return MIN_SIZE + (STD_SIZE - MIN_SIZE) * percent;
      } else {
        return STD_SIZE + (MAX_SIZE - STD_SIZE) * percent;
      }
    }

    /**
     * return the size for the given node.
     * @param transformer transformer handling the node size
     * @param node the node
     * @return the node size
     */
    public abstract float getSize(
        float voltagePercent, float degreePercent);

    public static NodeSize getDefault() {
      return VOLTAGE;
    }

    public static float getDefaultSize() {
      return STD_SIZE;
    }
    
    /**
     * Converts the lower level size model representation to size models at this
     * layer.
     * 
     * @param convertFrom The lower level size model.
     * @return Size model that is usable at this layer.
     */
    public static NodeSize convertSizeRepresentation(
        NodeDisplayProperty.Size convertFrom) {
      
      if (NodeDisplayProperty.Size.VOLTAGE == convertFrom) {
        return NodePreferencesIds.NodeSize.VOLTAGE;
      } else if (NodeDisplayProperty.Size.DEGREE == convertFrom) {
        return NodePreferencesIds.NodeSize.DEGREE;
      }
      return null;
    }
  }
}
