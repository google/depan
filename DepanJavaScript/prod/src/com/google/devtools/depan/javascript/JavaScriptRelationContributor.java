package com.google.devtools.depan.javascript;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.registry.RelationContributor;
import com.google.devtools.depan.javascript.graph.JavaScriptRelation;

import java.util.Arrays;
import java.util.Collection;

public class JavaScriptRelationContributor implements RelationContributor {

  public static final String ID =
      "com.google.devtools.depan.filesystem.FileSystemRelationContributor";

  @Override
  public Collection<? extends Relation> getRelations() {
    return Arrays.asList(JavaScriptRelation.values());
  }
}
