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

package com.google.devtools.depan.matchers.persistence;

import com.google.devtools.depan.edges.matchers.GraphEdgeMatchers;
import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptors;
import com.google.devtools.depan.model.GraphEdgeMatcher;
import com.google.devtools.depan.platform.PlatformTools;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.resources.PropertyDocumentReference;
import com.google.devtools.depan.resources.ResourceContainer;
import com.google.devtools.depan.resources.ResourceDocumentReference;
import com.google.devtools.depan.resources.analysis.AnalysisResources;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class GraphEdgeMatcherResources {

  /** Name of resource tree container for matcher resources. */
  public static final String MATCHERS = "matchers";

  /** Base file name for a new matcher resource. */
  public static final String BASE_NAME = "Edge Matcher";

  /** Expected extensions for a matcher resource. */
  public static final String EXTENSION = "gemxml";

  /**
   * Kludgy bit of Singleton mis-use to simplify access to a very
   * commonly referenced resource.  Other solutions are welcome.
   */
  public static PropertyDocumentReference<GraphEdgeMatcherDescriptor>
      FORWARD_REF;

  private GraphEdgeMatcherResources() {
    // Prevent instantiation.
  }

  public static void installResources(ResourceContainer root) {
    ResourceContainer matchers = root.addChild(MATCHERS);
    matchers.addResource(GraphEdgeMatcherDescriptors.EMPTY);
    FORWARD_REF = installMatcher(matchers, GraphEdgeMatcherDescriptors.FORWARD);
  }

  public static void installMatchers(
      ResourceContainer container, Collection<RelationSetDescriptor> relSets) {

    for (RelationSetDescriptor descr : relSets) {
      RelationSet relSet = descr.getInfo();
      GraphEdgeMatcher matcher =
          GraphEdgeMatchers.createForwardEdgeMatcher(relSet);
      GraphEdgeMatcherDescriptor resource =
          new GraphEdgeMatcherDescriptor(
              descr.getName(), descr.getModel(), matcher);
      container.addResource(descr.getName(), resource);
    }
  }

  private static PropertyDocumentReference<GraphEdgeMatcherDescriptor>
      installMatcher(
          ResourceContainer container, GraphEdgeMatcherDescriptor matcher) {
    container.addResource(matcher);
    return ResourceDocumentReference
        .buildResourceReference(container, matcher);
  }

  /**
   * Kludgy bit of Singleton mis-use to simplify access to a very
   * commonly referenced resource.  Other solutions are welcome.
   */
  public static ResourceContainer getContainer() {
    return AnalysisResources.getRoot().getChild(MATCHERS);
  }

  public static String getBaseNameExt() {
    return PlatformTools.getBaseNameExt(BASE_NAME, EXTENSION);
  }

  public static List<PropertyDocumentReference<GraphEdgeMatcherDescriptor>>
      getMatchers(DependencyModel model) {

    List<PropertyDocumentReference<GraphEdgeMatcherDescriptor>> result =
        Lists.newArrayList();

    // Filter for GEMs with the supplied model.
    ResourceContainer container = getContainer();
    for (Object resource : container.getResources()) {
        if (resource instanceof GraphEdgeMatcherDescriptor) {
          GraphEdgeMatcherDescriptor checkRes =
              (GraphEdgeMatcherDescriptor) resource;

          if (checkRes.forModel(model)) {
            PropertyDocumentReference<GraphEdgeMatcherDescriptor> ref =
                ResourceDocumentReference.buildResourceReference(
                    container, checkRes);
            result.add(ref);
          }
        }
      }

    return result;
  }
}
