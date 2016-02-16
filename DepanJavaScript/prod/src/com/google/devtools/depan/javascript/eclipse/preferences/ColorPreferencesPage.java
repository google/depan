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

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page for JavaScript node color selections.
 *
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class ColorPreferencesPage extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage {

  public ColorPreferencesPage() {
    super(GRID);
  }

  @Override
  protected void createFieldEditors() {
    Composite parent = getFieldEditorParent();

    ColorFieldEditor builtin = new ColorFieldEditor(
        ColorPreferencesIds.COLOR_BUILTIN, "Built-ins", parent);
    ColorFieldEditor jsclass = new ColorFieldEditor(
        ColorPreferencesIds.COLOR_CLASS, "Classes", parent);
    ColorFieldEditor jsenum = new ColorFieldEditor(
        ColorPreferencesIds.COLOR_ENUM, "Enums", parent);
    ColorFieldEditor field= new ColorFieldEditor(
        ColorPreferencesIds.COLOR_FIELD, "Fields", parent);
    ColorFieldEditor function = new ColorFieldEditor(
        ColorPreferencesIds.COLOR_FUNCTION, "Functions", parent);
    ColorFieldEditor variable = new ColorFieldEditor(
        ColorPreferencesIds.COLOR_VARIABLE, "Variables", parent);

    addField(builtin);
    addField(jsclass);
    addField(jsenum);
    addField(field);
    addField(function);
    addField(variable);
  }

  @Override
  public void init(IWorkbench workbench) {
    setDescription("Define JavaScript Node Color Preferences.");
    setPreferenceStore(JavaScriptActivator.getDefault().getPreferenceStore());
  }
}
