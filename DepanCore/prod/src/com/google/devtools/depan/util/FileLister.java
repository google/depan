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

package com.google.devtools.depan.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Recursively search a directory tree, for each File found in it, call
 * FileFinderListener.proceedFile. A filter can be set prior calling this
 * method.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 * 
 */
public class FileLister {

  /**
   * Base directory to explore.
   */
  private File classPath;

  /**
   * File listener for callbacks.
   */
  private FileListerListener listener;

  /**
   * @param classPath path to the classFile (usually top level package as com in
   *        com.google.common)
   */
  public FileLister(File classPath, FileListerListener depmk) {
    this.classPath = classPath;
    this.listener = depmk;
  }

  /**
   * begin the search of class files in the classPath given to the constructor.
   */
  public void start() {
    listener.startProcessing();
    for (File f : classPath.listFiles()) {
      dfsFileSearch(f);      
    }
    listener.endOfProcessing();
  }

  /**
   * operate a recursive depth first search in the directory. If file is a
   * directory, recursively search into for files. For each file found, check if
   * it passes the filter of DepMaker, and if yes, call proceedFile with the
   * file.
   * 
   * @param file File where to start the dfsFileSearch
   */
  private void dfsFileSearch(File file) {
    boolean dirMatches = listener.directoryFilter(file.getPath()); 

    if (file.isDirectory()) {
      // call callback for entering the directory only if the directory matches
      if (dirMatches) {
        listener.enterDirectory(
            file.getPath().replaceFirst(classPath.getPath(), ""));
      }
      // look inside of the directory anyway (maybe sub directories matches...
      for (File f : file.listFiles()) {
        dfsFileSearch(f);
      }
      // get out of the directory, callback.
      if (dirMatches) {
        listener.outDirectory(
            file.getPath().replaceFirst(classPath.getPath(), ""));
      }
    } else { // dir.isFile() == true
      if (listener.fileFilter(file.getPath()) && dirMatches) {
        try {
          listener.proceedFile(new FileInputStream(file),
              file.getPath().replaceFirst(classPath.getPath(), ""));
        } catch (FileNotFoundException e) {
          // should not happen, cause we just seen it on the hard drive...
          // but who knows...
          e.printStackTrace();
        }
      }
    }
  }
}
