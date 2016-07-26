/*
 * Copyright 2014 The Depan Project Authors
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

/**
 * Handle composition of Graphs.
 * 
 * Multiple combination operators are feasible.
 * The current set of composition operations is smale:
 * - merge();
 */
package com.google.devtools.depan.graph_doc.operations;

import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.model.GraphModel;

/**
 * @author Lee Carver <leeca@pnambic.com>
 */
public class MergeGraphDoc {

  MergeGraphModel resultGraph = new MergeGraphModel();

  MergeDependencyModel resultModel = new MergeDependencyModel();

  public GraphDocument getGraphDocument() {
    GraphModel graph = resultGraph.getGraphModel();
    DependencyModel model = resultModel.getDependencyModel();
    GraphDocument result = new GraphDocument(model, graph);

    // Release all internal state
    resultGraph = null;
    resultModel = null;

    return result;
  }

  public void merge(GraphDocument nextGraph) {
    resultGraph.merge(nextGraph.getGraph());
    resultModel.merge(nextGraph.getDependencyModel());
  }
}
