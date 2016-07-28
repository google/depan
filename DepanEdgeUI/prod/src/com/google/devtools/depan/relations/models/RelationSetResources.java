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

package com.google.devtools.depan.relations.models;

import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.resources.ResourceContainer;
import com.google.devtools.depan.resources.analysis.AnalysisResources;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class RelationSetResources {

  public static final String RELATION_SETS = "relation_sets";

  static {
    AnalysisResources.getRoot().addChild(RELATION_SETS);
  }

  private RelationSetResources() {
    // Prevent instantiation.
  }

  public static ResourceContainer getContainer() {
    return AnalysisResources.getRoot().getChild(RELATION_SETS);
  }

  public static List<RelationSetDescriptor> getRelationSets(
      DependencyModel model) {

    List<RelationSetDescriptor> result = Lists.newArrayList();

    // Filter for GEMs with the supplied model.
    for (Object resource : getContainer().getResources()) {
        if (resource instanceof RelationSetDescriptor) {
          RelationSetDescriptor checkRes = (RelationSetDescriptor) resource;

          if (checkRes.forModel(model)) {
            result.add(checkRes);
          }
        }
      }

    return result;
  }
}
