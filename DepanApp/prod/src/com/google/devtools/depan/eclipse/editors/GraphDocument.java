/*
 * Copyright 2010 The Depan Project Authors
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.devtools.depan.eclipse.plugins.SourcePlugin;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.RelationshipSet;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * A document that provides information about a dependency graph.  In addition
 * to the basic nodes and edges of the dependency graph is supplemented with
 * a set of associated Analysis plugins.  This allows the UI to make productive
 * choices for the user in the face of many different kinds of analysis.
 * 
 * <p>This data structure is expected to replace the raw dependency graph files
 * (.dpang) that DepAn uses for persistent graphs.
 *
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class GraphDocument {

  /**
   * Standard extension to use when loading or saving {@code ViewDocument}s.
   * The characters represent "DepAn Graph Info".
   */
  public static final String EXTENSION = "dgi";

  /**
   * The group of graph analyzer plugins that the UI should use for display
   * of the dependency graph.  This is intended to be user editable, but
   * more often is is just pre-configured by the analysis Wizards.
   * 
   * <p>The UI is expected to display choices in the same order as the the
   * list of analyzers.  No duplicates are allowed, and the first entry
   * ({@code [0]}) is the source for the initial (default) choices.
   * 
   * <p>TODO:  Allow the order of dependency analyzers to be altered.
   * Allow dependency analyzers to be deleted.
   */
  private final List<SourcePlugin> graphAnalyzers;

  /**
   * The dependency graph provided by this document.
   */
  private GraphModel graph;

  /**
   * Create a graph from an analyzer an a graph model.
   * 
   * @param defaultAnalyzer
   * @param graph
   */
  public GraphDocument(GraphModel graph, SourcePlugin defaultAnalyzer) {
    this.graphAnalyzers = Lists.newArrayList(defaultAnalyzer);
    this.graph = graph;
  }

  /**
   * Create a graph document from a list of analyzers and a graph model.
   * The list of analyzers may not be empty, and the first element will become
   * the default analyzer for the document.  Any non-duplicate analyzers are
   * added to the graph document.
   * 
   * @param analyzers non-empty list of analyzers for this graph
   * @param graph
   */
  public GraphDocument(GraphModel graph, List<SourcePlugin> analyzers) {
    this(graph, analyzers.get(0));

    for (SourcePlugin analyzer : analyzers) {
      addAnalysis(analyzer);
    }
  }

  /////////////////////////////////////
  // Handle analysis preference

  public SourcePlugin getDefaultAnalysis() {
    return graphAnalyzers.get(0);
  }

  /**
   * Make the analyzer become the default analysis choice.
   * 
   * <p>The implementation ensures that the analyzer is the first entry
   * in the list of analyzers.
   * 
   * @param analysis dependency analyzer to add
   */
  public void setDefaultAnalyis(SourcePlugin analysis) {
    if (getDefaultAnalysis() == analysis) {
      return;
    }
    if (graphAnalyzers.contains(analysis)) {
      graphAnalyzers.remove(analysis);
    }
    graphAnalyzers.add(0, analysis);
  }

  /**
   * Provide the current set of analyzers.
   * 
   * @return list of dependency analyzers for the UI to use when manipulating
   *     this graph model
   */
  public List<SourcePlugin> getAnalyzers() {
    return ImmutableList.copyOf(graphAnalyzers);
  }

  /**
   * Add the analyzer to the list of dependency analyzers for this graph.
   * Duplicates are not allowed.  If the analyzer is new, it will be added
   * to the end of the list.
   * 
   * @param analysis
   */
  public void addAnalysis(SourcePlugin analysis) {
    if (!graphAnalyzers.contains(analysis)) {
      graphAnalyzers.add(analysis);
    }
  }

  /**
   * Provide a complete list of all RelationshipSets that are built-in by
   * any of the analysis plug-ins.
   */
  public Collection<RelationshipSet> getBuiltinAnalysisRelSets() {
    Set<RelationshipSet> result = Sets.newHashSet();
    for (SourcePlugin plugin : getAnalyzers()) {
      result.addAll(plugin.getBuiltinRelationshipSets());
    }
    return result;
  }

  /////////////////////////////////////
  // Graph Model access

  public GraphModel getGraph() {
    return graph;
  }
}
