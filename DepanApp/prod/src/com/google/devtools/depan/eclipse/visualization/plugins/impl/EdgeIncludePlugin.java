/*
 * Copyright 2008 Yohann R. Coppel
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

import com.google.common.collect.Sets;
import com.google.devtools.depan.eclipse.plugins.SourcePlugin;
import com.google.devtools.depan.eclipse.plugins.SourcePluginRegistry;
import com.google.devtools.depan.eclipse.visualization.ogl.EdgeRenderingProperty;
import com.google.devtools.depan.eclipse.visualization.plugins.core.EdgeRenderingPlugin;
import com.google.devtools.depan.graph.api.Relation;

import java.util.Collection;
import java.util.Set;

/**
 * Plugin that put edges that should not be rendered out of the Rendering pipe.
 *
 * @author Yohann Coppel
 *
 */
public class EdgeIncludePlugin implements EdgeRenderingPlugin {

  /**
   * types {@link Relation}s that can be rendered.
   */
  private Set<Relation> visible = Sets.newHashSet();

  public EdgeIncludePlugin() {
    // to start, every kind of relation is visible.
    for (SourcePlugin plugin : SourcePluginRegistry.getInstances()) {
      for (Relation r : plugin.getRelations()) {
        visible.add(r);
      }
    }
  }

  @Override
  public boolean apply(EdgeRenderingProperty p) {
    if (visible.contains(p.edge.getRelation())) {
      return true;
    }
    return false;
  }

  public void includeRelation(Relation relation) {
    visible.add(relation);
  }

  public void rejectRelation(Relation relation) {
    visible.remove(relation);
  }

  public Collection<Relation> getVisibleRelations() {
    return visible;
  }

  @Override
  public void dryRun(EdgeRenderingProperty p) {
    // nothing to do
  }

  @Override
  public boolean keyPressed(int keycode, char character, boolean ctrl,
      boolean alt, boolean shift) {
    // no key bindings for this plugin.
    return false;
  }

  @Override
  public void postFrame() {
    // nothing to do
  }

  @Override
  public void preFrame(float elapsedTime) {
    // nothing to do
  }

}
