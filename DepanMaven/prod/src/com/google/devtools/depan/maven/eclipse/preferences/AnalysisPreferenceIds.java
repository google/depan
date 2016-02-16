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

package com.google.devtools.depan.maven.eclipse.preferences;

import com.google.devtools.depan.maven.eclipse.MavenActivator;


/**
 * A namespace class for Maven plugin preference IDs.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class AnalysisPreferenceIds {

  private AnalysisPreferenceIds() {
    // Prevent instantiation.
  }
  public static final String MVN_ANALYSIS_EXECUTABLE =
      MavenActivator.MVN_PREF_PREFIX + "executable";
  public static final String MVN_ANALYSIS_SYSTEMJAVA =
      MavenActivator.MVN_PREF_PREFIX + "system-java";
  public static final String MVN_ANALYSIS_JAVAHOME =
      MavenActivator.MVN_PREF_PREFIX + "java-home";
  public static final String MVN_ANALYSIS_EFFECTIVEPOM =
      MavenActivator.MVN_PREF_PREFIX + "effective-pom";
}
