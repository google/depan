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
import com.google.common.collect.Maps;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Support common plugin pattern with multiple contributions to a single
 * extension point.
 * 
 * Extension points based on this class should reside in an {@code .plugins}
 * directory for their supporting system. (The {@code .registry} package
 * suffix is reserved for registry support.)
 * 
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

  private Map<Relation, RelationContributor> relationToContrib =
      Maps.newHashMap();

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
    PlatformLogger.LOG.error(
        "Relation registry load failure for {}", entryId, err);
  }

  @Override
  protected void load(String extensionId) {
    super.load(extensionId);
    deriveDetails();
  }

  private void deriveDetails() {
    // Build reverse map from relation to contributor
    for (ContributionEntry<RelationContributor> entry : getContributions()) {
      RelationContributor contrib = entry.getInstance();
      for (Relation relation : contrib.getRelations()) {
        relationToContrib .put(relation, contrib);
      }
    }
  }

  /////////////////////////////////////
  // Project Relations

  public Collection<Relation> getRelations() {
    return buildRelations(getContributions());
  }

  public Collection<Relation> getRelations(Collection<String> contribIds) {
    return buildRelations(getContributions(contribIds));
  }

  public List<String> getContribIds() {
    List<String> result = Lists.newArrayList();
    for (ContributionEntry<RelationContributor> contrib : getContributions()) {
      result.add(contrib.getId());
    }
    return result;
  }

  private Collection<Relation> buildRelations(
      Collection<ContributionEntry<RelationContributor>> contrib) {

    List<Relation> result = Lists.newArrayList();
    for (ContributionEntry<RelationContributor> entry : contrib) {
      result.addAll(entry.getInstance().getRelations());
    }
    return result;
  }

  private String getRelationSource(Relation relation) {
    RelationContributor result = relationToContrib.get(relation);
    if (null != result) {
      return result.getLabel();
    }

    String msg = MessageFormat.format(
        "- unsrcd {0} -", relation.getForwardName());
    return msg;
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

  public static Collection<Relation> getRegistryRelations() {
    return getInstance().getRelations();
  }

  public static Collection<Relation> getRegistryRelations(
      Collection<String> contribIds) {
    return getInstance().getRelations(contribIds);
  }

  public static String getRegistryRelationSource(
      Relation relation) {
    return getInstance().getRelationSource(relation);
  }

  public static List<String> getRegistryContribIds() {
    return getInstance().getContribIds();
  }
}
