/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.java.bytecode;

import com.google.devtools.depan.filesystem.elements.DirectoryElement;
import com.google.devtools.depan.java.bytecode.impl.ClassDepLister;
import com.google.devtools.depan.java.graph.JavaRelation;
import com.google.devtools.depan.util.FileListerListener;

import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Stack;

/**
 * Implements a {@link FileListerListener}. When a directory is found, call the
 * methods of a {@link DependenciesListener} to create the directory hierarchy,
 * and when a file is found, creates a ClassDepListener an run the bytecode
 * analysis on it.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class ClassLookup implements FileListerListener {

  /**
   * number of classes successfully loaded
   */
  private int classesLoaded = 0;

  /**
   * number of classes which failed to load
   */
  private int classesFailed = 0;

  /**
   * Whitelist of directories used to filter classfiles.  Classfiles are only
   * inspected if they are members of one of the whilelisted directories.
   */
  private final Collection<String> directoryWhitelist;

  /**
   * DependenciesListener for dependencies callbacks.
   */
  private DependenciesListener listener = null;

  /**
   * stack for directories. Needed to give a directory container when a file
   * is found.
   */
  private Stack<DirectoryElement> dirsStacks = new Stack<DirectoryElement>();

  /**
   * Construct a ClassLookup object.
   *
   * @param directoryWhitelist Whitelist of directories used to filter
   * classfiles.  Classfiles are only inspected if they are members of one of
   * the whilelisted directories.
   * @param listener {@link DependenciesListener} receiving callbacks when a
   *        dependency is found.
   */
  public ClassLookup(
      Collection<String> directoryWhitelist, DependenciesListener listener) {
    this.directoryWhitelist = directoryWhitelist;
    this.listener = listener;
  }

  /**
   * Determine if the named file is included in any of the whitelisted
   * directories.
   *
   * @param name containing package name for Java element to check
   * @return true if package name is the beginning of any whitelisted package
   */
  private boolean directoryWhitelistFilter(String name) {
    for (String context : directoryWhitelist) {
      if (name.startsWith(context)) {
        return true;
      }
    }
    // name not found on any whitelisted directory
    return false;
  }
  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.util.interfaces.FileListerListener
   *      #fileFilter(java.lang.String)
   */
  public boolean fileFilter(String name) {
    return directoryWhitelistFilter(name) && name.endsWith(".class");
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.util.interfaces.FileListerListener
   *      #directoryFilter(java.lang.String)
   */
  public boolean directoryFilter(String name) {
    return directoryWhitelistFilter(name);
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.util.interfaces.FileListerListener
   *      #proceedFile(java.io.InputStream, java.lang.String)
   */
  public void proceedFile(InputStream is, String name) {
    ClassDepLister cd = new ClassDepLister(listener, dirsStacks.peek());
    try {
      ClassReader cr = new ClassReader(is);
      cr.accept(cd, 0);
      classesLoaded++;
    } catch (IOException e) {
      System.err.println("Unable to load class " + name);
      e.printStackTrace();
      classesFailed++;
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.deptool.util.interfaces.DepMaker#endOfProcessing()
   */
  public void endOfProcessing() {
    System.out.println("" + classesLoaded + "/"
        + (classesLoaded + classesFailed) + " classes loaded. " + classesFailed
        + " failed.");
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.deptool.util.interfaces.FileListerListener
   *      #startProcessing()
   */
  public void startProcessing() {
    classesLoaded = 0;
    classesFailed = 0;
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.deptool.util.interfaces.FileListerListener
   *      #enterDirectory(java.io.File)
   */
  public void enterDirectory(String path) {
    DirectoryElement de = new DirectoryElement(path);
    if (dirsStacks.size() > 0) {
      listener.newDep(dirsStacks.peek(), de, JavaRelation.DIRECTORY);
    }
    dirsStacks.push(de);
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.deptool.util.interfaces.FileListerListener
   *      #outDirectory(java.io.File)
   */
  public void outDirectory(String path) {
    dirsStacks.pop();
  }
}
