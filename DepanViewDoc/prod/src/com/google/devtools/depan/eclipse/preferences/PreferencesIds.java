/*
 * Copyright 2007 The Depan Project Authors
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

package com.google.devtools.depan.eclipse.preferences;

import com.google.devtools.depan.view_doc.eclipse.ViewDocResources;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * An namespace class for preferences IDs.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public final class PreferencesIds {

  // private constructor to prevent instantiation
  private PreferencesIds() {
  }

  public static final String PREFIX = "prefs_";
  public static final String VIEW_PREFIX = PREFIX + "view_";

  public static final IEclipsePreferences getDefaultNode() {
    return DefaultScope.INSTANCE.getNode(ViewDocResources.PLUGIN_ID);
  }

  public static final IEclipsePreferences getInstanceNode() {
    return InstanceScope.INSTANCE.getNode(ViewDocResources.PLUGIN_ID);
  }

  public static final ScopedPreferenceStore getInstanceStore() {
    return new ScopedPreferenceStore(
        InstanceScope.INSTANCE, ViewDocResources.PLUGIN_ID);
  }
}
