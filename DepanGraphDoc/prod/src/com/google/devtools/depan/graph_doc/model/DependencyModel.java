package com.google.devtools.depan.graph_doc.model;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.registry.RelationRegistry;

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
