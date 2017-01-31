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

package com.google.devtools.depan.view_doc.model;

import com.google.devtools.depan.eclipse.visualization.ogl.NodeSizeSupplier;

import java.awt.Color;

/**
 * Class handling displaying properties for a Node.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class NodeDisplayProperty {

  public static final int DEFAULT_SIZE = 20;

  private boolean isVisible = true;
  private NodeSizeSupplier size = null;
  private int givenSize = DEFAULT_SIZE;
  private Color color = null;

  /**
   * Default constructor. Construct a {@link NodeDisplayProperty} with default
   * values. (isVisible = true, size = AUTO, color = null, isSelected = false). 
   */
  public NodeDisplayProperty() {
  }
  
  /**
   * Construct a {@link NodeDisplayProperty} with the given properties.
   * 
   * @param isVisible if the node should be visible or not.
   * @param size method used to give a size to a node.
   * @param color node's color
   */
  public NodeDisplayProperty(
      boolean isVisible, NodeSizeSupplier size, Color color) {
    this.isVisible = isVisible;
    this.size = size;
    this.color = color;
  }

  /**
   * @return the isVisible
   */
  public boolean isVisible() {
    return isVisible;
  }

  /**
   * @param value the visibility value to assign.
   */
  public void setVisible(boolean value) {
    this.isVisible = value;
  }

  /**
   * @return the size
   */
  public NodeSizeSupplier getSize() {
    return size;
  }
  
  /**
   * Use {@code null} to use diagram's mode for size.
   */
  public void setSize(NodeSizeSupplier size) {
    this.size = size;
  }
  
  /**
   * Set the node's color. Use <code>null</code> to use the default
   * colors.
   * 
   * @param c the new node color, or <code>null</code> for default.
   */
  public void setColor(Color c) {
    this.color = c;
  }
  
  /**
   * returns the color of this node, or <code>null</code> if this node uses
   * the default color.
   * 
   * @return the node's color, or <code>null</code> if it uses the default
   *         color.
   */
  public Color getColor() {
    return color;
  }

  /**
   * Return the size associated to this node. The real size of the node can
   * however be different. See {@link #getSize()};
   * 
   * @see #getSize()
   * @return the size associated to this node.
   */
  public int getGivenSize() {
    return givenSize;
  }

  /**
   * set the node size to the given value. The real size of the node can
   * however be different. See {@link #getSize()};
   * 
   * @see #getSize()
   * @param newSize the new size for this node.
   */
  public void setGivenSize(int newSize) {
    this.givenSize = newSize;
  }
}
