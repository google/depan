// Copyright 2010 The Depan Project Authors

package com.google.devtools.depan.filesystem.builder;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.GraphNode;

/**
 * Provides descriptive and parenting information for paths that are being
 * added to a hierarchical dependency model.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public interface PathInfo {

  /**
   * Provide the full path as a string.
   *
   * @return path for instance
   */
  public String getId();

  /**
   * Create the GraphNode appropriate for this path.
   * 
   * <p>In the FileSystem dependency framework, you might get a
   * {@link FileElement} or a {@link DirectoryElement}.  Other dependency
   * frameworks are free to generated other types of {@link GraphNode}s
   * as appropriate.
   * 
   * @return GraphNode node associated with this path/
   */
  public GraphNode createNode();

  /**
   * Provide the {@link Relation} instance to use for edges from the path's
   * parent node to its own dependency graph node.
   * 
   * @return {@link Relation} instance for incoming edges
   */
  public Relation getToRelation();

  /**
   * Provide a {@link PathInfo} instance for this instance's hierarchical
   * parent.
   * 
   * @return {@link PathInfo} for parent
   */
  public PathInfo getParentInfo();
}
