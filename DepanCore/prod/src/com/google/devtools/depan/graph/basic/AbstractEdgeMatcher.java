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

package com.google.devtools.depan.graph.basic;

import com.google.devtools.depan.graph.api.Edge;
import com.google.devtools.depan.graph.api.EdgeMatcher;

/**
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public abstract class AbstractEdgeMatcher<T> implements EdgeMatcher<T> {

  @Override
  public boolean edgeForward(Edge<T> edge) {
    return relationForward(edge.getRelation());
  }

  @Override
  public boolean edgeReverse(Edge<T> edge) {
    return relationReverse(edge.getRelation());
  }
}
