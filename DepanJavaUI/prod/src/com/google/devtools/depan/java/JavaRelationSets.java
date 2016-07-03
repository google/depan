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

package com.google.devtools.depan.java;

import com.google.devtools.depan.filesystem.graph.FileSystemRelation;
import com.google.devtools.depan.java.graph.JavaRelation;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.models.RelationSetDescriptor.Builder;

import com.google.common.collect.Lists;

import java.util.Collection;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class JavaRelationSets {

  /**
   * List of all built-in RelationshipSets. Make it easier to iterate.
   */
  public static final Collection<RelationSetDescriptor> builtins;

  public static final RelationSetDescriptor CONTAINER;

  public static final RelationSetDescriptor PKG_MEMBER;

  public static final RelationSetDescriptor CLASS_MEMBER;

  public static final RelationSetDescriptor STATIC_MEMBER;

  public static final RelationSetDescriptor INSTANCE_MEMBER;

  public static final RelationSetDescriptor EXTENSION;

  public static final RelationSetDescriptor USES;

  public static final RelationSetDescriptor ALL;

  static {
    // class member relationships only
    Builder classBuilder = RelationSetDescriptor.createBuilder(
        "Class Members");
    classBuilder.addRelation(JavaRelation.INNER_TYPE);
    classBuilder.addRelation(JavaRelation.MEMBER_FIELD);
    classBuilder.addRelation(JavaRelation.MEMBER_METHOD);
    classBuilder.addRelation(JavaRelation.MEMBER_TYPE);
    classBuilder.addRelation(JavaRelation.STATIC_FIELD);
    classBuilder.addRelation(JavaRelation.STATIC_METHOD);
    classBuilder.addRelation(JavaRelation.STATIC_TYPE);
    classBuilder.addRelation(JavaRelation.ANONYMOUS_TYPE);
    CLASS_MEMBER = classBuilder.build();

    // package + class member relationships
    Builder pkgBuilder = RelationSetDescriptor.createBuilder(
        "Package Members");
    pkgBuilder.addRelation(JavaRelation.CLASS);
    pkgBuilder.addRelation(JavaRelation.PACKAGE);
    pkgBuilder.addBuilderSet(classBuilder);
    PKG_MEMBER = pkgBuilder.build();

    // container relationships
    Builder containerBuilder = RelationSetDescriptor.createBuilder(
        "Java Containers");
    containerBuilder.addRelation(FileSystemRelation.CONTAINS_DIR);
    containerBuilder.addRelation(FileSystemRelation.CONTAINS_FILE);
    containerBuilder.addRelation(JavaRelation.CLASSFILE);
    containerBuilder.addRelation(JavaRelation.LOCAL_VARIABLE);
    containerBuilder.addBuilderSet(classBuilder);
    CONTAINER = containerBuilder.build();

    // static class member relationships
    Builder staticBuilder = RelationSetDescriptor.createBuilder(
        "Static Members");
    staticBuilder.addRelation(JavaRelation.STATIC_FIELD);
    staticBuilder.addRelation(JavaRelation.STATIC_METHOD);
    staticBuilder.addRelation(JavaRelation.STATIC_TYPE);
    STATIC_MEMBER = staticBuilder.build();

    // instance class member relationships
    Builder instanceBuilder = RelationSetDescriptor.createBuilder(
        "Instance Members");
    instanceBuilder.addRelation(JavaRelation.MEMBER_FIELD);
    instanceBuilder.addRelation(JavaRelation.MEMBER_METHOD);
    instanceBuilder.addRelation(JavaRelation.MEMBER_TYPE);
    INSTANCE_MEMBER = instanceBuilder.build();

    // object extension relationships
    Builder extBuilder = RelationSetDescriptor.createBuilder(
        "Extensions");
    extBuilder.addRelation(JavaRelation.WRITE);
    extBuilder.addRelation(JavaRelation.EXTENDS);
    extBuilder.addRelation(JavaRelation.IMPLEMENTS);
    extBuilder.addRelation(JavaRelation.INTERFACE_EXTENDS);
    extBuilder.addRelation(JavaRelation.METHOD_OVERLOAD);
    extBuilder.addRelation(JavaRelation.METHOD_OVERRIDE);
    extBuilder.addRelation(JavaRelation.ERROR_HANDLING);
    EXTENSION = extBuilder.build();

    // object use relationships
    Builder useBuilder = RelationSetDescriptor.createBuilder(
        "Uses");
    useBuilder.addRelation(JavaRelation.CALL);
    useBuilder.addRelation(JavaRelation.READ);
    useBuilder.addRelation(JavaRelation.TYPE);
    USES = useBuilder.build();

    // check all relationships
    Builder allBuilder = RelationSetDescriptor.createBuilder("All Java");
    for (JavaRelation relation : JavaRelation.values()) {
      allBuilder.addRelation(relation);
    }
    ALL = allBuilder.build();

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
  }
}
