/*
 * Copyright 2008 The Depan Project Authors
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
import com.google.devtools.depan.persistence.plugins.PersistencePlugin;

import com.thoughtworks.xstream.XStream;

/**
 * DepAn Plug-in that models the File System. It is intended to be included from
 * other plug-ins that use a file system; however, it is possible to use this
 * plug-in in DepAn as is.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public class FileSystemPersistencePlugin implements PersistencePlugin {

  @Override
  public void config(XStream xstream) {
    xstream.alias("fs-dir", DirectoryElement.class);
    xstream.alias("fs-file", FileElement.class);
    xstream.alias("fs-relation", FileSystemRelation.class);
  }
}
