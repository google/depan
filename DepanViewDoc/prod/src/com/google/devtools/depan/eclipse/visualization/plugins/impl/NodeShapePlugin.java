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
import com.google.devtools.depan.view_doc.model.NodeShapeMode;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * A plugin that sets up and changes the shape for nodes.
 *
 * @author Yohann Coppel
 */
public class NodeShapePlugin extends NodeRenderingPlugin.Simple {

  private boolean hasChanged = true;

  private NodeShapeMode mode = null;

  @Override
  public boolean apply(NodeRenderingProperty p) {
    if (!hasChanged) {
      return true;
    }

    @SuppressWarnings("unchecked")
    Map<NodeShapeMode, NodeShapeSupplier> modeMap =
        (Map<NodeShapeMode, NodeShapeSupplier>) p.pluginStore.get(this);
    NodeShapeSupplier supplier = null;
    if (null != modeMap ) {
      supplier = modeMap.get(mode);
    }
    if (null == supplier) {
      supplier = NodeShapeSupplier.DEFAULT;
    }

    p.shape = supplier.getShape();
    return true;
  }

  @Override
  public void postFrame() {
    hasChanged = false;
  }

  public void setNodeShapeMode(NodeShapeMode mode) {
    this.mode = mode;
    hasChanged = true;
  }

  public void setNodeShapeByMode(
      NodeRenderingProperty p, NodeShapeMode mode, NodeShapeSupplier supplier) {
    @SuppressWarnings("unchecked")
    Map<NodeShapeMode, NodeShapeSupplier> modeMap =
        (Map<NodeShapeMode, NodeShapeSupplier>) p.pluginStore.get(this);
    if (null == modeMap) {
      modeMap = Maps.newHashMap();
      p.pluginStore.put(this, modeMap);
    }
    modeMap.put(mode, supplier);

    if (this.mode == mode) {
      hasChanged = true;
    }
  }
}
