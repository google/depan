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

import com.google.devtools.depan.platform.Colors;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import java.awt.Color;

/**
 * Preferences default values for editor colors.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class ColorPreferencesInitializer extends AbstractPreferenceInitializer {

  @Override
  public void initializeDefaultPreferences() {
    IEclipsePreferences defaults = PreferencesIds.getDefaultNode();

    setDefaultRGB(defaults, ColorPreferencesIds.COLOR_BACKGROUND, Color.WHITE);
    setDefaultRGB(defaults, ColorPreferencesIds.COLOR_FOREGROUND, Color.BLACK);
  }

  private void setDefaultRGB(IEclipsePreferences p, String key, Color color) {
    p.put(key, Colors.getRgb(color));
  }
}
