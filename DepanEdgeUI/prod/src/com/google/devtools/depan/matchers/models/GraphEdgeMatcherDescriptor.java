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

package com.google.devtools.depan.matchers.models;

import com.google.devtools.depan.graph.api.EdgeMatcher;

/**
 * Document type for named {@link EdgeMatcher}s.
 * 
 * @since 2015 Based on legacy {@code RelationshipSet}.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class GraphEdgeMatcherDescriptor {
    // implements EdgeMatcher<String> {

  /**
   * Preferred file extension for documents.
   */
  public static final String EXTENSION = "gemxml";

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

  public EdgeMatcher<String> getEdgeMatcher() {
    return matcher;
  }
}
