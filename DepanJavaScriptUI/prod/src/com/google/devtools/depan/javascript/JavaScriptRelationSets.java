/*
 * Copyright 2009 The Depan Project Authors
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

package com.google.devtools.depan.javascript;

import com.google.devtools.depan.filesystem.graph.FileSystemRelation;
import com.google.devtools.depan.javascript.graph.JavaScriptRelation;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;

import com.google.common.collect.Lists;

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
  private static final Collection<RelationSetDescriptor> BUILT_IN_SETS;

  /**
   * The default RelationshipSets exported by the JavaScript plug-in.
   */
  private static final RelationSetDescriptor DEFAULT_SET;

  /**
   * A standard containment relationship for all JavaScript components,
   * including the common FileSystem relations.
   */
  private static final RelationSetDescriptor LEXICAL_CONTAINMENT;

  /**
   * A containment relationship for JavaScript components that only include
   * scope binding.
   */
  private static final RelationSetDescriptor BINDING_CONTAINMENT;

  static {
    // container relationships
    RelationSetDescriptor.Builder lexBuilder =
        RelationSetDescriptor.createBuilder(
            "JavaScript Lexical Containment",
            JavaScriptPluginActivator.JAVASCRIPT_MODEL);
    lexBuilder.addRelation(FileSystemRelation.CONTAINS_DIR);
    lexBuilder.addRelation(FileSystemRelation.CONTAINS_FILE);
    lexBuilder.addRelation(FileSystemRelation.SYMBOLIC_LINK);
    lexBuilder.addRelation(JavaScriptRelation.DEFINES_NAME);
    lexBuilder.addRelation(JavaScriptRelation.IMPLIES_NAME);
    lexBuilder.addRelation(JavaScriptRelation.BINDS_ELEMENT);
    LEXICAL_CONTAINMENT = lexBuilder.build();

    RelationSetDescriptor.Builder bindBuilder =
        RelationSetDescriptor.createBuilder(
            "JavaScript Binding Containment",
            JavaScriptPluginActivator.JAVASCRIPT_MODEL);
    bindBuilder.addRelation(JavaScriptRelation.BINDS_ELEMENT);
    BINDING_CONTAINMENT = bindBuilder.build();

    // Publish the built-in relation sets
    BUILT_IN_SETS = Lists.newArrayList();
    BUILT_IN_SETS.add(LEXICAL_CONTAINMENT);
    BUILT_IN_SETS.add(BINDING_CONTAINMENT);

    // Publish the default relation set
    DEFAULT_SET = LEXICAL_CONTAINMENT;
  }

  private JavaScriptRelationSets() {
    // Prevent instantiation.
  }

  /**
   * Returns all built-in relationship sets defined by the JavaScript plug-in.
   */
  public static Collection<RelationSetDescriptor> getBuiltinSets() {
    return BUILT_IN_SETS;
  }

  /**
   * Returns the default relationship set for JavaScript graphs.
   */
  public static RelationSetDescriptor getDefaultDescriptor() {
    return DEFAULT_SET;
  }
}
