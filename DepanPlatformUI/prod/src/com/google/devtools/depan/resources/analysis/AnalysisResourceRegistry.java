/*
 * Copyright 2017 The Depan Project Authors
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

package com.google.devtools.depan.resources.analysis;

import com.google.devtools.depan.platform.PlatformLogger;
import com.google.devtools.depan.platform.plugin.ContributionEntry;
import com.google.devtools.depan.platform.plugin.ContributionRegistry;
import com.google.devtools.depan.platform.plugin.ConfigurationDependOrder;
import com.google.devtools.depan.resources.ResourceContainer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class AnalysisResourceRegistry
    extends ContributionRegistry<AnalysisResourceInstaller> {

  private static final String INSTALL_AFTER_TAG = "install_after";

  private static final String INSTALL_AFTER_ATTR = "installer_ref";

  /**
   * Extension point name for persistence configuration
   */
  public final static String EXTENTION_POINT =
      "com.google.devtools.depan.resources.analysis.installer";

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
    PlatformLogger.LOG.error(
        "Analysis resource registry load failure for {}", entryId, err);
  }

  private void installResources() {
    ResourceContainer root = AnalysisResources.getRoot();

    ConfigurationDependOrder<AnalysisResourceInstaller> sequencer =
        new ConfigurationDependOrder<AnalysisResourceInstaller>(
            INSTALL_AFTER_TAG, INSTALL_AFTER_ATTR);
    sequencer.addContributions(getContributions());
    for (AnalysisResourceInstaller installer : sequencer.getDependOrder()) {
      installer.installResource(root);
    }
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
