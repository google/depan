// Copyright 2010 The Depan Project Authors

package com.google.devtools.depan.filesystem.builder;

import com.google.devtools.depan.filesystem.graph.FileElement;
import com.google.devtools.depan.filesystem.graph.FileSystemRelation;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.GraphNode;

import java.io.File;

/**
 * Provides a PathInfo instance for files.  This assumes that files are
 * parented by directories.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class FilePathInfo extends AbstractFilePathInfo {

  public FilePathInfo(File path) {
    super(path);
  }

  @Override
  public GraphNode createNode() {
    return new FileElement(getFilePath().getPath());
  }

  @Override
  public Relation getToRelation() {
    return FileSystemRelation.CONTAINS_FILE;
  }
}
