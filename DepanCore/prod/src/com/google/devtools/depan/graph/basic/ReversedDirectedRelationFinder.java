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

package com.google.devtools.depan.graph.basic;

import com.google.devtools.depan.graph.api.DirectedRelationFinder;
import com.google.devtools.depan.graph.api.Relation;

/**
 * A {@link DirectedRelationFinder} that reverse all selected relations of
 * his parent given at construction time.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class ReversedDirectedRelationFinder implements DirectedRelationFinder {

  private final DirectedRelationFinder parent;

  public ReversedDirectedRelationFinder(DirectedRelationFinder parent) {
    this.parent = parent;
  }

  @Override
  public boolean matchBackward(Relation find) {
    return !parent.matchBackward(find);
  }

  @Override
  public boolean matchForward(Relation find) {
    return !parent.matchForward(find);
  }

}
