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

package com.google.devtools.depan.maven.integration;

import com.google.devtools.depan.eclipse.persist.XStreamFactory.Config;
import com.google.devtools.depan.maven.graph.ArtifactElement;
import com.google.devtools.depan.maven.graph.PropertyElement;

import com.thoughtworks.xstream.XStream;

/**
 * The configuration mechanism for <code>XStream</code> objects. It provides
 * aliases for classes in File System Plug-in that will be written to a file
 * through XStream.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class MavenDefinitions implements Config {
  /**
   * One and only instance of this class.
   */
  private static MavenDefinitions INSTANCE = new MavenDefinitions();

  /**
   * Returns the singleton instance of this class.
   *
   * @return The singleton instance of this class.
   */
  public static MavenDefinitions getInstance() {
    return INSTANCE;
  }

  private MavenDefinitions() {
    // no outside instantiation
  }

  /**
   * Configures the <code>XStream</code> object passed as a parameter such that
   * aliases for classes that will be written to a file are provided to this
   * <code>XStream</code> object.
   */
  @Override
  public void config(XStream xstream) {
    xstream.alias("mvn-artifact", ArtifactElement.class);
    xstream.alias("mvn-property", PropertyElement.class);
  }
}
