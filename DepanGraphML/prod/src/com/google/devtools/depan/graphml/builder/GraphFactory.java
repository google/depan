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

package com.google.devtools.depan.graphml.builder;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.GraphNode;

/**
 * Define the translation between a GraphML nodes and edges to DepAn's
 * nodes and edges.
 * 
 * In a more generalized GraphML integration, this interface should also
 * be an Eclipse extension point.  There are many feasible ways to translate
 * a GraphML file into a DepAn .dgi file.  A rich GraphML integration might
 * offer user-defined translations.
 * 
 * EdgeLoader and NodeLoader probably need element and attribute handlers
 * from this class.  This should be added when GraphML support is generalized
 * beyond the current Maven integration support.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public interface GraphFactory {

  /**
   * Provide the list of analysis plugins that contribute to the DepAn
   * graph.  DepAn's UI components use this to show the relevant nodes
   * and edges for each graph.
   */
  String[] getAnalysisPlugins();

  /**
   * Provide the DepAn {@link GraphNode} that is associated with
   * the supplied info gathered from the GraphML node definition.
   * 
   * For Maven, this is the full Artifact label and it is parsed
   * and repacked into an {@code ArtifactNode} defined in the
   * Maven plugin.
   */
  GraphNode buildNode(String info);

  /**
   * Provide the DepAn {@link Relation} that is associated with
   * the supplied info gathered from the GraphML edge definition.
   * 
   * For Maven, this is Relation type based on the edge label defined
   * in the GraphML file.
   */
  Relation buildRelation(String info);
}
