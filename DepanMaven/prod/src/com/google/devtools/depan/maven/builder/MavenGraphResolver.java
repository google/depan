/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.maven.builder;

import com.google.devtools.depan.maven.graph.ArtifactElement;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.api.GraphBuilder;
import com.google.devtools.depan.model.builder.api.GraphBuilders;
import com.google.devtools.depan.model.builder.chain.DependenciesDispatcher;
import com.google.devtools.depan.model.builder.chain.DependenciesListener;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Resolve {@link ArtifactElement} references to known {@link ArtifactElement}
 * definitions.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class MavenGraphResolver {

  private final Map<GraphNode, GraphNode> updateMap =
      Maps.newHashMap();

  public GraphModel resolveReferences(GraphModel analysisGraph) {
    Collection<GraphNode> nodes = analysisGraph.getNodes();

    Map<String, List<ArtifactElement>> baseMap =
        Maps.newHashMapWithExpectedSize(nodes.size());

    // Build map of known artifact base names to all matching
    // ArtifactElements.
    for (GraphNode node : nodes) {
      if (node instanceof ArtifactElement) {
        ArtifactElement artifact = (ArtifactElement) node;
        String baseLabel = artifact.getBaseLabel();
        List<ArtifactElement> known = baseMap.get(baseLabel);
        if (null == known) {
          known = Lists.newLinkedList();
          baseMap.put(baseLabel, known);
        }
        known.add(artifact);
      }
    }

    // Use the sets of ArtifactElements from the baseMap to build
    // the update map for resolvable ArtifactElements.
    for (List<ArtifactElement> bases : baseMap.values()) {
      if (bases.size() < 2) {
        continue;
      }
      buildUpdateMap(bases);
    }

    // Use the constructed update map to build a graph with all
    // resolvable ArtifactElement references mapped to their
    // definitions.
    return rewriteReferences(analysisGraph);
  }

  private void buildUpdateMap(List<ArtifactElement> bases) {
    for (ArtifactElement artifact : bases) {
      ArtifactElement result = artifact;
      for (ArtifactElement mapTo : bases) {
        result = resolveRef(result, mapTo);
      }
      if (!result.equals(artifact)) {
        updateMap.put(artifact, result);
      }
    }
  }

  private ArtifactElement resolveRef(
      ArtifactElement ref, ArtifactElement def) {
    if (ref.equals(def)) {
      return ref;
    }

    if (!matchClassifier(ref, def)) {
      return ref;
    }

    // If a reference omits the packaging, resolve to the defining
    // ArtifactElement.
    if (null == ref.getPackaging()) {
      return def;
    }
    return ref;
  }

  /**
   * Classifiers match if they are both {@code null} or they are both
   * the same text value.
   */
  private boolean matchClassifier(
      ArtifactElement ref, ArtifactElement def) {
    String refClass = ref.getClassifier();
    String defClass = def.getClassifier();
    if (null == refClass) {
      return null == defClass;
    }
    return refClass.equals(defClass);
  }

  private GraphModel rewriteReferences(GraphModel analysisGraph) {
    GraphBuilder graphBuilder = GraphBuilders.createGraphModelBuilder();
    DependenciesListener builder = new DependenciesDispatcher(graphBuilder);
    for (GraphEdge edge : analysisGraph.getEdges()) {
      GraphNode mapHead = mapNode(edge.getHead());
      GraphNode mapTail = mapNode(edge.getTail());
      builder.newDep(mapHead, mapTail, edge.getRelation());
    }
    return graphBuilder.createGraphModel();
  }

  private GraphNode mapNode(GraphNode node) {
    GraphNode lookup = updateMap.get(node);
    if (null != lookup) {
      return lookup;
    }
    return node;
  }
}
