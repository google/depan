package com.google.devtools.depan.ruby;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.registry.RelationContributor;
import com.google.devtools.depan.ruby.graph.RubyRelation;

import java.util.Arrays;
import java.util.Collection;

public class RubyRelationContributor implements RelationContributor {

  public static final String ID =
      "com.google.devtools.depan.java.JavaRelationContributor";

  @Override
  public Collection<? extends Relation> getRelations() {
    return Arrays.asList(RubyRelation.values());
  }
}
