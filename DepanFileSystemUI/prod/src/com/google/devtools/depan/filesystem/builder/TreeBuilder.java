// Copyright 2010 The Depan Project Authors

package com.google.devtools.depan.filesystem.builder;

import com.google.devtools.depan.model.GraphNode;

/**
 * Build hierarchical dependencies, including any required parent, into a
 * dependency graph.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public abstract class TreeBuilder {

  /**
   * Ensure that the leaf path is installed in the dependency graph.  Any
   * missing parent elements are created as needed.
   * 
   * @param leafInfo hierarchical path to install
   * @return {@link GraphNode} from the dependency graph
   */
  public final GraphNode insertLeaf(PathInfo leafInfo) {
    // Early exit if leafPath is already in graph
    GraphNode childNode = lookupNode(leafInfo);
    if (null != childNode) {
      return childNode;
    }

    // Setup for tree ascent
    PathInfo childInfo = leafInfo;
    childNode = childInfo.createNode();
    final GraphNode result = childNode;

    PathInfo nextInfo = childInfo.getParentInfo();
    while (null != nextInfo) {
      PathInfo parentInfo = nextInfo;

      GraphNode parentNode = lookupNode(parentInfo);
      if (null != parentNode) {
        nextInfo = null;
      }
      else {
        parentNode = parentInfo.createNode();
        nextInfo = parentInfo.getParentInfo();
      }

      insertEdge(parentNode, childNode, childInfo);
      childInfo = parentInfo;
      childNode = parentNode;
    }
    return result;
  }

  /**
   * Insert the path into the dependency graph with the correct parent and
   * relation type.
   * 
   * @param parentNode head of hierarchical edge
   * @param childNode tail of hierarchical edge
   * @param childInfo provides relation type for created edge
   */
  protected abstract void insertEdge(
      GraphNode parentNode, GraphNode childNode, PathInfo childInfo);

  /**
   * Provide an existing node for the path, or {@code null} if no node exists
   * at this time.
   * 
   * @param path for node
   * @return {@link GraphNode} for path
   */
  protected abstract GraphNode lookupNode(PathInfo path);
}
