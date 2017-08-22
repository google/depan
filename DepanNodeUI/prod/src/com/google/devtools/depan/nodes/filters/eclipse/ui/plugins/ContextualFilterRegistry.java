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

package com.google.devtools.depan.nodes.filters.eclipse.ui.plugins;

import com.google.devtools.depan.nodes.filters.model.ContextualFilter;
import com.google.devtools.depan.platform.PlatformLogger;
import com.google.devtools.depan.platform.plugin.ContributionEntry;
import com.google.devtools.depan.platform.plugin.ContributionRegistry;

import com.google.common.collect.Maps;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import java.util.Map;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class ContextualFilterRegistry extends
    ContributionRegistry<ContextualFilterContributor<? extends ContextualFilter>> {

  /**
   * Extension point name for persistence configuration
   */
  public final static String EXTENTION_POINT =
      "com.google.devtools.depan.nodes.filters.eclipse.ui.plugins.filters";

  /**
   * Static instance. This class is a singleton.
   * It is created lazily on first access.
   */
  private static ContextualFilterRegistry INSTANCE = null;

  static class Entry extends ContributionEntry<ContextualFilterContributor<? extends ContextualFilter>>{

    public Entry(String bundleId, IConfigurationElement element) {
      super(bundleId, element);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ContextualFilterContributor<? extends ContextualFilter> createInstance() throws CoreException {
      return (ContextualFilterContributor<? extends ContextualFilter>) buildInstance(ATTR_CLASS);
    }
  }

  /**
   * Singleton class: private constructor to prevent instantiation.
   */
  private ContextualFilterRegistry() {
  }

  @Override
  protected ContributionEntry<ContextualFilterContributor<? extends ContextualFilter>> buildEntry(
      String bundleId, IConfigurationElement element) {
    return new Entry(bundleId, element);
  }

  @Override
  protected void reportException(String entryId, Exception err) {
    PlatformLogger.LOG.error(
        "Contextual filter registry load failure for {}", entryId, err);
  }

  /////////////////////////////////////
  // Project Relations

  public Map<String, ContextualFilterContributor<? extends ContextualFilter>> getContributionMap() {
    Map<String, ContextualFilterContributor<? extends ContextualFilter>> result = Maps.newHashMap();
    for (ContributionEntry<ContextualFilterContributor<? extends ContextualFilter>> contrib : getContributions()) {
      ContextualFilterContributor<? extends ContextualFilter> fromGraphDoc = contrib.getInstance();
      result.put(fromGraphDoc.getLabel(), fromGraphDoc);
    }

    return result ;
  }

  private ContextualFilterContributor<? extends ContextualFilter> findContributor(ContextualFilter filter) {
    for (ContributionEntry<ContextualFilterContributor<? extends ContextualFilter>> contrib : getContributions()) {
      ContextualFilterContributor<? extends ContextualFilter> result =
          contrib.getInstance();
      if (result.handlesFilterInstance(filter)) {
        return result;
      }
    }

    return null;
  }

  /////////////////////////////////////
  // Singleton access methods

  /**
   * Provide the {@code SourcePluginRegistry} singleton.
   * It is created lazily when needed.
   */
  public static synchronized ContextualFilterRegistry getInstance() {
    if (null == INSTANCE) {
      INSTANCE = new ContextualFilterRegistry();
      INSTANCE.load(EXTENTION_POINT);
    }
    return INSTANCE;
  }

  public static Map<String, ContextualFilterContributor<? extends ContextualFilter>>
      getRegistryContributionMap() {
    return getInstance().getContributionMap();
  }

  public static ContextualFilterContributor<? extends ContextualFilter> findRegistryContributor(
      ContextualFilter filter) {
    return getInstance().findContributor(filter);
  }
}
