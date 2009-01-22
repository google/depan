/*
 * Copyright 2009 Google Inc.
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

package com.google.devtools.depan.javascript.integration;

import com.google.devtools.depan.collect.Lists;
import com.google.devtools.depan.filesystem.graph.FileSystemRelation;
import com.google.devtools.depan.javascript.graph.JavaScriptRelation;
import com.google.devtools.depan.model.RelationshipSet;
import com.google.devtools.depan.model.RelationshipSetAdapter;

import java.util.Collection;

/**
 * Define the built-in Relation sets that are provided by the JavaScript
 * plug-in.  These are exported to the application view the plug-in integration
 * methods defined in {@code JavaScriptPlugin}.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class JavaScriptRelationSets {

  /**
   * List of all built-in RelationshipSets. Make it easier to iterate.
   */
  private static final Collection<RelationshipSet> BUILTIN_SETS;

  /**
   * The default RelationshipSets exported by the JavaScript plug-in.
   */
  private static final RelationshipSet DEFAULT_SET;

  /**
   * A standard containment relationship for all JavaScript components,
   * including the common FileSystem relations.
   */
  private static final RelationshipSetAdapter LEXICAL_CONTAINMENT =
      new RelationshipSetAdapter("JavaScript Lexical Containment");

  /**
   * A containment relationship for JavaScript components that only include
   * scope binding.
   */
  private static final RelationshipSetAdapter BINDING_CONTAINMENT =
      new RelationshipSetAdapter("JavaScript Binding Containment");

  static {
    // container relationships
    LEXICAL_CONTAINMENT.addOrReplaceRelation(
        FileSystemRelation.CONTAINS_DIR, true, false);
    LEXICAL_CONTAINMENT.addOrReplaceRelation(
        FileSystemRelation.CONTAINS_FILE, true, false);
    LEXICAL_CONTAINMENT.addOrReplaceRelation(
        FileSystemRelation.SYMBOLIC_LINK, true, false);
    LEXICAL_CONTAINMENT.addOrReplaceRelation(
        JavaScriptRelation.DEFINES_NAME, true, false);
    LEXICAL_CONTAINMENT.addOrReplaceRelation(
        JavaScriptRelation.IMPLIES_NAME, true, false);
    LEXICAL_CONTAINMENT.addOrReplaceRelation(
        JavaScriptRelation.BINDS_ELEMENT, true, false);

    BINDING_CONTAINMENT.addOrReplaceRelation(
      JavaScriptRelation.BINDS_ELEMENT, true, false);

    // Publish the built-in relation sets
    BUILTIN_SETS = Lists.newArrayList();
    BUILTIN_SETS.add(LEXICAL_CONTAINMENT);
    BUILTIN_SETS.add(BINDING_CONTAINMENT);

    // Publish the default relation set
    DEFAULT_SET = LEXICAL_CONTAINMENT;
  }

  /**
   * Prevent instantiation of this name-space class.
   */
  private JavaScriptRelationSets() {
  }

  /**
   * Returns all built-in relationship sets defined by the JavaScript plug-in.
   *
   * @return Built-in relationship sets provided by the JavaScript plug-in.
   */
  public static Collection<RelationshipSet> getBuiltinSets() {
    return BUILTIN_SETS;
  }

  /**
   * Returns the default relationship set for JavaScript graphs.
   *
   * @return The default relationship set provided by the JavaScript plug-in.
   */
  public static RelationshipSet getDefaultRelationshipSet() {
    return DEFAULT_SET;
  }
}
