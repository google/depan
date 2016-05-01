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

/**
 * {@link Plugin} that alter the state of and {@link EdgeRenderingProperty}.
 *
 * @author Yohann Coppel
 *
 */
public interface EdgeRenderingPlugin extends Plugin {

  /**
   * Method called only once on each {@link EdgeRenderingProperty}, before
   * rendering the first frame.
   *
   * @param p
   */
  void dryRun(EdgeRenderingProperty p);

  /**
   * Method called for rendering the p, on every frame. This plugin can get the
   * given {@link EdgeRenderingProperty} out of the pipe, by returning
   * <code>false</code>. The {@link EdgeRenderingPlugin} will not be applied
   * to any following plugins, and therefore will probably not be rendered (if
   * the rendering plugin is after this one).
   *
   * @param p the {@link EdgeRenderingPlugin},
   * @return <code>true</code> if the edge can continue to the next plugin in
   * the pipe, <code>false</code> otherwise.
   */
  boolean apply(EdgeRenderingProperty p);
}
