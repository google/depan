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

package com.google.devtools.depan.relations.models;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.model.RelationSets;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Adds a name to a {@link RelationSet}.
 * 
 * This is a persistable document type.
 * 
 * @since 2015 Based on legacy RelationshipSet
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class RelationSetDescriptor
    implements RelationSet {

  public static final String EXTENSION = "relxml";

  private final String name;

  private final RelationSet relationSet;

  /**
   * Construct a {@link RelationSetDescriptor} with the given name.
   *
   * @param name name for this {@link RelationSetDescriptor}
   * @param relationSet set of relations to name
   */
  public RelationSetDescriptor(String name, RelationSet relationSet) {
    this.name = name;
    this.relationSet = relationSet;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean contains(Relation relation) {
    return relationSet.contains(relation);
  }

  /**
   * Provide a builder to incrementally assemble a RelationSet value object.
   */
  public static Builder createBuilder(String name) {
    return new Builder(name);
  }

  /**
   * Standard builder to incrementally assemble a RelationSet value object.
   */
  public static class Builder {
    private final String name;

    private final Set<Relation> relations = Sets.newHashSet();

    /**
     * construct a {@link RelationshipSet} with the given name
     *
     * @param name name for this {@link RelationshipSet}
     */
    public Builder(String name) {
      this.name = name;
    }

    public void addRelation(Relation relation) {
      relations.add(relation);
    }

    /**
     * Allow builder sets to contribute to each other for consistency,
     * without creating a dependency.
     */
    public void addBuilderSet(Builder builer) {
      relations.addAll(builer.relations);
    }

    public RelationSetDescriptor build() {
      // Make a defensive copy.
      Set<Relation> clone = Sets.newHashSet(relations);
      RelationSet builtSet = RelationSets.createSimple(clone);
      return new RelationSetDescriptor(name, builtSet);
    }
  }
}
