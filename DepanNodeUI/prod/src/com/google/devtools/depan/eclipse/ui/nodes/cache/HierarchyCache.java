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

package com.google.devtools.depan.eclipse.ui.nodes.cache;

import com.google.devtools.depan.eclipse.ui.nodes.trees.GraphData;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeTreeProvider;
import com.google.devtools.depan.graph.api.EdgeMatcher;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.nodes.trees.TreeModel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
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
  private Map<EdgeMatcher<String>, GraphData<T>> hierarchies =
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
   * internal set of nodes and the provided edge matcher.  A new
   * {@code GraphData} is created if the {@code edgeMatcher} has not been
   * used before.
   * 
   * @param edgeMatcher Edge matcher to use for hierarchy construction
   * @return description of hierarchy
   */
  public GraphData<T> getHierarchy(EdgeMatcher<String> edgeMatcher) {
    if (!hierarchies.containsKey(edgeMatcher)) {
      GraphData<T> result =
          GraphData.createGraphData(provider, graph, edgeMatcher);
      hierarchies.put(edgeMatcher, result);
    }

    return hierarchies.get(edgeMatcher);
  }

  public GraphData<T> allNodes() {
    Collection<GraphNode> nodes = graph.getNodes();

    TreeModel hierarchy = new TreeModel.Flat(nodes);
    return new GraphData<T>(provider, hierarchy );
  }

  public GraphData<T> excludedNodes(Collection<GraphNode> included) {
    Collection<GraphNode> nodes = Lists.newArrayList(graph.getNodes());
    nodes.removeAll(included);

    TreeModel hierarchy = new TreeModel.Flat(nodes);
    return new GraphData<T>(provider, hierarchy );
  }
}
