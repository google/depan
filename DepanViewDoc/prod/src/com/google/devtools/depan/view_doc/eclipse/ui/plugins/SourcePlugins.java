/*
 * Copyright 2015 The Depan Project Authors
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

package com.google.devtools.depan.view_doc.eclipse.ui.plugins;

import com.google.devtools.depan.graph.api.Relation;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Utility methods for {@link JoglPlugin} instances.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class SourcePlugins {

  private SourcePlugins() {
    // prevent instantiation
  }

  /**
   * Provide the list of relations defined by a set of plugins.
   * Often used to update the accessible relations when changing between
   * editor views, where different views have different sets of plugins.
   */
  public static List<Relation> getRelations(List<JoglPlugin> plugins) {
    List<Relation> result = Lists.newArrayList();
    for (JoglPlugin p : plugins) {
      for (Relation relation : p.getRelations()) {
        result.add(relation);
      }
    }
    return result;
  }
}
