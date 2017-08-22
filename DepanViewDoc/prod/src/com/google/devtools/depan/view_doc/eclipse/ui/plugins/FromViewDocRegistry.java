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

package com.google.devtools.depan.view_doc.eclipse.ui.plugins;

import com.google.devtools.depan.platform.PlatformLogger;
import com.google.devtools.depan.platform.plugin.ContributionEntry;
import com.google.devtools.depan.platform.plugin.ContributionRegistry;
import com.google.devtools.depan.view_doc.eclipse.ViewDocLogger;

import com.google.common.collect.Maps;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import java.util.Map;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class FromViewDocRegistry extends
    ContributionRegistry<FromViewDocContributor> {

  /**
   * Extension point name for configuration
   */
  public final static String EXTENTION_POINT =
      "com.google.devtools.depan.view_doc.eclipse.ui.registry.from_view_doc";

  /**
   * Static instance. This class is a singleton.
   * It is created lazily on first access.
   */
  private static FromViewDocRegistry INSTANCE = null;

  static class Entry extends ContributionEntry<FromViewDocContributor>{

    public Entry(String bundleId, IConfigurationElement element) {
      super(bundleId, element);
    }

    @Override
    protected FromViewDocContributor createInstance() throws CoreException {
      return (FromViewDocContributor) buildInstance(ATTR_CLASS);
    }
  }

  private FromViewDocRegistry() {
    // Prevent instantiation.
  }

  @Override
  protected ContributionEntry<FromViewDocContributor> buildEntry(
      String bundleId, IConfigurationElement element) {
    return new Entry(bundleId, element);
  }

  @Override
  protected void reportException(String entryId, Exception err) {
    PlatformLogger.LOG.error(
        "FromViewDoc load failure for " + entryId, err);
  }

  /////////////////////////////////////
  // Project Relations

  public Map<String, FromViewDocContributor> getContributionMap() {
    Map<String, FromViewDocContributor> result = Maps.newHashMap();
    for (ContributionEntry<FromViewDocContributor> contrib : getContributions()) {
      FromViewDocContributor fromGraphDoc = contrib.getInstance();
      String label = fromGraphDoc.getLabel();
      if (!result.containsKey(label)) {
        result.put(label, fromGraphDoc);
      } else {
        ViewDocLogger.LOG.warn(
            "Duplicate FromViewDoc contribution for label {}", label);
      }
    }

    return result ;
  }

  /////////////////////////////////////
  // Singleton access methods

  /**
   * Provide the {@code FromViewDocRegistry} singleton.
   * It is created lazily when needed.
   */
  public static synchronized FromViewDocRegistry getInstance() {
    if (null == INSTANCE) {
      INSTANCE = new FromViewDocRegistry();
      INSTANCE.load(EXTENTION_POINT);
    }
    return INSTANCE;
  }

  public static Map<String, FromViewDocContributor> getRegistryContributionMap() {
    return getInstance().getContributionMap();
  }
}
