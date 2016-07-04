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

package com.google.devtools.depan.ruby.eclipse.preferences;

import com.google.devtools.depan.ruby.eclipse.RubyActivator;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Preferences default values for editor colors.
 *
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class AnalysisPreferencesInitializer extends AbstractPreferenceInitializer {

  @Override
  public void initializeDefaultPreferences() {
    IPreferenceStore defaults = RubyActivator.getDefault().getPreferenceStore();

    defaults.setDefault(AnalysisPreferenceIds.MVN_ANALYSIS_EXECUTABLE, 
        "c:\\Program Files (x86)\\Maven\\apache-maven-3.3.9\\bin\\mvn.cmd");
    defaults.setDefault(AnalysisPreferenceIds.MVN_ANALYSIS_SYSTEMJAVA,
        true);
    defaults.setDefault(AnalysisPreferenceIds.MVN_ANALYSIS_JAVAHOME,
        "");
    defaults.setDefault(AnalysisPreferenceIds.MVN_ANALYSIS_EFFECTIVEPOM,
        "help:effective-pom");
  }
}
