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

package com.google.devtools.depan.graph.registry;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.platform.PlatformLogger;
import com.google.devtools.depan.platform.plugin.ContributionEntry;
import com.google.devtools.depan.platform.plugin.ContributionRegistry;

import com.google.common.collect.Lists;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import java.util.List;

/**
 * @author Lee Carver
 */
public class RelationRegistry extends
    ContributionRegistry<RelationContributor> {

  /**
   * Extension point name for persistence configuration
   */
  public final static String EXTENTION_POINT =
      "com.google.devtools.depan.graph.registry.relation";

  /**
   * Static instance. This class is a singleton.
   * It is created lazily on first access.
   */
  private static RelationRegistry INSTANCE = null;

  static class Entry extends ContributionEntry<RelationContributor>{

    public Entry(String bundleId, IConfigurationElement element) {
      super(bundleId, element);
    }

    @Override
    protected RelationContributor createInstance() throws CoreException {
      return (RelationContributor) buildInstance(ATTR_CLASS);
    }
  }

  /**
   * Singleton class: private constructor to prevent instantiation.
   */
  private RelationRegistry() {
  }

  @Override
  protected ContributionEntry<RelationContributor> buildEntry(
      String bundleId, IConfigurationElement element) {
    return new Entry(bundleId, element);
  }

  @Override
  protected void reportException(String entryId, Exception err) {
    PlatformLogger.logException(
        "Relation registry load failure for " + entryId, err);
  }

  /////////////////////////////////////
  // Project Relations

  public List<Relation> getRelations() {
    List<Relation> result = Lists.newArrayList();
    for (ContributionEntry<RelationContributor> entry : getContributions()) {
      result.addAll(entry.getInstance().getRelations());
    }
    return result;
  }


  public List<Relation> getRelations(List<String> contribIds) {
    List<Relation> result = Lists.newArrayList();
    for (ContributionEntry<RelationContributor> entry 
        : getContributions(contribIds)) {
      result.addAll(entry.getInstance().getRelations());
    }
    return result;
  }

  /////////////////////////////////////
  // Singleton access methods

  /**
   * Provide the {@code SourcePluginRegistry} singleton.
   * It is created lazily when needed.
   */
  public static synchronized RelationRegistry getInstance() {
    if (null == INSTANCE) {
      INSTANCE = new RelationRegistry();
      INSTANCE.load(EXTENTION_POINT);
    }
    return INSTANCE;
  }

  public static List<Relation> getRegistryRelations() {
    return getInstance().getRelations();
  }

  public static List<Relation> getRegistryRelations(List<String> contribIds) {
    return getInstance().getRelations(contribIds);
  }
}
