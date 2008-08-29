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

import java.io.InputStream;

/**
 * Listener for callbacks when directories and files are found when exploring
 * a directory, or a jar file for example.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 * 
 */
public interface FileListerListener {

  /**
   * Filter to apply to (normal - i.e not a directory) files found in the
   * directory. May be a simple filename check as "end with .class"
   * 
   * @param name filename
   * @return true if the filename pass the filter, and that proceedFile should
   *         be called on this file later
   * 
   */
  public boolean fileFilter(String name);

  /**
   * Filter to apply to directories. If this methods return false,
   * subdirectories should not be traversed. May be a simple directory path
   * check as "start with 'directoryfilter'"
   * 
   * @param name filename to check
   * @return true if the directory can contain interresting files.
   */
  public boolean directoryFilter(String name);
  
  /**
   * method called when a file passing the fileFilter test is found.
   * 
   * @param f file found.
   */
  public void proceedFile(InputStream f, String name);

  /**
   * called when entering in the given directory
   * 
   * @param directoryPath
   */
  public void enterDirectory(String directoryPath);

  /**
   * called when we step out from the given directory
   * 
   * @param directoryPath
   */
  public void outDirectory(String directoryPath);

  /**
   * callback called at the begining of the processing
   */
  public void startProcessing();

  /**
   * callback called at the end of the processing
   */
  public void endOfProcessing();
  
}
