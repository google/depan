/*
 * Copyright 2007 Google Inc.
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
 *
 */
public final class LabelPreferencesIds extends NodePreferencesIds {

  //private constructor to prevent instantiation of namespace class.
  private LabelPreferencesIds() {
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
}
