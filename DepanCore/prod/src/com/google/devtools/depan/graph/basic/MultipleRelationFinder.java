/*
 * Copyright 2007 Google Inc.
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

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.api.RelationFinder;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class MultipleRelationFinder implements RelationFinder {

  private HashSet<Relation> set = new HashSet<Relation>();
  
  public MultipleRelationFinder(Collection<Relation> matchingRelations) {
    set.addAll(matchingRelations);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.google.devtools.depan.graph.api.RelationFinder
   *      #match(com.google.devtools.depan.graph.api.Relation)
   */
  public boolean match(Relation find) {
    return set.contains(find);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.google.devtools.depan.graph.api.DirectedRelationFinder
   *      #matchBackward(com.google.devtools.depan.graph.api.Relation)
   */
  public boolean matchBackward(Relation find) {
    return match(find);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.google.devtools.depan.graph.api.DirectedRelationFinder
   *      #matchForward(com.google.devtools.depan.graph.api.Relation)
   */
  public boolean matchForward(Relation find) {
    return match(find);
  }

}
