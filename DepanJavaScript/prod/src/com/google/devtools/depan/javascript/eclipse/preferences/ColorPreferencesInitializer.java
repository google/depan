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

package com.google.devtools.depan.javascript.eclipse.preferences;

import com.google.devtools.depan.javascript.eclipse.JavaScriptActivator;
import com.google.devtools.depan.platform.Colors;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import java.awt.Color;

/**
 * Preferences default values for JavaScript graph element colors.
 *
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class ColorPreferencesInitializer extends AbstractPreferenceInitializer {

  @Override
  public void initializeDefaultPreferences() {
    IPreferenceStore prefs = JavaScriptActivator.getDefault().getPreferenceStore();

    // Color choices similar to Java.
    setDefaultRGB(prefs, ColorPreferencesIds.COLOR_BUILTIN, Color.RED);
    setDefaultRGB(prefs, ColorPreferencesIds.COLOR_CLASS, Color.BLUE);
    setDefaultRGB(prefs, ColorPreferencesIds.COLOR_ENUM, Color.BLUE);
    setDefaultRGB(prefs, ColorPreferencesIds.COLOR_FIELD, Color.YELLOW);
    setDefaultRGB(prefs, ColorPreferencesIds.COLOR_FUNCTION, Color.RED);
    setDefaultRGB(prefs, ColorPreferencesIds.COLOR_VARIABLE, Color.YELLOW);
  }

  private void setDefaultRGB(IPreferenceStore prefs, String key, Color color) {
    prefs.setDefault(key, Colors.getRgb(color));
  }
}
