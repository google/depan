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
 * Provide aspect ratios for nodes.
 * 
 * Instances exist for each node.
 * 
 * @author Lee Carver
 */
public interface NodeRatioSupplier {

  float getRatio();

  public static class Fixed implements NodeRatioSupplier {
    private final float ratio;

    public Fixed(float ratio) {
      super();
      this.ratio = ratio;
    }

    @Override
    public float getRatio() {
      return ratio;
    }
  };

  public static NodeRatioSupplier DEFAULT = new NodeRatioSupplier() {

    @Override
    public float getRatio() {
      return 1.0f;
    }
  };
}
