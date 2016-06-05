package com.google.devtools.depan.filesystem.graph;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.Element;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.Collection;

/**
 * Provide reference collections for FileSystem's contributed graph elements.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class FileSystemElements {

  private FileSystemElements() {
    // Prevent instantiations.
  }

  public static final Collection<Class<? extends Element>> NODES;
  static {
    NODES = Lists.newArrayList();
    NODES.add(DirectoryElement.class);
    NODES.add(FileElement.class);
  }
 
  public static final Collection<? extends Relation> RELATIONS =
      Arrays.asList(FileSystemRelation.values());
}
