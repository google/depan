/*
 * Copyright 2016 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.devtools.depan.javascript.eclipse;

import com.google.devtools.depan.eclipse.preferences.PreferencesIds;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 * 
 * In the current implementation, all resources are "borrowed" from other
 * plug-ins, especially the {@code FileSystem} and {@code Java} plugins.
 * As this plug-in matures, plug-in specific images and other resources are
 * anticipated.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class JavaScriptActivator extends AbstractUIPlugin {

  /** Plug-in ID used to identify this plug-in. */
  public static final String PLUGIN_ID = "com.google.devtools.depan.javascript.ui";

  /**
   * Bundle that is responsible for storing the resources for this plug-in.
   */
  public static final Bundle BUNDLE = Platform.getBundle(PLUGIN_ID);

  /**
   * Prefix to use for preferences.
   */
  public static final String JS_PREF_PREFIX = PreferencesIds.PREFIX + "js_";

  // The shared instance
  private static JavaScriptActivator plugin;

  /**
   * The constructor
   */
  public JavaScriptActivator() {
  }

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }

  /**
   * Returns the shared instance
   *
   * @return the shared instance
   */
  public static JavaScriptActivator getDefault() {
    return plugin;
  }

  /////////////////////////////////////
  // Plugin Images 

  // TODO(leeca): Add JavaScript specific resources, instead of borrowing
  // images, etc. from Java, FileSystem, etc.
}
