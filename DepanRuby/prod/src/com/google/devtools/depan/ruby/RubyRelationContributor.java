package com.google.devtools.depan.ruby;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.registry.RelationContributor;
import com.google.devtools.depan.ruby.graph.RubyElements;

import java.util.Collection;

public class RubyRelationContributor implements RelationContributor {

  public static final String LABEL = "Ruby";

  public static final String ID =
      "com.google.devtools.depan.ruby.RubyRelationContributor";

  @Override
  public String getLabel() {
    return LABEL;
  }

  @Override
  public Collection<? extends Relation> getRelations() {
    return RubyElements.RELATIONS;
  }
}
