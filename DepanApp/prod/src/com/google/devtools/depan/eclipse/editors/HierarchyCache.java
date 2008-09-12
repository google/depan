/*
 * Copyright 2008 Google Inc.
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

package com.google.devtools.depan.eclipse.editors;

import com.google.devtools.depan.collect.Maps;
import com.google.devtools.depan.eclipse.trees.GraphData;
import com.google.devtools.depan.eclipse.trees.NodeTreeProvider;
import com.google.devtools.depan.graph.api.DirectedRelationFinder;
import com.google.devtools.depan.model.GraphModel;

import java.util.Map;

/**
 * Maintain a set of known hierarchies over a set of nodes.
 * This also create new hiearchies on demand,
 * if the relation finder hasn't been seen before.
 *
 * @author <a href='mailto:leeca@google.com'>Lee Carver </a>
 */
public class HierarchyCache<T> {
  private final NodeTreeProvider<T> provider;
  private final GraphModel graph;
  /**
   * We store all the trees for now, which allows quick switch when changing
   * from one view to another. If it's too memory consuming, we could change
   * that easily.
   */
  private Map<DirectedRelationFinder, GraphData<T>> hierarchies =
      Maps.newHashMap();

  /**
   * Construct the cache, saving the values needed to create new hierarchies.
   * 
   * @param provider source if information about nodes
   * @param graph set of nodes for hierarchies.
   */
  public HierarchyCache(NodeTreeProvider<T> provider, GraphModel graph) {
    this.provider = provider;
    this.graph = graph;
  }

  /**
   * Provide a description of a hierarchy ({@code GraphData}) based on the
   * internal set of nodes and the provided set of relations.  A new
   * {@code GraphData} is created if the {@code relFinder} has not been
   * used before.
   * 
   * @param relFinder relations to use for hierarchy construction
   * @return description of hierarchy
   */
  public GraphData<T> getHierarchy(DirectedRelationFinder relFinder) {
    if (!hierarchies.containsKey(relFinder)) {
      GraphData<T> result =
          GraphData.createGraphData(provider, graph, relFinder);
      hierarchies.put(relFinder, result);
    }

    return hierarchies.get(relFinder);
  }
}
