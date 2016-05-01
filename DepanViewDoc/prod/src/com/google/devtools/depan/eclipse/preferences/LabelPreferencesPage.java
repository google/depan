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

import com.google.devtools.depan.eclipse.preferences.LabelPreferencesIds.LabelPosition;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * Preferences pages for label options.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class LabelPreferencesPage extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage {

  private ScopedPreferenceStore preferences;

  public LabelPreferencesPage() {
    super(GRID);
    preferences = PreferencesIds.getInstanceStore();
    setPreferenceStore(preferences);
  }

  @Override
  protected void createFieldEditors() {
    Composite parent = getFieldEditorParent();

    // label positions.
    RadioGroupFieldEditor position = new RadioGroupFieldEditor(
        LabelPreferencesIds.LABEL_POSITION,
        "Label position", 3, new String[][] {
            // first line of 3 elements
            {"North-west", LabelPosition.NW.toString()},
            {"North", LabelPosition.N.toString()},
            {"North-east", LabelPosition.NE.toString()},
            // second line
            {"West", LabelPosition.W.toString()},
            {"Center", LabelPosition.CENTER.toString()},
            {"East", LabelPosition.E.toString()},
            // third line
            {"South-west", LabelPosition.SW.toString()},
            {"South", LabelPosition.S.toString()},
            {"South-east", LabelPosition.SE.toString()},
            // last line
            {"Inside, as node shape", LabelPosition.INSIDE.toString()},
            {"No labels", LabelPosition.NOLABEL.toString()},
            {"Only if selected", LabelPosition.IFSELECTED.toString()}
        },
        parent, true);

    addField(position);
  }

  @Override
  public void init(IWorkbench workbench) {
  }
}
