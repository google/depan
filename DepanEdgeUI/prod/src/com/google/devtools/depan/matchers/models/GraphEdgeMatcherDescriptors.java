/**
 * 
 */
package com.google.devtools.depan.matchers.models;

import com.google.devtools.depan.analysis_doc.model.ModelMatcher;
import com.google.devtools.depan.edges.matchers.GraphEdgeMatchers;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class GraphEdgeMatcherDescriptors {

  private GraphEdgeMatcherDescriptors() {
    // Prevent instantiation
  }

  public static final GraphEdgeMatcherDescriptor EMPTY =
      new GraphEdgeMatcherDescriptor(
          "Empty", ModelMatcher.ALL_MODELS, GraphEdgeMatchers.EMPTY);

  public static final GraphEdgeMatcherDescriptor FORWARD =
      new GraphEdgeMatcherDescriptor(
          "Forward edges", ModelMatcher.ALL_MODELS, GraphEdgeMatchers.FORWARD);
}
