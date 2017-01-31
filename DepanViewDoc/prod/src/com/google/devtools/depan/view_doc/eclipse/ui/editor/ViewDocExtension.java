package com.google.devtools.depan.view_doc.eclipse.ui.editor;

import com.google.devtools.depan.eclipse.visualization.ogl.GLEntity;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeShapeSupplier;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view_doc.eclipse.ui.plugins.JoglPluginRegistry;
import com.google.devtools.depan.view_doc.eclipse.ui.plugins.ViewExtension;
import com.google.devtools.depan.view_doc.model.NodeColorMode;
import com.google.devtools.depan.view_doc.model.NodeRatioMode;
import com.google.devtools.depan.view_doc.model.NodeShapeMode;
import com.google.devtools.depan.view_doc.model.NodeSizeMode;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ViewDocExtension implements ViewExtension {

  // Shape modes
  public static final NodeShapeMode SHAPE_ROLE_MODE_ID =
      new NodeShapeMode.Labeled("Role");

  private static final List<NodeShapeMode> NODE_SHAPE_MODES =
      Lists.newArrayList(SHAPE_ROLE_MODE_ID);

  @Override
  public void deriveDetails(ViewEditor editor) {
  }

  @Override
  public void prepareView(ViewEditor editor) {
    GraphModel model = editor.getViewGraph();
    for (GraphNode node : model.getNodes()) {
      editor.setNodeShapeByMode(node, SHAPE_ROLE_MODE_ID, getNodeShape(node));
    }
  }

  private NodeShapeSupplier getNodeShape(GraphNode node) {
    GLEntity e = JoglPluginRegistry.getShape(node);
    if (null != e) {
      return new NodeShapeSupplier.Fixed(e);
    }
    return NodeShapeSupplier.DEFAULT;
  }

  @Override
  public List<NodeColorMode> getNodeColorModes() {
    return Collections.emptyList();
  }

  @Override
  public List<NodeShapeMode> getNodeShapeModes() {
    return NODE_SHAPE_MODES;
  }

  @Override
  public List<NodeSizeMode> getNodeSizeModes() {
    return Collections.emptyList();
  }

  @Override
  public Collection<? extends NodeRatioMode> getNodeRatioModes() {
    return Collections.emptyList();
  }
}
