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

import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.nodes.filters.model.ContextualFilterDocument;
import com.google.devtools.depan.platform.PlatformTools;
import com.google.devtools.depan.resources.ResourceContainer;
import com.google.devtools.depan.resources.analysis.AnalysisResources;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class ContextualFilterResources {

  /** Name of resource tree container for filter resources. */
  public static final String FILTERS = "filters";

  /** Base file name for a new filter resource. */
  public static final String BASE_NAME = "Filter";

  /** Expected extensions for a filter resource. */
  public static final String EXTENSION = "cfxml";

  private ContextualFilterResources() {
    // Prevent instantiation.
  }

  public static void installResources(ResourceContainer root) {
    root.addChild(FILTERS);
  }

  /**
   * Kludgy bit of Singleton mis-use to simplify access to a very
   * commonly referenced resource.  Other solutions are welcome.
   */
  public static ResourceContainer getContainer() {
    return AnalysisResources.getRoot().getChild(FILTERS);
  }

  public static String getBaseNameExt() {
    return PlatformTools.getBaseNameExt(BASE_NAME, EXTENSION);
  }

  public static List<ContextualFilterDocument> getMatchers(
      DependencyModel model) {

    List<ContextualFilterDocument> result = Lists.newArrayList();

    // Filter for GEMs with the supplied model.
    for (Object resource : getContainer().getResources()) {
        if (resource instanceof ContextualFilterDocument) {
          ContextualFilterDocument checkFilter =
              (ContextualFilterDocument) resource;

          if (checkFilter.forModel(model)) {
            result.add(checkFilter);
          }
        }
      }

    return result;
  }
}
