package com.google.devtools.depan.java;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.registry.RelationContributor;
import com.google.devtools.depan.java.graph.JavaElements;

import java.util.Collection;

public class JavaRelationContributor implements RelationContributor {

  public static final String LABEL = "Java";

  public static final String ID =
      "com.google.devtools.depan.java.JavaRelationContributor";

  @Override
  public String getLabel() {
    return LABEL;
  }

  @Override
  public Collection<? extends Relation> getRelations() {
    return JavaElements.RELATIONS;
  }
}
