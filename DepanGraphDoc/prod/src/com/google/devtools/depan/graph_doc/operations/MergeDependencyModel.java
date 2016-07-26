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

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Combine two {@link DependencyModel}, preserving the order to contributions.
 * 
 * The resulting order is not fully specified.  For the combination
 * [ A, B ] + [ Z, B ], the result is allowed to be either [ A, Z, B ]
 * or [ Z, A, B ].  In practice, it is common for latter merges to be latter
 * in the list, but this is not required.
 * 
 * @author Lee Carver <leeca@pnambic.com>
 */
public class MergeDependencyModel {

  // Dependency Model
  private List<String> currNodes = Lists.newArrayList();

  private List<String> currRels = Lists.newArrayList();

  public DependencyModel getDependencyModel() {
    DependencyModel result = new DependencyModel(currNodes, currRels);

    // Release all internal state
    currNodes = null;
    currRels = null;

    return result;
  }

  /**
   * Merge the node and relation contributions from each Dependency Model
   * @param dependencyModel
   */
  public void merge(DependencyModel depModel) {
    editContribOrder(currNodes, depModel.getNodeContribs());
    editContribOrder(currRels, depModel.getRelationContribs());
  }

  /**
   * Add any new contributions before a match on any existing one.
   * Contributions without an order dependency on an existing contribution
   * are added at the end.
   */
  private void editContribOrder(
      List<String> editContribs, List<String> newContribs) {
    List<String> append = Lists.newArrayList();

    for (String contrib : newContribs) {
      int index = editContribs.indexOf(contrib);
      if (index < 0) {
        append.add(contrib);
      } else if (!(append.isEmpty())) {
        editContribs.addAll(index, append);
        append.clear();
      }
    }

    editContribs.addAll(append);
  }
}
