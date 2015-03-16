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

package com.google.devtools.depan.model;

import com.google.devtools.depan.graph.api.DirectedRelation;
import com.google.devtools.depan.graph.api.DirectedRelationFinder;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.basic.MultipleDirectedRelationFinder;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A default implementation for {@link RelationshipSet}.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class RelationshipSetAdapter extends MultipleDirectedRelationFinder
    implements RelationshipSet {

  private final String name;

  /**
   * construct a {@link RelationshipSet} with the given name
   *
   * @param name name for this {@link RelationshipSet}
   */
  public RelationshipSetAdapter(String name) {
    this.name = name;
  }

  /**
   * Construct a {@link RelationshipSet} with the given name, extracting
   * its properties from the <code>finder</code>. This {@link RelationshipSet}
   * will match the same relations as <code>finder</code> (except if it is
   * changed later)
   *
   * @param name name for this {@link RelationshipSet}
   * @param finder a {@link DirectedRelationFinder} to copy.
   */
  public RelationshipSetAdapter(String name, DirectedRelationFinder finder,
      Collection<? extends Relation> relations) {
    this.name = name;
    for (Relation relation : relations) {
      if (finder.matchForward(relation)) {
        this.setMatchForward(relation, true);
      }
      if (finder.matchBackward(relation)) {
        this.setMatchBackward(relation, true);
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.relationships.RelationshipSet
   *      #getBackwardRelations()
   */
  public Collection<Relation> getBackwardRelations() {
    Collection<Relation> backwardRelations = new ArrayList<Relation>();
    for (Relation relation : map.keySet()) {
      DirectedRelation direction = map.get(relation);
      if (direction.matchBackward()) {
        backwardRelations.add(relation);
      }
    }
    return backwardRelations;
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.relationships.RelationshipSet
   *      #getForwardRelations()
   */
  public Collection<Relation> getForwardRelations() {
    Collection<Relation> forwardRelations = new ArrayList<Relation>();
    for (Relation relation : map.keySet()) {
      DirectedRelation direction = map.get(relation);
      if (direction.matchForward()) {
        forwardRelations.add(relation);
      }
    }
    return forwardRelations;
  }

  /* (non-Javadoc)
   * @see com.google.devtools.depan.relationships.RelationshipSet#getName()
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the friendly-name of this object.
   *
   * @return The friendly-name of this object.
   */
  @Override
  public String getDisplayName() {
    if (name.trim().isEmpty()) {
      StringBuilder result = new StringBuilder();
      result.append("Temporary Set: ");
      result.append(super.toString());
      return result.toString();
    }
    return getName();
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.relationship.RelationshipSet
   *      #setMatchBackward(com.google.devtools.depan.graph.api.Relation,
   *      boolean)
   */
  public void setMatchBackward(Relation relation, boolean isMatching) {
    this.addOrReplaceRelation(
        relation, this.matchForward(relation), isMatching);
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.relationship.RelationshipSet
   *      #setMatchForward(com.google.devtools.depan.graph.api.Relation,
   *      boolean)
   */
  public void setMatchForward(Relation relation, boolean isMatching) {
    this.addOrReplaceRelation(
        relation, isMatching, this.matchBackward(relation));
  }

  @Override
  public String toString() {
    return name;
  }

}
