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

package com.google.devtools.depan.graph_doc.eclipse.ui.plugins;

import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptors;
import com.google.devtools.depan.platform.PlatformLogger;
import com.google.devtools.depan.platform.plugin.ContributionEntry;
import com.google.devtools.depan.platform.plugin.ContributionRegistry;
import com.google.devtools.depan.relations.models.RelationSetDescriptors;
import com.google.devtools.depan.resources.PropertyDocument;
import com.google.devtools.depan.resources.ResourceContainer;
import com.google.devtools.depan.resources.analysis.AnalysisResources;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;


/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class AnalysisResourceRegistry
    extends ContributionRegistry<AnalysisResourceInstaller> {

  /**
   * Extension point name for persistence configuration
   */
  public final static String EXTENTION_POINT =
      "com.google.devtools.depan.graph_doc.eclipse.ui.registry.analysis_resource";

  /**
   * Static instance. This class is a singleton.
   * It is created lazily on first access.
   */
  private static AnalysisResourceRegistry INSTANCE = null;

  static class Entry extends ContributionEntry<AnalysisResourceInstaller>{

    public Entry(String bundleId, IConfigurationElement element) {
      super(bundleId, element);
    }

    @Override
    protected AnalysisResourceInstaller createInstance() throws CoreException {
      return (AnalysisResourceInstaller) buildInstance(ATTR_CLASS);
    }
  }

  private AnalysisResourceRegistry() {
    // Prevent instantiation.
  }

  @Override
  protected ContributionEntry<AnalysisResourceInstaller> buildEntry(
      String bundleId, IConfigurationElement element) {
    return new Entry(bundleId, element);
  }

  @Override
  protected void reportException(String entryId, Exception err) {
    PlatformLogger.logException(
        "Analysis resource registry load failure for " + entryId, err);
  }

  private void installResources() {
    // Start with build-in resources 
    ResourceContainer root = AnalysisResources.getRoot();
    installBuiltInResources(root);

    // Add in contributed resources
    for (ContributionEntry<AnalysisResourceInstaller> contrib
        : getContributions()) {
      contrib.getInstance().installResource(root);
    }
  }

  private static void installBuiltInResources(ResourceContainer root) {
    ResourceContainer matchers = root.getChild(AnalysisResources.MATCHERS);
    addResource(matchers, GraphEdgeMatcherDescriptors.FORWARD);
    addResource(matchers, GraphEdgeMatcherDescriptors.EMPTY);

    ResourceContainer relSets = root.getChild(AnalysisResources.RELATION_SETS);
    addResource(relSets, RelationSetDescriptors.EMPTY);
  }

  private static void addResource(
      ResourceContainer container, PropertyDocument<?> resource) {
    container.addResource(resource.getName(), resource);
  }

  /////////////////////////////////////
  // Singleton access methods

  /**
   * Provide the {@code SourcePluginRegistry} singleton.
   * It is created lazily when needed.
   */
  public static synchronized AnalysisResourceRegistry getInstance() {
    if (null == INSTANCE) {
      INSTANCE = new AnalysisResourceRegistry();
      INSTANCE.load(EXTENTION_POINT);
    }
    return INSTANCE;
  }

  public static void installRegistryResources() {
    getInstance().installResources();
  }
}
