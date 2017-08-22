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

import static com.google.devtools.depan.platform.PlatformLogger.LOG;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

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
 * @author <a href='mailto:leeca@pnambic.com'>Lee Carver</a>
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
  protected abstract ContributionEntry<T> buildEntry(
      String bundleId, IConfigurationElement element);

  protected abstract void reportException(String entryId, Exception err);

  /**
   * Derived classes should override if their are additional
   * installation needs beyond their retention in then {@link #entries}
   * field.
   */
  protected void installContribution(String entryId, T plugin) {
    // Since a ContributionEntry contains a callable instance
    // no further installation is necessary.
  }

  /**
   * Load the plugins from the extension point, and fill the lists of entries.
   */
  protected void load(String extensionId) {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint point = registry.getExtensionPoint(extensionId);
    if (null == point) {
      return;
    }
    for (IExtension extension: point.getExtensions()) {
      IContributor contrib = extension.getContributor();
      String bundleId = contrib.getName();
      // ... and for each elements
      for (IConfigurationElement element :
          extension.getConfigurationElements()) {

        // obtain an object on the entry
        ContributionEntry<T> entry = buildEntry(bundleId, element);
        String entryId = entry.getId();
        if (Strings.isNullOrEmpty(entryId)) {
          LOG.warn("Empty entry id in {} for {}", bundleId, extensionId);
        }
        entries.put(entryId, entry);

        // Try to instantiate the contribution and install it.
        try {
          T plugin = entry.prepareInstance();
          installContribution(entryId, plugin);
        } catch (CoreException err) {
          reportException(entryId, err);
          throw new RuntimeException(err);
        }
      }
    }
  }

  protected Collection<ContributionEntry<T>> getContributions() {
    return entries.values();
  }

  protected ContributionEntry<T> getContribution(String contribId) {
    return entries.get(contribId);
  }

  protected Collection<ContributionEntry<T>>
      getContributions(Collection<String> contribIds) {
    Collection<ContributionEntry<T>> result =
        Lists.newArrayListWithExpectedSize(contribIds.size());
    for (String contribId : contribIds) {
      result.add(getContribution(contribId));
    }
    return result;
  }

  /**
   * Rare, and during serialization, so linear search lookup should not
   * be a performance bottleneck.
   */
  protected String getContributionId(T contrib) {
    for (Entry<String, ContributionEntry<T>> entry : entries.entrySet()) {
      if (entry.getValue().getInstance() == contrib) {
        return entry.getKey();
      }
    }
    // Unregistered contribution entry instance.
    return null;
  }

  /**
   * For {@code XStreamConfigRegistry}.
   */
  public Collection<Bundle> getPluginBundles() {
    Collection<Bundle> result = Sets.newHashSet();
    for (ContributionEntry<T> entry: getContributions()) {
      result.add(Platform.getBundle(entry.getBundleId()));
    }
    return result;
  }
}
