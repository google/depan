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
    // Prevent instantiation
  }

  public static RelationSet EMPTY = new RelationSet() {

    @Override
    public boolean contains(Relation relation) {
      return false;
    }
  };

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
  };

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
  }
}
