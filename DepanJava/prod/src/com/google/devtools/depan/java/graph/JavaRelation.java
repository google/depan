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

package com.google.devtools.depan.java.graph;

import com.google.devtools.depan.graph.api.Relation;

/**
 * The kinds of relations recognized among Java elements.
 * Relations among source code artifacts (directories, files) are realized
 * using the FileSystem relations.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public enum JavaRelation implements Relation {
  // "Use" relationships
  CALL("caller", "called", "calls", "called by"),
  READ("reader", "read", "reads", "supplies"),
  TYPE("user", "uses", "type references", "used by"),

  // "Container" relationships
  PACKAGEDIR("directory", "package", "contains package", "from directory"),
  CLASSFILE("file", "class", "contains class", "from file"),
  PACKAGE("package", "subpackage", "contains package", "subpackage of"),
  CLASS("package", "class", "contains class", "in package"),
  INNER_TYPE("type", "innertype", "contain type", "in type"),
  LOCAL_VARIABLE("variable", "method", "contain variable", "has variable"),
  ANONYMOUS_TYPE("parent", "anonymous", "contains type", "in type"),

  // Instance member relationships
  MEMBER_TYPE("type", "membertype", "has member type", "member type of"),
  MEMBER_METHOD("type", "method", "has member method", "member method of"),
  MEMBER_FIELD("type", "field", "has member field", "member field of"),

  // Static member relationships
  STATIC_TYPE("type", "staticType", "has static type", "static type of"),
  STATIC_METHOD("type", "staticMethod", "has static method", "static method of"),
  STATIC_FIELD("type", "staticField", "has static field", "static field of"),

  // "Use" relationships
  WRITE("writer", "writen", "writes", "written by"),
  EXTENDS("super", "derived", "extends", "derived from"),
  IMPLEMENTS("realize", "implements", "implements", "implemented by"),

  // "Extension" relationships
  INTERFACE_EXTENDS("interfaceRealize", "interfaceImplements", "realizes", "implements"),
  METHOD_OVERLOAD("overloader", "overloaded", "overloads", "overloaded by"),
  METHOD_OVERRIDE("overridder", "overriden", "overrides", "overriden by"),
  ERROR_HANDLING("try", "catch", "handles", "handled by"),

  // Annotation relationships
  RUNTIME_ANNOTATION("type", "annotation", "annotated (runtime)", "annotator (runtime)"),
  COMPILE_ANNOTATION("type", "annotation", "annotated (class)", "annotator (class)")
  ;

  /**
   * Role of the head object for the relation.
   */
  private final String headRole;

  /**
   * Role of the tail object for the relation.
   */
  private final String tailRole;

  /**
   * Name of the relation in the forward direction.
   */
  private final String forwardName;

  /**
   * Name of the relation in the reverse direction.
   */
  private final String reverseName;

  /**
   * Constructor for a new Relation.
   * @param forwardName name of the left hand side element.
   * @param reverseName name of the right hand side element.
   */
  private JavaRelation(
      String headRole, String tailRole,
      String reverseName, String forwardName) {
    this.headRole = headRole;
    this.tailRole = tailRole;
    this.forwardName = forwardName;
    this.reverseName = reverseName;
  }

  // TODO: @Override
  public String getHeadRole() {
    return headRole;
  }

  // TODO: @Override
  public String getTailRole() {
    return tailRole;
  }

  @Override
  public String getForwardName() {
    return forwardName;
  }

  @Override
  public String getReverseName() {
    return reverseName;
  }
}
