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

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page for Java node color selections.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class ColorPreferencesPage extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage {

  public ColorPreferencesPage() {
    super(GRID);
  }

  @Override
  protected void createFieldEditors() {
    Composite parent = getFieldEditorParent();

    ColorFieldEditor type = new ColorFieldEditor(
        ColorPreferencesIds.COLOR_TYPE, "Types", parent);
    ColorFieldEditor method = new ColorFieldEditor(
        ColorPreferencesIds.COLOR_METHOD, "Methods", parent);
    ColorFieldEditor field = new ColorFieldEditor(
        ColorPreferencesIds.COLOR_FIELD, "Fields", parent);
    ColorFieldEditor interfac = new ColorFieldEditor(
        ColorPreferencesIds.COLOR_INTERFACE, "Interfaces", parent);
    ColorFieldEditor pkge = new ColorFieldEditor(
        ColorPreferencesIds.COLOR_PACKAGE, "Packages", parent);
    ColorFieldEditor source = new ColorFieldEditor(
        ColorPreferencesIds.COLOR_SOURCE, "Source files", parent);
    ColorFieldEditor directory = new ColorFieldEditor(
        ColorPreferencesIds.COLOR_DIRECTORY, "Directories", parent);

    addField(type);
    addField(method);
    addField(field);
    addField(interfac);
    addField(pkge);
    addField(source);
    addField(directory);
  }

  @Override
  public void init(IWorkbench workbench) {
    setDescription("Define Java Node Color Preferences.");
    setPreferenceStore(JavaActivator.getDefault().getPreferenceStore());
  }
}
