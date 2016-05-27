package com.google.devtools.depan.graph_doc.eclipse.ui.resources;

import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptors;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.models.RelationSetDescriptors;

import java.util.Arrays;
import java.util.List;

public class GraphResources {
  
  private final DependencyModel depsModel;

  public GraphResources(DependencyModel depsModel) {
    this.depsModel = depsModel;
  }

  public RelationSetDescriptor getDefaultRelationSet() {
    return RelationSetDescriptors.EMPTY;
  }

  public List<RelationSetDescriptor> getRelationSetsChoices() {
    return Arrays.asList(RelationSetDescriptors.EMPTY);
  }

  public GraphEdgeMatcherDescriptor getDefaultEdgeMatcher() {
    return GraphEdgeMatcherDescriptors.FORWARD;
  }

  public List<GraphEdgeMatcherDescriptor> getEdgeMatcherChoices() {
    return Arrays.asList(
        GraphEdgeMatcherDescriptors.FORWARD,
        GraphEdgeMatcherDescriptors.EMPTY);
  }
}
