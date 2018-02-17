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

import com.google.devtools.depan.eclipse.visualization.plugins.core.NodeRenderingPlugin;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view_doc.model.NodeShapeMode;

import com.google.common.collect.Maps;
import com.jogamp.opengl.util.awt.TextureRenderer;
import com.jogamp.opengl.util.texture.Texture;

import java.awt.Color;
import java.util.Map;

/**
 * This class is used to store the rendering properties for a node.
 * It is accessed for every node, at every frame, by every RenderingPlugin.
 * Therefore, accessing and updating values must be fast. That's why
 * members are are mostly native types, and public.
 */
public class NodeRenderingProperty extends RenderingProperty {
  //private static final float TRANSITION_TIME = 1.0f;
  private static final float SPEED = 10.0f;
  private static final double COLLAPSE_TOLERANCE = 0.1;

  /**
   * Underlying {@link GraphNode}.
   */
  public GraphNode node;

  // actual values, used when drawing
  public float ratio;
  public float size;
  public float positionX;
  public float positionY;
  public Color strokeColor;
  public Color fillColor;
  public float strokeWidth;
  public Color textColor;
  public float textDx;
  public float textDy;

  // target values. Plugins should modify these values, in order to get
  // nice transitions from one value to another.
  public float targetRatio;
  public float targetSize;
  public float targetPositionX;
  public float targetPositionY;
  public Color targetStrokeColor;
  public Color targetFillColor;
  public float targetStrokeWidth;
  public Color targetTextColor;
  public float targetTextDx;
  public float targetTextDy;

  public boolean isFilled;
  public boolean isVisible;
  private boolean isSelected;

  /**
   * Used when the color value of this node is overridden.
   */
  public Color overriddenColor = null;

  /**
   * Used when the size computation method of this node is overridden.
   */
  public NodeSizeSupplier overriddenSize = null;

  /**
   * If the node is collapsed, this is the parent.
   */
  public NodeRenderingProperty collapsedUnder = null;

  /**
   * Say if a node has child collapsed under itself. Is set to true/false by the
   * plugin in charge of collapsing/uncollapsing nodes.
   */
  public boolean hasCollapsedNodeUnder = false;

  /////////////////////////////////////
  // Text rendering properties

  public boolean isTextVisible;

  /**
   * TextureRenderer used as a temporary object, before creating the Texture
   * object, when an OpenGL context is active (during rendering).
   */
  public TextureRenderer textRenderer;

  /**
   * The texture containing the rendered label. Cannot be created
   * while no GL context are active, therefore, must be calculated
   * during the rendering process, if textIsDirty is set to true.
   */
  public Texture textTexture = null;

  /**
   * @see #textTexture
   */
  public boolean textIsDirty = true;

  /**
   * a space where each NodeRenderingPlugin can store an object for this node.
   * The key must be the plugin that stores the value.
   * <p>
   * This is not persisted.
   */
  public Map<NodeRenderingPlugin, Object> pluginStore = Maps.newHashMap();

  public NodeRenderingProperty(int shapeId, GraphNode node) {
    super(shapeId);
    this.node = node;
    init();
  }

  public void init() {
    isVisible = true;

    ratio = 1f;
    size = 10.0f;
    positionX = 0f;
    positionY = 0f;
    strokeColor = Color.BLUE;
    fillColor = Color.BLACK;
    strokeWidth = 1.0f;
    textColor = Color.WHITE;

    shape = NodeShapeMode.DEFAULT_SHAPE;
    isFilled = true;
    isTextVisible = true;

    textRenderer = FontManager.makeText(node.friendlyString());
    textIsDirty = true;

    // text on the right
    textDx = 0f;
    textDy = -1f;

    // copy values into targets
    stopSteps();
  }

  @Override
  public void step(float elapsedTime) {
    ratio += (targetRatio - ratio) / SPEED;
    size += (targetSize - size) / SPEED;
    positionX += (targetPositionX - positionX) / SPEED;
    positionY += (targetPositionY - positionY) / SPEED;
    strokeColor = colorStep(strokeColor, targetStrokeColor, SPEED, elapsedTime);
    fillColor = colorStep(fillColor, targetFillColor, SPEED, elapsedTime);
    textColor = colorStep(textColor, targetTextColor, SPEED, elapsedTime);
    strokeWidth += (targetStrokeWidth - strokeWidth) / SPEED;
    textDx += (targetTextDx - textDx) / SPEED;
    textDy += (targetTextDy - textDy) / SPEED;
  }

  public void finishSteps() {
    ratio = targetRatio;
    size = targetSize;
    positionX = targetPositionX;
    positionY = targetPositionY;
    strokeColor = targetStrokeColor;
    fillColor = targetFillColor;
    strokeWidth = targetStrokeWidth;
    textColor = targetTextColor;
    textDx = targetTextDx;
    textDy = targetTextDy;
  }

  public void stopSteps() {
    targetRatio = ratio;
    targetSize = size;
    targetPositionX = positionX;
    targetPositionY = positionY;
    targetStrokeColor = strokeColor;
    targetFillColor = fillColor;
    targetStrokeWidth = strokeWidth;
    targetTextColor = textColor;
    targetTextDx = textDx;
    targetTextDy = textDy;
  }

  /**
   * Say if the node is collapsed, and has reached the position
   * of its parent.
   */
  public boolean isCompletelyCollapsed() {
    if (collapsedUnder == null) {
      return false;
    }
    if (Math.abs(positionX - collapsedUnder.positionX) < COLLAPSE_TOLERANCE
        && Math.abs(positionY - collapsedUnder.positionY) < COLLAPSE_TOLERANCE) {
      return true;
    }
    return false;
  }

  /**
   * @return true if this node is selected, or if is collapsed and its parent is
   * selected.
   */
  public boolean isSelected() {
    if (isSelected) {
      return true;
    }
    if (collapsedUnder != null) {
      return collapsedUnder.isSelected();
    }
    return false;
  }

  public void setSelected(boolean selected) {
    this.isSelected = selected;
  }

  /**
   * Indicate if the node is apparent on the screen.  A node is apparent on
   * the display if it is directly visible, or if it one of it collapse parents
   * if directly visible
   */
  public boolean isApparent() {
    if (isVisible) {
      return true;
    }
    if (collapsedUnder != null) {
      return collapsedUnder.isApparent();
    }
    return false;
  }

  @Override
  public String toString() {
    MessageBuilder result = new MessageBuilder();
    result.fmtValue(node.getId());
    result.fmtBoolean(isVisible, "hidden", "visible");
    return result.build();
  }
}
