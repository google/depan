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
package com.google.devtools.depan.eclipse.preferences;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * Preference page for color selections.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class ColorPreferencesPage extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage {

  private ScopedPreferenceStore preferences;

  public ColorPreferencesPage() {
    super(GRID);
    preferences = PreferencesIds.getInstanceStore();
    setPreferenceStore(preferences);
  }

  @Override
  protected void createFieldEditors() {
    Composite parent = getFieldEditorParent();

    ColorFieldEditor background = new ColorFieldEditor(
        ColorPreferencesIds.COLOR_BACKGROUND, "Background", parent);
    ColorFieldEditor foreground = new ColorFieldEditor(
        ColorPreferencesIds.COLOR_FOREGROUND, "Text color", parent);

    addField(background);
    addField(foreground);
  }

  @Override
  public void init(IWorkbench workbench) {
  }
}
