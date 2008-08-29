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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.google.devtools.depan.util.FileListerListener;
import com.google.devtools.depan.util.ProgressListener;
import com.google.devtools.depan.util.StringUtils;

/**
 * Same as FileLister, except that it reads the files contained in a jar or zip
 * archive. (any kind of zipped file actually).
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 * 
 */
public class JarFileLister {

  /**
   * Listener for callbacks at each file, directory....
   */
  private FileListerListener listener;
  
  /**
   * Zip archive to read.
   */
  private ZipFile zipFile;

  /**
   * {@link ProgressListener} for monitoring.
   */
  private ProgressListener progressListener;
  
  /**
   * curent directory stored as a list of folder names. e.g. the directory
   * com/google/deptool will be stored as a list: ["com", "google", "deptool"].
   */
  private ArrayList<String> directories = null;
  
  /**
   * list of full directories. Is always synchronized with the directories list.
   * if directories contains  ["com", "google", "deptool"], directoriesFull
   * must contains ["com", "com/google", "com/google/deptool"].
   */
  private ArrayList<String> directoriesFull = null;

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
  public JarFileLister(File file, FileListerListener listener)
      throws ZipException, IOException {
    this.listener = listener;
    zipFile = new ZipFile(file);
  }

  /**
   * begin the search of class files in the classPath given to the constructor.
   */
  public void start() {
    listener.startProcessing();
    parse();
    listener.endOfProcessing();
  }

  /**
   * Give the number of files in the zip archive.
   * @return the number of files in the zip archive.
   */
  public int getFileNumber() {
    return zipFile.size();
  }

  /**
   * Effectively read the archive, and call the {@link FileListerListener} when
   * necessary. 
   */
  private void parse() {
    Enumeration<? extends ZipEntry> entries;
    zipFile.size();
    entries = zipFile.entries();

    String dir =  null;
    resetDirs();
    
    // number of files loaded
    int n = 0;
    
    while (entries.hasMoreElements()) {
      ZipEntry entry = entries.nextElement();
      String name = entry.getName();
      
      if (null != progressListener) {
        progressListener.progress(name, ++n, zipFile.size());
      }

      // in a jar file, directories are part of the filename, so we can test
      // it as directory to.
      if (!entry.isDirectory() && listener.directoryFilter(name)
          && listener.fileFilter(name)) {
        String e = new File(name).getParent();
        if (null != e && (null == dir || !dir.equals(e))) {
          dir = e;
          enterDirectory(dir);
        }

        try {
          listener.proceedFile(zipFile.getInputStream(entry), name);
        } catch (IOException e1) {
          System.err.println("Error while reading file " + name + ".");
          e1.printStackTrace();
        }
      }
    }

    try {
      zipFile.close();
    } catch (IOException e) {
      System.err.println("Error when closing zip file " + zipFile.getName()
          + ".");
      e.printStackTrace();
    }
  }

  /**
   * reset the lists containing the directories paths.
   */
  private void resetDirs() {
    directories = new ArrayList<String>();
    directoriesFull = new ArrayList<String>();
  }

  /**
   * Enter in a directory. lastDir reprensent the last folder name, and fullPath
   * is the full directory name. e.g. fullPath = "com/google/devtools", lastDir
   * is then "devtools"
   * 
   * @param fullPath full path to the directory we push.
   * @param lastDir last folder name of fullPath.
   */
  private void pushDir(String fullPath, String lastDir) {
    directories.add(lastDir);
    directoriesFull.add(fullPath);
    assert(directories.size() == directoriesFull.size());
  }
  
  /**
   * Pop the two lists containing directories.
   */
  private void popDir() {
    directories.remove(directories.size() - 1);
    directoriesFull.remove(directoriesFull.size() - 1);
    assert(directories.size() == directoriesFull.size());
  }
  
  /**
   * Called when entering a directory.
   * This step is important for container dependencies, because in jarfile,
   * directories are generally not stored as a hierarchy, but in filenames.
   * i.e. we can find the files a/b/c.class and a/b/x/z.class in a jar file,
   * without effectively find a directory called a/b or a/b/x in this jar file.
   * Hierarchy is flat, just like java packages.
   * 
   * @param fullPath full path of the directory we are visiting.
   */
  private void enterDirectory(String fullPath) {
    String[] dirs = fullPath.split("/");
    int directoriesSize = directories.size();
    
    // find the first directory which differs from the current directory and
    // fullPath
    int firstDifferent = 0;
    while (firstDifferent < Math.min(dirs.length, directoriesSize)
        && directories.get(firstDifferent).equals(dirs[firstDifferent])) {
      ++firstDifferent;
    }
    
    // remove directories we left on the list starting from the end of the list
    for (int i = directoriesSize - 1; i >= firstDifferent; --i) {
      listener.outDirectory(directoriesFull.get(i));
      popDir();
    }
    
    // enter in new directories
    for (int i = firstDifferent; i < dirs.length; ++i) {
      String dir = StringUtils.join("/", 0, i, dirs);
      listener.enterDirectory(dir);
      pushDir(dir, dirs[i]);
    }
  }
  
  /**
   * Set a new {@link ProgressListener}.
   * @param l
   */
  public void setProgressListener(ProgressListener l) {
    this.progressListener = l;
  }
}
