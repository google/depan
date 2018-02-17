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

/**
 * Provide shapes for nodes.
 * 
 * Instances exist for each node.
 * 
 * @author Lee Carver
 */
public interface NodeShapeSupplier {

  GLEntity getShape();

  public static class Fixed implements NodeShapeSupplier {
    private final GLEntity shape;

    public Fixed(GLEntity shape) {
      this.shape = shape;
    }

    @Override
    public GLEntity getShape() {
      return shape;
    }
  }

  public static final NodeShapeSupplier DEFAULT = new NodeShapeSupplier() {

    @Override
    public GLEntity getShape() {
      return ShapeFactory.createEllipse();
    }
  };
}
