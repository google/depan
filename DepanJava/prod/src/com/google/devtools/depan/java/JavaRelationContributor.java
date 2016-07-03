package com.google.devtools.depan.java;

import com.google.devtools.depan.java.graph.JavaRelation;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.registry.RelationContributor;

import java.util.Arrays;
import java.util.Collection;

public class JavaRelationContributor implements RelationContributor {

  public static final String ID =
      "com.google.devtools.depan.java.JavaRelationContributor";

  @Override
  public Collection<? extends Relation> getRelations() {
    return Arrays.asList(JavaRelation.values());
  }
}
