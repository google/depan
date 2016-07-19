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

package com.google.devtools.depan.graph_doc.model;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.registry.RelationRegistry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Placeholder for name of dependency analysis.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class DependencyModel {

  private final List<String> nodeContribIds;
  private final List<String> relationContribIds;

  public DependencyModel(
      List<String> nodeContribIds, List<String> relationContribIds) {
    this.nodeContribIds = nodeContribIds;
    this.relationContribIds = relationContribIds;
  }

  public List<Relation> getRelations() {
    return RelationRegistry.getRegistryRelations(relationContribIds);
  }

  public List<String> getNodeContribs() {
    return ImmutableList.copyOf(nodeContribIds);
  }

  public List<String> getRelationContribs() {
    return ImmutableList.copyOf(relationContribIds);
  }

  public static class Builder {
    private final List<String> nodeContribIds = Lists.newArrayList();
    private final List<String> relationContribIds = Lists.newArrayList();

    public void addNodeContrib(String nodeContribId) {
      nodeContribIds.add(nodeContribId);
    }

    public void addRelationContrib(String relationContribId) {
      relationContribIds.add(relationContribId);
    }

    public DependencyModel build() {
      return new DependencyModel(nodeContribIds, relationContribIds);
    }
  }
}
