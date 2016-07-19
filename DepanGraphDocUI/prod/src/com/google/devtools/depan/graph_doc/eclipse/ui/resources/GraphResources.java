/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.graph_doc.eclipse.ui.resources;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;

import java.util.List;

/**
 * Describes the various graph analysis tools that fit with the graph's
 * {@link Relation}s and {@link GraphNode}s.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class GraphResources {
  
  private final DependencyModel depsModel;

  private final RelationSetDescriptor defRelSet;
  private final List<RelationSetDescriptor> relSets;

  private final GraphEdgeMatcherDescriptor defMatcher;
  private final List<GraphEdgeMatcherDescriptor> matchers;

  /**
   * @param relSets
   * @param matchers
   * @param defRelSet
   * @param defMatcher
   */
  public GraphResources(
      DependencyModel depsModel,
      List<RelationSetDescriptor> relSets,
      List<GraphEdgeMatcherDescriptor> matchers,
      RelationSetDescriptor defRelSet,
      GraphEdgeMatcherDescriptor defMatcher) {
    this.depsModel = depsModel;
    this.relSets = relSets;
    this.matchers = matchers;
    this.defRelSet = defRelSet;
    this.defMatcher = defMatcher;
  }

  public RelationSetDescriptor getDefaultRelationSet() {
    return defRelSet;
  }

  public List<RelationSetDescriptor> getRelationSetsChoices() {
    return relSets;
  }

  public GraphEdgeMatcherDescriptor getDefaultEdgeMatcher() {
    return defMatcher;
  }

  public List<GraphEdgeMatcherDescriptor> getEdgeMatcherChoices() {
    return matchers;
  }
}
