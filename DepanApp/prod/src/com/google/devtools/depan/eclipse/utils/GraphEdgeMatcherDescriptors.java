/*
 * Copyright 2015 The Depan Project Authors
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

package com.google.devtools.depan.eclipse.utils;

import com.google.devtools.depan.eclipse.editors.ViewDocument;
import com.google.devtools.depan.eclipse.ui.edges.matchers.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.edges.matchers.GraphEdgeMatchers;
import com.google.devtools.depan.graph.api.EdgeMatcher;
import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.relations.RelationSetDescriptor;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

/**
 * Utilities for working with edge matchers at the application/workbench
 * level.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class GraphEdgeMatcherDescriptors {

  private GraphEdgeMatcherDescriptors() {
    // Prevent instantiation.
  }

  public static List<GraphEdgeMatcherDescriptor> buildGraphChoices(
      GraphDocument graphInfo) {
    List<GraphEdgeMatcherDescriptor> result = Lists.newArrayList();
    addKnownSets(result, graphInfo.getBuiltinEdgeMatchers());
    return result;
  }

  public static List<GraphEdgeMatcherDescriptor> buildViewChoices(
      ViewDocument viewInfo) {
    List<GraphEdgeMatcherDescriptor> result = Lists.newArrayList();
    addKnownSets(result, viewInfo.getBuiltinEdgeMatchers());
    return result;
  }

  public static void addKnownSets(
      List<GraphEdgeMatcherDescriptor> edgeMatchers,
      Collection<GraphEdgeMatcherDescriptor> builtinEdgeMatchers) {
    edgeMatchers.addAll(builtinEdgeMatchers);

    // TODO: Load user defined edge matchers
    // addProjectRelSets(edgeMatchers);
  }

  public static GraphEdgeMatcherDescriptor buildRelationSetMatcher(
      RelationSetDescriptor relationSet) {
    String name = relationSet.getName();
    EdgeMatcher<String> forwardMatcher = 
        GraphEdgeMatchers.createForwardEdgeMatcher(relationSet);;
    return new GraphEdgeMatcherDescriptor("Edges from " + name, forwardMatcher);
  }
}
