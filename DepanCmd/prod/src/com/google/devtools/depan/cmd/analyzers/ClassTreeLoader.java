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

package com.google.devtools.depan.cmd.analyzers;

import com.google.devtools.depan.filesystem.builder.TreeLoader;
import com.google.devtools.depan.filesystem.graph.DirectoryElement;
import com.google.devtools.depan.filesystem.graph.FileElement;
import com.google.devtools.depan.java.bytecode.eclipse.ClassFileReader;
import com.google.devtools.depan.model.builder.chain.DependenciesListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Build dependencies from all .class files in a file system tree.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class ClassTreeLoader extends TreeLoader {

  private final ClassFileReader reader;

  public ClassTreeLoader(
      String prefixPath,
      DependenciesListener builder,
      ClassFileReader reader) {
    super(builder, prefixPath);
    this.reader = reader;
  }

  @Override
  protected void beginAnalysis(String treePath) {
    super.beginAnalysis(treePath);
  }

  @Override
  protected void finishAnalysis(String treePath) {
    super.finishAnalysis(treePath);
  }

  @Override
  protected DirectoryElement visitDirectory(File treeFile) throws IOException {
    return super.visitDirectory(treeFile);
  }

  @Override
  protected FileElement visitFile(File treeFile) throws IOException {
    FileElement fileNode = super.visitFile(treeFile);

    if (treeFile.getName().endsWith(".class")) {
      FileInputStream content = new FileInputStream(treeFile);
      reader.readClassFile(getBuilder(), fileNode, content);
    }

    return fileNode;
  }
}
