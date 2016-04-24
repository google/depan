// Copyright 2010 The Depan Project Authors

package com.google.devtools.depan.filesystem.builder;

import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.chain.DependenciesListener;

/**
 * Install the {@link GraphNode}s obtained from hierarchical {@link PathInfo}
 * instance into the dependency model.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class FileSystemTreeBuilder extends TreeBuilder {
  private final DependenciesListener builder;

  public FileSystemTreeBuilder(DependenciesListener builder) {
    super();
    this.builder = builder;
  }

  @Override
  protected void insertEdge(
      GraphNode parentNode, GraphNode childNode, PathInfo childInfo) {
    builder.newDep(parentNode, childNode, childInfo.getToRelation());
  }

  @Override
  protected GraphNode lookupNode(PathInfo path) {
    GraphNode node = path.createNode();
    GraphNode found = builder.newNode(node);
    if (node != found) {
      return found;
    }
    return null;
  }
}
