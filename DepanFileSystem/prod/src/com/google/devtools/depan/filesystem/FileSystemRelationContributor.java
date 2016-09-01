package com.google.devtools.depan.filesystem;

import com.google.devtools.depan.filesystem.graph.FileSystemElements;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.registry.RelationContributor;

import java.util.Collection;

public class FileSystemRelationContributor implements RelationContributor {

  public static final String LABEL = "File System";

  public static final String ID =
      "com.google.devtools.depan.filesystem.FileSystemRelationContributor";

  @Override
  public String getLabel() {
    return LABEL;
  }

  @Override
  public Collection<? extends Relation> getRelations() {
    return FileSystemElements.RELATIONS;
  }
}
