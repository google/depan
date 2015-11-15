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

package com.google.devtools.depan.graph.api;

/**
 * Use to determine if an edge from a node is matched.
 * 
 * An edge match means either:
 * <ul>
 *  <li>node is head, and the relation matches forward</li>
 *  <li>node is tail, and the relation matches reverse</li>
 * </ul>
 * In most practical cases, the caller has efficient access to whether
 * a node is head or tail for any edge.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public interface EdgeMatcher<T> {

  boolean relationForward(Relation relation);

  boolean relationReverse(Relation relation);

  boolean edgeForward(Edge<T> edge);

  boolean edgeReverse(Edge<T> edge);
}
