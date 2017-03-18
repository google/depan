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

package com.google.devtools.depan.nodes.filters.persistence;

import com.google.devtools.depan.resources.ResourceContainer;
import com.google.devtools.depan.resources.analysis.AnalysisResources;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class NodeKindResources {

  /** Name of resource tree container for node-kind resources. */
  public static final String NODES = "nodes";

  public static final String BASE_NAME = null;

  /** Expected extensions for node-kind resources. */
  public static final String EXTENSION = "nkxml";

  private NodeKindResources() {
    // Prevent instantiation.
  }

  public static void installResources(ResourceContainer root) {
    root.addChild(NODES);
  }

  /**
   * Kludgy bit of Singleton mis-use to simplify access to a very
   * commonly referenced resource.  Other solutions are welcome.
   */
  public static ResourceContainer getContainer() {
    return AnalysisResources.getRoot().getChild(NODES);
  }
}
