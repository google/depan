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

import com.google.devtools.depan.model.GraphEdge;

import com.jogamp.opengl.util.awt.TextureRenderer;
import com.jogamp.opengl.util.texture.Texture;

import java.awt.Color;

/**
 *
 * Rendering options for an edge.
 *
 * @see NodeRenderingProperty
 * @author Yohann Coppel
 */
public class EdgeRenderingProperty extends RenderingProperty {
  private static final float SPEED = 10.0f;

  public GraphEdge edge;
  public NodeRenderingProperty node1;
  public NodeRenderingProperty node2;

  public boolean isVisible;

  /** Center coordinates for the first node */
  public float p1X;
  public float p1Y;

  /** Center coordinates for the second node */
  public float p2X;
  public float p2Y;

  /** Cache of arc points */
  public ArcInfo arcInfo;

  /**
   * Deviation for the edge (how curved it is).
   */
  public float deviation;
  public float strokeWidth;
  public Color strokeColor;
  public Color textColor;

  public float targetDeviation;
  public Color targetStrokeColor;
  public float targetStrokeWidth;
  public Color targetTextColor;

  public boolean isTextVisible;

  /**
   * Stores the new <code>Color</code> of the associated {@link GraphEdge}.
   */
  public Color overriddenStrokeColor = null;

  public TextureRenderer textRenderer;
  /** the texture containing the rendered label. Cannot be created
   * while no GL context are active, therefore, must be calculated
   * during the rendering process, if textIsDirty is set to true.
   */
  public Texture textTexture = null;
  public boolean textIsDirty = true;

  public EdgeRenderingProperty(int shapeId, GraphEdge edge,
      NodeRenderingProperty node1, NodeRenderingProperty node2) {
    super(shapeId);
    this.edge = edge;
    this.node1 = node1;
    this.node2 = node2;

    init();
  }

  @Override
  public void step(float elapsedTime) {
    deviation += (targetDeviation - deviation) / SPEED;
    strokeColor = colorStep(strokeColor, targetStrokeColor, SPEED, elapsedTime);
    textColor = colorStep(textColor, targetTextColor, SPEED, elapsedTime);
    strokeWidth += (targetStrokeWidth - strokeWidth) / SPEED;
  }

  private void init() {
    isVisible = true;

    deviation = 0.25f;
    p1X = 0.0f;
    p1Y = 0.0f;
    p2X = 0.0f;
    p2Y = 0.0f;
    strokeColor = Color.WHITE;
    textColor = Color.WHITE;
    strokeWidth = 1.0f;

    shape = new Arrow();

    isTextVisible = true;
    textRenderer = FontManager.makeText(edge.getRelation().toString());
    textIsDirty = true;

    stopSteps();
  }

  public void stopSteps() {
    targetDeviation = deviation;
    targetStrokeColor = strokeColor;
    targetStrokeWidth = strokeWidth;
    targetTextColor = textColor;
  }

  /**
   * Returns the {@link Arrow} associated with this edge.
   *
   * @return {@link Arrow} associated with this edge. May return null.
   */
  public Arrow getArrow() {
    if (shape instanceof Arrow) {
      return (Arrow) shape;
    }
    return null;
  }

  @Override
  public String toString() {
    MessageBuilder result = new MessageBuilder();
    result.fmtValue("head", node1.node.getId());
    result.fmtValue("tail", node2.node.getId());
    result.fmtBoolean(isVisible, "hidden", "visible");
    return result.build();
  }

  public ArcInfo getArcFor(
      Vec2 headVec, Vec2 tailVec, GLEntity headShape, GLEntity tailShape) {
    if (!matchArcCache(headVec, tailVec, headShape, tailShape)) {
      arcInfo = ArcInfo.buildArcInfo(headVec, tailVec, headShape, tailShape);
    }
    return arcInfo;
  }

  private boolean matchArcCache(
      Vec2 headVec, Vec2 tailVec, GLEntity headShape, GLEntity tailShape) {
    if (arcInfo == null) {
      return false;
    }
    return arcInfo.isFor(headVec, tailVec, headShape, tailShape);
  }
}
