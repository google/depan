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

package com.google.devtools.depan.eclipse.visualization.plugins.impl;

import com.google.devtools.depan.eclipse.visualization.ogl.NodeRatioSupplier;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeRenderingProperty;
import com.google.devtools.depan.eclipse.visualization.plugins.core.NodeRenderingPlugin;

/**
 * A plugin that modifies node size and ratio.
 *
 * @author Yohann Coppel
 */
public class NodeRatioPlugin extends NodeRenderingPlugin.Simple {

  @Override
  public boolean apply(NodeRenderingProperty p) {
    NodeRatioSupplier supplier = (NodeRatioSupplier) p.pluginStore.get(this);

    // ratio
    p.targetRatio = supplier.getRatio();
    return true;
  }

  /////////////////////////////////////
  // size

  public void setRatioSupplier(
      NodeRenderingProperty p, NodeRatioSupplier supplier) {
    p.pluginStore.put(this, supplier);
  }
}
