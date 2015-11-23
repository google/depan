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

package com.google.devtools.depan.model;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.api.RelationSet;

import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Set;

/**
 * Utility object and methods for {@link RelationSet}s.
 * 
 * Several common relation set definitions and factories are provided.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class RelationSets {

  private RelationSets() {
    // Prevent instantiation.
  }

  /////////////////////////////////////
  // ALL Relation Set

  // Only need one instance
  public static final RelationSet ALL = new  RelationSet() {
    @Override
    public boolean contains(Relation relation) {
      return true;
    }
  };

  /////////////////////////////////////
  // EMPTY Relation Set

  // Only need one instance
  public static RelationSet EMPTY = new RelationSet() {
    @Override
    public boolean contains(Relation relation) {
      return false;
    }
  };

  /////////////////////////////////////
  // Relation sets for a single relation

  public static RelationSet createSingle(Relation relation) {
    return new Single(relation);
  }

  public static class Single implements RelationSet {

    private final Relation single;

    public Single(Relation single) {
      this.single = single;
    }

    @Override
    public boolean contains(Relation relation) {
      return (single == relation);
    }

    /**
     * Provide the one relation in this relation-set.
     * Intended primarily for serialization.
     */
    public Relation getRelation() {
      return single;
    }
  }

  /////////////////////////////////////
  // Array implemented relation sets
  // Common for plugin definitions.

  public static RelationSet createArray(Relation[] relations) {
    return new Array(relations);
  }

  public static class Array implements RelationSet {

    private final Relation[] relations;

    public Array(Relation[] relations) {
      this.relations = relations;
    }

    @Override
    public boolean contains(Relation relation) {
      return Arrays.asList(relations).contains(relation);
    }

    /**
     * Provide a snapshot of the array of relations.
     * Intended primarily for serialization.
     */
    public Relation[] getRelations() {
      return Arrays.copyOf(relations, relations.length);
    }
  };

  /////////////////////////////////////
  // Set implemented relation sets

  public static RelationSet createSimple(Set<Relation> relationSet) {
    return new Simple(relationSet);
  }

  public static class Simple implements RelationSet {
    private final Set<Relation> relationSet;

    public Simple(Set<Relation> relationSet) {
      this.relationSet = relationSet;
    }

    @Override
    public boolean contains(Relation relation) {
      return relationSet.contains(relation);
    }

    /**
     * Provide a snapshot of the set of relations.
     * Intended primarily for serialization.
     */
    public Set<Relation> getRelations() {
      return Sets.newHashSet(relationSet);
    }
  }
}
