/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.maven.builder;

/**
 * Define the set of processing options for the main POM file.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public enum PomProcessing {
  NONE("as is"), EFFECTIVE("compute");

  public final String label;
  
  private PomProcessing(String label) {
    this.label = label;
  }

  public static PomProcessing getPomProcessing(String choice) {
    for (PomProcessing item : PomProcessing.values()) {
      if (item.label.equals(choice)) {
        return item;
      }
    }
    return null;
  }
}
