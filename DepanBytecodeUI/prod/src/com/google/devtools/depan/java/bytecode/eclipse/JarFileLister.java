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

package com.google.devtools.depan.java.bytecode.eclipse;


import com.google.devtools.depan.filesystem.graph.DirectoryElement;
import com.google.devtools.depan.filesystem.graph.FileElement;
import com.google.devtools.depan.filesystem.graph.FileSystemRelation;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.chain.DependenciesListener;
import com.google.devtools.depan.platform.jobs.ProgressListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Similar to {@code ClassTreeLoader}, except that it reads the files contained
 * in a jar or zip archive. (any kind of zipped file actually).
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class JarFileLister {

  private static final Logger LOG =
      LoggerFactory.getLogger(JarFileLister.class.getName());

  private final ZipFile zipFile;

  private final DependenciesListener builder;

  private final ClassFileReader reader;

  private final ProgressListener progress;

  /**
   * Create a new JarFileLister, to list files in file, and call callbacks of
   * listener.
   * 
   * @param file {@link File} representing the jar archive to read
   * @param listener {@link FileListerListener} for callbacks when a directory,
   *        file... is found
   * @throws ZipException if any exception occured while trying to open the zip
   *         file
   * @throws IOException for any other {@link IOException}s throws while
   *         opening the {@link ZipFile}
   */
  public JarFileLister(ZipFile zipFile, DependenciesListener builder,
      ClassFileReader reader, ProgressListener progress) {
    this.zipFile = zipFile;
    this.builder = builder;
    this.reader = reader;
    this.progress = progress;
  }

  /**
   * begin the search of class files in the classPath given to the constructor.
   */
  public void start() {
    parse();
  }

  /**
   * Give the number of files in the zip archive.
   * @return the number of files in the zip archive.
   */
  public int getFileNumber() {
    return zipFile.size();
  }

  protected DependenciesListener getBuilder() {
    return builder;
  }

  /**
   * Read the Java archive, creating directory nodes, file nodes, and parsing
   * the .class contents that are present.
   */
  private void parse() {
    int count = zipFile.size();
    int index = 0;

    Enumeration<? extends ZipEntry> entries = zipFile.entries();
    while (entries.hasMoreElements()) {
      ZipEntry entry = entries.nextElement();
      String name = entry.getName();

      GraphNode entryNode = createEntryNode(entry);
      String parent = new File(name).getParent();
      if (null == parent) {
        builder.newNode(entryNode);
      } else {
        DirectoryElement parentNode = new DirectoryElement(parent);
        if (entry.isDirectory()) {
          builder.newDep(parentNode, entryNode, FileSystemRelation.CONTAINS_DIR);
        } else {
          builder.newDep(parentNode, entryNode, FileSystemRelation.CONTAINS_FILE);
        }
      }

      // If it is a .class file, parse those contents.
      // TODO(leeca): re-add path filtering
      if (!entry.isDirectory() && name.endsWith(".class")) {
        try {
          InputStream inputStream = zipFile.getInputStream(entry);
          reader.readClassFile(getBuilder(), (FileElement) entryNode, inputStream);
        } catch (IOException e1) {
          LOG.error("Error while reading file {}.", name);
        }
      }

      progress.progress(name, ++index, count);
    }

    try {
      zipFile.close();
    } catch (IOException e) {
      LOG.warn(
          "Error when closing zip file {}.", zipFile.getName());
    }
  }

  private GraphNode createEntryNode(ZipEntry entry) {
    if (entry.isDirectory()) {
      // Ensure directory name is in canonical form
      String dirName = new File(entry.getName()).getPath();
      return new DirectoryElement(dirName);
    }
    return new FileElement(entry.getName());
  }
}
