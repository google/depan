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

package com.google.devtools.depan.platform.plugin;

import com.google.devtools.depan.model.Element;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
 * Registry of known NodeElement definitions.  NodeElement contributions are
 * provided by plugins and supply the basic display properties for their
 * GraphNodes (Elements).  The data from all contributions is loaded for
 * convenient accesses.
 * 
 * <p>It is also a static interface to transform {@link Element}s into useful
 * values such as colors, shapes, etc. The algorithm for this task is simple.
 * The plugin registry first look at the class of the given {@link Element}.
 * Then it selects the plugin providing the class, and call the right function
 * to that plugin.
 *
 * @author Yohann Coppel
 */
public abstract class ContributionRegistry<T> {

  /**
   * A list of all registered plugins entries. The key is the plugin id.
   */
  private final Map<String, ContributionEntry<T>> entries = Maps.newHashMap();

  /**
   * Allow derived classes to create a singleton instance.
   */
  protected ContributionRegistry() {
  }

  // Hook methods
  protected abstract void installContribution(String entryId, T plugin);

  protected abstract ContributionEntry<T> buildEntry(IConfigurationElement element);

  protected abstract void reportException(String entryId, Exception err);

  /**
   * Load the plugins from the extension point, and fill the lists of entries.
   */
  protected void load(String extensionId) {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint point = registry.getExtensionPoint(extensionId);
    for (IExtension extension: point.getExtensions()) {
      // ... and for each elements
      for (IConfigurationElement element :
          extension.getConfigurationElements()) {

        // obtain an object on the entry
        ContributionEntry<T> entry = buildEntry(element);
        String entryId = entry.getId();
        entries.put(entryId, entry);

        // try to instantiate the contribution and install it
        try {
          T plugin = entry.getInstance();
          installContribution(entryId, plugin);
        } catch (CoreException err) {
          reportException(entryId, err);
          throw new RuntimeException(err);
        }
      }
    }
  }

  // Keep this private for now.
  private Collection<Bundle> getPluginBundles() {
    Collection<Bundle> result = Lists.newArrayList();
    for (String bundleId: entries.keySet()) {
      result.add(Platform.getBundle(bundleId));
    }
    return result;
  }

  public PluginClassLoader getClassLoader() {
    return new PluginClassLoader(getPluginBundles());
  }
}
