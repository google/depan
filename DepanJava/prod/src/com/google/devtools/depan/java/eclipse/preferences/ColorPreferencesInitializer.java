/*
 * Copyright 2007 The Depan Project Authors
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
package com.google.devtools.depan.java.eclipse.preferences;

import com.google.devtools.depan.java.eclipse.JavaActivator;
import com.google.devtools.depan.platform.Colors;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import java.awt.Color;

/**
 * Preferences default values for Java graph element colors.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class ColorPreferencesInitializer extends AbstractPreferenceInitializer {

  @Override
  public void initializeDefaultPreferences() {
    IPreferenceStore defaults = JavaActivator.getDefault().getPreferenceStore();

    setDefaultRGB(defaults, ColorPreferencesIds.COLOR_FIELD, Color.YELLOW);
    setDefaultRGB(defaults, ColorPreferencesIds.COLOR_INTERFACE, Color.GREEN);
    setDefaultRGB(defaults, ColorPreferencesIds.COLOR_METHOD, Color.RED);
    setDefaultRGB(defaults, ColorPreferencesIds.COLOR_PACKAGE, Color.CYAN);
    setDefaultRGB(defaults, ColorPreferencesIds.COLOR_SOURCE, Color.MAGENTA);
    setDefaultRGB(defaults, ColorPreferencesIds.COLOR_DIRECTORY, Color.ORANGE);
    setDefaultRGB(defaults, ColorPreferencesIds.COLOR_TYPE, Color.BLUE);
  }

  private void setDefaultRGB(IPreferenceStore prefs, String key, Color color) {
    prefs.setDefault(key, Colors.getRgb(color));
  }
}
