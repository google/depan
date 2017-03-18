/*
 * Copyright 2017 The Depan Project Authors
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
package com.google.devtools.depan.view_doc.persistence;

import com.google.devtools.depan.platform.PlatformTools;
import com.google.devtools.depan.resources.ResourceContainer;
import com.google.devtools.depan.resources.analysis.AnalysisResources;

/**
 * Edge based resources share the same container.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class EdgeDisplayResources {

  /** Name of resource tree container. */
  public static final String EDGES = "edges";

  /** Base file name for a new edge display resource. */
  public static final String BASE_NAME = "Edge Display";

  /** Expected extensions for a edge display resource. */
  public static final String EXTENSION = "edspxml";

  private EdgeDisplayResources() {
    // Prevent instantiation.
  }

  public static void installResources(ResourceContainer root) {
    root.addChild(EDGES);
  }

  public static ResourceContainer getContainer() {
    return AnalysisResources.getRoot().getChild(EDGES);
  }

  public static String getBaseNameExt() {
    return PlatformTools.getBaseNameExt(BASE_NAME, EXTENSION);
  }
}
