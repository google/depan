package com.google.devtools.depan.eclipse.visualization.ogl;

import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds.NodeShape;
import com.google.devtools.depan.model.GraphNode;

public interface NodeShapeSupplier {

  GLEntity getShape(GraphNode node);

  public static class Standard implements NodeShapeSupplier {

    @Override
    public GLEntity getShape(GraphNode node) {
      return NodeShape.getDefaultShape();
    }
  }

  public static final Standard STANDARD = new Standard();
}
