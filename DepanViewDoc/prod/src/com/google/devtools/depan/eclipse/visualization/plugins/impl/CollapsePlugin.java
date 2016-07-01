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

import com.google.devtools.depan.eclipse.visualization.ogl.EdgeRenderingProperty;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeRenderingProperty;
import com.google.devtools.depan.eclipse.visualization.plugins.core.EdgeRenderingPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.core.NodeRenderingPlugin;

/**
 * This plugin handle collapsed nodes.
 * When a node is collapsed, it is first moved to the position of its parent,
 * and once the position is reached, it is not painted anymore.
 *
 * For edges, it's similar. Once a node at one of the two end points has reached
 * its parent, the position of the corresponding point is set to the position
 * of the parent.
 *
 * Additionally, if a edge is between 2 nodes N1 and N2, and that N2 is
 * collapsed under N1, once N1 has reached N2, the edge is not painted anymore.
 *
 * @author Yohann Coppel
 *
 */
public class CollapsePlugin implements NodeRenderingPlugin, EdgeRenderingPlugin {

  @Override
  public void postFrame() {
  }

  @Override
  public void preFrame(float elapsedTime) {
  }

  @Override
  public boolean keyPressed(int keycode, char character, boolean ctrl,
      boolean alt, boolean shift) {
    return false;
  }

  // nodes -------

  /**
   * Collapse the node <code>node</code> under the given <code>parent</code>
   * @param node node to collapse
   * @param parent its parent (collapsed under this node).
   */
  public void collapseUnder(NodeRenderingProperty node, NodeRenderingProperty parent) {
    if (parent == node) {
      return;
    }
    node.collapsedUnder = parent;
    parent.hasCollapsedNodeUnder = true;
    // setup a new DeltaCollapse, that holds the difference in position at
    // collapsing time. During uncollapsing, we restore this difference.
    node.pluginStore.put(this,
        new DeltaCollapse(parent.positionX-node.positionX,
            parent.positionY-node.positionY));
  }

  /**
   * Uncollapse <code>node</code>, previously collapsed under
   * <code>parent</code>.
   * The difference in position when the collapse operation occurs is restored.
   * @param node node to uncollapse
   * @param parent
   */
  public void unCollapse(NodeRenderingProperty node, NodeRenderingProperty parent) {
    if (parent == node || node.collapsedUnder != parent) {
      return;
    }
    node.collapsedUnder = null;
    parent.hasCollapsedNodeUnder = false;
    DeltaCollapse delta = (DeltaCollapse) node.pluginStore.get(this);
    node.targetPositionX = parent.targetPositionX-delta.dxAtCollapse;
    node.targetPositionY = parent.targetPositionY-delta.dyAtCollapse;
    delta.reachedParent = false;
  }

  @Override
  public boolean apply(NodeRenderingProperty node) {
    if (node.collapsedUnder != null) {
      // if the node is collapsed, get the data.
      DeltaCollapse store = getStore(node);
      if (node.isCompletelyCollapsed()) {
        // the node has finished his move towards the position of its parent
        if (store != null) {
          // the node has reached the position of it's parent.
          // We will not draw it anymore.
          store.reachedParent = true;
        }
        return false;
      }
      if (store != null && store.reachedParent) {
        // The node has already reached the position of his parent previously
        // set it's position to the position of its parent.
        node.positionX = node.collapsedUnder.positionX;
        node.positionY = node.collapsedUnder.positionY;
        node.targetPositionX = node.positionX;
        node.targetPositionY = node.positionY;
        return false;
      } else {
        // a collapsed has been requested, but the node has not reached the position
        // of his parent yet. move it towards its parent.
        node.targetPositionX = node.collapsedUnder.targetPositionX;
        node.targetPositionY = node.collapsedUnder.targetPositionY;
        return true;
      }
    }
    return true;
  }

  @Override
  public void dryRun(NodeRenderingProperty node) {
  }

  // edges --------

  @Override
  public boolean apply(EdgeRenderingProperty edge) {
    if (edge.node1.isCompletelyCollapsed()) {
      // first node has reached its parent. set the end point position.
      edge.p1X = edge.node1.collapsedUnder.positionX;
      edge.p1Y = edge.node1.collapsedUnder.positionY;
      if (edge.node2 == edge.node1.collapsedUnder) {
        // do not draw the edge if both end points are the same node
        return false;
      }
    }
    // idem for node 2
    if (edge.node2.isCompletelyCollapsed()) {
      edge.p2X = edge.node2.collapsedUnder.positionX;
      edge.p2Y = edge.node2.collapsedUnder.positionY;
      if (edge.node1 == edge.node2.collapsedUnder) {
        return false;
      }
    }
    return true;
  }

  private DeltaCollapse getStore(NodeRenderingProperty p) {
    return (DeltaCollapse) p.pluginStore.get(this);
  }

  @Override
  public void dryRun(EdgeRenderingProperty p) {
  }

  private static class DeltaCollapse {

    float dxAtCollapse = 0f;
    float dyAtCollapse = 0f;

    boolean reachedParent = false;

    public DeltaCollapse(float dxAtCollapse, float dyAtCollapse) {
      this.dxAtCollapse = dxAtCollapse;
      this.dyAtCollapse = dyAtCollapse;
    }
  }

}
