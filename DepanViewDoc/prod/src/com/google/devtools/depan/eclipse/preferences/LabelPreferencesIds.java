/*
 * Copyright 2007 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.devtools.depan.eclipse.preferences;

/**
 * An namespace class for label preferences IDs.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public final class LabelPreferencesIds {

  private LabelPreferencesIds() {
    // Prevent instantiation.
  }

  public static final String LABEL_PREFIX =
    PreferencesIds.VIEW_PREFIX + "label_";

  /**
   * Label position: possible values: N, S, E, W, NE, NW, SE, SW.
   * @see edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position
   */
  public static final String LABEL_POSITION = LABEL_PREFIX + "position";
  public static final String LABEL_POSITION_DEFAULT =
      LabelPosition.getDefault().toString();

  public enum LabelPosition {
    INSIDE {
      @Override
      public double[] getDxDy() {
        return new double[]{0, 0};
      }
    },
    NOLABEL {
      @Override
      public double[] getDxDy() {
        return new double[]{0, 0};
      }
    },
    N {
      @Override
      public double[] getDxDy() {
        return new double[]{0, 1};
      }
    },
    IFSELECTED {
      @Override
      public double[] getDxDy() {
        return getDefault().getDxDy();
      }
    },
    NE {
      @Override
      public double[] getDxDy() {
        return new double[]{1, 1};
      }
    },
    E {
      @Override
      public double[] getDxDy() {
        return new double[]{1, 0};
      }
    },
    SE {
      @Override
      public double[] getDxDy() {
        return new double[]{1, -1};
      }
    },
    S {
      @Override
      public double[] getDxDy() {
        return new double[]{0, -1};
      }
    },
    SW {
      @Override
      public double[] getDxDy() {
        return new double[]{-1, -1};
      }
    },
    W {
      @Override
      public double[] getDxDy() {
        return new double[]{-1, 0};
      }
    },
    NW {
      @Override
      public double[] getDxDy() {
        return new double[]{-1, 1};
      }
    },
    CENTER {
      @Override
      public double[] getDxDy() {
        return new double[]{0, 0};
      }
    };

    public abstract double[] getDxDy();

    public static LabelPosition getDefault() {
      return S;
    }
  }
}
