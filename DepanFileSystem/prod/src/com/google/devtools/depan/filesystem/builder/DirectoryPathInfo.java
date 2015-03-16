// Copyright 2010 The Depan Project Authors

package com.google.devtools.depan.filesystem.builder;

import com.google.devtools.depan.filesystem.graph.DirectoryElement;
import com.google.devtools.depan.filesystem.graph.FileSystemRelation;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.GraphNode;

import java.io.File;

/**
 * Provides a PathInfo instance for directories.  This assumes that directories
 * are parented by other directories.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class DirectoryPathInfo extends AbstractFilePathInfo {

  public DirectoryPathInfo(File path) {
    super(path);
  }

  @Override
  public GraphNode createNode() {
    return new DirectoryElement(getFilePath().getPath());
  }

  @Override
  public Relation getToRelation() {
    return FileSystemRelation.CONTAINS_DIR;
  }
}
