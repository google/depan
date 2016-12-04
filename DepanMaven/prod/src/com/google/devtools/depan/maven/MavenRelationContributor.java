package com.google.devtools.depan.maven;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.registry.RelationContributor;
import com.google.devtools.depan.maven.graph.MavenElements;

import java.util.Collection;

public class MavenRelationContributor implements RelationContributor {

  public static final String LABEL = "Maven";

  public static final String ID =
      "com.google.devtools.depan.maven.MavenRelationContributor";

  @Override
  public String getLabel() {
    return LABEL;
  }

  @Override
  public Collection<? extends Relation> getRelations() {
    return MavenElements.RELATIONS;
  }
}
