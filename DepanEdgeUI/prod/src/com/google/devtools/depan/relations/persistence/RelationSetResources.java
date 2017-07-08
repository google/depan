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

package com.google.devtools.depan.relations.persistence;

import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.platform.PlatformTools;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.models.RelationSetDescriptors;
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
public class RelationSetResources {

  /** Name of resource tree container for relation resources. */
  public static final String RELATIONS = "relations";

  /** Base file name for a new RelSet resource. */
  public static final String BASE_NAME = "relset";

  /** Expected extensions for a RelSet resource. */
  public static final String EXTENSION = "relxml";

  /**
   * Kludgy bit of Singleton mis-use to simplify access to a very
   * commonly referenced resource.  Other solutions are welcome.
   */
  public static PropertyDocumentReference<RelationSetDescriptor> ALL_REF;

  public static PropertyDocumentReference<RelationSetDescriptor> EMPTY_REF;

  private RelationSetResources() {
    // Prevent instantiation.
  }

  public static void installResources(ResourceContainer root) {
    ResourceContainer relSets = root.addChild(RELATIONS);
    ALL_REF = installRelSet(relSets, RelationSetDescriptors.ALL);
    EMPTY_REF = installRelSet(relSets, RelationSetDescriptors.EMPTY);
  }

  public static void installRelSets(
      ResourceContainer container, Collection<RelationSetDescriptor> relSets) {

    for (RelationSetDescriptor descr : relSets) {
      container.addResource(descr.getName(), descr);
    }
  }

  private static PropertyDocumentReference<RelationSetDescriptor>
      installRelSet(
      ResourceContainer container, RelationSetDescriptor relSet) {
    container.addResource(relSet);
    return ResourceDocumentReference
        .buildResourceReference(container, relSet);
  }

  /**
   * Kludgy bit of Singleton mis-use to simplify access to a very
   * commonly referenced resource.  Other solutions are welcome.
   */
  public static ResourceContainer getContainer() {
    return AnalysisResources.getRoot().getChild(RELATIONS);
  }

  public static String getBaseNameExt() {
    return PlatformTools.getBaseNameExt(BASE_NAME, EXTENSION);
  }

  public static List<PropertyDocumentReference<RelationSetDescriptor>>
      getRelationSets(DependencyModel model) {

    List<PropertyDocumentReference<RelationSetDescriptor>> result =
        Lists.newArrayList();

    // Filter for GEMs with the supplied model.
    ResourceContainer container = getContainer();
    for (Object resource : container.getResources()) {
        if (resource instanceof RelationSetDescriptor) {
          RelationSetDescriptor checkRes = (RelationSetDescriptor) resource;

          if (checkRes.forModel(model)) {
            PropertyDocumentReference<RelationSetDescriptor> ref =
                ResourceDocumentReference.buildResourceReference(
                    container, checkRes);
            result.add(ref);
          }
        }
      }

    return result;
  }
}
