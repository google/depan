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

package com.google.devtools.depan.view;

import com.google.devtools.depan.collect.Sets;

import java.util.Set;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class ViewModelHelper {

  /**
   * Last view number that was used.  This is auto-incremented by one for
   * each created view, regardless of the supplied prefix. The value does
   * not change for saved views that are loaded, but it can be incremented
   * past them if new view names conflict with loaded views. 
   */
  private static int viewNumber = 0;
  
  /**
   * Names of views already saved. So if we create a new view that should be
   * named View_4, but that there already is a loaded view called View_4 (loaded
   * views don't increment n), we can increment n until we found a unused name.
   */
  private static Set<String> existingNames = Sets.newHashSet();

  /**
   * Standard prefix for new view generation.
   */
  public static String DEFAULT_VIEW_PREFIX = "View";

  /** Never instantiate an instance */
  private ViewModelHelper() {
  }

  /**
   * Provide a session unique name for the view.
   * @param prefix label to prefix the view numbers
   * @return name for a view
   */
  public static String nextViewName(String prefix) {
    String newName;
    do {
      viewNumber++;
      newName = prefix + " " + viewNumber;
    } while (existingNames.contains(newName));
    return newName;
  }
  
  public static String nextViewName() {
    return nextViewName(DEFAULT_VIEW_PREFIX);
  }

  /**
   * Add the view name to the managed list.
   * @param viewName view to add to managed list
   */
  public static void addViewName(String viewName) {
    existingNames.add(viewName);
  }
}
