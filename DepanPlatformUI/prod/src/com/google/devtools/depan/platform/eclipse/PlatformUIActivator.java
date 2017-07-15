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

package com.google.devtools.depan.platform.eclipse;

import com.google.devtools.depan.resources.analysis.AnalysisResourceRegistry;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Make sure plugin resources are installed.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class PlatformUIActivator extends AbstractUIPlugin {

  // The shared instance
  private static PlatformUIActivator plugin;

  @Override
  public void start(BundleContext context) throws Exception {
    AnalysisResourceRegistry.installRegistryResources();
    plugin = this;
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    plugin = null;
  }

  public static IDialogSettings getPlatformDialogSettings() {
    return plugin.getDialogSettings();
  }
}
