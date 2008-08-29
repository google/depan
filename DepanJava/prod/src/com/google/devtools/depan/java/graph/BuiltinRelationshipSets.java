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

package com.google.devtools.depan.java.graph;

import com.google.devtools.depan.collect.Lists;
import com.google.devtools.depan.model.RelationshipSet;
import com.google.devtools.depan.model.RelationshipSetAdapter;

import java.util.Collection;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class BuiltinRelationshipSets {

  /**
   * List of all built-in RelationshipSets. Make it easier to iterate.
   */
  public static Collection<RelationshipSet> builtins;

  /**
   * Container relationships.
   */
  public static final RelationshipSetAdapter CONTAINER =
      new RelationshipSetAdapter("Containers");

  /**
   *
   */
  public static final RelationshipSetAdapter PKG_MEMBER =
      new RelationshipSetAdapter("Package Members");

  /**
   *
   */
  public static final RelationshipSetAdapter CLASS_MEMBER =
      new RelationshipSetAdapter("Class Members");

  /**
   *
   */
  public static final RelationshipSetAdapter STATIC_MEMBER =
      new RelationshipSetAdapter("Static Members");

  /**
   *
   */
  public static final RelationshipSetAdapter INSTANCE_MEMBER =
      new RelationshipSetAdapter("Instance Members");

  /**
   *
   */
  public static final RelationshipSetAdapter EXTENSION =
      new RelationshipSetAdapter("Extensions");

  /**
   *
   */
  public static final RelationshipSetAdapter USES =
      new RelationshipSetAdapter("Uses");

  /**
   * A set matching all relations in both directions.
   */
  public static final RelationshipSetAdapter ALL =
      new RelationshipSetAdapter("All");

  /**
   * A set matching no relations.
   */
  public static final RelationshipSetAdapter NONE =
    new RelationshipSetAdapter("None");

  static {
    // container relationships
    CONTAINER.addOrReplaceRelation(JavaRelation.DIRECTORY, true, true);
    CONTAINER.addOrReplaceRelation(JavaRelation.CLASS, true, true);
    CONTAINER.addOrReplaceRelation(JavaRelation.CLASSFILE, true, true);
    CONTAINER.addOrReplaceRelation(JavaRelation.FILE, true, true);
    CONTAINER.addOrReplaceRelation(JavaRelation.PACKAGE, true, true);
    CONTAINER.addOrReplaceRelation(JavaRelation.LOCAL_VARIABLE, true, true);
    CONTAINER.addOrReplaceRelation(JavaRelation.INNER_TYPE, true, true);
    CONTAINER.addOrReplaceRelation(JavaRelation.MEMBER_FIELD, true, true);
    CONTAINER.addOrReplaceRelation(JavaRelation.MEMBER_METHOD, true, true);
    CONTAINER.addOrReplaceRelation(JavaRelation.MEMBER_TYPE, true, true);
    CONTAINER.addOrReplaceRelation(JavaRelation.STATIC_FIELD, true, true);
    CONTAINER.addOrReplaceRelation(JavaRelation.STATIC_METHOD, true, true);
    CONTAINER.addOrReplaceRelation(JavaRelation.STATIC_TYPE, true, true);
    CONTAINER.addOrReplaceRelation(JavaRelation.ANONYMOUS_TYPE, true, true);

    // package + class member relationships
    PKG_MEMBER.addOrReplaceRelation(JavaRelation.CLASS, true, false);
    PKG_MEMBER.addOrReplaceRelation(JavaRelation.PACKAGE, true, false);
    PKG_MEMBER.addOrReplaceRelation(JavaRelation.INNER_TYPE, true, false);
    PKG_MEMBER.addOrReplaceRelation(JavaRelation.MEMBER_FIELD, true, false);
    PKG_MEMBER.addOrReplaceRelation(JavaRelation.MEMBER_METHOD, true, false);
    PKG_MEMBER.addOrReplaceRelation(JavaRelation.MEMBER_TYPE, true, false);
    PKG_MEMBER.addOrReplaceRelation(JavaRelation.STATIC_FIELD, true, false);
    PKG_MEMBER.addOrReplaceRelation(JavaRelation.STATIC_METHOD, true, false);
    PKG_MEMBER.addOrReplaceRelation(JavaRelation.STATIC_TYPE, true, false);

    // class member relationships only
    CLASS_MEMBER.addOrReplaceRelation(JavaRelation.INNER_TYPE, true, false);
    CLASS_MEMBER.addOrReplaceRelation(JavaRelation.MEMBER_FIELD, true, false);
    CLASS_MEMBER.addOrReplaceRelation(JavaRelation.MEMBER_METHOD, true, false);
    CLASS_MEMBER.addOrReplaceRelation(JavaRelation.MEMBER_TYPE, true, false);
    CLASS_MEMBER.addOrReplaceRelation(JavaRelation.STATIC_FIELD, true, false);
    CLASS_MEMBER.addOrReplaceRelation(JavaRelation.STATIC_METHOD, true, false);
    CLASS_MEMBER.addOrReplaceRelation(JavaRelation.STATIC_TYPE, true, false);

    // static class member relationships
    STATIC_MEMBER.addOrReplaceRelation(JavaRelation.STATIC_FIELD, true, false);
    STATIC_MEMBER.addOrReplaceRelation(JavaRelation.STATIC_METHOD, true, false);
    STATIC_MEMBER.addOrReplaceRelation(JavaRelation.STATIC_TYPE, true, false);

    // instance class member relationships
    INSTANCE_MEMBER.addOrReplaceRelation(
        JavaRelation.MEMBER_FIELD, true, false);
    INSTANCE_MEMBER.addOrReplaceRelation(
        JavaRelation.MEMBER_METHOD, true, false);
    INSTANCE_MEMBER.addOrReplaceRelation(JavaRelation.MEMBER_TYPE, true, false);

    // object extension relationships
    EXTENSION.addOrReplaceRelation(JavaRelation.WRITE, true, false);
    EXTENSION.addOrReplaceRelation(JavaRelation.EXTENDS, true, false);
    EXTENSION.addOrReplaceRelation(JavaRelation.IMPLEMENTS, true, false);
    EXTENSION.addOrReplaceRelation(JavaRelation.INTERFACE_EXTENDS, true, false);
    EXTENSION.addOrReplaceRelation(JavaRelation.METHOD_OVERLOAD, true, false);
    EXTENSION.addOrReplaceRelation(JavaRelation.METHOD_OVERRIDE, true, false);
    EXTENSION.addOrReplaceRelation(JavaRelation.ERROR_HANDLING, true, false);

    // object use relationships
    USES.addOrReplaceRelation(JavaRelation.CALL, true, false);
    USES.addOrReplaceRelation(JavaRelation.READ, true, false);
    USES.addOrReplaceRelation(JavaRelation.TYPE, true, false);

    // check all relationships
    for (JavaRelation relation : JavaRelation.values()) {
      ALL.addOrReplaceRelation(relation, true, true);
    }

    // add predefined sets to the built-in list
    builtins = Lists.newArrayList();
    builtins.add(CLASS_MEMBER);
    builtins.add(PKG_MEMBER);
    builtins.add(STATIC_MEMBER);
    builtins.add(INSTANCE_MEMBER);
    builtins.add(EXTENSION);
    builtins.add(USES);
    builtins.add(CONTAINER);
    builtins.add(ALL);
    builtins.add(NONE);
  }
}
