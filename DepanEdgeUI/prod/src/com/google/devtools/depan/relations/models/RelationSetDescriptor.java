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

import com.google.devtools.depan.analysis_doc.model.ModelAnalysisDocument;
import com.google.devtools.depan.analysis_doc.model.ModelMatcher;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.model.RelationSets;

import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Set;

/**
 * Document type for named {@link RelationSet} resources.
 * 
 * @since Jun-2016 Contains a RelationSet, but isn't one.  This encourages
 *  clean handling as a document type.
 *
 * @since 2015 Based on legacy RelationshipSet
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class RelationSetDescriptor
    extends ModelAnalysisDocument<RelationSet>{

  public RelationSetDescriptor(
      String name, ModelMatcher matcher, RelationSet info) {
    super(name, matcher, info);
  }

  /**
   * Construct a {@link RelationSetDescriptor} with the given name.
   *
   * @param name name for this {@link RelationSetDescriptor}
   * @param relationSet set of relations to name
   */
  public RelationSetDescriptor(
      String name, DependencyModel model, RelationSet relationSet) {
    super(name, model, relationSet);
  }

  /**
   * Provide a builder to incrementally assemble a RelationSet value object.
   */
  public static Builder createBuilder(String name, DependencyModel model) {
    return new Builder(name, model);
  }

  /**
   * Standard builder to incrementally assemble a RelationSet value object.
   */
  public static class Builder {
    private final String name;

    private final DependencyModel model;

    private final Set<Relation> relations = Sets.newHashSet();

    /**
     * construct a {@link RelationshipSet} with the given name
     *
     * @param name name for this {@link RelationshipSet}
     */
    public Builder(String name, DependencyModel model) {
      this.name = name;
      this.model = model;
    }

    public void addRelation(Relation relation) {
      relations.add(relation);
    }

    public void addRelations(Collection<? extends Relation> source) {
      for (Relation relation : source) {
        relations.add(relation);
      }
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
      return new RelationSetDescriptor(name, model, builtSet);
    }
  }
}
