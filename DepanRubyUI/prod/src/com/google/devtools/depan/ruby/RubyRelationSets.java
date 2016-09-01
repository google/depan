/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.ruby;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.models.RelationSetDescriptor.Builder;
import com.google.devtools.depan.ruby.graph.RubyElements;
import com.google.devtools.depan.ruby.graph.RubyRelation;

import com.google.common.collect.Lists;

import java.util.Collection;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class RubyRelationSets {

  /**
   * List of all built-in RelationshipSets. Make it easier to iterate.
   */
  private static final Collection<RelationSetDescriptor> BUILT_IN_SETS;

  private static final RelationSetDescriptor EXTENDS_TYPE;

  private static final RelationSetDescriptor MEMBER_ELEMENTS;

  private static final RelationSetDescriptor RUBY_ALL;

  static {

    RelationSetDescriptor.Builder dependentBuilder =
        createRelSetBuilder("Ruby Extends");
    dependentBuilder.addRelation(RubyRelation.EXTENDS_TYPE);
    EXTENDS_TYPE = dependentBuilder.build();

    RelationSetDescriptor.Builder moduleBuilder =
        createRelSetBuilder("Ruby Members");
    moduleBuilder.addRelation(RubyRelation.CLASS_MEMBER);
    moduleBuilder.addRelation(RubyRelation.INSTANCE_MEMBER);
    moduleBuilder.addRelation(RubyRelation.SINGLETON_MEMBER);
    MEMBER_ELEMENTS = moduleBuilder.build();

    RelationSetDescriptor.Builder containerBuilder =
        createRelSetBuilder("All Ruby");
    for (Relation r : RubyElements.RELATIONS) {
      containerBuilder.addRelation(r);
    }

    RUBY_ALL = containerBuilder.build();

    BUILT_IN_SETS = Lists.newArrayList();
    BUILT_IN_SETS.add(MEMBER_ELEMENTS);
    BUILT_IN_SETS.add(EXTENDS_TYPE);
    BUILT_IN_SETS.add(RUBY_ALL);
  }

  /**
   * Returns all built-in relationship sets defined by the Ruby plug-in.
   */
  public static Collection<RelationSetDescriptor> getBuiltinSets() {
    return BUILT_IN_SETS;
  }

  public static RelationSetDescriptor getDefaultDescriptor() {
    return RUBY_ALL;
  }

  private RubyRelationSets() {
    // Prevent instantiation.
  }

  private static Builder createRelSetBuilder(String name) {
    return RelationSetDescriptor.createBuilder(
        name, RubyPluginActivator.RUBY_MODEL);
  }
}
