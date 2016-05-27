package com.google.devtools.depan.eclipse.visualization.ogl;

import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds.NodeSize;

public interface NodeSizeSupplier {
  float getSize();

  float getOverridenSize(NodeSize overrideSize);

  public static class Standard implements NodeSizeSupplier {

    @Override
    public float getSize() {
      return NodeSize.getDefaultSize();
    }

    @Override
    public float getOverridenSize(NodeSize overrideSize) {
      return overrideSize.getSize(1.0f, 1.0f);
    }
  }

  public static final Standard STANDARD = new Standard();
}
