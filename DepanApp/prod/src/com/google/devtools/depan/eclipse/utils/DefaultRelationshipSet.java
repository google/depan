/*
 * Copyright 2008 The Depan Project Authors
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

package com.google.devtools.depan.eclipse.utils;

import java.util.Collection;
import java.util.Collections;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.RelationshipSet;

public class DefaultRelationshipSet implements RelationshipSet {

  RelationshipSet underlyingSet = null;

  public final static RelationshipSet SET = new DefaultRelationshipSet();

  private DefaultRelationshipSet() {
  }

  @Override
  public Collection<Relation> getBackwardRelations() {
    if (underlyingSet == null) return Collections.emptyList();
    return null;
  }

  @Override
  public Collection<Relation> getForwardRelations() {
    if (underlyingSet == null) return Collections.emptyList();
    return null;
  }

  @Override
  public String getName() {
    if (underlyingSet == null) return "Default";
    return "Default ("+underlyingSet.getName()+")";
  }

  @Override
  public void setMatchBackward(Relation relation, boolean isMatching) {
    if (underlyingSet != null) underlyingSet.setMatchBackward(relation, isMatching);
  }

  @Override
  public void setMatchForward(Relation relation, boolean isMatching) {
    if (underlyingSet != null) underlyingSet.setMatchForward(relation, isMatching);
  }

  @Override
  public boolean matchBackward(Relation find) {
    if (underlyingSet == null) return false;
    return underlyingSet.matchBackward(find);
  }

  @Override
  public boolean matchForward(Relation find) {
    if (underlyingSet == null) return false;
    return underlyingSet.matchForward(find);
  }

}
