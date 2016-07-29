/*
 * Copyright 2015 The Depan Project Authors
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

package com.google.devtools.depan.edges.matchers;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.model.GraphEdgeMatcher;

/**
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class BinaryEdgeMatcher extends GraphEdgeMatcher {
  private final RelationSet forward;
  private final RelationSet reverse;

  public BinaryEdgeMatcher(RelationSet forward, RelationSet reverse) {
    this.forward = forward;
    this.reverse = reverse;
  }

  @Override
  public boolean relationForward(Relation relation) {
    return forward.contains(relation);
  }

  @Override
  public boolean relationReverse(Relation relation) {
    return reverse.contains(relation);
  }
}
