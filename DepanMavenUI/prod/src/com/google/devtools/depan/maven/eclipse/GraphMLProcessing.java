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

package com.google.devtools.depan.maven.eclipse;

import com.google.devtools.depan.graphml.builder.GraphFactory;
import com.google.devtools.depan.maven.graphml.MavenGraphFactory;

/**
 * Define the GraphML interpretation options, primarily for user
 * selection in the {@link NewGraphMLPage}.
 * 
 * For a rich GraphML importer, this should probably be part
 * of an extension point that include the {@link GraphFactory}.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public enum GraphMLProcessing {
  MAVEN("Maven") {
    @Override
    public GraphFactory getGraphFactory() {
      return new MavenGraphFactory();
    }
  },
  CUSTOM("custom") {
    @Override
    public GraphFactory getGraphFactory() {
      return null;
    }
  };

  public final String label;
  
  private GraphMLProcessing(String label) {
    this.label = label;
  }

  public static GraphMLProcessing getGraphMLProcessing(String choice) {
    for (GraphMLProcessing item : GraphMLProcessing.values()) {
      if (item.label.equals(choice)) {
        return item;
      }
    }
    return null;
  }

  public abstract GraphFactory getGraphFactory();
}
