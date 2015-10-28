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

package com.google.devtools.depan.javascript.integration;

import com.google.common.collect.Lists;
import com.google.devtools.depan.eclipse.persist.XStreamFactory.Config;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.javascript.graph.JavaScriptBuiltinElement;
import com.google.devtools.depan.javascript.graph.JavaScriptClassElement;
import com.google.devtools.depan.javascript.graph.JavaScriptEnumElement;
import com.google.devtools.depan.javascript.graph.JavaScriptFieldElement;
import com.google.devtools.depan.javascript.graph.JavaScriptFunctionElement;
import com.google.devtools.depan.javascript.graph.JavaScriptRelation;
import com.google.devtools.depan.javascript.graph.JavaScriptVariableElement;
import com.google.devtools.depan.model.Element;

import com.thoughtworks.xstream.XStream;

import java.util.Collection;

/**
 * Definitions of JavaScript dependency graph elements and relations.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class JavaScriptDefinitions {

  /**
   * Define all Element types introduced by the JavaScript plug-in.
   */
  private static final Collection<Class<? extends Element>> CLASSES;

  /**
   * Define all Relation types introduced by the JavaScript plug-in.
   */
  private static final Collection<Relation> RELATIONS;

  /**
   * Define the XML serialization options used for JavaScript graph components.
   */
  private static final Config CONFIG_XML_PERSIST;

  static {
    CLASSES = Lists.newArrayList();
    CLASSES.add(JavaScriptBuiltinElement.class);
    CLASSES.add(JavaScriptClassElement.class);
    CLASSES.add(JavaScriptEnumElement.class);
    CLASSES.add(JavaScriptFieldElement.class);
    CLASSES.add(JavaScriptFunctionElement.class);
    CLASSES.add(JavaScriptVariableElement.class);

    RELATIONS = Lists.newArrayList();
    for (Relation r : JavaScriptRelation.values()) {
      RELATIONS.add(r);
    }

    // Define XML serialization
    CONFIG_XML_PERSIST = new Config() {

      @Override
      public void  config(XStream xstream) {
        xstream.alias("js-builtin", JavaScriptBuiltinElement.class);
        xstream.alias("js-class", JavaScriptClassElement.class);
        xstream.alias("js-enum", JavaScriptEnumElement.class);
        xstream.alias("js-field", JavaScriptFieldElement.class);
        xstream.alias("js-function", JavaScriptFunctionElement.class);
        xstream.alias("js-variable", JavaScriptVariableElement.class);
        xstream.alias("js-relation", JavaScriptRelation.class);
      }
    };
  }

  /**
   * Prevent instantiation of this name-space class.
   */
  private JavaScriptDefinitions() {
  }

  /**
   * Returns a collection of classes that includes <code>Class</code>
   * representation for each type of node in a JavaScript dependency.
   *
   * @return A collection of classes that includes <code>Class</code>
   * representation for each type of node in a JavaScript dependency.
   */
  public static Collection<Class<? extends Element>> getClasses() {
    return CLASSES;
  }

  /**
   * Returns all types of relations between JavaScript elements.
   *
   * @return Types of relations for JavaScript elements.
   */
  public static Collection<Relation> getRelations() {
    return RELATIONS;
  }

  /**
   * Provide an object that can configure an XStream to handle JavaScript
   * graph elements.
   * 
   * @return XStream configuration object
   */
  public static Config getXMLConfig() {
    return CONFIG_XML_PERSIST;
  }
}
