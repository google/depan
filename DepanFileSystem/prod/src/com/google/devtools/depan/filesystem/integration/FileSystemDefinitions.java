/*
 * Copyright 2008 Google Inc.
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

package com.google.devtools.depan.filesystem.integration;

import com.google.devtools.depan.filesystem.graph.DirectoryElement;
import com.google.devtools.depan.filesystem.graph.FileElement;
import com.google.devtools.depan.filesystem.graph.FileSystemRelation;
import com.google.devtools.depan.model.XmlPersistentObject.Config;

import com.thoughtworks.xstream.XStream;

/**
 * The configuration mechanism for <code>XStream</code> objects. It provides
 * aliases for classes in File System Plug-in that will be written to a file
 * through XStream.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public class FileSystemDefinitions implements Config {
  /**
   * One and only instance of this class.
   */
  private static FileSystemDefinitions INSTANCE = new FileSystemDefinitions();

  /**
   * Returns the singleton instance of this class.
   *
   * @return The singleton instance of this class.
   */
  public static FileSystemDefinitions getInstance() {
    return INSTANCE;
  }

  private FileSystemDefinitions() {
    // no outside instantiation
  }

  /**
   * Configures the <code>XStream</code> object passed as a parameter such that
   * aliases for classes that will be written to a file are provided to this
   * <code>XStream</code> object.
   */
  @Override
  public void config(XStream xstream) {
    xstream.alias("fs-dir", DirectoryElement.class);
    xstream.alias("fs-file", FileElement.class);
    xstream.alias("fs-relation", FileSystemRelation.class);
  }
}
