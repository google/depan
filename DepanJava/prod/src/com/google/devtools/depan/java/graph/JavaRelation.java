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

import com.google.devtools.depan.graph.api.Relation;

/**
 * Some dependencies types. A type can eventually have more than one superType.
 * However, No loops are allowed in the graph.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public enum JavaRelation implements Relation {
  // "Use" relationships
  CALL("caller", "called"),
  READ("reader", "read"),
  TYPE("user", "uses"),

  // "Container" relationships
  DIRECTORY("directory", "subdirectory"),
  FILE("directory", "file"),
  PACKAGE("package", "subpackage"),
  CLASS("package", "class"),
  CLASSFILE("file", "class"),
  INNER_TYPE("type", "innertype"),
  LOCAL_VARIABLE("variable", "method"),
  ANONYMOUS_TYPE("parent", "anonymous"),

  // Instance member relationships
  MEMBER_TYPE("type", "membertype"),
  MEMBER_METHOD("type", "method"),
  MEMBER_FIELD("type", "field"),

  // Static member relationships
  STATIC_TYPE("type", "staticMethod"),
  STATIC_METHOD("type", "staticMethod"),
  STATIC_FIELD("type", "staticField"),

  // "Use" relationships
  WRITE("writer", "writen"),
  EXTENDS("super", "extends"),
  IMPLEMENTS("realize", "implements"),

  // "Extension" relationships
  INTERFACE_EXTENDS("interfaceRealize", "interfaceImplements"),
  METHOD_OVERLOAD("overloader", "overloaded"),
  METHOD_OVERRIDE("overridder", "overriden"),
  ERROR_HANDLING("try", "catch"),
  ;

  /**
   * name of the element on the left of the relation.
   */
  public final String forwardName;
  /**
   * name of the element on the right side of the relation.
   */
  public final String reverseName;

  /**
   * Constructor for a new Relation.
   * @param forwardName name of the left hand side element.
   * @param reverseName name of the right hand side element.
   */
   private JavaRelation(String reverseName, String forwardName) {
    this.forwardName = forwardName;
    this.reverseName = reverseName;
  }

  /* (non-Javadoc)
   * @see com.google.devtools.depan.graph.api.Relation#getForwardName()
   */
  public String getForwardName() {
    return forwardName;
  }

  /* (non-Javadoc)
   * @see com.google.devtools.depan.graph.api.Relation#getReverseName()
   */
  public String getReverseName() {
    return reverseName;
  }
}
