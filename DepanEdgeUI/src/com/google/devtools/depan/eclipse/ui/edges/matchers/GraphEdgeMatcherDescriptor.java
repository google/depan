/*
 * Copyright 2007 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.devtools.depan.eclipse.ui.edges.matchers;

import com.google.devtools.depan.graph.api.Edge;
import com.google.devtools.depan.graph.api.EdgeMatcher;
import com.google.devtools.depan.graph.api.Relation;

/**
 * Adds a name to a {@link EdgeMatcher}.
 * 
 * @since 2015 Based on legacy {@code RelationshipSet}.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class GraphEdgeMatcherDescriptor
    implements EdgeMatcher<String> {

  private final String name;
  private final EdgeMatcher<String> matcher;

  /**
   * Construct a {@link GraphEdgeMatcherDescriptor} with the given name.
   *
   * @param name name for this {@link RelationshipSet}
   * @param matcher edge matcher for the supplied name
   */
  public GraphEdgeMatcherDescriptor(String name, EdgeMatcher<String> matcher) {
    this.name = name;
    this.matcher = matcher;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean relationForward(Relation relation) {
    return matcher.relationForward(relation);
  }

  @Override
  public boolean relationReverse(Relation relation) {
    return matcher.relationReverse(relation);
  }

  @Override
  public boolean edgeForward(Edge<String> edge) {
    return matcher.edgeForward(edge);
  }

  @Override
  public boolean edgeReverse(Edge<String> edge) {
    return matcher.edgeReverse(edge);
  }
}
