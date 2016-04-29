/**
 * 
 */
package com.google.devtools.depan.eclipse.ui.edges.matchers;

import com.google.devtools.depan.edges.matchers.GraphEdgeMatchers;

/**
 * @author Standard Lee
 */
public class GraphEdgeMatcherDescriptors {

  private GraphEdgeMatcherDescriptors() {
    // Prevent instantiation
  }

  public static final GraphEdgeMatcherDescriptor EMPTY =
      new GraphEdgeMatcherDescriptor("Empty", GraphEdgeMatchers.EMPTY);

  public static final GraphEdgeMatcherDescriptor FORWARD =
      new GraphEdgeMatcherDescriptor(
          "Forward edges", GraphEdgeMatchers.FORWARD);
}
