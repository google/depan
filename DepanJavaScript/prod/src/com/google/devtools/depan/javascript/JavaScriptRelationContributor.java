package com.google.devtools.depan.javascript;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.registry.RelationContributor;
import com.google.devtools.depan.javascript.graph.JavaScriptElements;

import java.util.Collection;

public class JavaScriptRelationContributor implements RelationContributor {

  public static final String LABEL = "JavaScript";

  public static final String ID =
      "com.google.devtools.depan.filesystem.JavaScriptRelationContributor";

  @Override
  public String getLabel() {
    return LABEL;
  }

  @Override
  public Collection<? extends Relation> getRelations() {
    return JavaScriptElements.RELATIONS;
  }
}
