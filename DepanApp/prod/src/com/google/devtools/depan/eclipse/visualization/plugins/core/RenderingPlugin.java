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

package com.google.devtools.depan.eclipse.visualization.plugins.core;

import com.google.devtools.depan.eclipse.visualization.ogl.EdgeRenderingProperty;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeRenderingProperty;
import com.google.devtools.depan.eclipse.visualization.ogl.RenderingProperty;

/**
 * This is a plugin that is both a {@link EdgeRenderingPlugin} and a
 * {@link NodeRenderingPlugin}, ant that have the same action associated to
 * these two objects.
 *
 * If you need to write a {@link Plugin} with different actions for edges and
 * nodes, write one implementing both interfaces {@link EdgeRenderingPlugin} and
 * {@link NodeRenderingPlugin} instead of extending this class.
 *
 * @author Yohann Coppel
 *
 */
public abstract class RenderingPlugin implements Plugin, EdgeRenderingPlugin,
    NodeRenderingPlugin {

  /**
   * Method called once, before the first frame, on every object that could
   * be rendered later.
   *
   * @param p
   */
  public abstract void dryRun(RenderingProperty p);

  /**
   * Method called at every frame, on every objects to render.
   *
   * @param p
   * @return
   */
  public abstract boolean apply(RenderingProperty p);

  @Override
  public final boolean apply(EdgeRenderingProperty p) {
    return apply((RenderingProperty) p);
  }

  @Override
  public final boolean apply(NodeRenderingProperty p) {
    return apply((RenderingProperty) p);
  }

  @Override
  public final void dryRun(EdgeRenderingProperty p) {
    dryRun((RenderingProperty) p);
  }

  @Override
  public final void dryRun(NodeRenderingProperty p) {
    dryRun((RenderingProperty) p);
  }

}
