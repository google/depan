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

package com.google.devtools.depan.ruby.integration;

import com.google.devtools.depan.eclipse.persist.XStreamFactory.Config;
import com.google.devtools.depan.ruby.graph.ClassElement;
import com.google.devtools.depan.ruby.graph.ClassMethodElement;
import com.google.devtools.depan.ruby.graph.InstanceMethodElement;
import com.google.devtools.depan.ruby.graph.RubyRelation;
import com.google.devtools.depan.ruby.graph.SingletonMethodElement;

import com.thoughtworks.xstream.XStream;

/**
 * The configuration mechanism for <code>XStream</code> objects. It provides
 * aliases for classes in Ruby Plug-in that will be written to a file
 * through XStream.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class RubyDefinitions implements Config {
  /**
   * One and only instance of this class.
   */
  private static RubyDefinitions INSTANCE = new RubyDefinitions();

  /**
   * Returns the singleton instance of this class.
   *
   * @return The singleton instance of this class.
   */
  public static RubyDefinitions getInstance() {
    return INSTANCE;
  }

  private RubyDefinitions() {
    // no outside instantiation
  }

  /**
   * Configures the <code>XStream</code> object passed as a parameter such that
   * aliases for classes that will be written to a file are provided to this
   * <code>XStream</code> object.
   */
  @Override
  public void config(XStream xstream) {
    xstream.alias("ruby-class", ClassElement.class);
    xstream.alias("ruby-class_method", ClassMethodElement.class);
    xstream.alias("ruby-instance_method", InstanceMethodElement.class);
    xstream.alias("ruby-singleton_method", SingletonMethodElement.class);
    xstream.alias("ruby-relation", RubyRelation.class);
  }
}
