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
import com.google.devtools.depan.platform.plugin.PluginClassLoader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thoughtworks.xstream.XStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import java.util.Collection;
import java.util.Map;

/**
 * Registry of XStream configuration contributions.  The data from all
 * contributions is loaded for convenient accesses.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class PersistencePluginRegistry {
  /**
   * Extension point name for persistence configuration
   */
  public final static String EXTENTION_POINT =
      "com.google.devtools.depan.persistence";

  /**
   * Static instance. This class is a singleton.
   * It is created lazily on first access.
   */
  private static PersistencePluginRegistry INSTANCE = null;

  /**
   * A list of all registered plugins entries. The key is the plugin id.
   */
  private final Map<String, PersistencePlugin.Entry> entries = Maps.newHashMap();

  // TODO: Probably separate for different configuration categories.
  private final Collection<PersistencePlugin> configs = Lists.newArrayList();

 /**
   * Singleton class: private constructor to prevent instantiation.
   */
  private PersistencePluginRegistry() {
  }

  /**
   * Load the plugins from the extension point, and fill the lists of entries.
   */
  private void load() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint point = registry.getExtensionPoint(EXTENTION_POINT);

    // for each extension
    for (IExtension extension: point.getExtensions()) {
      // ... and for each elements
      for (IConfigurationElement element :
          extension.getConfigurationElements()) {
        // obtain an object on the entry
        PersistencePlugin.Entry entry = new PersistencePlugin.Entry(element);
        String entryId = entry.getId();
        entries.put(entryId, entry);
        try {
          // try to instantiate the plugin
          PersistencePlugin plugin = entry.getInstance();
          configs.add(plugin);
        } catch (CoreException err) {
          PersistenceLogger.logException(
              "NodeElement load failure for " + entryId, err);
          throw new RuntimeException(err);
        }
      }
    }
  }

  public Collection<Bundle> getPluginBundles() {
    Collection<Bundle> result = Lists.newArrayList();
    for (String bundleId: entries.keySet()) {
      result.add(Platform.getBundle(bundleId));
    }
    return result;
  }

  private void configXstream(XStream xstream) {
    for (PersistencePlugin plugin : configs) {
      plugin.config(xstream);
    }

    Collection<Bundle> bundles = getPluginBundles();
    xstream.setClassLoader(new PluginClassLoader(bundles));
  }

  /////////////////////////////////////
  // Static NodeElement Methods

  /**
   * Provide the {@code SourcePluginRegistry} singleton.
   * It is created lazily when needed.
   */
  public static synchronized PersistencePluginRegistry getInstance() {
    if (null == INSTANCE) {
      INSTANCE = new PersistencePluginRegistry();
      INSTANCE.load();
    }
    return INSTANCE;
  }

  public static void config(XStream xstream) {
    getInstance().configXstream(xstream);
  }
}
