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

import com.google.devtools.depan.eclipse.preferences.PreferencesIds;
import com.google.devtools.depan.ruby.eclipse.RubyActivator;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * Preference page for Maven POM Analysis options.
 *
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class AnalysisPreferencesPage extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage {

  private ScopedPreferenceStore preferences;

  public AnalysisPreferencesPage() {
    super(GRID);
    preferences = PreferencesIds.getInstanceStore();
    setPreferenceStore(preferences);
  }

  @Override
  protected void createFieldEditors() {
    Composite parent = getFieldEditorParent();

    FileFieldEditor executable = new FileFieldEditor(
        AnalysisPreferenceIds.MVN_ANALYSIS_EXECUTABLE,
        "Maven Executable", 
        true, parent);
    executable.setEmptyStringAllowed(false);

    BooleanFieldEditor systemjava = new BooleanFieldEditor(
        AnalysisPreferenceIds.MVN_ANALYSIS_SYSTEMJAVA,
        "Use System JAVA_HOME",
        BooleanFieldEditor.SEPARATE_LABEL, parent);

    final DirectoryFieldEditor javahome = new DirectoryFieldEditor(
        AnalysisPreferenceIds.MVN_ANALYSIS_JAVAHOME,
        "JAVA_HOME", parent);

    StringFieldEditor effectivepom = new StringFieldEditor(
        AnalysisPreferenceIds.MVN_ANALYSIS_EFFECTIVEPOM,
        "Maven Effective POM command", parent);
    effectivepom.setEmptyStringAllowed(false);

    addField(executable);
    addField(systemjava);
    addField(javahome);
    addField(effectivepom);
  }

  @Override
  public void init(IWorkbench workbench) {
    setDescription("Define Maven POM analysis preferences.");
    setPreferenceStore(RubyActivator.getDefault().getPreferenceStore());
  }
}
