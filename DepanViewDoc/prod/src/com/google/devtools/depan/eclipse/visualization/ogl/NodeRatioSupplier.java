package com.google.devtools.depan.eclipse.visualization.ogl;

public interface NodeRatioSupplier {
  float getRatio();

  public static class Full implements NodeRatioSupplier {

    @Override
    public float getRatio() {
      return 1.0f;
    }
  }

  public static final Full FULL = new Full();

  public static class Simple implements NodeRatioSupplier {

    private final float ratio;

    public Simple(float ratio) {
      this.ratio = ratio;
    }

    @Override
    public float getRatio() {
      return ratio;
    }
  }
}
