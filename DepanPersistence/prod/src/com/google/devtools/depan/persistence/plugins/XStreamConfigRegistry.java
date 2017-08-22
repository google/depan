/*
 * Copyright 2008 The Depan Project Authors
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

package com.google.devtools.depan.persistence.plugins;

import com.google.devtools.depan.persistence.PersistenceLogger;
import com.google.devtools.depan.platform.plugin.ContributionEntry;
import com.google.devtools.depan.platform.plugin.ContributionRegistry;

import com.thoughtworks.xstream.XStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.osgi.framework.Bundle;

import java.util.Collection;

/**
 * Registry of XStream configuration contributions.  The data from all
 * contributions is loaded for convenient accesses.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class XStreamConfigRegistry extends
  ContributionRegistry<XStreamConfig> {

  /**
   * Extension point name for persistence configuration
   */
  public final static String EXTENTION_POINT =
      "com.google.devtools.depan.persistence.xstream.config";

  /**
   * Static instance. This class is a singleton.
   * It is created lazily on first access.
   */
  private static XStreamConfigRegistry INSTANCE = null;


  static class Entry extends ContributionEntry<XStreamConfig>{

    public Entry(String bundleId, IConfigurationElement element) {
      super(bundleId, element);
    }

    @Override
    protected XStreamConfig createInstance() throws CoreException {
      return (XStreamConfig) buildInstance(ATTR_CLASS);
    }
  }

  /**
   * Singleton class: private constructor to prevent instantiation.
   */
  private XStreamConfigRegistry() {
  }

  @Override
  protected ContributionEntry<XStreamConfig>
      buildEntry(String bundleId, IConfigurationElement element) {
    return new Entry(bundleId, element);
  }

  @Override
  protected void reportException(String entryId, Exception err) {
    PersistenceLogger.LOG.error(
        "XStream configuration load failure for {}", entryId, err);
  }

  private void configXStream(XStream xstream) {
    for (ContributionEntry<XStreamConfig> entry : getContributions()) {
      entry.getInstance().config(xstream);
    }
  }

  /////////////////////////////////////
  // Static NodeElement Methods

  /**
   * Provide the {@code SourcePluginRegistry} singleton.
   * It is created lazily when needed.
   */
  public static synchronized XStreamConfigRegistry getInstance() {
    if (null == INSTANCE) {
      INSTANCE = new XStreamConfigRegistry();
      INSTANCE.load(EXTENTION_POINT);
    }
    return INSTANCE;
  }

  public static void config(XStream xstream) {
    getInstance().configXStream(xstream);
  }

  public static Collection<Bundle> getRegistryPluginBundles() {
    return getInstance().getPluginBundles();
  }
}
