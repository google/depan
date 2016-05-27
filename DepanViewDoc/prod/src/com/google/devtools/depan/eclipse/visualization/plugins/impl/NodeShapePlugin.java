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

import com.google.devtools.depan.eclipse.visualization.ogl.NodeRenderingProperty;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeShapeSupplier;
import com.google.devtools.depan.eclipse.visualization.plugins.core.NodeRenderingPlugin;

/**
 * A plugin that setup the shape for nodes.
 *
 * @author Yohann Coppel
 */
public class NodeShapePlugin extends NodeRenderingPlugin.Simple {

  private boolean hasChanged = true;

  @Override
  public boolean apply(NodeRenderingProperty p) {
    if (!hasChanged) {
      return true;
    }

    NodeShapeSupplier supplier = (NodeShapeSupplier) p.pluginStore.get(this);
    p.shape = supplier.getShape(p.node);
    return true;
  }

  @Override
  public void postFrame() {
    hasChanged = false;
  }

  public void setShapeSupplier(
      NodeRenderingProperty p, NodeShapeSupplier supplier) {
    p.pluginStore.put(this, supplier);
    hasChanged = true;
  }
}
