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

package com.google.devtools.depan.platform;

/**
 * Collection of static methods for manipulating the dotted names,
 * such as used for fully qualified names in Java and JavaScript.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class DottedNameTools {

  private DottedNameTools() {
    // No instances allowed.
  }

  /**
   * Provide the last segment in a dot-separated name.  This
   * means the run of text following the last period, if there is one,
   * or the entire text if no period is present.
   * 
   * @param javaName A fully-qualified Java entity name to shorten
   * @return final name segment from {@code javaName}
   */
  public static String getFinalNameSegment(String javaName) {
    if (!javaName.contains(".")) {
      return javaName;
    }

    int lastDotIndex = javaName.lastIndexOf('.');
    return javaName.substring(lastDotIndex + 1, javaName.length());
  }

  public static String getParentNameSegments(String dottedName) {
    int dotSplit = dottedName.lastIndexOf('.');
    if (dotSplit < 1) {
      return "";
    }

    return dottedName.substring(0, dotSplit);
  }
}
